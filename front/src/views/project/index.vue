<template>
  <div class="app-container">
    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane name="Doc">
        <span slot="label"><i class="el-icon-document"></i> 文档管理</span>
        <doc-info :project-id="projectIdDoc"></doc-info>
      </el-tab-pane>
      <el-tab-pane name="Info">
        <span slot="label"><i class="el-icon-info"></i> 项目信息</span>
        <project-info :project-id="projectIdInfo" />
      </el-tab-pane>
      <el-tab-pane name="Member">
        <span slot="label"><i class="el-icon-user"></i> 项目成员</span>
        <project-member :project-id="projectIdMember" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import DocInfo from './DocInfo'
import ProjectInfo from './ProjectInfo'
import ProjectMember from './ProjectMember'

export default {
  components: { ProjectMember, DocInfo, ProjectInfo },
  data() {
    return {
      projectId: 0,
      projectIdDoc: 0,
      projectIdInfo: 0,
      projectIdMember: 0,
      activeName: 'Doc'
    }
  },
  created() {
  },
  mounted() {
    this.projectId = parseInt(this.$route.params.projectId)
    this.loadData(this.projectId)
  },
  methods: {
    handleClick() {
      this.loadData(this.projectId)
    },
    loadData(projectId) {
      this[`projectId${this.activeName}`] = projectId
    }
  }
}
</script>
