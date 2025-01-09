<template>
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
      :height="600"
      :row-height="20"
      border
      class="param-table1"
      ref="virtualTable"
    >
    <u-table-column
      :tree-node="true"
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
          @submit.native.prevent
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
      v-if="isColumnShow('type')"
      prop="type"
      :label="$t('type')"
      width="130"
    >
      <template slot-scope="scope">
        <WeSelect :type="'arr'" :value="scope.row.type" @change="(val) =>onWeChange(val,scope.row,'type')" :filterable="true" :allowCreate="true" :list="configTypes" :clearable="false" :size="'mini'"/>
      </template>
    </u-table-column>
    <u-table-column
      v-if="isColumnShow('enum')"
      prop="enum"
      :label="$t('linkDict')"
      width="120"
    >
      <template slot-scope="scope">
        <WeSelect :type="'obj'" :value="scope.row.enumId" @change="(val) =>onWeChange(val,scope.row,'enumId')" :filterable="true" :allowCreate="false" :list="enumData" :clearable="true" :size="'mini'"/>
      </template>
    </u-table-column>
    <u-table-column
      v-if="isColumnShow('required')"
      prop="required"
      :label="$t('require')"
      width="80"
    >
      <template slot-scope="scope">
        <WeSwitch :value="scope.row.required" @change="(val) =>onWeChange(val,scope.row,'required')" :activeColor="'#13ce66'" :activeValue="1" :inactivealVue="0"/>
      </template>
    </u-table-column>
    <u-table-column
      v-if="isColumnShow('maxLength')"
      prop="maxLength"
      :label="$t('maxLength')"
      width="130"
    >
      <template slot-scope="scope">
        <WeInput :value="scope.row.maxLength" @change="(val) =>onWeChange(val,scope.row,'maxLength')" :placeholder="$t('maxLength')" :size="'mini'" :maxlength="10" :showWordLimit="true"/>
      </template>
    </u-table-column>
    <u-table-column
      v-if="isColumnShow('description')"
      prop="description"
      :label="descriptionLabel"
    >
      <template slot-scope="scope">
        <el-form :ref="`form_description_${scope.row.id}`" :model="scope.row" :rules="paramRowRule" size="mini" @submit.native.prevent>
          <el-form-item
            prop="description"
            label-width="0"
          >
          <WeInput :value="scope.row.description" :size="'mini'" @change="(val) =>onWeChange(val,scope.row,'description')" :placeholder="descriptionLabel" :showWordLimit="false"/>
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
        <WeInput :value="scope.row.example" :size="'mini'" @change="(val) =>onWeChange(val,scope.row,'example')" :placeholder="exampleLabel" :showWordLimit="false"/>
      </template>
    </u-table-column>
    <u-table-column
      prop="orderIndex"
      :label="$t('orderIndex')"
      width="125"
    >
      <template slot-scope="scope">
        <el-input-number
          v-model.lazy="scope.row.orderIndex"
          controls-position="right"
          size="mini"
          style="width: 100px"
          @change="(val) =>onWeChange(val,scope.row,'orderIndex')"
        />
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
              <el-link v-if="canAddNode" type="primary" icon="el-icon-circle-plus-outline" @click="onParamNodeAdd(scope.row)"></el-link>
            </el-tooltip>
            <el-link type="danger" icon="el-icon-delete" @click="onParamRemove(scope.row)"></el-link>
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
</template>
<script>
import WeInput from "./input.vue"
import WeSwitch from "./switch.vue"
import WeSelect from "./select.vue"
export default {
  name: 'EditTable',
  components:{WeInput,WeSwitch,WeSelect},
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
      configTypes: [],
      paramRowRule: {
        name: [
          { required: true, message: $t('notEmpty'), trigger: ['blur', 'change'] }
        ]
      },
      newData:[],
      scrollTOP:0,
      scrollTOP1:0,
      pagingScrollTopLeft:{
        height:500,top:500,left:100,width:100
      }
    }
  },
  watch: {
    data(val) {
      this.rows = val
      this.newData = JSON.parse(JSON.stringify(this.rows))
    },
  },
  mounted() {
    this.rows = this.data
    this.newData = JSON.parse(JSON.stringify(this.rows))
    if (this.moduleId) {
      this.loadEnumData(this.moduleId, data => {
        this.enumData = data
      })
      this.pmsFrontConfig('front.param.type-array').then(value => {
        this.configTypes = value ? JSON.parse(value) : this.getTypeConfig()
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
      child.maxLength = String(child.maxLength)//转为字符类型，解决警告
      this.pmsNextOrderIndex(children).then(order => {
        child.orderIndex = order
      })
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
      //   this.removeRow(this.rows, row.id)
      // } else {
        row.isDeleted = 1
        this.rows.forEach(e => {
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
    getData() {
      return this.rows
      return this.newData
    },
    onWeChange(val,row,code){
      this.rows.forEach(e => {
        if(e.id === row.id){
          e[code] = val
        }
        if(e.children.length > 0){
          this.setChildren(val,row,code,e.children)
        }
      })
      this.$forceUpdate();
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
