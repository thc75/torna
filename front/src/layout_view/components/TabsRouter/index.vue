<template>
  <div
    id="tabs-router"
    ref="tabs_router"
    @mousewheel.prevent="mouseWheelScroll"
  >
    <div
      v-for="(tab,index) in tabsList"
      :key="tab.path"
      :class="{'active':isActive(tab)}"
      class="tab-item"
      draggable="true"
      @dragstart.stop="dragStartIndex = index"
      @dragenter.stop="dragEnterIndex = index"
      @dragend="dragend($event)"
      @mouseenter="isTabMayClick = true"
      @mouseleave="isTabMayClick = false"
      @contextmenu.prevent="openContextMenu(tab,$event)"
    >
      <router-link
        tag="div"
        :to="{path: tab.path, query: tab.query, fullPath: tab.fullPath}"
        class="tab-title"
        :title="tab.title"
      >{{ tab.title }}
      </router-link>
      <div class="el-icon-close" @click.prevent.stop="closeTab(tab)" />
    </div>
    <ul v-show="showContextMenu" class="contextMenu" :style="contextMenuStyle">
      <li v-show="tabsList.length > 1" @click="closeOthersTabs()"><i class="el-icon-circle-close" /> {{ $ts('closeOthers') }}</li>
      <li v-show="!isFirstTab()" @click="closeLeftTabs"><i class="el-icon-back" /> {{ $ts('closeLeft') }}</li>
      <li v-show="!isLastTab()" @click="closeRightTabs"><i class="el-icon-right" /> {{ $ts('closeRight') }}</li>
      <li @click="closeAllTabs()"><i class="el-icon-circle-close" /> {{ $ts('closeAll') }}</li>
    </ul>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dragStartIndex: '',
      dragEnterIndex: '',
      isTabMayClick: false,
      showContextMenu: false,
      contextMenuStyle: {
        top: 0,
        left: 0
      },
      selectedTab: {}
    }
  },
  computed: {
    tabsList() {
      return this.$store.state.tabsRouter.visitedTabs
    }
  },
  watch: {
    $route() {
      this.addTabs()
      this.closeContextMenu()
    },
    showContextMenu(value) {
      if (value) {
        document.body.addEventListener('click', this.closeContextMenu)
        document.addEventListener('scroll', this.closeContextMenu)
      } else {
        document.body.removeEventListener('click', this.closeContextMenu)
        document.removeEventListener('scroll', this.closeContextMenu)
      }
    }
  },
  mounted() {
    this.addTabs()
  },
  methods: {
    // 是否选中
    isActive(route) {
      return route.path === this.$route.path
    },
    isFirstTab() {
      if (this.selectedTab && this.tabsList && this.tabsList.length > 0) {
        return this.selectedTab.path === this.tabsList[0].path
      }
      return false
    },
    isLastTab() {
      if (this.selectedTab && this.tabsList && this.tabsList.length > 0) {
        return this.selectedTab.path === this.tabsList[this.tabsList.length - 1].path
      }
      return false
    },
    addTabs() {
      const { name, path } = this.$route
      if (name && path.indexOf('/view/') === -1) {
        return
      }
      this.$store.dispatch('tabsRouter/addVisitedTabs', this.$route)
        .then(({ position, length }) => {
          if (this.isTabMayClick) {
            return
          }
          const tabsRouter = this.$refs.tabs_router
          if (!this.canHorizontalScroll(tabsRouter)) {
            return
          }
          const element = tabsRouter.querySelectorAll('.tab-item')
          const tabClientWidth = element[position] && element[position].clientWidth
          // Math.ceil(Math.floor(滚动容器宽度/标签元素宽度)/2)
          const number = Math.ceil(Math.floor(tabsRouter.clientWidth / tabClientWidth) / 2)
          // 靠前
          if (position <= number) {
            tabsRouter.scrollLeft = 0
          } else if (position >= length - number) { // 靠后
            tabsRouter.scrollLeft = tabsRouter.scrollWidth
          } else {
            tabsRouter.scrollLeft = (position - number) * tabClientWidth
          }
        })
    },
    // 关闭点击选中标签
    closeTab(tab) {
      this.$store.dispatch('tabsRouter/deleteVisitedTabs', tab).then(({ visitedTabs, index }) => {
        if (this.isActive(tab)) {
          this.toLastView(visitedTabs, index)
        }
      })
    },
    // 跳转到最近的标签
    toLastView(visitedTabs, index) {
      const latestView = visitedTabs.slice(index - 1)[0]
      if (latestView) {
        this.$router.push(latestView.fullPath)
      } else {
        this.$router.replace({ path: '/view' })
      }
    },
    dragend(e) {
      if (this.dragEnterIndex < this.dragStartIndex) { // 拖动到前面
        this.tabsList.splice(this.dragEnterIndex, 0, this.tabsList[this.dragStartIndex])
        this.tabsList.splice(Number(this.dragStartIndex) + 1, 1)
      } else { // 拖动到后面
        this.tabsList.splice(Number(this.dragEnterIndex) + 1, 0, this.tabsList[this.dragStartIndex])
        this.tabsList.splice(Number(this.dragStartIndex), 1)
      }
      this.dragStartIndex = ''
      e.preventDefault()
    },
    // 是否有横向滚动条
    canHorizontalScroll(element) {
      if (element.scrollLeft > 0) {
        return true
      } else {
        element.scrollLeft++
        const left = element.scrollLeft
        left && (element.scrollLeft = 0)
        return left > 0
      }
    },
    mouseWheelScroll(event) {
      this.$refs.tabs_router.scrollLeft += event.deltaY ? event.deltaY
        : event.detail && event.detail !== 0 ? event.detail : -event.wheelDelta
      this.closeContextMenu()
    },
    // 右键菜单
    openContextMenu(tab, event) {
      const menuMinWidth = 105
      const offsetLeft = this.$el.getBoundingClientRect().left
      const offsetWidth = this.$el.offsetWidth
      const maxLeft = offsetWidth - menuMinWidth
      const left = event.clientX - offsetLeft + 15

      if (left > maxLeft) {
        this.left = maxLeft
      } else {
        this.left = left
      }
      this.contextMenuStyle = {
        top: `${event.clientY}px`,
        left: `${this.left}px`
      }
      this.selectedTab = tab
      this.showContextMenu = true
    },
    // 关闭其他
    closeOthersTabs() {
      this.$router.push(this.selectedTab)
      this.$store.dispatch('tabsRouter/deleteOthersVisitedTabs', this.selectedTab)
    },
    // 关闭左侧
    closeLeftTabs() {
      this.$store.dispatch('tabsRouter/deleteLeftTabs', this.selectedTab)
        .then(visitedTabs => {
          if (!visitedTabs.find(i => i.path === this.$route.path)) {
            this.toLastView(visitedTabs, visitedTabs.length)
          }
        })
    },
    // 关闭右侧
    closeRightTabs() {
      this.$store.dispatch('tabsRouter/deleteRightTabs', this.selectedTab)
        .then(visitedTabs => {
          if (!visitedTabs.find(i => i.path === this.$route.path)) {
            this.toLastView(visitedTabs, visitedTabs.length)
          }
        })
    },
    // 关闭全部
    closeAllTabs() {
      this.$store.dispatch('tabsRouter/deleteAllVisitedTabs').then(() => {
        this.$router.replace({ path: '/view' })
      })
    },
    // 关闭菜单
    closeContextMenu() {
      this.showContextMenu = false
      this.selectedTab = {}
    }
  }
}
</script>

