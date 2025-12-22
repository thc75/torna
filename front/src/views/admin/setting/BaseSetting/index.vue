<template>
  <div class="app-container">
    <el-form :model="config" size="mini" label-width="200px" style="width: 80%">
      <el-form-item :label="$t('BasicConfig.tornaFrontUrl')">
        <el-input
          v-model="config.tornaFrontUrl.value"
          @change="onConfigChange(config.tornaFrontUrl)"
        />
      </el-form-item>
      <el-form-item :label="$t('BasicConfig.regEnable')">
        <el-switch
          v-model="config.regEnable.value"
          :active-text="$t('BasicConfig.allow')"
          active-value="true"
          inactive-text=""
          inactive-value="false"
          @change="onConfigChange(config.regEnable)"
        />
      </el-form-item>
      <el-form-item :label="$t('BasicConfig.MFA')">
        <el-switch
          v-model="config.MFA.value"
          :active-text="$t('enable')"
          active-value="true"
          inactive-text=""
          inactive-value="false"
          @change="onConfigChange(config.MFA)"
        />
      </el-form-item>
      <el-form-item :label="$t('BasicConfig.docSortRule')">
        <el-radio-group v-model="config.docSortType.value" @change="onDocSortTypeChange">
          <el-radio-button label="by_order">{{ $t('BasicConfig.sortByOrderIndex') }}</el-radio-button>
          <el-radio-button label="by_name">{{ $t('BasicConfig.sortByName') }}</el-radio-button>
          <el-radio-button label="by_url">{{ $t('BasicConfig.sortByUrl') }}</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <span slot="label">
          {{ $t('BasicConfig.imageUploadSetting') }}
          <el-link
            type="primary"
            :underline="false"
            class="el-icon-question"
            @click="$refs.help.open('static/help/upload.md')"
          />
        </span>
        <div>{{ $t('BasicConfig.uploadDirLabel') }}</div>
        <el-input
          v-model="config.uploadDir.value"
          :placeholder="$t('BasicConfig.uploadDirPlaceholder')"
          @change="onConfigChange(config.uploadDir)"
        />

        <div>{{ $t('BasicConfig.customDomainLabel') }}</div>
        <el-input
          v-model="config.uploadDomain.value"
          :placeholder="$t('BasicConfig.customDomainPlaceholder')"
          @change="onConfigChange(config.uploadDomain)"
        />
      </el-form-item>

      <!-- 使用MeterSphere -->
      <el-form-item :label="$t('BasicConfig.useMeterSphere')">
        <el-switch
          v-model="config.msEnable.value"
          active-value="true"
          inactive-text=""
          inactive-value="false"
          @change="onConfigChange(config.msEnable)"
        />
      </el-form-item>

      <!-- 隐藏旧版mock -->
      <el-form-item :label="$t('BasicConfig.hideOldMock')">
        <el-switch
          v-model="config.hideOldMock.value"
          active-value="true"
          inactive-text=""
          inactive-value="false"
          @change="onConfigChange(config.hideOldMock)"
        />
      </el-form-item>
    </el-form>
    <help ref="help" />
  </div>
</template>

<script>
import Help from '@/components/Help'
import { saveAdminConfig, loadAdminConfig } from '../setting'

export default {
  name: 'BaseSetting',
  components: { Help },
  data() {
    return {
      config: {
        regEnable: { key: 'torna.register.enable', value: 'false' },
        docSortType: { key: 'torna.doc-sort-type', value: 'by_order', remark: this.$t('BasicConfig.docSortRuleRemark') },
        uploadDir: { key: 'torna.upload.dir', value: '', remark: this.$t('BasicConfig.uploadDirRemark') },
        uploadDomain: { key: 'torna.upload.domain', value: '', remark: this.$t('BasicConfig.uploadDomainRemark') },
        tornaFrontUrl: { key: 'torna.front-url', value: '', remark: this.$t('BasicConfig.tornaFrontUrlRemark') },
        msEnable: { key: 'metershpere.enable', value: 'false', remark: this.$t('BasicConfig.msEnableRemark') },
        hideOldMock: { key: 'torna.hideOldMock', value: 'false', remark: this.$t('BasicConfig.hideOldMock') },
        MFA: { key: 'torna.mfa.enable', value: 'false', remark: this.$t('BasicConfig.MFARemark') }
      },
      docSortTypeMap: {
        'by_order': this.$t('BasicConfig.sortByOrderIndex'),
        'by_name': this.$t('BasicConfig.sortByName'),
        'by_url': this.$t('BasicConfig.sortByUrl')
      }
    }
  },
  created() {
    this.reload()
  },
  methods: {
    reload() {
      loadAdminConfig(this.config, () => {
        const baseUrlConfig = this.config.tornaFrontUrl
        if (!baseUrlConfig.value) {
          baseUrlConfig.value = this.getBaseUrl()
          saveAdminConfig(baseUrlConfig, true)
        }
      })
    },
    onConfigChange(config) {
      saveAdminConfig(config)
    },
    onDocSortTypeChange(val) {
      this.config.docSortType.remark = this.$t('BasicConfig.docSortRuleRemark') + '：' + this.docSortTypeMap[val]
      this.onConfigChange(this.config.docSortType)
    }
  }
}
</script>
