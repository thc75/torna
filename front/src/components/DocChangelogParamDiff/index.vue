<template>
  <div class="rich-editor">
    <table class="changelog-table">
      <tr>
        <th colspan="2">
          <el-tag size="mini" :type="getTagType(detail.modifyType)" :closabl="false">
            {{ getParamModifyTypeName(detail.modifyType) }}
          </el-tag>
          {{ content.targetName }}
        </th>
      </tr>
      <tr v-for="key in keys" :key="key">
        <th>{{ getI18nName(key) }}</th>
        <td>
          <span>{{ getOldValue(key) }}</span> <span v-show="showArrow(key)" class="el-icon-right"></span> <span>{{ getNewValue(key) }}</span>
        </td>
      </tr>
    </table>
  </div>
</template>
<style>
.changelog-table {
  color: #606266;
  font-size: 13px;
}
.changelog-table td {
  word-wrap:break-word;
  word-break:break-all;
}
</style>
<script>
import { Enums } from '@/utils/enums'
const POSITION_TYPE = Enums.POSITION_TYPE
export default {
  name: 'DocChangelogParamDiff',
  props: {
    /*
    "value":{
        "newValue":{
          "PARAM_EXAMPLE":"xxxxxx",
          "PARAM_TYPE":"string",
          "PARAM_REQUIRED":1
        },
        "oldValue":{
          "PARAM_EXAMPLE":"aaaakkk",
          "PARAM_TYPE":"integer",
          "PARAM_REQUIRED":0
        }
      }
     */
    detail: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      keys: [],
      value: {},
      content: {}
    }
  },
  mounted() {
    this.content = this.detail.content
    this.value = this.content.value
    const keysNew = Object.keys(this.value.newValue)
    this.keys = keysNew.length === 0 ? Object.keys(this.value.oldValue) : keysNew
  },
  methods: {
    getOldValue(key) {
      const val = this.value.oldValue[key]
      if (key === 'PARAM_REQUIRED' && val !== undefined) {
        return val === 0 ? $ts('no') : $ts('yes')
      }
      return val
    },
    getNewValue(key) {
      const val = this.value.newValue[key]
      if (key === 'PARAM_REQUIRED' && val !== undefined) {
        return val === 0 ? $ts('no') : $ts('yes')
      }
      return val
    },
    showArrow(key) {
      return this.getOldValue(key) !== undefined && this.getNewValue(key) !== undefined
    },
    getI18nName(key) {
      return $ts(POSITION_TYPE[key].label)
    },
    getTagType(type) {
      // 变更类型，0：修改，1：新增，2：删除
      switch (type) {
        case 0:
          return 'warning'
        case 1:
          return 'success'
        case 2:
          return 'danger'
      }
      return ''
    },
    getParamModifyTypeName(type) {
      // 变更类型，0：修改，1：新增，2：删除
      switch (type) {
        case 0:
          return $ts('update')
        case 1:
          return $ts('newAdd')
        case 2:
          return $ts('delete')
      }
      return ''
    }
  }
}
</script>