<style scoped lang="scss">
#tabs-router {
  width: 100%;
  background: #FDFCFC;
  padding: 5px 0;
  box-shadow: 0 1px 3px 0 RGBA(0, 0, 0, .12), 0 0 3px 0 RGBA(0, 0, 0, .04);
  display: flex;
  align-items: center;
  overflow-x: auto;

  .tab-item {
    border: 1px solid #D8DCE5;
    color: #495060;
    font-size: 12px;
    border-radius: 4px;
    height: 26px;
    width: 110px;
    min-width: 110px;
    background: #FFF;
    padding: 0 8px;
    margin-left: 5px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: space-between;

    &:first-of-type {
      margin-left: 15px;
    }

    &:last-of-type {
      margin-right: 15px;
    }

    &.active {
      color: #FFF;
      background-color: #409EFF;
      border-color: #409EFF;
    }

    &:not(.active):hover {
      background: #F8F8F8;
    }

    .tab-title {
      margin-top: 4px;
      max-width: 80px;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }

    .el-icon-close {
      margin-top: 2px;
      padding: 2px;
      border-radius: 50%;
      transition: all .3s cubic-bezier(.645, .045, .355, 1);
      transform-origin: 100% 50%;

      &:hover {
        background-color: #B4BCCC;
        color: #FFF;
      }
    }
  }

  &:hover {
  }

  &::-webkit-scrollbar {
    height: 5px;
  }

  &::-webkit-scrollbar-thumb {
    border-radius: 6px;
    background: RGBA(0, 0, 0, 0.1);
  }

  .contextMenu {
    position: absolute;
    background-color: #FFF;
    box-shadow: 2px 2px 3px 0 RGBA(0, 0, 0, .3);
    padding: 5px 0;
    list-style-type: none;
    border-radius: 4px;
    font-size: 13px;
    margin: 0;
    color: #333;

    li {
      padding: 7px 16px;
      cursor: pointer;

      &:hover {
        background: #EEE;
      }
    }
  }
}
</style>
