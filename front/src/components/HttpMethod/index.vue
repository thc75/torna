<template>
  <el-tag
    class="el-tag-method"
    :type="type"
    disable-transitions
  >
    {{ method.toUpperCase() }}
  </el-tag>
</template>
<style>
.el-tag-method {
  height: auto !important;
  padding: 0 4px !important;
  line-height: 20px !important;
}
</style>
<script>
const tagMap = {
  'GET': 'info',
  'POST': '',
  'PUT': 'warning',
  'DELETE': 'danger',
  'HEAD': 'success'
}

export default {
  name: 'HttpMethod',
  props: {
    method: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      type: ''
    }
  },
  watch: {
    method(val) {
      this.initType(val)
    }
  },
  mounted() {
    this.initType(this.method)
  },
  methods: {
    initType(method) {
      this.type = tagMap[method.toUpperCase()] || ''
    }
  }
}
</script>
