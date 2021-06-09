import variables from '@/styles/variables.scss'

const LEFT_WIDTH_KEY = 'torna-menuwidth'
// 分隔条位置
const RESIZE_BAR_MARGIN_LEFT = `${-2}px`
const OPACITY_1 = '1'
const OPACITY_0 = '0'
const MIN_WIDTH = 50
const MAX_WIDTH = 800
const CLIENT_WIDTH = document.body.clientWidth - 14

export function ResizeBar(opts) {
  document.body.style.overflowY = 'scroll'
  this.leftPanel = document.getElementById(opts['leftPanel'])
  this.rightPanel = document.getElementById(opts['rightPanel'])
  this.resizeBar = document.getElementById(opts['resizeBar'])
  this.navBar = document.getElementById(opts['navBar'])
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
    const width = this.getAttr(LEFT_WIDTH_KEY)
    this.setLeftWidth(width)
  },
  setLeftWidth(width) {
    width = width || variables.sideBarViewWidth
    this.leftPanel.style.width = width
    this.navBar.style.width = `${CLIENT_WIDTH - parseInt(width)}px`
    this.rightPanel.style.marginLeft = width
    this.resizeBar.style.opacity = OPACITY_0
    this.resizeBar.style.marginLeft = RESIZE_BAR_MARGIN_LEFT
    this.setAttr(LEFT_WIDTH_KEY, width)
  },
  setAttr: function(key, val) {
    if (val === undefined) {
      val = ''
    }
    localStorage.setItem(key, val + '')
  },
  getAttr: function(key) {
    return localStorage.getItem(key)
  },
  destroyed() {
    document.body.style.overflowY = 'auto'
  }
}
