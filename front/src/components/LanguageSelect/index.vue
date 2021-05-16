<template>
  <el-select v-model="formData.language" size="mini" style="width: 100px" @change="onLanguageChange">
    <el-option
      v-for="item in languageOptions"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </el-select>
</template>
<script>
import { get_lang, set_lang } from '@/utils/i18n/common'

export default {
  data() {
    return {
      formData: {
        language: ''
      },
      languageOptions: [
        { label: '简体中文', value: 'zh' },
        { label: 'English', value: 'en' }
      ]
    }
  },
  mounted() {
    this.formData.language = get_lang()
  },
  methods: {
    onLanguageChange(language) {
      set_lang(language)
      location.reload()
    }
  }
}
</script>
