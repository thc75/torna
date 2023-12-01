<template>
  <div style="display: inline-block">
    <span>{{ valueOld }}</span> <span v-show="showArrow" class="el-icon-right"></span> <span>{{ valueNew }}</span>
  </div>
</template>
<script>
import { Enums } from '@/utils/enums'
const DOC_STATUS = Enums.DOC_STATUS
const docStatusMap = {}
for (const obj of DOC_STATUS) {
  docStatusMap['' + obj.value] = obj
}
function formatVal(targetName, val) {
  if (val === undefined) {
    return val
  }
  const obj = docStatusMap['' + val]
  switch (targetName) {
    case 'status':
      return $t(obj.label)
    // 是/否
    case 'isShow':
    case 'isUseGlobalHeaders':
    case 'isUseGlobalParams':
    case 'isUseGlobalReturns':
    case 'isRequestArray':
    case 'isResponseArray':
      return val === 1 ? $t('yes') : $t('no')
  }
  return val
}
export default {
  name: 'DocChangelogSimpleDiff',
  props: {
    detail: {
      type: Object,
      required: true
    }
  },
  computed: {
    showArrow() {
      return this.valueOld && this.valueNew
    },
    valueOld() {
      const targetName = this.detail.targetName
      return formatVal(targetName, this.content.value.oldValue)
    },
    valueNew() {
      const targetName = this.detail.targetName
      return formatVal(targetName, this.content.value.newValue)
    },
    // {"value":{"newValue":"jim","oldValue":"超级管理员"},"targetName":"author"}
    content() {
      return this.detail.content
    }
  }
}
</script>
