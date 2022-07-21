<template>
  <div :class="{'has-logo':showLogo}">
    <logo v-if="showLogo" :collapse="false" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <div class="side-opt-bar">
        <el-radio-group v-model="dimension" size="mini" @change="onDimensionChange">
          <el-radio-button :label="1">{{ $ts('spaceDimension') }}</el-radio-button>
          <el-radio-button :label="2">{{ $ts('projectDimension') }}</el-radio-button>
        </el-radio-group>
        <el-radio-group v-model="expandAll" size="mini" @change="onTriggerStatus">
          <el-radio-button :label="true">{{ $ts('expand') }}</el-radio-button>
          <el-radio-button :label="false">{{ $ts('collapse') }}</el-radio-button>
        </el-radio-group>
      </div>
      <doc-select v-if="dimension === 1" ref="ref1" :node-click="onNodeClick" :on-space-change="onSpaceChange" />
      <doc-select-v2 v-if="dimension === 2" ref="ref2" :node-click="onNodeClick" :on-project-change="onProjectChange" />
    </el-scrollbar>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import Logo from '@/components/Logo'
import DocSelect from '@/components/DocSelect'
import DocSelectV2 from '@/components/DocSelectV2'

$addI18n({
  'spaceDimension': { 'zh': '空间维度', 'en': 'Space Dimension' },
  'projectDimension': { 'zh': '项目维度', 'en': 'Project Dimension' }
})

export default {
  components: { Logo, DocSelect, DocSelectV2 },
  data() {
    return {
      types: this.getEnums().FOLDER_TYPE,
      dimension: 2,
      expandAll: false,
      docViewTabs: false
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
  mounted() {
    this.expandAll = (this.getAttr(this.getTriggerStatusKey()) || 'false') === 'true'
    this.dimension = parseInt(this.getAttr(this.getTriggerDimensionKey())) || 2
    this.$nextTick(() => {
      this.onTriggerStatus(this.expandAll)
    })
    this.docViewTabs = this.$store.state.settings.docViewTabSwitch
  },
  methods: {
    // 树点击事件
    onNodeClick(data, node, tree) {
      if (data.type === this.types.TYPE_DOC) {
        // 目前没想到好的办法传输文档名称到标签路由中
        // this.goRoute(`/view/${data.docId}`)
        this.toRoute({ path: `/view/${data.docId}` }, data.label)
      }
    },
    onSpaceChange(spaceId) {
      this.setSpaceId(spaceId)
    },
    onProjectChange(projectId) {
      this.setProjectId(projectId)
    },
    onTriggerStatus(val) {
      this.setAttr(this.getTriggerStatusKey(), val)
      this.getSelectRef().onTriggerStatus(val)
    },
    onDimensionChange(val) {
      this.setAttr(this.getTriggerDimensionKey(), val)
    },
    getSelectRef() {
      if (this.dimension === 1) {
        return this.$refs.ref1
      } else if (this.dimension === 2) {
        return this.$refs.ref2
      }
    },
    getTriggerStatusKey() {
      return `torna.doc.view.tree.trigger`
    },
    getTriggerDimensionKey() {
      return `torna.doc.view.dimension`
    }
  }
}
</script>
<style lang="scss">
.side-opt-bar {
  margin-right: 10px;
  .el-radio-group {
    margin-top: 10px;
    margin-left: 10px;
  }
}
</style>
