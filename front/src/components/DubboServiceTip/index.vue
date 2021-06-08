<template>
  <el-popover
    v-model="visible"
    placement="right"
    trigger="hover"
    width="500"
    @show="onShow"
  >
    <div style="padding: 10px">
      <el-form size="mini" label-width="60px" class="text-form">
        <el-form-item label="接口名">{{ propData.interfaceName }}</el-form-item>
        <el-form-item label="版本号">{{ propData.version }}</el-form-item>
        <el-form-item label="协议">{{ propData.protocol }}</el-form-item>
        <el-form-item label="作者">{{ propData.author }}</el-form-item>
      </el-form>
      <div v-show="propData.dependency">
        <h3>依赖</h3>
        <el-input v-model="propData.dependency" :rows="5" type="textarea" readonly />
      </div>
    </div>
    <el-tag slot="reference" class="el-tag--small" type="primary" @click.stop>Dubbo</el-tag>
  </el-popover>
</template>
<script>
export default {
  props: {
    docId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      visible: false,
      propData: {
        dependency: '',
        interfaceName: '',
        version: '',
        protocol: '',
        author: ''
      }
    }
  },
  methods: {
    onShow() {
      this.get('/prop/getDocProps', { id: this.docId }, resp => {
        this.propData = resp.data
      })
    }
  }
}
</script>
