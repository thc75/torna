<template>
  <el-table
    :data="getter(rows)"
    row-key="id"
    border
    default-expand-all
    highlight-current-row
    :empty-text="emptyText"
    class="param-table"
  >
    <el-table-column
      v-if="isColumnShow('name')"
      prop="name"
      :label="nameLabel"
      :width="nameWidth"
    >
      <template slot-scope="scope">
        <span v-if="isTextColumn('name')">
          {{ scope.row.name }}
        </span>
        <el-form
          v-else
          :ref="`form_name_${scope.row.id}`"
          :model="scope.row"
          :rules="paramRowRule"
          size="mini"
          style="display: inline-block;width: 220px;"
          :class="hasNoParentAndChildren(scope.row) ? 'el-table--row-no-parent-children' : ''"
        >
          <el-form-item
            prop="name"
            label-width="0"
          >
            <el-input v-model="scope.row.name" :placeholder="$ts('paramName')" maxlength="64" show-word-limit />
          </el-form-item>
        </el-form>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('type')"
      prop="type"
      :label="$ts('type')"
      width="130"
    >
      <template slot-scope="scope">
        <el-select v-model="scope.row.type" size="mini">
          <el-option v-for="type in getTypeConfig()" :key="type" :label="type" :value="type"></el-option>
        </el-select>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('enum')"
      prop="enum"
      :label="$ts('linkDict')"
      width="120"
    >
      <template slot-scope="scope">
        <el-select v-model="scope.row.enumId" :clearable="true" size="mini">
          <el-option v-for="enumInfo in enumData" :key="enumInfo.id" :label="enumInfo.name" :value="enumInfo.id">
            {{ enumInfo.name }}
          </el-option>
        </el-select>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('required')"
      prop="required"
      :label="$ts('require')"
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
      :label="$ts('maxLength')"
      width="130"
    >
      <template slot-scope="scope">
        <el-input v-model="scope.row.maxLength" :placeholder="$ts('maxLength')" size="mini" maxlength="10" show-word-limit />
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('description')"
      prop="description"
      :label="descriptionLabel"
    >
      <template slot-scope="scope">
        <el-form :ref="`form_description_${scope.row.id}`" :model="scope.row" :rules="paramRowRule" size="mini">
          <el-form-item
            prop="description"
            label-width="0"
          >
            <el-input v-model="scope.row.description" :placeholder="descriptionLabel" maxlength="512" show-word-limit />
          </el-form-item>
        </el-form>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('example')"
      prop="example"
      :label="exampleLabel"
    >
      <template slot-scope="scope">
        <el-input
          v-model="scope.row.example"
          :placeholder="exampleLabel"
          maxlength="128"
          size="mini"
          show-word-limit
        />
      </template>
    </el-table-column>
    <el-table-column
      prop="orderIndex"
      :label="$ts('orderIndex')"
      width="125"
    >
      <template slot-scope="scope">
        <el-input-number
          v-model="scope.row.orderIndex"
          controls-position="right"
          size="mini"
          style="width: 100px"
        />
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('opt')"
      :label="$ts('operation')"
      width="90"
    >
      <template slot-scope="scope">
        <div>
          <div v-show="scope.row.isDeleted === 0">
            <el-tooltip :content="$ts('addChildNode')" placement="top" :open-delay="500">
              <el-link v-if="canAddNode" type="primary" icon="el-icon-circle-plus-outline" @click="onParamNodeAdd(scope.row)"></el-link>
            </el-tooltip>
            <el-link type="danger" icon="el-icon-delete" @click="onParamRemove(scope.row)"></el-link>
          </div>
          <div v-show="scope.row.isDeleted === 1">
            <el-tooltip :content="$ts('clickRestore')" placement="top">
              <el-link type="danger" icon="el-icon-remove" @click="scope.row.isDeleted = 0"></el-link>
            </el-tooltip>
          </div>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>
<script>
export default {
  name: 'EditTable',
  props: {
    data: {
      type: Array,
      default: () => []
    },
    moduleId: {
      type: String,
      default: ''
    },
    emptyText: {
      type: String,
      default: $ts('noData')
    },
    canAddNode: {
      type: Boolean,
      default: true
    },
    nameLabel: {
      type: String,
      default: $ts('name')
    },
    nameWidth: {
      type: Number,
      default: 300
    },
    textColumns: {
      type: Array,
      default: () => []
    },
    descriptionLabel: {
      type: String,
      default: $ts('description')
    },
    exampleLabel: {
      type: String,
      default: $ts('example')
    },
    hiddenColumns: {
      type: Array,
      default: () => []
    },
    getter: {
      type: Function,
      default: (rows) => { return rows.filter(row => !row.hidden) }
    }
  },
  data() {
    return {
      rows: [],
      enumData: [],
      paramRowRule: {
        name: [
          { required: true, message: $ts('notEmpty'), trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  watch: {
    data(val) {
      this.rows = val
    }
  },
  mounted() {
    if (this.moduleId) {
      this.loadEnumData(this.moduleId, data => {
        this.enumData = data
      })
    }
  },
  methods: {
    isColumnShow(label) {
      return this.hiddenColumns.filter(lb => lb === label).length === 0
    },
    isTextColumn(name) {
      return this.textColumns.filter(val => val === name).length > 0
    },
    onParamNodeAdd(row) {
      const children = row.children || []
      const child = this.getParamNewRow()
      child.parentId = row.id
      this.pmsNextOrderIndex(children).then(order => {
        child.orderIndex = order
      })
      children.push(child)
      row.children = children
    },
    onParamRemove(row) {
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
<style scoped>
.param-table .el-table--row-no-parent-children {
  padding-left: 23px !important;
}
</style>
