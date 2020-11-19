<template>
  <div class="app-container">
    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
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
export default {
  components: { SpaceInfo, SpaceMember },
  data() {
    return {
      activeName: 'Info',
      spaceIdInfo: 0,
      spaceIdMember: 0
    }
  },
  computed: {
    spaceId() {
      return this.$store.state.settings.spaceId
    }
  },
  watch: {
    spaceId(id) {
      this.loadData(id)
    }
  },
  mounted() {
    this.loadData(this.spaceId)
  },
  methods: {
    loadData(spaceId) {
      this[`spaceId${this.activeName}`] = spaceId
    },
    handleClick() {
      this.loadData(this.spaceId)
    }
  }
}
</script>

