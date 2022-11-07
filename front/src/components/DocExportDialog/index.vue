<template>
  <div>
    <el-dialog
      :title="$ts('exportDoc')"
      :visible.sync="dialogVisible"
    >
      <el-form
        ref="dialogForm"
        :model="dialogFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item :label="$ts('exportAs')">
          <template>
            <el-radio-group v-model="dialogFormData.style">
              <el-radio :label="1">{{ $ts('singlePage') }}</el-radio>
              <el-radio :label="2">{{ $ts('multiPage') }}</el-radio>
            </el-radio-group>
          </template>
        </el-form-item>
        <el-form-item :label="$ts('fileType')">
          <template>
            <el-radio-group v-model="dialogFormData.type">
              <el-radio label="html">html</el-radio>
              <el-radio label="md">markdown</el-radio>
              <el-radio label="word">word</el-radio>
            </el-radio-group>
          </template>
        </el-form-item>
        <el-form-item :label="$ts('selectDoc')">
          <el-radio-group v-model="dialogFormData.isAll">
            <el-radio :label="1">{{ $ts('allDocs') }}</el-radio>
            <el-radio :label="0">{{ $ts('partDocs') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$ts('selectEnv')">
          <el-checkbox v-model="checkAll" :indeterminate="isIndeterminate">{{ $ts('selectAll') }}</el-checkbox>
          <el-checkbox-group v-model="dialogFormData.envIds">
            <el-checkbox v-for="env in envs" :key="env.id" :label="env.id">{{ env.name }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item v-show="dialogFormData.isAll === 0">
          <doc-tree ref="docTreeRef" readonly />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" :loading="loading" @click="onDialogSave">{{ $ts('dlgExport') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import DocTree from '@/components/DocTree'
import ExportUtil from '@/utils/export'

const EXPORT_STYLE = {
  ALL_IN_ONE: 1,
  MULTI_PAGE: 2
}
const EXPORT_TYPE = {
  HTML: 'html',
  MARKDOWN: 'md',
  WORD: 'word'
}
export default {
  components: { DocTree },
  data() {
    return {
      dialogVisible: false,
      dialogFormData: {
        style: EXPORT_STYLE.ALL_IN_ONE,
        type: EXPORT_TYPE.HTML,
        isAll: 1,
        envIds: []
      },
      envs: [],
      loading: false
    }
  },
  computed: {
    isIndeterminate() {
      const checkedCount = this.dialogFormData.envIds.length
      return checkedCount > 0 && checkedCount < this.envs.length
    },
    checkAll: {
      get() {
        return this.dialogFormData.envIds.length === this.envs.length
      },
      set(val) {
        this.$set(this.dialogFormData, 'envIds', val ? this.envs.map(item => item.id) : [])
      }
    }
  },
  methods: {
    show(data, moduleId) {
      this.loadEnv(moduleId)
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.docTreeRef.setData(data)
      })
    },
    loadEnv(moduleId) {
      this.get('module/environment/list', { moduleId: moduleId }, resp => {
        this.envs = resp.data
        this.dialogFormData.envIds = this.envs.map(item => item.id)
      })
    },
    onDialogSave() {
      let keys
      if (this.dialogFormData.isAll === 1) {
        keys = this.$refs.docTreeRef.getAllKeys()
      } else {
        keys = this.$refs.docTreeRef.getCheckedAllKeys()
        if (!keys || keys.length === 0) {
          this.tipError(this.$ts('pleaseCheckDoc'))
          return
        }
      }
      if (keys.length === 0) {
        return
      }
      this.loading = true
      this.post('/doc/detail/search', { docIdList: keys }, resp => {
        const docInfoList = resp.data
        this.formatEnv(docInfoList)
        this.doExport(docInfoList)
        this.loading = false
      })
    },
    formatEnv(docInfoList) {
      const envIds = this.dialogFormData.envIds
      if (envIds.length === 0) {
        for (const item of docInfoList) {
          item.debugEnvs = []
        }
      } else {
        const debugEnvs = []
        for (const envId of envIds) {
          for (const env of this.envs) {
            if (env.id === envId) {
              debugEnvs.push(env)
            }
          }
        }
        for (const item of docInfoList) {
          item.debugEnvs = debugEnvs
        }
      }
    },
    doExport(docInfoList) {
      const type = this.dialogFormData.type
      const style = this.dialogFormData.style
      switch (type) {
        case EXPORT_TYPE.HTML:
          if (style === EXPORT_STYLE.ALL_IN_ONE) {
            ExportUtil.exportHtmlAllInOne(docInfoList)
          } else {
            ExportUtil.exportHtmlMultiPages(docInfoList)
          }
          break
        case EXPORT_TYPE.MARKDOWN:
          if (style === EXPORT_STYLE.ALL_IN_ONE) {
            ExportUtil.exportMarkdownAllInOne(docInfoList)
          } else {
            ExportUtil.exportMarkdownMultiPages(docInfoList)
          }
          break
        case EXPORT_TYPE.WORD:
          if (style === EXPORT_STYLE.ALL_IN_ONE) {
            ExportUtil.exportWordAllInOne(docInfoList)
          } else {
            ExportUtil.exportWordMultiPages(docInfoList)
          }
          break
        default:
      }
    }
  }
}
</script>
