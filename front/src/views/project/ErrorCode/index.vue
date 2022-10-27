<template>
  <div class="app-container">
    <el-tabs>
      <el-tab-pane :label="$ts('projectErrorCode')">
        <el-alert type="info" :closable="false">
          <span slot="title">{{ $ts('projectErrorCodeTip') }}</span>
        </el-alert>
        <br>
        <rich-text-editor
          :value="projectErrorCode"
          :editable="true"
          :height="editorHeight"
          @input="projectErrorCodeEditorInput"
        />
        <el-button type="primary" style="margin-top: 20px" @click="onProjectErrorCodeSave">{{ $ts('save') }}</el-button>
      </el-tab-pane>
      <el-tab-pane :label="$ts('applicationErrorCode')">
        <el-alert type="info" :title="$ts('applicationErrorCodeTip')" :closable="false" />
        <br/>
        <el-tabs tab-position="left" type="card" @tab-click="onTabSelect">
          <el-tab-pane v-for="item in moduleData" :key="item.id" :label="item.name"></el-tab-pane>
          <rich-text-editor
            :value="moduleErrorCode"
            :editable="true"
            :height="editorHeight"
            @input="moduleErrorCodeEditorInput"
          />
          <el-button type="primary" style="margin-top: 20px" @click="onModuleErrorCodeSave">{{ $ts('save') }}</el-button>
        </el-tabs>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import RichTextEditor from '@/components/RichTextEditor'
$addI18n({
  'projectErrorCode': { 'zh': '项目错误码', 'en': 'Project Error Code' },
  'projectErrorCodeTip': { 'zh': '定义项目级别错误码，能被下面各个应用访问', 'en': 'The project error codes that can be visited by the following applications' },
  'applicationErrorCode': { 'zh': '应用错误码', 'en': 'Application Error Code' },
  'applicationErrorCodeTip': { 'zh': '定义每个应用单独的错误码', 'en': 'Define a separate error code for each application' }
})

const clientHeight = document.body.clientHeight
const editor_height = (clientHeight - 310)
export default {
  name: 'ErrorCode',
  components: { RichTextEditor },
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      projectErrorCode: '',
      moduleErrorCode: '',
      currentModule: '',
      editorHeight: editor_height,
      moduleData: [],
      props: {
        label: 'name',
        children: 'children',
        disabled: 'disabled'
      }
    }
  },
  watch: {
    projectId(projectId) {
      this.loadData(projectId)
    }
  },
  methods: {
    loadData(projectId) {
      this.get('/code/project/get', { projectId: projectId }, resp => {
        this.projectErrorCode = resp.data
      })
      this.loadProjectModule(projectId)
    },
    loadProjectModule(projectId) {
      this.get('/module/list', { projectId: projectId }, resp => {
        this.moduleData = resp.data
        if (this.moduleData.length > 0) {
          this.selectNode(this.moduleData[0])
        }
      })
    },
    projectErrorCodeEditorInput(content) {
      this.projectErrorCode = content
    },
    moduleErrorCodeEditorInput(content) {
      this.moduleErrorCode = content
    },
    onProjectErrorCodeSave() {
      this.post('/code/project/save', {
        id: this.projectId,
        content: this.projectErrorCode
      }, resp => {
        this.tipSuccess($ts('saveSuccess'))
      })
    },
    onModuleErrorCodeSave() {
      this.post('/code/module/save', {
        id: this.currentModule.id,
        content: this.moduleErrorCode
      }, resp => {
        this.tipSuccess($ts('saveSuccess'))
      })
    },
    onTabSelect(tab) {
      const index = parseInt(tab.index)
      const node = this.moduleData[index]
      this.selectNode(node)
    },
    selectNode(node) {
      this.currentModule = node
      this.loadModuleErrorCode(node.id)
    },
    loadModuleErrorCode(moduleId) {
      this.get('/code/module/get', { moduleId: moduleId }, resp => {
        this.moduleErrorCode = resp.data
      })
    }
  }
}
</script>
