<template>
  <div :class="classObj" class="app-wrapper">
    <div v-if="canVisit">
      <div v-if="device==='mobile'&&sidebarView.opened" class="drawer-bg" @click="handleClickOutside" />
      <sidebar class="sidebar-container-view" />
      <div class="main-container-view">
        <div :class="{'fixed-header':fixedHeader}">
          <navbar />
        </div>
        <view-main />
      </div>
    </div>
    <div v-if="showPassword">
      <el-form
        ref="encryptForm"
        :model="encryptFormData"
        :rules="encryptFormRules"
        class="center-form encrypt-form"
        auto-complete="on"
        @submit.native.prevent
      >
        <el-form-item prop="password">
          <el-input
            v-model="encryptFormData.password"
            type="password"
            placeholder="访问密码"
            prefix-icon="el-icon-lock"
          >
            <el-button slot="append" class="btn-send" native-type="submit" @click="onCheckPassword">确定</el-button>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { Navbar, Sidebar, ViewMain } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import md5 from 'js-md5'

export default {
  name: 'LayoutShare',
  components: {
    Navbar,
    Sidebar,
    ViewMain
  },
  mixins: [ResizeMixin],
  data() {
    return {
      shareConfig: {
        id: '',
        type: 0
      },
      encryptFormData: {
        password: ''
      },
      encryptFormRules: {
        password: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    sidebarView() {
      return this.$store.state.app.sidebarView
    },
    device() {
      return this.$store.state.app.device
    },
    fixedHeader() {
      return this.$store.state.settings.fixedHeader
    },
    classObj() {
      return {
        hideSidebarView: !this.sidebarView.opened,
        openSidebarView: this.sidebarView.opened,
        withoutAnimation: this.sidebarView.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    },
    canVisit() {
      const config = this.shareConfig
      return config.type === this.getEnums().SHARE_TYPE.PUBLIC || this.rightEncrypt
    },
    rightEncrypt() {
      return this.getAttr(this.getStoreKey(this.shareConfig)) === 'true'
    },
    showPassword() {
      return this.shareConfig.type === this.getEnums().SHARE_TYPE.ENCRYPT && !this.rightEncrypt
    }
  },
  created() {
    this.initShare()
  },
  methods: {
    initShare() {
      const shareId = this.$route.params.shareId
      if (shareId) {
        this.get('/share/get', { id: shareId }, resp => {
          this.shareConfig = resp.data
        })
      }
    },
    getStoreKey(shareConfig) {
      return `torna.share.${shareConfig.id}`
    },
    onCheckPassword() {
      this.$refs.encryptForm.validate(valid => {
        if (valid) {
          this.post('/share/checkPassword', {
            id: this.shareConfig.id,
            password: md5(this.encryptFormData.password)
          }, resp => {
            this.setAttr(this.getStoreKey(this.shareConfig), 'true')
            location.reload()
          })
        }
      })
    },
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBarView', { withoutAnimation: false })
    }
  }
}
</script>

<style lang="scss" scoped>
  @import "~@/styles/mixin.scss";
  @import "~@/styles/variables.scss";

  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;
    &.mobile.openSidebar{
      position: fixed;
      top: 0;
    }
  }
  .drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }

  .fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$sideBarViewWidth});
    transition: width 0.28s;
  }

  .hideSidebar .fixed-header {
    width: 0
  }

  .hideSidebarView .fixed-header {
    width: 100%;
  }

  .mobile .fixed-header {
    width: 100%;
  }
  .encrypt-form {
    margin-top: 200px;
  }
</style>