<template>
  <div>
    <h2>{{ settings.moduleVO.name }}配置</h2>
    <h4>全局Headers</h4>
    <el-button type="primary" size="mini" style="margin-bottom: 10px" @click="onHeaderAdd">添加Header</el-button>
    <el-table
      :data="settings.globalHeaders"
      border
      :header-cell-style="cellStyleSmall()"
      :cell-style="cellStyleSmall()"
    >
      <el-table-column label="Name" prop="configKey" width="300px" />
      <el-table-column label="Value" prop="configValue" />
      <el-table-column
        label="操作"
        width="150"
      >
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="onHeaderUpdate(scope.row)">修改</el-button>
          <el-popconfirm
            :title="`确定要删除 ${scope.row.configKey} 吗？`"
            @onConfirm="onHeaderDelete(scope.row)"
          >
            <el-button slot="reference" type="text" size="mini">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <h4>调试Host</h4>
    <el-alert title="调试页面请求Host" :closable="false" style="margin-bottom: 10px;" />
    <el-form ref="debugHostRef" :model="settings" size="mini">
      <el-form-item prop="debugHost">
        <el-input v-model="settings.debugHost" placeholder="如：http://10.0.10.11:8080" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSaveDebugHost">保存</el-button>
      </el-form-item>
    </el-form>
    <div v-if="settings.moduleVO.type === 1">
      <h4>Swagger多个Method重复，只显示</h4>
      <el-form ref="allowMethodsRef" :model="settings" size="mini">
        <el-form-item prop="allowMethods">
          <el-select v-model="settings.allowMethod" @change="onSaveAllowMethods">
            <el-option v-for="method in allMethods" :key="method" :label="method" :value="method">
              {{ method }}
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <!--dialog-->
    <el-dialog
      :title="dialogHeaderTitle"
      :visible.sync="dialogHeaderVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogHeaderForm')"
    >
      <el-form
        ref="dialogHeaderForm"
        :rules="dialogFormRules"
        :model="dialogHeaderFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item
          prop="configKey"
          label="Name"
        >
          <el-input v-model="dialogHeaderFormData.configKey" placeholder="name" />
        </el-form-item>
        <el-form-item
          prop="configValue"
          label="Value"
        >
          <el-input v-model="dialogHeaderFormData.configValue" placeholder="value" />
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
  name: 'ModuleSetting',
  props: {
    moduleId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      settings: {
        moduleVO: {
          name: '',
          type: 0
        },
        globalHeaders: [],
        allowMethod: 'POST',
        debugHost: ''
      },
      allMethods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'HEAD'],
      dialogHeaderVisible: false,
      dialogHeaderTitle: '',
      dialogHeaderFormData: {
        id: '',
        moduleId: '',
        configKey: '',
        configValue: ''
      },
      dialogFormRules: {
        configKey: [
          { required: true, message: '不能为空', trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value && !/^[a-zA-Z0-9\-_]+$/.test(value)) {
              callback(new Error('格式错误，支持大小写英文、数字、-、下划线'))
            } else {
              callback()
            }
          }, trigger: 'blur' }
        ], configValue: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    moduleId(id) {
      this.loadSettings(id)
    }
  },
  methods: {
    reload() {
      this.loadSettings(this.moduleId)
    },
    loadHeaders() {
      this.get('/module/setting/globalHeader/list', { moduleId: this.moduleId }, resp => {
        this.settings.globalHeaders = resp.data
      })
    },
    loadSettings(moduleId) {
      if (moduleId) {
        this.get('/module/setting/get', { moduleId: moduleId }, function(resp) {
          this.settings = resp.data
        })
      }
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
      this.post('/module/setting/globalHeader/delete', row, () => {
        this.tip('删除成功')
        this.loadHeaders()
      })
    },
    onDialogHeaderSave() {
      this.$refs.dialogHeaderForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogHeaderFormData.id ? '/module/setting/globalHeader/update' : '/module/setting/globalHeader/add'
          this.dialogHeaderFormData.moduleId = this.moduleId
          this.post(uri, this.dialogHeaderFormData, () => {
            this.dialogHeaderVisible = false
            this.loadHeaders()
          })
        }
      })
    },
    onSaveAllowMethods() {
      const data = {
        moduleId: this.moduleId,
        method: this.settings.allowMethod
      }
      this.post('/module/setting/allowMethod/set', data, () => {
        this.tipSuccess('修改成功')
      })
    },
    onSaveDebugHost() {
      this.$refs.debugHostRef.validate((valid) => {
        if (valid) {
          const data = {
            moduleId: this.moduleId,
            debugHost: this.settings.debugHost
          }
          this.post('/module/setting/debughost/set', data, () => {
            this.tipSuccess('保存成功')
          })
        }
      })
    }
  }
}
</script>
