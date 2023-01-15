<template>
  <div>
    <div style="float: right">
      <el-button type="danger" size="mini" @click="onModuleDelete">{{ $ts('deleteModule') }}</el-button>
    </div>
    <h3>
      {{ $ts('appName') }}：
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
      <el-tab-pane name="envSetting" :label="$ts('debugEnv')">
        <env-setting ref="envSetting" :project-id="projectId" />
      </el-tab-pane>
      <el-tab-pane v-if="isSwaggerApp" name="swaggerSetting" :label="$ts('swaggerSetting')">
        <swagger-setting ref="swaggerSetting" />
      </el-tab-pane>
    </el-tabs>

  </div>
</template>
<script>
import PopoverUpdate from '@/components/PopoverUpdate'
import SwaggerSetting from '@/components/ModuleSetting/SwaggerSetting'
import EnvSetting from './EnvSetting'

// 添加国际化
$addI18n({
  'addEnv': { 'zh': '添加环境', 'en': 'Add Environment' },
  'swaggerSetting': { 'zh': 'Swagger设置', 'en': 'Swagger Setting' },
  'copyEnv': { 'zh': '{0} 拷贝', 'en': '{0} Copy' },
  'plzCheckEnv': { 'zh': '请勾选环境', 'en': 'Please select environment' }
})

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
        this.tipSuccess(this.$ts('updateSuccess'))
        this.moduleVO.name = value
        done()
      })
    },
    onModuleDelete() {
      this.confirm(this.$ts('deleteModuleConfirm'), () => {
        this.post('/module/delete', { id: this.moduleId }, () => {
          alert(this.$ts('deleteSuccess'))
          location.reload()
        })
      })
    }
  }
}
</script>

