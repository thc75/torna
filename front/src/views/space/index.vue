<template>
  <div class="app-container">
    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane name="Project">
        <span slot="label"><i class="el-icon-s-management"></i> 项目列表</span>
        <space-project :space="space" :space-id="spaceIdProject" />
      </el-tab-pane>
      <el-tab-pane name="Info">
        <span slot="label"><i class="el-icon-info"></i> 空间信息</span>
        <space-info :space-id="spaceIdInfo" />
      </el-tab-pane>
      <el-tab-pane name="Member">
        <span slot="label"><i class="el-icon-user"></i> 空间成员</span>
        <space-member :space-id="spaceIdMember" />
      </el-tab-pane>
      <el-tab-pane v-if="spaceId && hasRole(`space:${spaceId}`, Role.admin)" name="OpenUser">
        <span slot="label"><i class="el-icon-collection-tag"></i> 开放用户</span>
        <open-user :space-id="spaceIdOpenUser" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import SpaceInfo from './SpaceInfo'
import SpaceMember from './SpaceMember'
import SpaceProject from './SpaceProject'
import OpenUser from './OpenUser'
export default {
  name: 'SpaceHome',
  components: { SpaceProject, SpaceInfo, SpaceMember, OpenUser },
  data() {
    return {
      space: {
        id: 0
      },
      spaceId: '',
      activeName: 'Project',
      spaceIdProject: '',
      spaceIdInfo: '',
      spaceIdMember: '',
      spaceIdOpenUser: ''
    }
  },
  mounted() {
    const spaceId = this.$route.params.spaceId
    this.loadData(spaceId)
  },
  methods: {
    loadData(spaceId) {
      this.spaceId = spaceId
      if (this.space.id === 0) {
        this.get('/space/info', { spaceId: spaceId }, resp => {
          this.space = resp.data
          this.setCurrentInfo(this.space, '')
        })
      }
      this[`spaceId${this.activeName}`] = spaceId
    },
    handleClick() {
      this.loadData(this.spaceId)
    }
  }
}
</script>

