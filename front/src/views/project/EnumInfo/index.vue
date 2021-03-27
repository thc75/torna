<template>
  <div>
    <h3>
      <el-button
        v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
        type="primary"
        size="mini"
        @click="onItemInfoAdd"
      >
        添加字典分类
      </el-button>
    </h3>
    <el-tabs v-show="baseData.length > 0" v-model="activeName" type="border-card" @tab-click="onTabClick">
      <el-tab-pane v-for="info in baseData" :key="info.id" :label="info.name" :name="info.id">
        <span slot="label">
          {{ info.name }}
          <el-dropdown
            v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
            v-show="info.id === enumInfo.id"
            trigger="click"
            style="margin-left: 5px;"
            @command="handleCommand"
          >
            <span class="el-dropdown-link">
              <el-tooltip placement="top" content="更多操作" :open-delay="500">
                <a class="el-icon-setting el-icon--right"></a>
              </el-tooltip>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item icon="el-icon-edit" :command="onEnumInfoUpdate">修改</el-dropdown-item>
              <el-dropdown-item icon="el-icon-delete" :command="onEnumInfoDelete">删除</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </span>
      </el-tab-pane>
      <div>
        <el-alert v-if="enumInfo.description" :closable="false" :title="enumInfo.description" style="margin-bottom: 10px;" />
        <el-button
          v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
          type="text"
          @click="onEnumItemAdd"
        >
          添加字典
        </el-button>
        <el-table
          :data="enumData"
          border
          :header-cell-style="cellStyleSmall()"
          :cell-style="cellStyleSmall()"
        >
          <el-table-column label="名称" prop="name" />
          <el-table-column label="类型" prop="type" />
          <el-table-column label="值" prop="value" />
          <el-table-column label="描述" prop="description" />
          <el-table-column
            v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
            label="操作"
            width="150"
          >
            <template slot-scope="scope">
              <el-link type="primary" @click="onEnumItemUpdate(scope.row)">修改</el-link>
              <el-popconfirm
                :title="`确定要删除 ${scope.row.name} 吗？`"
                @onConfirm="onEnumItemDelete(scope.row)"
              >
                <el-link v-if="hasRole(`project:${projectId}`, [Role.admin])" slot="reference" type="danger" size="mini">删除</el-link>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-tabs>
    <!--dialog-->
    <el-dialog
      :title="dialogEnumInfoTitle"
      :visible.sync="dialogEnumInfoVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogEnumInfoForm')"
    >
      <el-form
        ref="dialogEnumInfoForm"
        :rules="dialogEnumInfoFormRules"
        :model="dialogEnumInfoFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item
          prop="name"
          label="分类名称"
        >
          <el-input v-model="dialogEnumInfoFormData.name" show-word-limit maxlength="100" />
        </el-form-item>
        <el-form-item
          prop="description"
          label="描述"
        >
          <el-input v-model="dialogEnumInfoFormData.description" type="textarea" show-word-limit maxlength="100" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogEnumInfoVisible = false">取 消</el-button>
        <el-button type="primary" @click="onEnumInfoDialogSave">保 存</el-button>
      </div>
    </el-dialog>
    <!--dialog-->
    <el-dialog
      :title="dialogEnumItemTitle"
      :visible.sync="dialogEnumItemVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogEnumItemForm')"
    >
      <el-form
        ref="dialogEnumItemForm"
        :rules="dialogEnumItemFormRules"
        :model="dialogEnumItemFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item
          prop="name"
          label="名称"
        >
          <el-input v-model="dialogEnumItemFormData.name" show-word-limit maxlength="100" />
        </el-form-item>
        <el-form-item
          prop="type"
          label="类型"
        >
          <el-select v-model="dialogEnumItemFormData.type" size="mini">
            <el-option v-for="type in getBaseTypeConfig()" :key="type" :label="type" :value="type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="value"
          label="值"
        >
          <el-input v-model="dialogEnumItemFormData.value" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item
          prop="description"
          label="描述"
        >
          <el-input v-model="dialogEnumItemFormData.description" show-word-limit maxlength="100" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogEnumItemVisible = false">取 消</el-button>
        <el-button type="primary" @click="onEnumItemDialogSave">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<style>
