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
  </div>
</template>

<script>
export default {
  data() {
    return {
      dragStartIndex: '',
      dragEnterIndex: '',
      isTabMayClick: false
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
}
</style>
