<template>
  <div :class="classObj" class="app-wrapper">
    <div v-if="canVisit">
      <div v-if="device==='mobile'&&sidebarView.opened" class="drawer-bg" @click="handleClickOutside" />
      <sidebar id="leftPanel" class="sidebar-container-view" />
      <div id="rightPanel" class="main-container-view">
        <div id="resizeBar" class="resize-bar"></div>
        <div id="navBar" :class="{'fixed-header':fixedHeader}">
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
            :placeholder="$ts('visitPassword')"
            prefix-icon="el-icon-lock"
          >
            <el-button slot="append" class="btn-send" native-type="submit" @click="onCheckPassword">{{ $ts('btnOk') }}</el-button>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    <h3 v-if="composeProject.status === getEnums().STATUS.DISABLE">
      Document not found
    </h3>
  </div>
</template>

<script>
import { Navbar, Sidebar, ViewMain } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import md5 from 'js-md5'
import { ResizeBar } from '@/utils/resizebar'

export default {
  name: 'LayoutComposeProject',
  components: {
    Navbar,
    Sidebar,
    ViewMain
  },
  mixins: [ResizeMixin],
  data() {
    return {
      composeProject: {
        id: '',
        isEncrypt: 0
      },
      encryptFormData: {
        password: ''
      },
      encryptFormRules: {
        password: [
          { required: true, message: $ts('notEmpty'), trigger: 'blur' }
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
      const config = this.composeProject
      return (config.type === this.getEnums().SHARE_TYPE.PUBLIC || this.rightEncrypt) && config.status === this.getEnums().STATUS.ENABLE
    },
    rightEncrypt() {
      return this.getAttr(this.getStoreKey(this.composeProject)) === 'true'
    },
    showPassword() {
      const config = this.composeProject
      return (config.type === this.getEnums().SHARE_TYPE.ENCRYPT && !this.rightEncrypt) && config.status === this.getEnums().STATUS.ENABLE
    }
  },
  created() {
    this.initComposeProject()
  },
  destroyed() {
    this.ResizeBar && this.ResizeBar.destroyed()
  },
  methods: {
    initComposeProject() {
      const showId = this.$route.params.showId
      if (showId) {
        this.get('/compose/project/get', { id: showId }, resp => {
          this.composeProject = resp.data
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
    getStoreKey(composeProject) {
      return `torna.show.${composeProject.id}`
    },
    onCheckPassword() {
      this.$refs.encryptForm.validate(valid => {
        if (valid) {
          this.post('/compose/project/checkPassword', {
            id: this.composeProject.id,
            password: md5(this.encryptFormData.password)
          }, resp => {
            this.setAttr(this.getStoreKey(this.composeProject), 'true')
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