.enum-title .el-link {
  margin-left: 10px;
}
</style>
<script>
export default {
  name: 'EnumInfo',
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      activeName: '',
      moduleId: '',
      enumInfo: {
        id: '',
        name: ''
      },
      baseData: [],
      enumData: [],
      dialogEnumInfoVisible: false,
      dialogEnumInfoTitle: '',
      dialogEnumInfoFormData: {
        id: '',
        name: '',
        description: '',
        moduleId: ''
      },
      dialogEnumInfoFormRules: {
        name: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        description: [
          { maxLength: 100, message: '长度不能超过100', trigger: 'blur' }
        ]
      },
      // item
      dialogEnumItemVisible: false,
      dialogEnumItemTitle: '',
      dialogEnumItemFormData: {
        id: '',
        name: '',
        enumId: '',
        type: 'string',
        value: '',
        description: ''
      },
      dialogEnumItemFormRules: {
        name: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        value: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        description: [
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
      this.loadData(this.moduleId)
    },
    loadData(moduleId) {
      this.get('/doc/enum/info/baselist', { moduleId: moduleId }, resp => {
        this.baseData = resp.data
        if (this.enumInfo.id) {
          this.loadTable(this.enumInfo.id)
        } else if (this.baseData.length > 0) {
          this.loadTable(this.baseData[0].id)
        }
      })
    },
    onTabClick(tab) {
      this.loadTable(tab.name)
    },
    loadTable(enumId) {
      for (const info of this.baseData) {
        if (enumId === info.id) {
          this.enumInfo = info
          break
        }
      }
      this.activeName = enumId
      this.loadEnumItem(enumId).then(data => {
        this.enumData = data
      })
    },
    onItemInfoAdd() {
      this.dialogEnumInfoTitle = '新增字典分类'
      this.dialogEnumInfoVisible = true
      this.dialogEnumInfoFormData.id = 0
      this.dialogEnumInfoFormData.moduleId = this.moduleId
    },
    onEnumInfoUpdate() {
      const row = this.enumInfo
      this.dialogEnumInfoTitle = '修改字典分类'
      this.dialogEnumInfoVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogEnumInfoFormData, row)
      })
    },
    onEnumInfoDelete() {
      const row = this.enumInfo
      this.confirm(`确认要删除 ${row.name} 吗？`, () => {
        this.get('/doc/enum/info/delete', { id: row.id }, () => {
          this.enumInfo.id = ''
          this.reload()
        })
      })
    },
    // item
    onEnumItemAdd() {
      const enumInfo = this.enumInfo
      this.dialogEnumItemTitle = '新增字典'
      this.dialogEnumItemVisible = true
      this.dialogEnumItemFormData.id = 0
      this.dialogEnumItemFormData.enumId = enumInfo.id
    },
    onEnumItemUpdate(row) {
      this.dialogEnumItemTitle = '修改字典'
      this.dialogEnumItemVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogEnumItemFormData, row)
      })
    },
    onEnumInfoDialogSave() {
      this.$refs.dialogEnumInfoForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogEnumInfoFormData.id ? '/doc/enum/info/update' : '/doc/enum/info/add'
          this.post(uri, this.dialogEnumInfoFormData, resp => {
            this.enumInfo = resp.data
            this.reload()
            this.dialogEnumInfoVisible = false
          })
        }
      })
    },
    onEnumItemDialogSave() {
      this.$refs.dialogEnumItemForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogEnumItemFormData.id ? '/doc/enum/item/update' : '/doc/enum/item/add'
          this.post(uri, this.dialogEnumItemFormData, () => {
            this.loadTable(this.dialogEnumItemFormData.enumId)
            this.dialogEnumItemVisible = false
          })
        }
      })
    },
    onEnumItemDelete(row) {
      this.get('/doc/enum/item/delete', { id: row.id }, () => {
        this.loadTable(row.enumId)
      })
    }
  }
}
</script>
<style scoped lang="scss">
.el-dropdown-link {
  a:hover {
    color: #409eff;
  }
}
</style>
