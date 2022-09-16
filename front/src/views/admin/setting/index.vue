<template>
  <div class="app-container">
    <h4>基础配置</h4>
    <el-form :model="config" size="mini" label-width="200px" style="width: 80%">
      <el-form-item label="允许注册">
        <el-switch
          v-model="config.regEnable.value"
          active-text="允许"
          active-value="true"
          inactive-text=""
          inactive-value="false"
          @change="onConfigChange(config.regEnable)"
        />
      </el-form-item>
      <el-form-item label="文档排序规则">
        <el-radio-group v-model="config.docSortType.value" @change="onDocSortTypeChange">
          <el-radio-button label="by_order">根据排序字段排序</el-radio-button>
          <el-radio-button label="by_name">根据文档名称排序</el-radio-button>
          <el-radio-button label="by_url">根据URL排序</el-radio-button>
        </el-radio-group>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

export default {
  data() {
    return {
      config: {
        regEnable: { key: 'torna.register.enable', value: 'false' },
        docSortType: { key: 'torna.doc-sort-type', value: 'by_order', remark: '文档排序规则' }
      },
      docSortTypeMap: {
        'by_order': '根据排序字段排序',
        'by_name': '根据文档名称排序',
        'by_url': '根据URL排序'
      }
    }
  },
  created() {
    this.loadConfig()
  },
  methods: {
    loadConfig() {
      const keys = []
      for (const configKey in this.config) {
        keys.push(this.config[configKey].key)
      }
      this.get('/system/config/adminsetting', { keys: keys.join(',') }, resp => {
        const configs = resp.data.configs
        for (const config of configs) {
          for (const configKey in this.config) {
            if (this.config[configKey].key === config.key) {
              this.config[configKey].value = config.value
              break
            }
          }
        }
      })
    },
    onConfigChange(config) {
      this.post('/system/config/update', config, () => {
        this.tipSuccess($ts('updateSuccess'))
      })
    },
    onDocSortTypeChange(val) {
      this.config.docSortType.remark = '文档排序规则，' + this.docSortTypeMap[val]
      this.onConfigChange(this.config.docSortType)
    }
  }
}
</script>
