<template>
  <div>
    <el-button
      type="primary"
      size="mini"
      style="margin-bottom: 10px"
      @click="onHeaderAdd"
    >
      添加
    </el-button>
    <el-table
      :data="globalHeaders"
      border
      highlight-current-row
    >
      <el-table-column label="Name" prop="name" width="300px" />
      <el-table-column label="Value" prop="example" />
      <el-table-column label="描述" prop="description" />
      <el-table-column
        label="操作"
        width="150"
      >
        <template slot-scope="scope">
          <el-link type="primary" size="mini" @click="onHeaderUpdate(scope.row)">修改</el-link>
          <el-popconfirm
            :title="`确定要删除 ${scope.row.name} 吗？`"
            @confirm="onHeaderDelete(scope.row)"
          >
            <el-link slot="reference" type="danger" size="mini">删除</el-link>
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
          <el-input v-model="dialogHeaderFormData.example" placeholder="value" show-word-limit maxlength="200" />
        </el-form-item>
        <el-form-item
          prop="description"
          label="描述"
        >
          <el-input v-model="dialogHeaderFormData.description" type="textarea" placeholder="描述" show-word-limit maxlength="200" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogHeaderVisible = false">取 消</el-button>
        <el-button type="primary" @click="onDialogHeaderSave">保 存</el-button>
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
          { required: true, message: '不能为空', trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value && !/^[a-zA-Z0-9\-_]+$/.test(value)) {
              callback(new Error('格式错误，支持大小写英文、数字、-、下划线'))
            } else {
              callback()
            }
          }, trigger: 'blur' }
        ], example: [
          { required: true, message: '不能为空', trigger: 'blur' }
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
      this.dialogHeaderTitle = '新增Header'
      this.dialogHeaderVisible = true
      this.dialogHeaderFormData.id = ''
    },
    onHeaderUpdate(row) {
      this.dialogHeaderTitle = '修改Header'
      this.dialogHeaderVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogHeaderFormData, row)
      })
    },
    onHeaderDelete(row) {
      this.post('/module/setting/globalHeaders/delete', row, () => {
        this.tip('删除成功')
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
