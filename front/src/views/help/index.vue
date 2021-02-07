<template>
  <div class="app-container2">
    <el-backtop />
    <el-container>
      <el-header>
        <help-menu />
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu :default-openeds="openMenu" :default-active="defaultActive" class="normal-menu" @select="onMenuClick">
            <el-submenu v-for="(item) in menus" :key="item.path" :index="item.title">
              <template slot="title">
                <i :class="item.icon"></i>
                <span slot="title">{{ item.title }}</span>
              </template>
              <el-menu-item v-for="(child) in item.children" :key="child.path" :index="child.id">{{ child.title }}</el-menu-item>
            </el-submenu>
          </el-menu>
        </el-aside>
        <el-main>
          <mavon-editor
            v-model="content"
            :boxShadow="false"
            :subfield="false"
            defaultOpen="preview"
            :editable="false"
            :toolbarsFlag="false"
          />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script>
import HelpMenu from '@/components/HelpMenu'
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
export default {
  components: { HelpMenu, mavonEditor },
  data() {
    return {
      defaultActive: '',
      docId: '',
      menus: [],
      openMenu: [],
      content: '使用手册',
      activeIndex: '1'
    }
  },
  watch: {
    $route() {
      this.loadMenu()
    }
  },
  created() {
    this.loadMenu()
  },
  methods: {
    loadMenu: function() {
      this.docId = this.$route.query.id || 'start'
      let currentDoc = null
      this.getMenu().then(menus => {
        this.menus = menus
        menus.forEach(item => {
          this.openMenu.push(item.title)
          if (this.docId) {
            const children = item.children || []
            for (let i = 0; i < children.length; i++) {
              const child = children[i]
              if (child.id && child.id === this.docId) {
                currentDoc = child
                this.defaultActive = child.id
                break
              }
            }
          }
        })
        if (currentDoc) {
          this.loadMarkdown(currentDoc.path)
        }
      })
    },
    getMenu() {
      return new Promise(resolve => {
        if (this.menus.length > 0) {
          resolve(this.menus)
        } else {
          this.getFile('static/help/menu.json', (menus) => {
            resolve(menus)
          })
        }
      })
    },
    onHeaderMenuSelect: function(index) {

    },
    onMenuClick: function(index, path) {
      this.goRoute(`/help?id=${index}`)
      // this.loadMarkdown(index)
    },
    loadMarkdown: function(path) {
      // this.goRoute(`/help?id=${id}`)
      this.getFile(`${path}?q=${new Date().getTime()}`, (content) => {
        this.content = content
      })
    }
  }
}
</script>
<style scoped>
.app-container2 {
  position: relative;
  max-width: 100%;
}
.el-header {
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}
</style>

