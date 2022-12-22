<template>
  <div class="app-container">
    <el-radio-group v-if="$route.path.indexOf('new') > -1" v-model="type" size="small">
      <el-radio-button :label="0">{{ $ts('standardDocument') }}</el-radio-button>
      <el-radio-button :label="3">{{ $ts('customDocument') }}</el-radio-button>
    </el-radio-group>
    <edit-doc-standard v-show="type === 0" />
    <edit-doc-custom v-show="type === 3" />
  </div>
</template>
<script>
import EditDocStandard from '../EditDocStandard'
import EditDocCustom from '../EditDocCustom'

export default {
  components: { EditDocStandard, EditDocCustom },
  data() {
    return {
      type: 0
    }
  }
}
</script>
