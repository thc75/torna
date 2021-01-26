<template>
  <div>
    <h3>空间列表</h3>
    <p style="margin-left: 10px;">
      <el-button type="primary" @click="onSpaceAdd">创建空间</el-button>
    </p>
    <div v-for="(space) in data" :key="space.id" class="space-card" @click="enterSpace(space)">
      <el-card shadow="hover" class="box-card">
        <div slot="header" class="clearfix">
          <span>{{ space.name }}</span>
        </div>
        <el-form ref="form" :model="space" class="text-form" label-width="100px">
          <el-form-item label="创建人">
            {{ space.creatorName }}
          </el-form-item>
          <el-form-item label="创建时间">
            {{ space.gmtCreate }}
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    <space-create-dialog ref="spaceCreateDlg" :success="onSpaceAddSuccess" />
  </div>
</template>
<style lang="scss">
.space-card {
  display: inline-block;
  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }

  .box-card {
    width: 300px;
    margin: 10px;
    cursor: pointer;
  }
}
</style>
<script>
import SpaceCreateDialog from '@/components/SpaceCreateDialog'
export default {
  name: 'SpaceList',
  components: { SpaceCreateDialog },
  data() {
    return {
      showDefault: true,
      data: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.get('/space/list', {}, resp => {
        this.data = resp.data
      })
    },
    enterSpace(space) {
      this.goRoute(`/space/project/${space.id}`)
    },
    onSpaceAdd() {
      this.$refs.spaceCreateDlg.show()
    },
    onSpaceAddSuccess() {
      this.loadData()
    }
  }
}
</script>
