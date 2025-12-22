<template>
  <el-table
    :data="data"
    row-key="id"
    :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    :row-class-name="tableRowClassName"
    :indent="20"
    :empty-text="emptyText"
    border
    highlight-current-row
    default-expand-all
    class="el-table-tree"
    @cell-mouse-enter="onCellMouseEnter"
    @cell-mouse-leave="onCellMouseLeave"
  >
    <el-table-column
      v-if="isColumnShow('name')"
      prop="name"
      :label="nameLabel"
      width="300"
    >
      <template slot-scope="scope">
        <span :class="hasNoParentAndChildren(scope.row) ? 'el-table--row-no-parent-children' : ''" style="white-space: nowrap">
          <copy-text :copy-content="scope.row.name" :show-content="scope.row.name" placement="top" :show-tip="false" />
        </span>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('type')"
      prop="type"
      :label="$t('type')"
      width="100"
    >
      <template slot-scope="scope">
        <span>{{ scope.row.type }}</span>
        <span v-show="scope.row.type === 'array' && scope.row.elementType">
          <el-tooltip effect="dark" :content="$t('elType', scope.row.elementType)" placement="top" :show-tip="false">
            <i class="el-icon-info"></i>
          </el-tooltip>
        </span>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('required')"
      prop="required"
      :label="$t('require')"
      :width="$width(60, { 'en': 75 })"
    >
      <template slot-scope="scope">
        <span :class="scope.row.required ? 'danger' : ''">{{ scope.row.required ? $t('yes') : $t('no') }}</span>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('maxLength')"
      prop="maxLength"
      :label="$t('maxLength')"
      :width="$width(80, { 'en': 100 })"
    />
    <el-table-column
      v-if="isColumnShow('description')"
      :key="Math.random()"
      prop="description"
      :label="descriptionLabel"
    >
      <template slot-scope="scope">
        <div v-if="scope.row.enumId" style="display: inline-block;">
          <copy-text :copy-content="scope.row.description" :show-content-html="scope.row.description" placement="top" :show-tip="false" />
          <el-tag
            :ref="`enumTagRef_${scope.row.id}`"
            effect="plain"
            class="enumShow"
            style="cursor: pointer"
            @click.stop="handleEnumShow(scope.row.id)"
          >
            {{ $t('expand') }}
          </el-tag>
          <enum-item-view
            :ref="`enumRef_${scope.row.id}`"
            :enum-id="scope.row.enumId"
            style="display: none"
            mounted-load
          />
        </div>
        <div v-else style="display: inline-block;">
          <div v-if="scope.row.description.length < 100">
            <copy-text :copy-content="scope.row.description" :show-content-html="scope.row.description" placement="top" :show-tip="false" />
          </div>
          <div v-else>
            <div style="height: 100px;overflow-y: auto">
              <copy-text :copy-content="scope.row.description" :show-content-html="scope.row.description" placement="top" :show-tip="false" />
            </div>
          </div>
        </div>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('example')"
      prop="example"
      :label="exampleLabel"
    >
      <template slot-scope="scope">
        {{ scope.row.example }}
      </template>
    </el-table-column>
  </el-table>
</template>
<style>
.el-table .warning-row {
  background: oldlace;
}
.el-table .copyBtn {
  margin-left: 10px;
  cursor: pointer;
  position: absolute;
  right: 5px;
}
.el-table .enumShow{
  position: relative;
}
</style>
<script>
import EnumItemView from '../EnumItemView'
import CopyText from '@/components/CopyText'

export default {
  name: 'ParameterTable',
  components: { CopyText, EnumItemView },
  props: {
    data: {
      type: Array,
      default: () => []
    },
    emptyText: {
      type: String,
      default: $t('emptyParam')
    },
    nameLabel: {
      type: String,
      default: $t('name')
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
      enumId: '',
      rowId: '',
      enumShow: {}
    }
  },
  watch: {
    data: function(oldValue) {
      for (const item of oldValue) {
        if (item.enumId) {
          this.enumShow[item.id] = false
        }
      }
    }
  },
  methods: {
    tableRowClassName({ row, index }) {
      if (row.isDeleted) {
        row.hidden = true
        return 'hidden-row'
      }
      if (row._changed) {
        return 'warning-row'
      }
      return ''
    },
    onEnumPopoverShow(ref) {
      this.$refs[ref].reload()
    },
    isColumnShow(label) {
      return this.hiddenColumns.filter(lb => lb === label).length === 0
    },
    onCellMouseEnter(row, column, cell, event) {
      this.rowId = row.id
    },
    onCellMouseLeave(row, column, cell, event) {
      this.rowId = ''
    },
    copy(text) {
      this.copyText(text)
    },
    handleEnumShow(key) {
      this.enumShow[key] = !this.enumShow[key]
      if (this.$refs[`enumRef_${key}`]) {
        this.$refs[`enumRef_${key}`].$el.style.display = this.enumShow[key] ? 'block' : 'none'
        this.$refs[`enumTagRef_${key}`].$el.innerHTML = this.enumShow[key] ? $t('collapse') : $t('expand')
      }
    },
    showEnum(id) {
      return this.enumShow[id]
    }
  }
}
</script>
