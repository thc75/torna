<template>
  <div class="app-container2">
    <el-backtop />
    <el-container>
      <el-header>
        <el-menu
          :default-active="activeIndex"
          mode="horizontal"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
        >
          <el-menu-item index="1">帮助中心</el-menu-item>
        </el-menu>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu :default-openeds="openMenu" :default-active="defaultActive" @select="onMenuClick">
            <el-submenu v-for="(item) in menus" :key="item.path" :index="item.title">
              <template slot="title">
                <i :class="item.icon"></i>
                <span slot="title">{{ item.title }}</span>
              </template>
              <el-menu-item v-for="(child) in item.children" :key="child.path" :index="child.path">{{ child.title }}</el-menu-item>
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
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
export default {
  components: { mavonEditor },
  data() {
    return {
      defaultActive: '',
      docId: '',
      menus: [],
      openMenu: [],
      content: '帮助中心',
      activeIndex: '1'
    }
  },
  created() {
    this.docId = this.$route.query.id
    this.loadMenu()
  },
  methods: {
    loadMenu: function() {
      let currentDoc = null
      this.getFile('static/help/menu.json', (menus) => {
        this.menus = menus
        menus.forEach(item => {
          this.openMenu.push(item.title)
          if (this.docId) {
            const children = item.children || []
            for (let i = 0; i < children.length; i++) {
              const child = children[i]
              if (child.path && child.path.endsWith(`${this.docId}.md`)) {
                currentDoc = child
                this.defaultActive = child.path
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
    onHeaderMenuSelect: function(index) {

    },
    onMenuClick: function(index, path) {
      this.loadMarkdown(index)
    },
    loadMarkdown: function(path) {
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
  width: 80%;
  max-width: 100%;
  margin: 0 auto;
}
.el-header {
  background-color: #545c64;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}
</style>

