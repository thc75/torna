<template>
  <div>
    <el-tabs>
      <el-tab-pane :label="$ts('projectConstant')">
        <el-alert type="info" :closable="false">
          <span slot="title">{{ $ts('projectConstantTip') }}
            <el-link type="primary" :underline="false" class="el-icon-question" @click="$refs.help.open('static/help/constant.md')" />
          </span>
        </el-alert>
        <br>
        <rich-text-editor
          :value="projectConstantInfo"
          :editable="true"
          :height="editorHeight"
          @input="projectConstantInfoEditorInput"
        />
        <el-button type="primary" style="margin-top: 20px" @click="onProjectConstantInfoSave">{{ $ts('save') }}</el-button>
      </el-tab-pane>
      <el-tab-pane :label="$ts('applicationConstant')">
        <el-alert type="info" :title="$ts('applicationConstantTip')" :closable="false" />
        <br/>
        <el-tabs type="card" @tab-click="onTabSelect">
          <el-tab-pane v-for="item in moduleData" :key="item.id" :label="item.name"></el-tab-pane>
          <rich-text-editor
            :value="moduleConstantInfo"
            :editable="true"
            :height="editorHeight"
            @input="moduleConstantInfoEditorInput"
          />
          <el-button type="primary" style="margin-top: 20px" @click="onModuleConstantInfoSave">{{ $ts('save') }}</el-button>
        </el-tabs>
      </el-tab-pane>
    </el-tabs>
    <help ref="help" />
  </div>
</template>
<script>
$addI18n({
  'projectConstantTip': { 'zh': '定义项目级别常量（错误码、枚举），能被下面各个应用访问', 'en': 'The project constant that can be visited by the following applications' },
  'applicationConstantTip': { 'zh': '定义每个应用单独的常量', 'en': 'Define a separate constant for each application' }
})
import RichTextEditor from '@/components/RichTextEditor'
import Help from '@/components/Help'

const clientHeight = document.body.clientHeight
const editor_height = (clientHeight - 310)
export default {
  name: 'ConstantInfo',
  components: { RichTextEditor, Help },
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      projectConstantInfo: '',
      moduleConstantInfo: '',
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
      this.get('/constant/project/get', { projectId: projectId }, resp => {
        this.projectConstantInfo = resp.data
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
    projectConstantInfoEditorInput(content) {
      this.projectConstantInfo = content
    },
    moduleConstantInfoEditorInput(content) {
      this.moduleConstantInfo = content
    },
    onProjectConstantInfoSave() {
      this.post('/constant/project/save', {
        id: this.projectId,
        content: this.projectConstantInfo
      }, resp => {
        this.tipSuccess($ts('saveSuccess'))
      })
    },
    onModuleConstantInfoSave() {
      this.post('/constant/module/save', {
        id: this.currentModule.id,
        content: this.moduleConstantInfo
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
      this.loadModuleConstantInfo(node.id)
    },
    loadModuleConstantInfo(moduleId) {
      this.get('/constant/module/get', { moduleId: moduleId }, resp => {
        this.moduleConstantInfo = resp.data
      })
    }
  }
}
</script>
