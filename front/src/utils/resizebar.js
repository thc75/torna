import variables from '@/styles/variables.scss'

const LEFT_WIDTH_KEY = 'torna-menuwidth'
// 分隔条位置
const RESIZE_BAR_MARGIN_LEFT = `${-4}px`
const OPACITY_1 = '1'
const OPACITY_0 = '0'
const MIN_WIDTH = 50
const MAX_WIDTH = 800

export function ResizeBar(vueScope, opts) {
  document.body.style.overflowY = 'scroll'
  this.vueScope = vueScope
  this.leftPanel = document.getElementById(opts['leftPanel'])
  this.rightPanel = document.getElementById(opts['rightPanel'])
  this.resizeBar = document.getElementById(opts['resizeBar'])
  this.navBar = document.getElementById(opts['navBar'])
  const that = this
  this.navBar.onclick = function() {
    that.resizeNavBarWidth()
  }
  this.initDragAside()
  this.initLeftWidth()
}

ResizeBar.prototype = {
  initDragAside() {
    const resizeBar = this.resizeBar
    const that = this
    let start, end, move, width
    resizeBar.onmousedown = function(e) {
      start = e.clientX
      document.onmousemove = function(e) {
        resizeBar.style.opacity = OPACITY_1
        end = e.clientX
        move = end - start
        if (end > MIN_WIDTH && end < MAX_WIDTH) {
          width = end
          resizeBar.style.marginLeft = `${move}px`
        }
      }
      document.onmouseup = function() {
        that.setLeftWidth(`${width}px`)
        document.onmousemove = null
        document.onmouseup = null
        resizeBar.releaseCapture && resizeBar.releaseCapture()
      }
      resizeBar.setCapture && resizeBar.setCapture()
      return false
    }
  },
  initLeftWidth() {
    const width = this.getLeftWidth()
    this.setLeftWidth(width)
  },
  getLeftWidth() {
    return this.getAttr(LEFT_WIDTH_KEY) || variables.sideBarViewWidth
  },
  setLeftWidth(width) {
    this.leftPanel.style.width = width
    this.rightPanel.style.marginLeft = width
    this.resizeBar.style.opacity = OPACITY_0
    this.resizeBar.style.marginLeft = RESIZE_BAR_MARGIN_LEFT
    this.resizeNavBarWidth()
    this.setAttr(LEFT_WIDTH_KEY, width)
  },
  resizeNavBarWidth() {
    // 如果是打开状态
    if (this.vueScope.sidebarView.opened) {
      const width = this.leftPanel.style.width
      this.navBar.style.width = `${document.body.clientWidth - parseInt(width)}px`
    } else {
      this.navBar.style.width = ''
    }
  },
  setAttr: function(key, val) {
    this.vueScope.setAttr(key, val)
  },
  getAttr: function(key) {
    return this.vueScope.getAttr(key)
  },
  destroyed() {
    document.body.style.overflowY = 'auto'
  }
}
