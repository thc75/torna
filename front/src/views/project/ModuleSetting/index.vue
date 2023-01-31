<template>
  <div>
    <div style="float: right">
      <el-button type="danger" size="mini" @click="onModuleDelete">{{ $t('deleteModule') }}</el-button>
    </div>
    <h3>
      {{ $t('appName') }}：
      <span style="font-weight: normal">
        {{ moduleVO.name }}
        <popover-update
          :value="moduleVO.name"
          :on-show="() => {return moduleVO.name}"
          :on-save="onSaveName"
        />
      </span>
    </h3>

    <el-tabs active-name="envSetting" tab-position="left">
      <el-tab-pane name="envSetting" :label="$t('debugEnv')">
        <env-setting ref="envSetting" :project-id="projectId" />
      </el-tab-pane>
      <el-tab-pane v-if="isSwaggerApp" name="swaggerSetting" :label="$t('swaggerSetting')">
        <swagger-setting ref="swaggerSetting" />
      </el-tab-pane>
    </el-tabs>

  </div>
</template>
<script>
import PopoverUpdate from '@/components/PopoverUpdate'
import SwaggerSetting from '@/components/ModuleSetting/SwaggerSetting'
import EnvSetting from './EnvSetting'

export default {
  name: 'ModuleSetting',
  components: { PopoverUpdate, SwaggerSetting, EnvSetting },
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      moduleVO: {
        id: '',
        name: '',
        type: 0
      }
    }
  },
  computed: {
    isSwaggerApp() {
      return this.moduleVO.type === 1
    }
  },
  methods: {
    reload(moduleId) {
      this.$refs.envSetting.reload(moduleId)
      this.loadModuleInfo(moduleId)
    },
    loadModuleInfo(moduleId) {
      this.get('/module/info', { moduleId: moduleId }, resp => {
        Object.assign(this.moduleVO, resp.data)
        if (this.isSwaggerApp) {
          this.$nextTick(() => {
            this.loadSwaggerConfig(moduleId)
          })
        }
      })
    },
    loadSwaggerConfig(moduleId) {
      const $ref = this.$refs.swaggerSetting
      $ref && $ref.reload(moduleId)
    },
    onSaveName(value, done) {
      const param = {
        id: this.moduleVO.id,
        name: value
      }
      this.post('/module/name/update', param, () => {
        this.tipSuccess(this.$t('updateSuccess'))
        this.moduleVO.name = value
        done()
      })
    },
    onModuleDelete() {
      this.confirm(this.$t('deleteModuleConfirm'), () => {
        this.post('/module/delete', { id: this.moduleId }, () => {
          alert(this.$t('deleteSuccess'))
          location.reload()
        })
      })
    }
  }
}
</script>

