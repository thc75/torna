<template>
  <div>
    <div class="info-tip">{{ $ts('rootArrayTip') }}：[{...}, {...}]、[1, 2, 3]、['hello', 'world']</div>
    <div class="table-opt-btn">
      <span class="normal-text">{{ $ts('elementType') }}</span>：
      <el-select v-model="elementType" size="mini" @change="onTypeChange">
        <el-option v-for="type in elTypes" :key="type" :value="type" :label="type">{{ type }}</el-option>
      </el-select>
    </div>
    <div v-if="isTypeObject">
      <el-button type="text" icon="el-icon-plus" @click="onParamAdd">{{ $ts('newParam') }}</el-button>
      <el-button type="text" icon="el-icon-bottom-right" @click="onImportRequestParamAdd">{{ $ts('importParam') }}</el-button>
    </div>
    <el-table
      :data="tableData"
      row-key="id"
      border
      default-expand-all
      highlight-current-row
      :empty-text="emptyText"
      class="param-table"
    >
      <el-table-column
        v-if="isTypeObject && isColumnShow('name')"
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
        v-if="isTypeObject && isColumnShow('type')"
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
        v-if="isTypeObject && isColumnShow('enum')"
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
        v-if="isTypeObject && isColumnShow('required')"
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
        v-if="isTypeObject && isColumnShow('maxLength')"
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
        v-if="isColumnShow('opt')"
        :label="$ts('operation')"
        width="90"
      >
        <template slot-scope="scope">
          <div>
            <div v-show="scope.row.isDeleted === 0">
              <el-tooltip :content="$ts('addChildNode')" placement="top" :open-delay="500">
                <el-link v-if="isTypeObject && canAddNode" type="primary" icon="el-icon-circle-plus-outline" @click="onParamNodeAdd(scope.row)"></el-link>
              </el-tooltip>
              <el-link v-if="isTypeObject" type="danger" icon="el-icon-delete" @click="onParamRemove(scope.row)"></el-link>
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
    <el-dialog
      :title="importParamTemplateTitle"
      :visible.sync="importParamTemplateDlgShow"
    >
      <el-input v-model="importParamTemplateValue" type="textarea" :rows="4" placeholder="[{...}, {...}]" />
      <div slot="footer" class="dialog-footer">
        <el-button @click="importParamTemplateDlgShow = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onImportParamSave">{{ $ts('dlgSave') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { is_array_string } from '@/utils/common'
export default {
  props: {
    data: {
      type: Array,
      default: () => []
    },
    elType: {
      type: String,
      default: ''
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
    }
  },
  data() {
    return {
      elTypes: [
        'object', 'number', 'string', 'boolean'
      ],
      elementType: 'object',
      enumData: [],
      paramRowRule: {
      },
      importParamTemplateTitle: '导入数组参数',
      importParamTemplateDlgShow: false,
      importParamTemplateValue: ''
    }
  },
  computed: {
    isTypeObject() {
      return this.elementType === 'object'
    },
    tableData() {
      if (this.data.length > 0) {
        return this.data.filter(row => !row.hidden)
      } else {
        return []
      }
    }
  },
  watch: {
    elType(elType) {
      this.elementType = elType || 'object'
    }
  },
  methods: {
    onParamAdd: function() {
      this.data.push(this.getParamNewRow())
    },
    onImportRequestParamAdd: function() {
      this.importParamTemplateValue = ''
      this.importParamTemplateDlgShow = true
    },
    isColumnShow(label) {
      return this.hiddenColumns.filter(lb => lb === label).length === 0
    },
    isTextColumn(name) {
      return this.textColumns.filter(val => val === name).length > 0
    },
    onTypeChange(val) {
      this.deleteAndHideData()
      const row = this.getParamNewRow()
      if (val !== 'object') {
        row.type = val
      }
      this.data.push(row)
    },
    onParamNodeAdd(row) {
      const children = row.children || []
      const child = this.getParamNewRow()
      child.parentId = row.id
      children.push(child)
      row.children = children
    },
    onParamRemove(row) {
      if (row.isNew) {
        this.removeRow(this.data, row.id)
      } else {
        row.isDeleted = 1
      }
    },
    onImportParamSave() {
      const val = this.importParamTemplateValue
      if (!is_array_string(val)) {
        this.tipError($ts('mustArray'))
        return
      }
      this.parseJSON(val, arr => {
        if (arr.length === 0) {
          this.tipError($ts('arrayMustHasElement'))
        } else {
          const json = arr[0]
          this.deleteAndHideData()
          this.doImportParam(this.data, json)
          this.importParamTemplateDlgShow = false
        }
      }, () => this.tipError('JSON格式错误'))
    },
    deleteAndHideData() {
      this.data.forEach(row => {
        row.isDeleted = 1
        row.hidden = true
      })
    },
    getData() {
      return {
        type: this.elementType,
        data: this.data
      }
    },
    validate() {
      const fn = rows => {
        let valid = true
        for (const row of rows) {
          if (!row.name) {
            return false
          }
          if (row.children && row.children.length > 0) {
            valid = fn(row.children)
          }
        }
        return valid
      }
      if (this.isTypeObject) {
        return fn(this.tableData)
      } else {
        const row = this.tableData[0]
        if (!row.description) {
          return false
        }
      }
      return true
    }
  }
}
</script>
