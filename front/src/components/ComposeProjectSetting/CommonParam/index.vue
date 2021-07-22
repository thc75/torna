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
    <el-table
      :data="globalParams"
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
      <el-table-column
        prop="required"
        :label="$ts('require')"
        :width="$width(60, { 'en': 75 })"
      >
        <template slot-scope="scope">
          <span :class="scope.row.required ? 'danger' : ''">{{ scope.row.required ? $ts('yes') : $ts('no') }}</span>
        </template>
      </el-table-column>
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
          prop="required"
          :label="$ts('required')"
        >
          <el-switch
            v-model="dialogParamFormData.required"
            :active-value="1"
            :active-text="$ts('yes')"
            inactive-text=""
          />
        </el-form-item>
        <el-form-item
          :label="$ts('example')"
        >
          <el-switch
            v-model="isDataNode"
            :active-text="$ts('isDataNode')"
            inactive-text=""
          />
          <el-input v-show="!isDataNode" v-model="dialogParamFormData.example" :placeholder="$ts('example')" show-word-limit maxlength="200" />
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

const DATA_PLACEHOLDER = '$data$'

export default {
  components: { },
  props: {
    listUrl: {
      type: String,
      required: true
    },
    addUrl: {
      type: String,
      required: true
    },
    updateUrl: {
      type: String,
      required: true
    },
    deleteUrl: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      globalParams: [],
      projectId: '',
      dialogParamVisible: false,
      dialogParamTitle: '',
      dialogParamFormData: {
        id: '',
        projectId: '',
        required: 1,
        name: '',
        type: 'string',
        parentId: '',
        example: '',
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
    reload(projectId) {
      if (projectId) {
        this.projectId = projectId
      }
      this.loadParams(this.projectId)
    },
    loadParams(projectId) {
      this.get(this.listUrl, { projectId: projectId }, resp => {
        const data = resp.data
        this.globalParams = this.convertTree(data)
      })
    },
    onParamAdd() {
      this.dialogParamTitle = this.$ts('addParam')
      this.dialogParamVisible = true
      this.dialogParamFormData = this.getNewData()
    },
    onParamUpdate(row) {
      this.dialogParamTitle = this.$ts('updateParam')
      this.dialogParamVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogParamFormData, row)
      })
    },
    getNewData(parent) {
      const parentId = parent ? parent.id : ''
      return {
        id: '',
        projectId: '',
        required: 1,
        name: '',
        type: 'string',
        parentId: parentId,
        example: '',
        description: ''
      }
    },
    onParamAddChild(row) {
      const child = this.getNewData(row)
      this.dialogParamTitle = this.$ts('addChildTitle', row.name)
      this.dialogParamVisible = true
      this.dialogParamFormData = child
    },
    onParamDelete(row) {
      this.post(this.deleteUrl, row, () => {
        this.tipSuccess(this.$ts('deleteSuccess'))
        this.reload()
      })
    },
    isNodeData(row) {
      return row.example === DATA_PLACEHOLDER
    },
    onDialogParamSave() {
      this.$refs.dialogParamForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogParamFormData.id ? this.updateUrl : this.addUrl
          this.dialogParamFormData.projectId = this.projectId
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
