<template>
  <el-container>
    <el-header>
      <el-menu
        :default-active="activeIndex"
        mode="horizontal"
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="0">
          <router-link to="/">Torna</router-link>
        </el-menu-item>
        <el-menu-item index="1">OpenAPI文档</el-menu-item>
      </el-menu>
    </el-header>
    <el-container>
      <el-backtop />
      <el-aside width="300px">
        <el-menu :default-openeds="openMenu" :default-active="defaultActive" @select="onMenuClick">
          <el-menu-item index="overview.md">
            <i class="el-icon-house"></i>
            使用前阅读
          </el-menu-item>
          <el-menu-item index="code.md">
            <i class="el-icon-tickets"></i>
            错误码
          </el-menu-item>
          <el-submenu v-for="(item) in menus" :key="item.path" :index="item.title">
            <template slot="title">
              <i :class="item.icon" />
              <span slot="title">{{ item.title }}</span>
            </template>
            <el-menu-item v-for="(child) in item.children" :key="child.path" :index="child.path">{{ child.title }}</el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>
      <el-main>
        <mavon-editor
          v-model="content"
          :box-shadow="false"
          :subfield="false"
          default-open="preview"
          :editable="false"
          :toolbars-flag="false"
        />
      </el-main>
    </el-container>
  </el-container>
</template>
<style>
.markdown-body table td, .markdown-body table th {
  padding: 2px 4px;
  font-size: 12px;
}
.markdown-body table th {
  background-color: #f3f3f3;
}
.markdown-body table tr:nth-child(2n) {
   background-color: #FFFFFF !important;
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
<script>
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
export default {
  name: 'OpenApiDoc',
  components: { mavonEditor },
  data() {
    return {
      defaultActive: '',
      menus: [],
      openMenu: [],
      content: '',
      activeIndex: '1'
    }
  },
  created() {
    this.loadMenu()
  },
  methods: {
    loadMenu: function() {
      this.getFile('static/openapi/menu.json', (menus) => {
        this.menus = menus
        menus.forEach(item => {
          this.openMenu.push(item.title)
        })
      })
    },
    onMenuClick: function(index, path) {
      this.loadMarkdown(index)
    },
    loadMarkdown: function(path) {
      this.getFile(`static/openapi/${path}?q=${new Date().getTime()}`, (content) => {
        this.content = content
      })
    }
  }
}
</script>
