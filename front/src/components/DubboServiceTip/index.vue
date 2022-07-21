<template>
  <el-popover
    v-model="visible"
    :placement="placement"
    trigger="hover"
    width="500"
    @show="onShow"
  >
    <div style="padding: 10px">
      <el-form size="mini" label-width="60px" class="text-form">
        <el-form-item :label="$ts('interface')">{{ propData.interfaceName }}</el-form-item>
        <el-form-item :label="$ts('version')">{{ propData.version }}</el-form-item>
        <el-form-item :label="$ts('protocol')">{{ propData.protocol }}</el-form-item>
        <el-form-item :label="$ts('author')">{{ propData.author }}</el-form-item>
      </el-form>
      <div v-show="propData.dependency">
        <h3>{{ $ts('dependency') }}</h3>
        <el-input v-model="propData.dependency" :rows="7" type="textarea" readonly />
      </div>
    </div>
    <el-tag slot="reference" class="el-tag--small" type="primary" style="font-weight: normal;" @click.stop>Dubbo</el-tag>
  </el-popover>
</template>
<script>
export default {
  props: {
    docId: {
      type: String
    },
    docIdGetter: {
      type: Function
    },
    placement: {
      type: String,
      default: 'right'
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
    defaultGetter() {
      return this.docId
    },
    onShow() {
      const docId = this.docIdGetter && this.docIdGetter() || this.docId
      this.get('/prop/getDocProps', { id: docId }, resp => {
        this.propData = resp.data
      })
    }
  }
}
</script>
