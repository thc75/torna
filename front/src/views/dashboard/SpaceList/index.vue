<template>
  <div class="app-container">
    <h3>空间列表</h3>
    <div v-for="(space) in data" :key="space.id" class="space-card">
      <el-card shadow="hover" class="box-card">
        <div slot="header" class="clearfix">
          <span>{{ space.name }}</span>
          <el-button style="float: right; padding: 4px 4px" type="primary" @click="enterSpace(space)">进入空间</el-button>
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
  </div>
</template>
<style lang="scss">
.space-card {
  display: inline-block;
  margin: 10px;
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
  }
}
</style>
<script>
export default {
  name: 'SpaceList',
  data() {
    return {
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
      this.goRoute(`/space/${space.id}`)
    }
  }
}
</script>
