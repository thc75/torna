<template>
  <div :class="classObj" class="app-wrapper">
    <div v-if="canVisit">
      <sidebar id="leftPanel" class="sidebar-container-view" />
      <div id="rightPanel" :class="{hasDocViewTabs:docViewTabs}" class="main-container-view">
        <div id="resizeBar" class="resize-bar"></div>
        <div id="navBar" :class="{'fixed-header':fixedHeader}">
          <navbar />
          <tabs-router />
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
        <div class="share-name" >{{ shareConfig.shareName }}</div>
        <el-form-item prop="password">
          <el-input
            v-model="encryptFormData.password"
            :type="isPasswordVisible ? 'text' : 'password'"
            :placeholder="$t('visitPassword')"
            class="password-style"
          >
            <template #append>
              <el-button
                @click="togglePasswordVisibility"
                class="lock-icon-button"
              >
                <i :class="isPasswordVisible ? 'el-icon-unlock' : 'el-icon-lock'"></i>
              </el-button>
              <el-button
                class="btn-send"
                native-type="submit"
                @click="onCheckPassword"
              >
                {{ $t('btnOk') }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { Navbar, Sidebar, ViewMain } from './components'
import TabsRouter from '@/components/TabsRouter'
import ResizeMixin from './mixin/ResizeHandler'
import md5 from 'js-md5'
import { ResizeBar } from '@/utils/resizebar'

export default {
  name: 'LayoutShare',
  components: {
    Navbar,
    Sidebar,
    ViewMain,
    TabsRouter
  },
  mixins: [ResizeMixin],
  data() {
    return {
      shareConfig: {
        id: '',
        type: 0,
        shareName: ''
      },
      encryptFormData: {
        password: ''
      },
      encryptFormRules: {
        password: [
          { required: true, message: $t('passwordNotEmpty'), trigger: 'blur' }
        ]
      },
      isPasswordVisible: false
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
    },
    docViewTabShow() {
      return this.$store.state.tabsRouter.showTabsView
    },
    docViewTabSwitch() {
      return this.$store.state.settings.docViewTabSwitch
    },
    docViewTabs() {
      return this.docViewTabSwitch && this.docViewTabShow
    }
  },
  created() {
    this.initShare()
  },
  destroyed() {
    this.ResizeBar && this.ResizeBar.destroyed()
  },
  methods: {
    initShare() {
      const shareId = this.$route.params.shareId
      if (shareId) {
        this.get('/share/get', { id: shareId }, resp => {
          this.shareConfig = resp.data
          if (this.canVisit) {
            this.$nextTick(() => {
              this.initResizeBar()
            })
          }
        })
      }
    },
    initResizeBar() {
      this.ResizeBar = new ResizeBar(this, {
        leftPanel: 'leftPanel',
        rightPanel: 'rightPanel',
        resizeBar: 'resizeBar',
        navBar: 'navBar'
      })
    },
    getStoreKey(shareConfig) {
      return `torna.share.${shareConfig.id}`
    },
    onCheckPassword() {
      this.$refs.encryptForm.validate(valid => {
        if (valid) {
          this.post('/share/checkPassword', {
            id: this.shareConfig.id,
            password: md5(this.encryptFormData.password.trim())
          }, resp => {
            this.setAttr(this.getStoreKey(this.shareConfig), 'true')
            location.reload()
          })
        }
      })
    },
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBarView', { withoutAnimation: false })
    },
    togglePasswordVisibility() {
      this.isPasswordVisible = !this.isPasswordVisible;
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
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    margin-top: 0;
    width: 600px;
    padding: 40px;
    box-sizing: border-box;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  }

  .share-name {
    text-align: center;
    margin-bottom: 25px;
    font-weight: bold;
    font-size: 20px;
    min-height: 25px;
  }

  .btn-send {
    height: 40px;
    line-height: 40px;
    padding: 0 10px;
  }
</style>
