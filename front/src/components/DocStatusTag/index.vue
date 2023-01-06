<template>
  <el-tag :type="getDocStatusType(status)">{{ getDocStatusName(status) }}</el-tag>
</template>
<script>
export default {
  name: 'DocStatusTag',
  props: {
    status: {
      type: Number,
      required: true
    }
  }
}
</script>
