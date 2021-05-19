<template>
  <div class="app-container">
    <el-button size="mini" type="primary" @click="onProjectAdd">{{ $ts('createProject') }}</el-button>
    <el-tabs
      v-model="active"
      type="card"
      style="margin-top: 10px;"
      @tab-click="onTabClick"
    >
      <el-tab-pane
        v-for="project in projectList"
        :key="project.id"
        :label="project.name"
        :name="project.name"
      >
        <span slot="label">
          {{ project.name }}
          <el-dropdown
            v-show="project.id === projectInfo.id"
            trigger="click"
            style="margin-left: 5px;"
            @command="handleCommand"
          >
            <span class="el-dropdown-link">
              <el-tooltip placement="top" content="更多操作" :open-delay="500">
                <a class="el-icon-setting el-icon--right"></a>
              </el-tooltip>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item icon="el-icon-edit" :command="onUpdate">{{ $ts('update')}}</el-dropdown-item>
              <el-dropdown-item icon="el-icon-delete" class="danger" :command="onDelete">{{ $ts('delete') }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </span>
      </el-tab-pane>
    </el-tabs>
    <compose-project-create-dialog ref="projectCreateDlg" :success="onProjectAddSuccess" />
  </div>
</template>
<script>
import ComposeProjectCreateDialog from '@/components/ComposeProjectCreateDialog'
export default {
  components: { ComposeProjectCreateDialog },
  props: {
    spaceId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      active: null,
      projectList: [],
      projectInfo: {
        id: ''
      }
    }
  },
  watch: {
    spaceId(val) {
      this.loadData(val)
    }
  },
  mounted() {
    this.loadData(this.spaceId)
  },
  methods: {
    loadData(spaceId) {
      if (spaceId) {
        this.get('/space/project/compose/list', { spaceId: spaceId }, resp => {
          this.projectList = resp.data
          if (this.projectList.length > 0) {
            const project = this.projectList[0]
            this.active = project.name
            this.projectInfo = project
          }
        })
      }
    },
    onProjectAdd() {
      this.$refs.projectCreateDlg.show(this.spaceId)
    },
    onProjectAddSuccess() {
      this.loadData(this.spaceId)
    },
    onTabClick(tab) {
      const label = tab.label
      for (const project of this.projectList) {
        if (label === project.name) {
          this.projectInfo = project
          break
        }
      }
    },
    onUpdate() {

    },
    onDelete() {

    }
  }
}
</script>
