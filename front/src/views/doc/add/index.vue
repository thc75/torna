<template>
  <div class="app-container">
    <el-radio-group v-if="$route.path.indexOf('new') > -1" v-model="type" size="small">
      <el-radio-button :label="getEnums().DOC_TYPE.HTTP">{{ $t('standardDocument') }}</el-radio-button>
      <el-radio-button :label="getEnums().DOC_TYPE.CUSTOM">{{ $t('customDocument') + `(${$t('richTextEditor')})` }}</el-radio-button>
      <el-radio-button :label="getEnums().DOC_TYPE.MARKDOWN">{{ $t('customDocument') + `(${$t('markdown')})` }}</el-radio-button>
    </el-radio-group>
    <edit-doc-standard v-show="type === getEnums().DOC_TYPE.HTTP" />
    <edit-doc-custom v-show="type === getEnums().DOC_TYPE.CUSTOM || type === getEnums().DOC_TYPE.MARKDOWN" :doc-type="type" />
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
