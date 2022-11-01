<template>
  <div>
    <el-drawer
      :visible.sync="drawer"
      :direction="direction"
      size="40%"
      :with-header="false"
      :before-close="handleClose"
    >
      <el-tabs v-if="!isNoData" type="card">
        <el-tab-pane v-if="hasConstant" :label="$ts('constInfo')">
          <div style="margin: 10px;">
            <div class="rich-editor" v-html="moduleConstantInfo.constantProject.replace(/\n/g,'<br />')"></div>
            <div class="rich-editor" v-html="moduleConstantInfo.constantModule.replace(/\n/g,'<br />')"></div>
          </div>
        </el-tab-pane>
        <el-tab-pane v-if="hasDict" :label="$ts('dictInfo')">
          <div style="margin: 10px;">
            <div v-for="enumInfo in list" :key="enumInfo.id">
              <h3>{{ enumInfo.name }}</h3>
              <el-alert v-if="enumInfo.description" :closable="false" :title="enumInfo.description" style="margin-bottom: 10px;" />
              <enum-item-view :list="enumInfo.items" />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      <div v-else style="text-align: center;margin-top: 30px;">
        <el-empty :description="$ts('noData')"></el-empty>
      </div>
    </el-drawer>
  </div>
</template>
<script>
let overflowYOld = 'auto'

// 停用滚动条
function hiddenScrollbar() {
  overflowYOld = document.body.style.overflowY
  document.body.style.overflowY = 'hidden'
  // 计算滚动条宽度 (滚动条的宽度不是都为17px的，算一下当前的比较精确)
  const scrollbarWidth = getScrollbarWidth()
  document.getElementById('navBar').style.marginRight = scrollbarWidth + 'px'
}

function getScrollbarWidth() {
  const outer = document.createElement('div')
  const inner = document.createElement('div')
  outer.style.overflow = 'scroll'
  document.body.appendChild(outer)
  outer.appendChild(inner)
  const scrollbarWidth = outer.offsetWidth - inner.offsetWidth
  document.body.removeChild(outer)
  return scrollbarWidth
}

// 显示滚动条
function showScrollbar() {
  document.body.style.overflowY = overflowYOld || 'auto'
  document.getElementById('navBar').style.marginRight = '0'
}

$addI18n({
  'constInfo': { 'zh': '常量信息', 'en': 'Constant Info' }
})
import EnumItemView from '@/components/EnumItemView'
export default {
  components: { EnumItemView },
  data() {
    return {
      drawer: false,
      direction: 'rtl',
      moduleConstantInfo: {
        constantModule: '',
        constantProject: ''
      },
      list: []
    }
  },
  computed: {
    hasConstant() {
      return (this.moduleConstantInfo.constantModule && this.moduleConstantInfo.constantModule.length > 0) || (this.moduleConstantInfo.constantProject && this.moduleConstantInfo.constantProject.length > 0)
    },
    hasDict() {
      return this.list.length > 0
    },
    isNoData() {
      return !(this.hasConstant || this.hasDict)
    }
  },
  methods: {
    show(moduleId) {
      if (moduleId) {
        this.get('/doc/enum/info/all', { moduleId: moduleId }, resp => {
          this.list = resp.data
        })
        this.get('/constant/module/all', { moduleId: moduleId }, resp => {
          this.moduleConstantInfo = resp.data
          this.drawer = true
        })
        // document.body.style.overflowY = 'hidden'
        hiddenScrollbar()
      }
    },
    handleClose() {
      this.drawer = false
      // document.body.style.overflowY = 'auto'
      showScrollbar()
    },
    toggleBody(isPin) {
      if (isPin) {
        document.body.style.height = '100vh'
        document.body.style['overflow-y'] = 'hidden'
      } else {
        document.body.style.height = 'unset'
        document.body.style['overflow-y'] = 'auto'
      }
    }
  }
}
</script>
