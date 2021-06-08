<template>
  <div>
    <el-tabs v-model="activeName">
      <el-tab-pane name="pre" label="Pre-request Script">
        <div class="table-opt-btn">
          <span class="tip">{{ $ts('preScriptTip') }}</span>
          <el-link type="primary" :underline="false">文档</el-link>
        </div>
        <editor
          v-model="preContent"
          lang="javascript"
          theme="chrome"
          height="300"
          class="normal-boarder"
          :options="aceEditorConfig"
          @init="editorInit"
        />
      </el-tab-pane>
      <el-tab-pane name="after" label="After Response Script">
        <div class="table-opt-btn">
          <span class="tip">{{ $ts('afterScriptTip') }}</span>
          <el-link type="primary" :underline="false">文档</el-link>
        </div>
        <editor
          v-model="afterContent"
          lang="javascript"
          theme="chrome"
          height="300"
          class="normal-boarder"
          :options="aceEditorConfig"
          @init="editorInit"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
export default {
  name: 'PreRequestScript',
  components: { editor: require('vue2-ace-editor') },
  data() {
    return {
      activeName: 'pre',
      preContent: '',
      afterContent: '',
      aceEditorConfig: {
        // 去除编辑器里的竖线
        showPrintMargin: false
      }
    }
  },
  methods: {
    editorInit: function() {
      // language extension prerequsite...
      require('brace/ext/language_tools')
      // language
      require('brace/mode/json')
      require('brace/mode/javascript')
      require('brace/mode/less')
      require('brace/theme/chrome')
      // snippet
      require('brace/snippets/javascript')
    },
    runPre(ctx) {
      const script = this.preContent
      if (!script) {
        return ctx
      }
      const code = `(function() {
          ${script}
        }())`
      // eslint-disable-next-line no-eval
      // const data = eval(fn)
      const fn = new Function('ctx', `return ${code}`)
      fn(ctx)
      return ctx
    },
    runAfter(resp) {
      const script = this.afterContent
      if (!script) {
        return resp
      }
      const code = `(function() {
          ${script}
        }())`
      // eslint-disable-next-line no-eval
      // const data = eval(fn)
      const fn = new Function('resp', `return ${code}`)
      fn(resp)
      return resp
    }
  }
}
</script>
