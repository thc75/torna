<template>
  <div>
    <p style="margin-left: 10px;">
      <el-button v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])" type="primary" @click="onProjectAdd">创建项目</el-button>
    </p>
    <div v-if="data.length === 0" class="info-tip">
      暂无项目
    </div>
    <div v-for="(project) in data" :key="project.id" class="project-card">
      <el-card shadow="hover" class="box-card">
        <div slot="header" class="clearfix">
          <span>
            <el-tooltip placement="top" content="私有项目">
              <i v-if="project.isPrivate" class="el-icon-lock"></i>
            </el-tooltip>
            {{ project.name }}
          </span>
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
    <project-create-dialog ref="projectCreateDlg" :success="onProjectAddSuccess" />
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
import ProjectCreateDialog from '@/components/ProjectCreateDialog'
export default {
  name: 'SpaceProject',
  components: { ProjectCreateDialog },
  props: {
    spaceId: {
      type: String,
      default: ''
    },
    space: {
      type: Object,
      default: () => {}
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
    },
    space(obj) {
      this.loadData(obj.id)
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
      const from = {
        spaceId: this.space.id,
        spaceName: this.space.name,
        projectId: item.id,
        projectName: item.name
      }
      this.setFrom(from)
      this.goRoute(`/project/info/${item.id}`)
    },
    onProjectAdd() {
      this.$refs.projectCreateDlg.show(this.spaceId)
    },
    onProjectAddSuccess() {
      this.loadData(this.spaceId)
    }
  }
}
</script>
