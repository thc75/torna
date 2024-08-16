<template>
  <div class="app-container">
    <el-tabs active-name="metersphere">
      <el-tab-pane name="metersphere" label="MeterSphere配置">
        <el-alert type="info" title="" :closable="false">
          <p>
            配置MeterSphere参数，文档推送到Torna后会同步到MeterSphere服务器。（Local Push -> Torna -> MeterSphere）
          </p>
          <p>
            配置完毕后前往 应用配置 绑定对应模块
          </p>
        </el-alert>
        <el-form ref="msForm" :model="config" :rules="formRules" size="mini" label-width="180px" style="width: 80%">
          <el-form-item prop="version" label="版本">
            <el-radio-group v-model="config.version" size="mini">
              <el-radio-button :label="1">2.x</el-radio-button>
              <el-radio-button :label="2">3.x</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item prop="msAddress" label="服务器地址">
            <el-input v-model="config.msAddress" :placeholder="addressTip" style="width: 400px" />
          </el-form-item>
          <el-form-item prop="msAccessKey" label="AccessKey">
            <el-input v-model="config.msAccessKey" style="width: 400px" />
          </el-form-item>
          <el-form-item prop="msSecretKey" label="SecretKey">
            <el-input v-model="config.msSecretKey" style="width: 400px" />
          </el-form-item>
          <el-form-item prop="msSpaceId" label="绑定工作空间">
            <el-select v-model="config.msSpaceId" size="mini">
              <el-option v-for="space in msSpaceList" :key="space.id" :value="space.id" :label="space.name">{{ space.name }}</el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="success" @click="onMeterSphereTest">Test</el-button>
            <el-button type="primary" @click="onSaveMeterSphere">{{ $t('dlgSave') }}</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>

export default {
  data() {
    return {
      config: {
        msAddress: '',
        msAccessKey: '',
        msSecretKey: '',
        msSpaceId: '',
        msSpaceName: '',
        version: 1
      },
      formRules: {
        msAddress: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
        ],
        msAccessKey: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
        ],
        msSecretKey: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
        ],
        msSpaceId: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
        ]
      },
      msSpaceList: [],
      spaceId: ''
    }
  },
  computed: {
    addressTip() {
      switch (this.config.version) {
        case 1:
          return '2.3(含)以上版本需要加/api，如：http://ip:8081/api'
        case 2:
          return '服务器地址，到端口，如：http://ip:8081'
      }
      return ''
    }
  },
  mounted() {
    this.spaceId = this.$route.params.spaceId
    this.onLoadConfig()
  },
  methods: {
    onLoadConfig() {
      this.get('third/metersphere/space/load', { spaceId: this.spaceId }, resp => {
        const data = resp.data
        if (data.msSpaceId) {
          this.msSpaceList = [{
            id: data.msSpaceId,
            name: data.msSpaceName
          }]
          this.onTest()
          Object.assign(this.config, resp.data)
        }
      })
    },
    onMeterSphereTest() {
      this.onTest(data => {
        if (data.success) {
          this.tipSuccess('连接成功！')
          if (this.msSpaceList.length > 0 && !this.config.msSpaceId) {
            this.config.msSpaceId = this.msSpaceList[0].id
          }
        }
      })
    },
    onTest(callback) {
      this.get('third/metersphere/space/test', this.config, resp => {
        const data = resp.data
        this.msSpaceList = data.spaces
        callback && callback.call(this, data)
      })
    },
    onSaveMeterSphere() {
      this.$refs.msForm.validate(valid => {
        if (valid) {
          const data = Object.assign({}, this.config)
          for (const el of this.msSpaceList) {
            if (el.id === data.msSpaceId) {
              data.msSpaceName = el.name
              break
            }
          }
          data.spaceId = this.spaceId
          this.post('third/metersphere/space/save', data, resp => {
            this.tipSuccess($t('saveSuccess'))
          })
        }
      })
    }
  }
}
</script>
