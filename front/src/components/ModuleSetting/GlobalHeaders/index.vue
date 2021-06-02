<template>
  <div>
    <el-button
      type="primary"
      size="mini"
      style="margin-bottom: 10px"
      @click="onHeaderAdd"
    >
      {{ $ts('add') }}
    </el-button>
    <el-table
      :data="globalHeaders"
      border
      highlight-current-row
    >
      <el-table-column label="Name" prop="name" width="300px" />
      <el-table-column label="Value" prop="example" />
      <el-table-column :label="$ts('description')" prop="description" />
      <el-table-column
        :label="$ts('operation')"
        width="150"
      >
        <template slot-scope="scope">
          <el-link type="primary" size="mini" @click="onHeaderUpdate(scope.row)">{{ $ts('update') }}</el-link>
          <el-popconfirm
            :title="$ts('deleteConfirm', scope.row.name)"
            @confirm="onHeaderDelete(scope.row)"
          >
            <el-link slot="reference" type="danger" size="mini">{{ $ts('delete') }}</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!--dialog-->
    <el-dialog
      :title="dialogHeaderTitle"
      :visible.sync="dialogHeaderVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogHeaderForm')"
    >
      <el-form
        ref="dialogHeaderForm"
        :rules="dialogHeaderFormRules"
        :model="dialogHeaderFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item
          prop="name"
          label="Name"
        >
          <el-input v-model="dialogHeaderFormData.name" placeholder="name" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item
          prop="example"
          label="Value"
        >
          <el-input v-model="dialogHeaderFormData.example" placeholder="value" clearable />
        </el-form-item>
        <el-form-item
          prop="description"
          :label="$ts('description')"
        >
          <el-input v-model="dialogHeaderFormData.description" type="textarea" :placeholder="$ts('description')" show-word-limit maxlength="200" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogHeaderVisible = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogHeaderSave">{{ $ts('dlgSave') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  data() {
    return {
      globalHeaders: [],
      moduleId: '',
      dialogHeaderVisible: false,
      dialogHeaderTitle: '',
      dialogHeaderFormData: {
        id: '',
        moduleId: '',
        name: '',
        example: '',
        description: ''
      },
      dialogHeaderFormRules: {
        name: [
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value && !/^[a-zA-Z0-9\-_]+$/.test(value)) {
              callback(new Error(this.$ts('errorParam')))
            } else {
              callback()
            }
          }, trigger: 'blur' }
        ], example: [
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loadHeaders(this.moduleId)
    },
    loadHeaders(moduleId) {
      this.get('/module/setting/globalHeaders/list', { moduleId: moduleId }, resp => {
        this.globalHeaders = resp.data
      })
    },
    onHeaderAdd() {
      this.dialogHeaderTitle = this.$ts('newHeader')
      this.dialogHeaderVisible = true
      this.dialogHeaderFormData.id = ''
    },
    onHeaderUpdate(row) {
      this.dialogHeaderTitle = this.$ts('updateHeader')
      this.dialogHeaderVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogHeaderFormData, row)
      })
    },
    onHeaderDelete(row) {
      this.post('/module/setting/globalHeaders/delete', row, () => {
        this.tipSuccess(this.$ts('deleteSuccess'))
        this.reload()
      })
    },
    onDialogHeaderSave() {
      this.$refs.dialogHeaderForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogHeaderFormData.id ? '/module/setting/globalHeaders/update' : '/module/setting/globalHeaders/add'
          this.dialogHeaderFormData.moduleId = this.moduleId
          this.post(uri, this.dialogHeaderFormData, () => {
            this.dialogHeaderVisible = false
            this.reload()
          })
        }
      })
    }
  }
}
</script>
