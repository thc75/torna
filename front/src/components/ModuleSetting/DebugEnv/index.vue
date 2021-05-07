<template>
  <div>
    <el-button
      type="primary"
      size="mini"
      style="margin-bottom: 10px"
      @click="onDebugEnvAdd"
    >
      新增环境
    </el-button>
    <el-table
      :data="debugEnv"
      border
      highlight-current-row
    >
      <el-table-column label="环境名称" prop="configKey" width="300px" />
      <el-table-column label="基本路径" prop="configValue" />
      <el-table-column
        label="操作"
        width="150"
      >
        <template slot-scope="scope">
          <el-link type="primary" size="mini" @click="onDebugEnvUpdate(scope.row)">修改</el-link>
          <el-popconfirm
            :title="`确定要删除 ${scope.row.configKey} 吗？`"
            @confirm="onDebugEnvDelete(scope.row)"
          >
            <el-link slot="reference" type="danger" size="mini">删除</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!--dialog-->
    <el-dialog
      :title="dialogDebugEnvTitle"
      :visible.sync="dialogDebugEnvVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogDebugEnvForm')"
    >
      <el-form
        ref="dialogDebugEnvForm"
        :rules="dialogDebugEnvFormRules"
        :model="dialogDebugEnvFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item
          prop="configKey"
          label="环境名称"
        >
          <el-input v-model="dialogDebugEnvFormData.configKey" placeholder="如：测试环境" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item
          prop="configValue"
          label="基本路径"
        >
          <el-input v-model="dialogDebugEnvFormData.configValue" placeholder="如：http://10.0.1.31:8080" show-word-limit maxlength="100" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogDebugEnvVisible = false">取 消</el-button>
        <el-button type="primary" @click="onDialogDebugEnvSave">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  data() {
    return {
      debugEnv: [],
      moduleId: '',
      dialogDebugEnvVisible: false,
      dialogDebugEnvTitle: '',
      dialogDebugEnvFormData: {
        id: '',
        moduleId: '',
        configKey: '',
        configValue: '',
        description: ''
      },
      dialogDebugEnvFormRules: {
        configKey: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        configValue: [
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
      this.loadDebugEnvs(this.moduleId)
    },
    loadDebugEnvs(moduleId) {
      this.get('/module/setting/debugEnv/list', { moduleId: moduleId }, resp => {
        this.debugEnv = resp.data
      })
    },
    onDebugEnvAdd() {
      this.dialogDebugEnvTitle = '新增环境'
      this.dialogDebugEnvVisible = true
      this.dialogDebugEnvFormData.id = ''
    },
    onDebugEnvUpdate(row) {
      this.dialogDebugEnvTitle = '修改环境'
      this.dialogDebugEnvVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogDebugEnvFormData, row)
      })
    },
    onDebugEnvDelete(row) {
      this.post('/module/setting/debugEnv/delete', row, () => {
        this.tip('删除成功')
        this.reload()
      })
    },
    onDialogDebugEnvSave() {
      this.$refs.dialogDebugEnvForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogDebugEnvFormData.id ? '/module/setting/debugEnv/update' : '/module/setting/debugEnv/add'
          this.dialogDebugEnvFormData.moduleId = this.moduleId
          this.post(uri, this.dialogDebugEnvFormData, () => {
            this.dialogDebugEnvVisible = false
            this.reload()
          })
        }
      })
    }
  }
}
</script>
