<template>
  <div>
    <el-link type="primary" :underline="false" @click="onAddRow">添加</el-link>
    <el-table
      :data="data"
      border
      :cell-style="cellStyleSmall()"
      :header-cell-style="headCellStyleSmall()"
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
              <el-input v-model="scope.row.name" placeholder="名称" maxlength="64" show-word-limit />
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
              <el-input v-model="scope.row.value" placeholder="值" maxlength="100" show-word-limit />
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80">
        <template slot-scope="scope">
          <div v-show="scope.row.isDeleted === 0">
            <el-link type="danger" icon="el-icon-delete" @click="onParamRemove(scope.row)"></el-link>
          </div>
          <div v-show="scope.row.isDeleted === 1">
            <el-tooltip content="点击恢复" placement="top">
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
    }
  },
  data() {
    return {
      paramRowRule: {
        name: [
          { required: true, message: '请填写名称', trigger: ['blur', 'change'] }
        ],
        value: [
          { required: true, message: '请填写值', trigger: ['blur', 'change'] }
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
    }
  }
}
</script>

