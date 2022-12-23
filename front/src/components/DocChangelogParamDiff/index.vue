<template>
  <div class="rich-editor">
    <table>
      <caption>{{ content.targetName }}</caption>
      <tr v-for="key in keys" :key="key">
        <th>{{ getI18nName(key) }}</th>
        <td>
          <span>{{ getOldValue(key) }}</span> <span v-show="showArrow(key)" class="el-icon-right"></span> <span>{{ getNewValue(key) }}</span>
        </td>
      </tr>
    </table>
  </div>
</template>
<script>
import { PositionNameMap } from '@/utils/enums'
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
    content: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      keys: [],
      value: {}
    }
  },
  mounted() {
    this.value = this.content.value
    const keysNew = Object.keys(this.value.newValue)
    this.keys = keysNew.length === 0 ? Object.keys(this.value.oldValue) : keysNew
  },
  methods: {
    getOldValue(key) {
      return this.value.oldValue[key]
    },
    getNewValue(key) {
      return this.value.newValue[key]
    },
    showArrow(key) {
      return this.getOldValue(key) !== undefined && this.getNewValue(key) !== undefined
    },
    getI18nName(key) {
      return $ts(PositionNameMap[key])
    }
  }
}
</script>
