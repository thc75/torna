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
        { label: '简体中文', value: 'zh-CN' },
        { label: 'English', value: 'en' }
      ]
    }
  },
  mounted() {
    this.get('system/i18n/lang/list', {}, resp => {
      const items = resp.data
      for (const item of items) {
        this.languageOptions.push(
          { label: item.description, value: item.lang }
        )
      }
      this.formData.language = get_lang()
    })
  },
  methods: {
    onLanguageChange(language) {
      set_lang(language)
      location.reload()
    }
  }
}
</script>
