<template>
  <div>
    <el-drawer
      :title="$ts('help')"
      :visible.sync="drawer"
      size="35%"
    >
      <mavon-editor
        v-model="content"
        :boxShadow="false"
        :subfield="false"
        defaultOpen="preview"
        :editable="false"
        :toolbarsFlag="false"
      />
    </el-drawer>
  </div>
</template>
<script>
import { mavonEditor } from 'mavon-editor'

export default {
  name: 'Help',
  components: { mavonEditor },
  props: {
    path: {
      type: String,
      required: false,
      default: ''
    }
  },
  data() {
    return {
      uri: '',
      drawer: false,
      content: '使用手册'
    }
  },
  watch: {
    path(val) {
      this.uri = val
      this.loadMarkdown(val)
    }
  },
  methods: {
    open(path) {
      if (path) {
        this.loadMarkdown(path)
      } else {
        this.loadMarkdown(this.uri)
      }
    },
    loadMarkdown: function(path) {
      if (!path) {
        return
      }
      // this.goRoute(`/help?id=${id}`)
      this.getFile(`${path}?q=${new Date().getTime()}`, (content) => {
        this.content = content
        this.drawer = true
      })
    }
  }
}
</script>
