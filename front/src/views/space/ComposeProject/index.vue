<template>
  <div class="app-container">
    <el-button
      v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])"
      size="small"
      type="primary"
      @click="onProjectAdd"
    >
      {{ $ts('createProject') }}
    </el-button>
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
            v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])"
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
    <div class="compose-content">
      <div class="compose-url"></div>
      <el-dropdown v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])" trigger="click" @command="handleCommand">
        <el-button type="primary" size="mini">
          {{ $ts('createDoc') }} <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="el-icon-document" :command="onDocAdd">{{ $ts('createDoc') }}</el-dropdown-item>
          <el-dropdown-item icon="el-icon-folder">{{ $ts('createFolder') }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <compose-project-create-dialog ref="projectCreateDlg" :success="onProjectAddSuccess" />
    <el-dialog
      :title="$ts('selectDoc')"
      :visible.sync="selectDocShow"
    >
      <doc-select
        ref="docSelect"
        show-checkbox
        :load-init="false"
        :indent="16"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="selectDocShow = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onProjectAddSave">{{ $ts('dlgSave') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import ComposeProjectCreateDialog from '@/components/ComposeProjectCreateDialog'
import DocSelect from '@/components/DocSelect'
export default {
  components: { ComposeProjectCreateDialog, DocSelect },
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
        id: '',
        name: ''
      },
      selectDocShow: false
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
    reload() {
      this.loadData(this.spaceId)
    },
    loadData(spaceId) {
      if (spaceId) {
        this.get('/space/compose/project/list', { spaceId: spaceId }, resp => {
          this.projectList = resp.data
          if (this.projectList.length > 0) {
            let projectList
            if (this.projectInfo.id) {
              projectList = this.projectList.filter(row => row.id === this.projectInfo.id)
            }
            if (!projectList || projectList.length === 0) {
              projectList = this.projectList
            }
            const project = projectList[0]
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
    onDocAdd() {
      this.selectDocShow = true
      this.$nextTick(() => {
        this.getSelect().init()
      })
    },
    getSelect() {
      return this.$refs.docSelect
    },
    onProjectAddSave() {
      const checkedKeys = this.getSelect().getCheckedDocIds()
      console.log(checkedKeys)
    },
    onUpdate() {
      this.$refs.projectCreateDlg.updateShow(this.projectInfo)
    },
    onDelete() {
      this.confirm(this.$ts('deleteConfirm', this.projectInfo.name), () => {
        const data = {
          id: this.projectInfo.id
        }
        this.post('/compose/project/delete', data, () => {
          this.tipSuccess(this.$ts('deleteSuccess'))
          this.reload()
        })
      })
    }
  }
}
</script>
