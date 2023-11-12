<template>
  <div>
    <el-tabs>
      <el-tab-pane :label="$t('projectConstant')">
        <el-alert type="info" :closable="false">
          <span slot="title">{{ $t('ConstantInfo.projectConstantTip') }}
            <el-link type="primary" :underline="false" class="el-icon-question" @click="$refs.help.open('static/help/constant.md')" />
          </span>
        </el-alert>
        <br>
        <rich-text-editor
          :value="projectConstantInfo"
          :editable="true"
          @input="projectConstantInfoEditorInput"
        />
        <el-button type="primary" style="margin-top: 20px" @click="onProjectConstantInfoSave">{{ $t('save') }}</el-button>
      </el-tab-pane>
      <el-tab-pane :label="$t('applicationConstant')">
        <el-alert type="info" :title="$t('ConstantInfo.applicationConstantTip')" :closable="false" />
        <br/>
        <el-tabs type="card" @tab-click="onTabSelect">
          <el-tab-pane v-for="item in moduleData" :key="item.id" :label="item.name"></el-tab-pane>
          <rich-text-editor
            :value="moduleConstantInfo"
            :editable="true"
            @input="moduleConstantInfoEditorInput"
          />
          <el-button type="primary" style="margin-top: 20px" @click="onModuleConstantInfoSave">{{ $t('save') }}</el-button>
        </el-tabs>
      </el-tab-pane>
    </el-tabs>
    <help ref="help" />
  </div>
</template>
<script>
import RichTextEditor from '@/components/RichTextEditor'
import Help from '@/components/Help'

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
        this.tipSuccess($t('saveSuccess'))
      })
    },
    onModuleConstantInfoSave() {
      this.post('/constant/module/save', {
        id: this.currentModule.id,
        content: this.moduleConstantInfo
      }, resp => {
        this.tipSuccess($t('saveSuccess'))
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
