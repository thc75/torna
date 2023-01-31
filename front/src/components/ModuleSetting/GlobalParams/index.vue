<template>
  <div>
    <el-button
      type="primary"
      size="mini"
      style="margin-bottom: 10px"
      @click="onParamAdd"
    >
      {{ $t('add') }}
    </el-button>
    <help-link style="margin-left: 20px;" to="/help?id=global" />
    <el-table
      :data="globalParams"
      border
      highlight-current-row
      row-key="id"
      default-expand-all
      :indent="20"
      class="el-table-tree"
    >
      <el-table-column :label="$t('paramName')" prop="name" width="300px">
        <template slot-scope="scope">
          <span :class="hasNoParentAndChildren(scope.row) ? 'el-table--row-no-parent-children' : ''">
            {{ scope.row.name }}
          </span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('type')" prop="type" width="120px" />
      <el-table-column
        prop="required"
        :label="$t('require')"
        :width="$width(60, { 'en': 75 })"
      >
        <template slot-scope="scope">
          <span :class="scope.row.required ? 'danger' : ''">{{ scope.row.required ? $t('yes') : $t('no') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('example')" prop="example" >
        <template slot-scope="scope">
          <span v-show="!isNodeData(scope.row)">
            {{ scope.row.example }}
          </span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('description')" prop="description" />
      <el-table-column
        :label="$t('operation')"
        :width="$width(200, { 'en': 240 })"
      >
        <template slot-scope="scope">
          <el-link type="primary" size="mini" @click="onParamAddChild(scope.row)">{{ $t('addChildNode') }}</el-link>
          <el-link type="primary" size="mini" @click="onParamUpdate(scope.row)">{{ $t('update') }}</el-link>
          <el-popconfirm
            :title="$t('deleteConfirm', scope.row.name)"
            @confirm="onParamDelete(scope.row)"
          >
            <el-link slot="reference" type="danger" size="mini">{{ $t('delete') }}</el-link>
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
          :label="$t('paramName')"
        >
          <el-input v-model="dialogParamFormData.name" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item
          prop="dataType"
          :label="$t('type')"
        >
          <el-select v-model="dialogParamFormData.type" size="mini">
            <el-option v-for="type in getTypeConfig()" :key="type" :label="type" :value="type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="required"
          :label="$t('required')"
        >
          <el-switch
            v-model="dialogParamFormData.required"
            :active-value="1"
            :inactive-value="0"
            :active-text="$t('yes')"
            :inactive-text="$t('no')"
          />
        </el-form-item>
        <el-form-item
          :label="$t('example')"
        >
          <el-switch
            v-model="isDataNode"
            :active-text="$t('isDataNode')"
            inactive-text=""
          />
          <el-input v-show="!isDataNode" v-model="dialogParamFormData.example" :placeholder="$t('example')" show-word-limit maxlength="200" />
        </el-form-item>
        <el-form-item
          prop="description"
          :label="$t('description')"
        >
          <el-input v-model="dialogParamFormData.description" type="textarea" show-word-limit maxlength="200" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogParamVisible = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogParamSave">{{ $t('dlgSave') }}</el-button>
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
      globalParams: [],
      environmentId: '',
      dialogParamVisible: false,
      dialogParamTitle: '',
      dialogParamFormData: {
        id: '',
        environmentId: '',
        name: '',
        type: 'string',
        required: 1,
        parentId: '',
        example: '',
        description: ''
      },
      dialogParamFormRules: {
        name: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value && !/^[a-zA-Z0-9\-_]+$/.test(value)) {
              callback(new Error(this.$t('errorParam')))
            } else {
              callback()
            }
          }, trigger: 'blur' }
        ]
      },
      style: this.getEnums().PARAM_STYLE.request
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
    reload(environmentId) {
      if (environmentId) {
        this.environmentId = environmentId
      }
      this.loadParams(this.environmentId)
    },
    loadParams(environmentId) {
      this.get('/module/environment/param/list', { environmentId: environmentId, style: this.style }, resp => {
        const data = resp.data
        this.globalParams = this.convertTree(data)
      })
    },
    onParamAdd() {
      this.dialogParamTitle = this.$t('addParam')
      this.dialogParamVisible = true
      this.dialogParamFormData = this.getNewData()
    },
    onParamUpdate(row) {
      this.dialogParamTitle = this.$t('updateParam')
      this.dialogParamVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogParamFormData, row)
      })
    },
    getNewData(parent) {
      const parentId = parent ? parent.id : ''
      return {
        id: '',
        environmentId: '',
        name: '',
        type: 'string',
        required: 1,
        parentId: parentId,
        example: '',
        description: '',
        style: this.getEnums().PARAM_STYLE.request
      }
    },
    onParamAddChild(row) {
      const child = this.getNewData(row)
      this.dialogParamTitle = this.$t('addChildTitle', row.name)
      this.dialogParamVisible = true
      this.dialogParamFormData = child
    },
    onParamDelete(row) {
      this.post('/module/environment/param/delete', row, () => {
        this.tipSuccess(this.$t('deleteSuccess'))
        this.reload()
      })
    },
    isNodeData(row) {
      return row.example === DATA_PLACEHOLDER
    },
    onDialogParamSave() {
      this.$refs.dialogParamForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogParamFormData.id ? 'module/environment/param/update' : 'module/environment/param/add'
          this.dialogParamFormData.environmentId = this.environmentId
          this.dialogParamFormData.style = this.style
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
