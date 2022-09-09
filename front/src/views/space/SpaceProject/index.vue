<template>
  <div class="app-container">
    <p>
      <el-button v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])" type="primary" @click="onProjectAdd">{{ $ts('createProject') }}</el-button>
    </p>
    <div v-if="data.length === 0" class="info-tip">
      {{ $ts('noProject') }}
    </div>
    <div v-else>
      <div style="float: right;margin-bottom: 10px;">
        <el-radio-group v-model="showType" size="mini" @change="onChangeShowType">
          <el-radio-button label="card">{{ $ts('card') }}</el-radio-button>
          <el-radio-button label="grid">{{ $ts('grid') }}</el-radio-button>
        </el-radio-group>
      </div>
      <div v-if="showType === 'card'" style="float: left">
        <div v-for="(project) in data" :key="project.id" class="torna-card" @click="enterProject(project)">
          <el-card shadow="hover" class="box-card">
            <div slot="header" class="clearfix">
              <span>
                <el-tooltip placement="top" :content="$ts('privateProject')">
                  <i v-if="project.isPrivate" class="el-icon-lock"></i>
                </el-tooltip>
                {{ project.name }}
              </span>
            </div>
            <el-form ref="form" :model="project" class="text-form" label-width="100px">
              <el-form-item :label="$ts('projectDesc')">
                {{ project.description }}
              </el-form-item>
              <el-form-item :label="$ts('creator')">
                {{ project.creatorName }}
              </el-form-item>
              <el-form-item :label="$ts('createTime')">
                {{ project.gmtCreate }}
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </div>
      <div v-else>
        <el-table
          :data="data"
          border
          highlight-current-row
        >
          <el-table-column :label="$ts('projectName')" prop="value">
            <template slot-scope="scope">
              <span>
                <el-link type="primary" style="font-size: 14px" @click="enterProject(scope.row)">{{ scope.row.name }}</el-link>
                <el-tooltip placement="right" :content="$ts('privateProject')">
                  <i v-if="scope.row.isPrivate" class="el-icon-lock"></i>
                </el-tooltip>
              </span>
            </template>
          </el-table-column>
          <el-table-column :label="$ts('projectDesc')" prop="description" />
          <el-table-column :label="$ts('creator')" prop="creatorName" />
          <el-table-column :label="$ts('createTime')" prop="gmtCreate" />
        </el-table>
      </div>
    </div>
    <project-create-dialog ref="projectCreateDlg" :success="onProjectAddSuccess" />
  </div>
</template>
<script>
import ProjectCreateDialog from '@/components/ProjectCreateDialog'
const SHOW_TYPE_KEY = 'torna.project-show-type'
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
      data: [],
      showType: 'card',
      search: ''
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
  created() {
    this.showType = this.getAttr(SHOW_TYPE_KEY) || 'card'
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
      const space = this.getSpace()
      const from = {
        spaceId: space.id,
        spaceName: space.name,
        projectId: item.id,
        projectName: item.name
      }
      this.setFrom(from)
      this.goProjectHome(item.id)
    },
    onProjectAdd() {
      this.$refs.projectCreateDlg.show(this.spaceId)
    },
    onProjectAddSuccess() {
      this.loadData(this.spaceId)
    },
    onChangeShowType(val) {
      this.setAttr(SHOW_TYPE_KEY, val)
    }
  }
}
</script>
