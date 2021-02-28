<template>
  <div>
    <el-button
      v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
      type="primary"
      size="mini"
      @click="onItemInfoAdd"
    >
      添加字典分类
    </el-button>
    <div v-for="enumInfo in enumData" :key="enumInfo.id">
      <h3 class="enum-title">
        {{ enumInfo.name }}
        <el-link v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])" type="primary" icon="el-icon-edit" @click="onEnumInfoUpdate(enumInfo)" />
        <el-popconfirm
          :title="`确定要删除 ${enumInfo.name} 吗？`"
          @onConfirm="onEnumInfoDelete(enumInfo)"
        >
          <el-link v-if="hasRole(`project:${projectId}`, [Role.admin])" slot="reference" type="danger" icon="el-icon-delete" />
        </el-popconfirm>
      </h3>
      <el-alert v-if="enumInfo.description" :closable="false" :title="enumInfo.description" style="margin-bottom: 10px;" />
      <el-button
        v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
        type="text"
        @click="onEnumItemAdd(enumInfo)"
      >
        添加字典
      </el-button>
      <el-table
        :data="enumInfo.items"
        border
        :header-cell-style="cellStyleSmall()"
        :cell-style="cellStyleSmall()"
      >
        <el-table-column label="名称" prop="name"/>
        <el-table-column label="类型" prop="type"/>
        <el-table-column label="值" prop="value"/>
        <el-table-column label="描述" prop="description"/>
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
      moduleId: '',
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
      this.get('/doc/enum/info/list', { moduleId: moduleId }, resp => {
        this.enumData = resp.data
      })
    },
    loadTable(enumId) {
      this.loadEnumItem(enumId).then(data => {
        for (let i = 0; i < this.enumData.length; i++) {
          const row = this.enumData[i]
          if (row.id === enumId) {
            row.items = data
            break
          }
        }
      })
    },
    onItemInfoAdd() {
      this.dialogEnumInfoTitle = '新增字典分类'
      this.dialogEnumInfoVisible = true
      this.dialogEnumInfoFormData.id = 0
      this.dialogEnumInfoFormData.moduleId = this.moduleId
    },
    onEnumInfoUpdate(row) {
      this.dialogEnumInfoTitle = '修改字典分类'
      this.dialogEnumInfoVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogEnumInfoFormData, row)
      })
    },
    onEnumItemAdd(enumInfo) {
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
          this.post(uri, this.dialogEnumInfoFormData, () => {
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
    onEnumInfoDelete(row) {
      this.get('/doc/enum/info/delete', { id: row.id }, () => {
        this.reload()
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
