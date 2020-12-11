<template>
  <div>
    <div v-for="(project) in data" :key="project.id" class="project-card">
      <el-card shadow="hover" class="box-card">
        <div slot="header" class="clearfix">
          <span>{{ project.name }}</span>
          <el-button style="float: right; padding: 4px 4px" type="primary" @click="enterProject(project)">进入项目</el-button>
        </div>
        <el-form ref="form" :model="project" class="text-form" label-width="100px">
          <el-form-item label="项目描述">
            {{ project.description }}
          </el-form-item>
          <el-form-item label="创建人">
            {{ project.creatorName }}
          </el-form-item>
          <el-form-item label="创建时间">
            {{ project.gmtCreate }}
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>
<style lang="scss">
.project-card {
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
  name: 'SpaceProject',
  props: {
    spaceId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      data: []
    }
  },
  watch: {
    spaceId(val) {
      this.loadData(val)
    }
  },
  methods: {
    loadData(spaceId) {
      if (spaceId) {
        this.get('/space/project/list', { spaceId: spaceId }, resp => {
          this.data = resp.data
        })
      }
    },
    enterProject(item) {
      this.goRoute(`/project/info/${item.id}`)
    }
  }
}
</script>
