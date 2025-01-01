<template>
  <div>
    <div class="info-tip">{{ $t('rootArrayTip') }}：[{...}, {...}]、[1, 2, 3]、['hello', 'world']</div>
    <div class="table-opt-btn">
      <span class="normal-text">{{ $t('elementType') }}</span>：
      <el-select v-model="elementType" size="mini" @change="onTypeChange">
        <el-option v-for="type in elTypes" :key="type" :value="type" :label="type">{{ type }}</el-option>
      </el-select>
    </div>
    <div v-if="isTypeObject">
      <el-button type="text" icon="el-icon-plus" @click="onParamAdd">{{ $t('newParam') }}</el-button>
      <el-button type="text" icon="el-icon-bottom-right" @click="onImportRequestParamAdd">{{ $t('importParam') }}</el-button>
    </div>
    <u-table
      :data="getter(rows)"
      row-id="id"
      row-key="id"
      use-virtual
      :treeConfig="{
        children: 'children',
        iconClose: 'el-icon-arrow-right',
        iconOpen: 'el-icon-arrow-down',
        expandAll: true
      }"
      :height="500"
      :row-height="20"
      border
      class="param-table1"
      ref="virtualTable"
    >
      <u-table-column
        :tree-node="true"
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
              <WeInput :value="scope.row.name" :size="'mini'" @change="(val) =>onWeChange(val,scope.row,'name')" :placeholder="$t('paramName')" :maxlength="64" :showWordLimit="true"/>
            </el-form-item>
          </el-form>
        </template>
      </u-table-column>
      <u-table-column
        v-if="isTypeObject && isColumnShow('type')"
        prop="type"
        :label="$t('type')"
        width="130"
      >
        <template slot-scope="scope">
          <WeSelect :type="'arr'" :value="scope.row.type" @change="(val) =>onWeChange(val,scope.row,'enumId')" :filterable="false" :allowCreate="false" :list="getTypeConfig()" :clearable="false" :size="'mini'"/>
        </template>
      </u-table-column>
      <u-table-column
        v-if="isTypeObject && isColumnShow('enum')"
        prop="enum"
        :label="$t('linkDict')"
        width="120"
      >
        <template slot-scope="scope">
          <WeSelect :type="'obj'" :value="scope.row.enumId" @change="(val) =>onWeChange(val,scope.row,'enumId')" :filterable="false" :allowCreate="false" :list="enumData" :clearable="true" :size="'mini'"/>
        </template>
      </u-table-column>
      <u-table-column
        v-if="isTypeObject && isColumnShow('required')"
        prop="required"
        :label="$t('require')"
        width="80"
      >
        <template slot-scope="scope">
          <WeSwitch :value="scope.row.required" @change="(val) =>onWeChange(val,scope.row,'required')" :activeColor="'#13ce66'" :activeValue="1" :inactivealVue="0"/>
        </template>
      </u-table-column>
      <u-table-column
        v-if="isTypeObject && isColumnShow('maxLength')"
        prop="maxLength"
        :label="$t('maxLength')"
        width="130"
      >
        <template slot-scope="scope">
          <WeInput :value="scope.row.maxLength" :size="'mini'" @change="(val) =>onWeChange(val,scope.row,'example')" :placeholder="$t('maxLength')" :maxlength="10" :showWordLimit="true"/>
        </template>
      </u-table-column>
      <u-table-column
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
              <WeInput :value="scope.row.description" @change="(val) =>onWeChange(val,scope.row,'description')" :placeholder="descriptionLabel" :maxlength="512" :showWordLimit="true"/>
            </el-form-item>
          </el-form>
        </template>
      </u-table-column>
      <u-table-column
        v-if="isColumnShow('example')"
        prop="example"
        :label="exampleLabel"
      >
        <template slot-scope="scope">
          <WeInput :value="scope.row.example" :size="'mini'" @change="(val) =>onWeChange(val,scope.row,'example')" :placeholder="exampleLabel" :maxlength="128" :showWordLimit="true"/>
        </template>
      </u-table-column>
      <u-table-column
        v-if="isColumnShow('opt')"
        :label="$t('operation')"
        width="90"
      >
        <template slot-scope="scope">
          <div>
            <div v-show="scope.row.isDeleted === 0">
              <el-tooltip :content="$t('addChildNode')" placement="top" :open-delay="500">
                <el-link v-if="isTypeObject && canAddNode" type="primary" icon="el-icon-circle-plus-outline" @click="onParamNodeAdd(scope.row)"></el-link>
              </el-tooltip>
              <el-link v-if="isTypeObject" type="danger" icon="el-icon-delete" @click="onParamRemove(scope.row)"></el-link>
            </div>
            <div v-show="scope.row.isDeleted === 1">
              <el-tooltip :content="$t('clickRestore')" placement="top">
                <el-link type="danger" icon="el-icon-remove" @click="scope.row.isDeleted = 0"></el-link>
              </el-tooltip>
            </div>
          </div>
        </template>
      </u-table-column>
    </u-table>
    <el-dialog
      :title="importParamTemplateTitle"
      :visible.sync="importParamTemplateDlgShow"
    >
      <el-input v-model="importParamTemplateValue" type="textarea" :rows="4" placeholder="[{...}, {...}]" />
      <div slot="footer" class="dialog-footer">
        <el-button @click="importParamTemplateDlgShow = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onImportParamSave">{{ $t('dlgSave') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { is_array_string } from '@/utils/common'
import WeInput from "../EditTable/input.vue"
import WeSwitch from "../EditTable/switch.vue"
import WeSelect from "../EditTable/select.vue"
export default {
  components:{WeInput,WeSwitch,WeSelect},
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
      default: $t('noData')
    },
    canAddNode: {
      type: Boolean,
      default: true
    },
    nameLabel: {
      type: String,
      default: $t('name')
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
      default: $t('description')
    },
    exampleLabel: {
      type: String,
      default: $t('example')
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
  mounted() {
    if (this.moduleId) {
      this.loadEnumData(this.moduleId, data => {
        this.enumData = data
      })
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
      child.maxLength = String(child.maxLength)//转为字符类型，解决警告
      children.push(child)
      row.children = children
      this.rows.forEach(e => {
        let flag = false
        if(e.id === row.id){
          e.children = row.children
          flag = true
        }
        if(!flag && e.children.length > 0){
          this.getaddChildren(e.children,row.id,row.children)
        }
      })
      const tableBodyWrapperTop = this.$refs.virtualTable.$el.querySelector('.el-table__body-wrapper').scrollTop
      this.$forceUpdate(); // 强制刷新组件
      
      this.$nextTick(() => {
        setTimeout(() => {
          const tableBodyWrapper = this.$refs.virtualTable.$el.querySelector('.el-table__body-wrapper');
          if(tableBodyWrapper){
            tableBodyWrapper.scrollTop = Number(tableBodyWrapperTop)
          }
        },100)
      });
    },
    getaddChildren(row,id,children){
      let flag = false
      row.forEach(e => {
        if(e.id === id){
          e.children = children
          flag = true
        }
        if(!flag && e.children.length > 0){
          this.getaddChildren(e.children,id,children)
        }
      })
    },
    onParamRemove(row) {
      // if (row.isNew) {
      //   this.removeRow(this.data, row.id)
      // } else {
        row.isDeleted = 1
        this.data.forEach(e => {
          if(e.id === row.id){
            e.isDeleted = 1
          }
          if(e.children.length > 0){
            this.getChildren(e.children,row.id)
          }
        })
        // 重新设置scrollTop
        const tableBodyWrapperTop = this.$refs.virtualTable.$el.querySelector('.el-table__body-wrapper').scrollTop
        this.$forceUpdate(); // 强制刷新组件
        this.$nextTick(() => {
          setTimeout(() => {
            const tableBodyWrapper = this.$refs.virtualTable.$el.querySelector('.el-table__body-wrapper');
            if(tableBodyWrapper){
              tableBodyWrapper.scrollTop = Number(tableBodyWrapperTop)
            }
          },10)
        });
      // }
    },
    getChildren(row,id){
      row.forEach(e => {
        if(e.id === id){
            e.isDeleted = 1
          }
        if(e.children.length > 0){
          this.getChildren(e.children,id)
        }
      })
    },
    onImportParamSave() {
      const val = this.importParamTemplateValue
      if (!is_array_string(val)) {
        this.tipError($t('mustArray'))
        return
      }
      this.parseJSON(val, arr => {
        if (arr.length === 0) {
          this.tipError($t('arrayMustHasElement'))
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
    onWeChange(val,row,code){
      this.data.forEach(e => {
        if(e.id === row.id){
          e[code] = val
        }
        if(e.children.length > 0){
          this.setChildren(val,row,code,e.children)
        }
      })
    },
    setChildren(val,row,code,rows){
      rows.forEach(e => {
        if(e.id === row.id){
          e[code] = val
        }
        if(e.children.length > 0){
          this.setChildren(val,row,code,e.children)
        }
      })
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
        return fn(this.data)
      } else {
        const row = this.data[0]
        if (!row.description) {
          return false
        }
      }
      return true
    }
  }
}
</script>
