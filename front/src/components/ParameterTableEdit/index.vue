<template>
  <el-table
    :data="data"
    border
    row-key="id"
    default-expand-all
    :tree-props="{ children: 'refs', hasChildren: 'hasChildren' }"
    :cell-style="cellStyleSmall()"
    :header-cell-style="headCellStyleSmall()"
    empty-text="无参数"
  >
    <el-table-column
      prop="name"
      label="名称"
      width="250"
    >
      <template slot-scope="scope">
        <span :class="{ 'required': scope.row.required}">{{ scope.row.name }}</span>
      </template>
    </el-table-column>
    <el-table-column
      prop="type"
      label="类型"
      width="100"
    >
      <template slot-scope="scope">
        <span>{{ scope.row.type }}</span>
        <span v-show="scope.row.type === 'array' && scope.row.elementType">
          <el-tooltip effect="dark" :content="`元素类型：${scope.row.elementType}`" placement="top">
            <i class="el-icon-info"></i>
          </el-tooltip>
        </span>
      </template>
    </el-table-column>
    <el-table-column
      prop="paramExample"
      label="参数值"
    >
      <template slot-scope="scope">
        <el-form
          v-if="scope.row.type !== 'object'"
          :ref="'req_form_example_' + scope.row.id"
          :model="scope.row"
          :rules="buildParamRules(scope.row)"
          size="mini"
          style="display: inline-block"
        >
          <el-form-item
            prop="paramExample"
            label-width="0"
            class="table-control"
          >
            <el-upload
              v-if="scope.row.type === 'file' || scope.row.elementType === 'file'"
              action=""
              :multiple="false"
              :auto-upload="false"
              :on-change="(file, fileList) => onSelectFile(file, fileList, scope.row)"
              :on-remove="(file, fileList) => onSelectFile(file, fileList, scope.row)"
            >
              <el-button slot="trigger" class="choose-file" type="primary">选择文件</el-button>
            </el-upload>
            <el-input v-else v-model="scope.row.paramExample" placeholder="参数值" clearable />
          </el-form-item>
        </el-form>
      </template>
    </el-table-column>
    <el-table-column
      prop="description"
      label="描述"
    />
  </el-table>
</template>
<style>
.table-control .el-form-item__error {
  position: inherit;
}
span.required:before {
  content: '*';
  color: #F56C6C;
  margin-right: 4px;
}
</style>
<script>
export default {
  name: 'ParameterTableEdit',
  props: {
    data: {
      type: Array,
      default: () => []
    },
    tree: {
      type: Boolean,
      default: true
    }
  },
  methods: {
    buildParamRules(row) {
      const rules = []
      if (row.required && row.type !== 'file') {
        rules.push({ required: true, message: '请填写参数值', trigger: 'blur' })
      }
      const max = parseInt(row.maxLength)
      if (max) {
        rules.push({ max: max, message: `长度不超过 ${max} 个字符`, trigger: 'blur' })
      }
      return {
        paramExample: rules
      }
    },
    onSelectFile(f, fileList, row) {
      const files = []
      fileList.forEach(file => {
        const rawFile = file.raw
        files.push(rawFile)
      })
      row.__file__ = { name: row.name, files: files }
    }
  }
}
</script>
