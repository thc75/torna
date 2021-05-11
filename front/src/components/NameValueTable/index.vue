<template>
  <div>
    <el-link type="primary" :underline="false" @click="onAddRow">{{ $ts('add') }}</el-link>
    <el-table
      :data="data"
      border
      class="param-table"
    >
      <el-table-column label="Name" prop="name">
        <template slot-scope="scope">
          <el-form
            :ref="`form_name_${scope.row.id}`"
            :model="scope.row"
            :rules="paramRowRule"
            size="mini"
            style="display: inline-block;"
          >
            <el-form-item
              prop="name"
              label-width="0"
            >
              <el-input v-show="nameSuggestData.length === 0" v-model="scope.row.name" :placeholder="$ts('name')" maxlength="64" show-word-limit />
              <el-autocomplete
                v-show="nameSuggestData.length > 0"
                v-model="scope.row.name"
                :fetch-suggestions="nameSearch"
                :placeholder="$ts('name')"
                style="width: 100%"
              />
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column label="Value" prop="value">
        <template slot-scope="scope">
          <el-form
            :ref="`form_value_${scope.row.id}`"
            :model="scope.row"
            :rules="paramRowRule"
            size="mini"
            style="display: inline-block;"
          >
            <el-form-item
              prop="value"
              label-width="0"
            >
              <el-input v-show="valueSuggestData.length === 0" v-model="scope.row.value" :placeholder="$ts('value')" maxlength="100" show-word-limit />
              <el-autocomplete
                v-show="valueSuggestData.length > 0"
                v-model="scope.row.value"
                :fetch-suggestions="valueSearch"
                :placeholder="$ts('value')"
                style="width: 100%"
              />
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column :label="$ts('operation')" width="80">
        <template slot-scope="scope">
          <div v-show="scope.row.isDeleted === 0">
            <el-link type="danger" icon="el-icon-delete" @click="onParamRemove(scope.row)"></el-link>
          </div>
          <div v-show="scope.row.isDeleted === 1">
            <el-tooltip :content="$ts('clickRestore')" placement="top" :open-delay="500">
              <el-link type="danger" icon="el-icon-remove" @click="scope.row.isDeleted = 0"></el-link>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
let idGen = 1

export default {
  name: 'NameValueTable',
  props: {
    data: {
      type: Array,
      default: () => []
    },
    nameSuggestData: {
      type: Array,
      default: () => []
    },
    valueSuggestData: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      paramRowRule: {
        name: [
          { required: true, message: $ts('notEmpty'), trigger: ['change'] }
        ],
        value: [
          { required: true, message: $ts('notEmpty'), trigger: ['change'] }
        ]
      }
    }
  },
  methods: {
    onAddRow() {
      const id = idGen++
      this.data.push({
        id: id,
        name: '',
        value: '',
        isDeleted: 0,
        isNew: 1
      })
    },
    onParamRemove(row) {
      if (row.isNew) {
        this.removeRow(this.data, row.id)
      } else {
        row.isDeleted = 1
      }
    },
    nameSearch(queryString, cb) {
      const nameSuggestData = this.nameSuggestData
      const results = queryString ? nameSuggestData.filter(this.createFilter(queryString)) : nameSuggestData
      // 调用 callback 返回建议列表的数据
      cb(results)
    },
    valueSearch(queryString, cb) {
      const valueSuggestData = this.valueSuggestData
      const results = queryString ? valueSuggestData.filter(this.createFilter(queryString)) : valueSuggestData
      // 调用 callback 返回建议列表的数据
      cb(results)
    },
    createFilter(queryString) {
      return (val) => {
        return (val.value.toLowerCase().indexOf(queryString.toLowerCase()) > -1)
      }
    },
    validate() {
      return this.validateTable(this.data, ['form_name_', 'form_value_'])
    }
  }
}
</script>

