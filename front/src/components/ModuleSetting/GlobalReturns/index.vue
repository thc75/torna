<template>
  <div>
    <el-button
      type="primary"
      size="mini"
      style="margin-bottom: 10px"
      @click="onParamAdd"
    >
      {{ $ts('add') }}
    </el-button>
    <help-link style="margin-left: 20px;" to="/help?id=global" />
    <el-table
      :data="globalReturns"
      border
      highlight-current-row
      row-key="id"
      default-expand-all
      :indent="20"
      class="el-table-tree"
    >
      <el-table-column :label="$ts('paramName')" prop="name" width="300px">
        <template slot-scope="scope">
          <span :class="hasNoParentAndChildren(scope.row) ? 'el-table--row-no-parent-children' : ''">
            {{ scope.row.name }}
          </span>
        </template>
      </el-table-column>
      <el-table-column :label="$ts('type')" prop="type" width="120px" />
      <el-table-column :label="$ts('example')" prop="example" >
        <template slot-scope="scope">
          <span v-show="!isNodeData(scope.row)">
            {{ scope.row.example }}
          </span>
        </template>
      </el-table-column>
      <el-table-column :label="$ts('description')" prop="description" />
      <el-table-column
        :label="$ts('operation')"
        :width="$width(200, { 'en': 240 })"
      >
        <template slot-scope="scope">
          <el-link type="primary" size="mini" @click="onParamAddChild(scope.row)">{{ $ts('addChildNode') }}</el-link>
          <el-link type="primary" size="mini" @click="onParamUpdate(scope.row)">{{ $ts('update') }}</el-link>
          <el-popconfirm
            :title="$ts('deleteConfirm', scope.row.name)"
            @confirm="onParamDelete(scope.row)"
          >
            <el-link slot="reference" type="danger" size="mini">{{ $ts('delete') }}</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!--dialog-->
    <el-dialog
      :title="dialogParamTitle"
      :visible.sync="dialogParamVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogParamForm')"
    >
      <el-form
        ref="dialogParamForm"
        :rules="dialogParamFormRules"
        :model="dialogParamFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item
          prop="name"
          :label="$ts('paramName')"
        >
          <el-input v-model="dialogParamFormData.name" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item
          prop="dataType"
          :label="$ts('type')"
        >
          <el-select v-model="dialogParamFormData.type" size="mini">
            <el-option v-for="type in getTypeConfig()" :key="type" :label="type" :value="type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          :label="$ts('linkDict')"
        >
          <el-select v-model="dialogParamFormData.enumId" :clearable="true" size="mini">
            <el-option v-for="enumInfo in enumData" :key="enumInfo.id" :label="enumInfo.name" :value="enumInfo.id">
              {{ enumInfo.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="example"
          :label="$ts('example')"
        >
          <el-switch
            v-model="isDataNode"
            :active-text="$ts('isDataNode')"
            inactive-text=""
          />
          <el-input v-show="!isDataNode" v-model="dialogParamFormData.example" show-word-limit maxlength="200" />
        </el-form-item>
        <el-form-item
          prop="description"
          :label="$ts('description')"
        >
          <el-input v-model="dialogParamFormData.description" type="textarea" show-word-limit maxlength="200" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogParamVisible = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogParamSave">{{ $ts('dlgSave') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import HelpLink from '@/components/HelpLink'
const DATA_PLACEHOLDER = '$data$'

export default {
  components: { HelpLink },
  data() {
    return {
      globalReturns: [],
      enumData: [],
      moduleId: '',
      dialogParamVisible: false,
      dialogParamTitle: '',
      dialogParamFormData: {
        id: '',
        moduleId: '',
        name: '',
        example: '',
        type: 'string',
        enumId: '',
        description: ''
      },
      dialogParamFormRules: {
        name: [
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value && !/^[a-zA-Z0-9\-_]+$/.test(value)) {
              callback(new Error(this.$ts('errorParam')))
            } else {
              callback()
            }
          }, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    isDataNode: {
      set(b) {
        if (b) {
          this.dialogParamFormData.type = 'object'
          this.dialogParamFormData.example = DATA_PLACEHOLDER
        } else {
          this.dialogParamFormData.type = 'string'
          this.dialogParamFormData.example = ''
        }
      },
      get() {
        return this.isNodeData(this.dialogParamFormData)
      }
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loadParams(this.moduleId)
      if (this.moduleId) {
        this.loadEnumData(this.moduleId, data => {
          this.enumData = data
        })
      }
    },
    loadParams(moduleId) {
      this.get('/module/setting/globalReturns/list', { moduleId: moduleId }, resp => {
        const data = resp.data
        this.globalReturns = this.convertTree(data)
      })
    },
    isNodeData(row) {
      return row.example === DATA_PLACEHOLDER
    },
    onParamAdd() {
      this.dialogParamTitle = this.$ts('addParam')
      this.dialogParamVisible = true
      this.dialogParamFormData.id = ''
      this.dialogParamFormData.enumId = ''
      this.dialogParamFormData.type = 'string'
    },
    onParamAddChild(row) {
      const child = this.getNewData(row)
      this.dialogParamTitle = this.$ts('addChildTitle', row.name)
      this.dialogParamVisible = true
      this.dialogParamFormData = child
    },
    getNewData(parent) {
      const parentId = parent ? parent.id : ''
      return {
        id: '',
        moduleId: '',
        name: '',
        type: 'string',
        parentId: parentId,
        example: '',
        description: ''
      }
    },
    onParamUpdate(row) {
      this.dialogParamTitle = this.$ts('updateParam')
      this.dialogParamVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogParamFormData, row)
      })
    },
    onParamDelete(row) {
      this.post('/module/setting/globalReturns/delete', row, () => {
        this.tip('删除成功')
        this.reload()
      })
    },
    onDialogParamSave() {
      this.$refs.dialogParamForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogParamFormData.id ? '/module/setting/globalReturns/update' : '/module/setting/globalReturns/add'
          this.dialogParamFormData.moduleId = this.moduleId
          this.post(uri, this.dialogParamFormData, () => {
            this.dialogParamVisible = false
            this.reload()
          })
        }
      })
    }
  }
}
</script>
