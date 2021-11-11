<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse, 'sidebar-logo-container-style': !noStyle}">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" to="/" class="sidebar-logo-link">
        <img src="@/assets/images/logo.png" class="sidebar-logo" :title="version">
      </router-link>
      <router-link v-else key="expand" to="/" class="sidebar-logo-link">
        <img src="@/assets/images/logo.png" class="sidebar-logo">
        <h1 class="sidebar-title">{{ title }} <span class="version">{{ version }}</span> </h1>
      </router-link>
    </transition>
  </div>
</template>

<script>
// eslint-disable-next-line
const VER="1.11.0"
export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      default: true
    },
    noStyle: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      version: VER,
      title: 'Torna'
    }
  },
  created() {
  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  & .sidebar-logo-link {
    height: 100%;
    width: 100%;

    & .sidebar-logo {
      width: 32px;
      height: 32px;
      vertical-align: middle;
      margin-right: 5px;
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0;
      font-weight: 600;
      line-height: 50px;
      font-size: 16px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
    }
    & .version {
      font-weight: normal;
      font-size: 12px;
      color: #909399;
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
  }
}
.sidebar-logo-container-style {
  box-shadow: 0 1px 4px rgba(0,21,41,0.08);
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  padding-left: 16px;
  overflow: hidden;
}
</style>
