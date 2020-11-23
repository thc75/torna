<template>
  <el-table
    :data="rows"
    border
    row-key="id"
    default-expand-all
    :cell-style="cellStyleSmall()"
    :header-cell-style="headCellStyleSmall()"
    :empty-text="emptyText"
    class="param-table"
  >
    <el-table-column
      v-if="isColumnShow('name')"
      prop="name"
      label="名称"
      width="300"
    >
      <template slot-scope="scope">
        <el-form
          :ref="`form_name_${scope.row.id}`"
          :model="scope.row"
          :rules="paramRowRule"
          size="mini"
          style="display: inline-block;width: 220px;"
        >
          <el-form-item
            prop="name"
            label-width="0"
          >
            <el-input v-model="scope.row.name" placeholder="参数名称" maxlength="64" show-word-limit />
          </el-form-item>
        </el-form>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('type')"
      prop="type"
      label="类型"
      width="120"
    >
      <template slot-scope="scope">
        <el-select v-model="scope.row.type" size="mini">
          <el-option v-for="type in typeConfig" :key="type" :label="type" :value="type"></el-option>
        </el-select>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('required')"
      prop="required"
      label="必填"
      width="80"
    >
      <template slot-scope="scope">
        <el-switch
          v-model="scope.row.required"
          active-color="#13ce66"
          :active-value="1"
          :inactive-value="0"
        />
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('maxLength')"
      prop="maxLength"
      label="最大长度"
      width="130"
    >
      <template slot-scope="scope">
        <el-input v-model="scope.row.maxLength" placeholder="最大长度" size="mini" maxlength="10" show-word-limit />
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('description')"
      prop="description"
      label="描述"
    >
      <template slot-scope="scope">
        <el-form :ref="`form_description_${scope.row.id}`" :model="scope.row" :rules="paramRowRule" size="mini">
          <el-form-item
            prop="description"
            label-width="0"
          >
            <el-input v-model="scope.row.description" placeholder="描述" maxlength="128" show-word-limit />
          </el-form-item>
        </el-form>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('example')"
      prop="example"
      label="示例值"
    >
      <template slot-scope="scope">
        <el-input
          v-model="scope.row.example"
          placeholder="示例值"
          maxlength="128"
          size="mini"
          show-word-limit
        />
      </template>
    </el-table-column>
    <el-table-column
      label="操作"
      width="80"
    >
      <template slot-scope="scope">
        <div>
          <div v-show="scope.row.isDeleted === 0">
            <el-tooltip content="添加子节点" placement="top">
              <el-link v-if="canAddNode" type="primary" icon="el-icon-circle-plus-outline" @click="onParamNodeAdd(scope.row)"></el-link>
            </el-tooltip>
            <el-link type="danger" icon="el-icon-delete" @click="onParamRemove(scope.row)"></el-link>
          </div>
          <div v-show="scope.row.isDeleted === 1">
            <el-tooltip content="点击恢复" placement="top">
              <el-link type="danger" icon="el-icon-remove" @click="scope.row.isDeleted = 0"></el-link>
            </el-tooltip>
          </div>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>
<script>
let idGen = 0
export default {
  name: 'ParamTable',
  props: {
    data: {
      type: Array,
      default: () => []
    },
    emptyText: {
      type: String,
      default: '无参数'
    },
    canAddNode: {
      type: Boolean,
      default: true
    },
    hiddenColumns: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      rows: [],
      typeConfig: [
        'string',
        'number',
        'boolean',
        'array',
        'object',
        'file'
      ],
      paramRowRule: {
        name: [
          { required: true, message: '请填写参数名称', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请填写描述', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    data(val) {
      this.rows = val
    }
  },
  methods: {
    isColumnShow(label) {
      return this.hiddenColumns.filter(lb => lb === label).length === 0
    },
    onParamNodeAdd: function(row) {
      const children = row.children || []
      children.push(this.getParamNewRow())
      children.hasChildren = true
      row.children = children
    },
    getParamNewRow: function(name, value) {
      return {
        id: new Date().getTime() + (idGen++),
        name: name || '',
        type: 'string',
        required: 1,
        description: '',
        maxLength: 64,
        example: value || '',
        isDeleted: 0,
        isNew: true,
        children: []
      }
    },
    onParamRemove: function(row) {
      if (row.isNew) {
        this.removeRow(this.rows, row.id)
      } else {
        row.isDeleted = 1
      }
    },
    getData() {
      return this.rows
    },
    validate() {
      return this.validateTable(this.rows, ['form_name_'])
    }
  }
}
</script>
