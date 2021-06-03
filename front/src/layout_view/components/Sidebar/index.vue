<template>
  <div :class="{'has-logo':showLogo}">
    <logo v-if="showLogo" :collapse="false" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <doc-select ref="tree" :node-click="onNodeClick" :on-space-change="onSpaceChange" />
    </el-scrollbar>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import Logo from '@/components/Logo'
import DocSelect from '@/components/DocSelect'

export default {
  components: { Logo, DocSelect },
  data() {
    return {
      types: this.getEnums().FOLDER_TYPE
    }
  },
  computed: {
    ...mapGetters([
      'sidebarView'
    ]),
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    }
  },
  methods: {
    // 树点击事件
    onNodeClick(data, node, tree) {
      if (data.type === this.types.TYPE_DOC) {
        this.goRoute(`/view/${data.docId}`)
      }
    },
    onSpaceChange(spaceId) {
      this.setSpaceId(spaceId)
    }
  }
}
</script>
