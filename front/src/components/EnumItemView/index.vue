<template>
  <el-table
    :data="data"
    border
    highlight-current-row
  >
    <el-table-column
      :label="$t('value')"
      prop="value"
      fixed
      :width="flexColumnWidth($t('value'),'value')"
    >
      <template slot-scope="scope">
        <span v-if="scope.row.name === scope.row.value">
          {{ scope.row.value }}
        </span>
        <span v-else>
          {{ `${scope.row.name}(${scope.row.value})` }}
        </span>
      </template>
    </el-table-column>
    <el-table-column
      :label="$t('type')"
      prop="type"
      fixed
      :width="flexColumnWidth($t('type'),'type')"
    />
    <el-table-column
      :label="$t('description')"
      prop="description"
      fixed
      :width="flexColumnWidth($t('description'),'description')"
    />
  </el-table>
</template>
<style scoped>
.el-table /deep/ th {
  padding: 0;
  white-space: nowrap;
  min-width: fit-content;
}

.el-table /deep/ td {
  padding: 1px;
  white-space: nowrap;
  width: fit-content;
}

/** 修改el-card默认paddingL:20px-内边距 **/
>>> .el-card__body {
  padding: 10px;
}

.el-table /deep/ .cell {
  white-space: nowrap;
  width: fit-content;
}
</style>
<script>
export default {
  name: 'EnumItemView',
  props: {
    enumId: {
      type: String,
      default: ''
    },
    mountedLoad: {
      type: Boolean,
      default: false
    },
    list: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      data: []
    }
  },
  watch: {
    enumId(enumId) {
      this.loadData(enumId)
    }
  },
  mounted() {
    if (this.mountedLoad) {
      this.loadData(this.enumId)
    }
    if (this.list.length > 0) {
      this.data = this.list
    }
  },
  methods: {
    loadData(enumId) {
      if (enumId) {
        this.loadEnumItem(enumId).then(data => {
          this.data = data
        })
      }
    },
    reload() {
      this.loadData(this.enumId)
    },
    /**
     * 遍历列的所有内容，获取最宽一列的宽度
     * @param arr
     */
    getMaxLength(arr) {
      return arr.reduce((acc, item) => {
        if (item) {
          const calcLen = this.getTextWidth(item)
          if (acc < calcLen) {
            acc = calcLen
          }
        }
        return acc
      }, 0)
    },
    /**
     * 使用span标签包裹内容，然后计算span的宽度 width： px
     * @param valArr
     */
    getTextWidth(str) {
      let width = 0
      const html = document.createElement('span')
      html.innerText = str
      html.className = 'getTextWidth'
      document.querySelector('body').appendChild(html)
      width = document.querySelector('.getTextWidth').offsetWidth
      document.querySelector('.getTextWidth').remove()
      return width
    },
    /**
     * el-table-column 自适应列宽
     * @param prop_label: 表名
     * @param table_data: 表格数据
     */
    flexColumnWidth(label, prop) {
      // console.log('label', label)
      // console.log('prop', prop)
      // 1.获取该列的所有数据
      const arr = this.data.map(row => {
        if (prop === 'value') {
          if (row.name === row.value) {
            return row.value
          } else {
            return `${row.name}(${row.value})`
          }
        } else {
          return row[prop]
        }
      })
      arr.push(label) // 把每列的表头也加进去算
      // console.log(arr)
      // 2.计算每列内容最大的宽度 + 表格的内间距（依据实际情况而定）
      return (this.getMaxLength(arr) + 25) + 'px'
    }
  }
}
</script>
