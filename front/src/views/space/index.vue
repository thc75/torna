<template>
  <div class="app-container">
    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane name="Project">
        <span slot="label"><i class="el-icon-s-management"></i> 项目列表</span>
        <space-project :space-id="spaceIdProject" />
      </el-tab-pane>
      <el-tab-pane name="Info">
        <span slot="label"><i class="el-icon-info"></i> 空间信息</span>
        <space-info :space-id="spaceIdInfo" />
      </el-tab-pane>
      <el-tab-pane name="Member">
        <span slot="label"><i class="el-icon-user"></i> 空间成员</span>
        <space-member :space-id="spaceIdMember" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import SpaceInfo from './SpaceInfo'
import SpaceMember from './SpaceMember'
import SpaceProject from './SpaceProject'
export default {
  name: 'SpaceHome',
  components: { SpaceProject, SpaceInfo, SpaceMember },
  data() {
    return {
      space: '',
      activeName: 'Project',
      spaceIdProject: '',
      spaceIdInfo: '',
      spaceIdMember: ''
    }
  },
  mounted() {
    this.spaceId = this.$route.params.spaceId
    this.loadData(this.spaceId)
  },
  methods: {
    loadData(spaceId) {
      this.get('/space/info', { spaceId: spaceId }, resp => {
        this.setCurrentSpace(resp.data)
        this.setCurrentProject('')
      })
      this[`spaceId${this.activeName}`] = spaceId
    },
    handleClick() {
      this.loadData(this.spaceId)
    }
  }
}
</script>

