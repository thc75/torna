<template>
  <div style="overflow:hidden">
    <iframe
      :src="`${getServerUrl()}/system/dingtalk/qr?userId=${getUserId()}`"
      width="380"
      height="380"
      style="border: 0;"
    />
  </div>
</template>
<style>
/* STEP2：指定这个包裹容器元素的CSS样式，尤其注意宽高的设置 */
.self-defined-classname {
  width: 300px;
  height: 300px;
}
</style>
<script>
import {} from '@/utils/ddlogin'
export default {
  name: 'DingDingLogin',
  props: {
    onLoginSuccess: {
      type: Function,
      required: true
    }
  },
  mounted() {
    window.addEventListener('message', this.receiveMessage, false)
  },
  methods: {
    receiveMessage(event) {
      const data = event.data
      if (!data || data.type !== 'tornaDingdingBind') {
        return
      }
      this.onLoginSuccess(data)
    }
  }
}

</script>
