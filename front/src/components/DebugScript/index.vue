<template>
  <div>
    <el-tabs v-model="activeName">
      <el-tab-pane name="pre" label="Pre-request Script">
        <div class="table-opt-btn">
          <el-switch
            v-model="preEnable"
            :active-text="$ts('enable')"
            inactive-text=""
            :active-value="true"
            :inactive-value="false"
          />
          <span class="split">|</span>
          <span class="tip">{{ $ts('preScriptTip') }}</span>
          <el-link type="primary" :underline="false" @click="openLink('/help?id=debug')">{{ $ts('document') }}</el-link>
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
          <el-switch
            v-model="afterEnable"
            :active-text="$ts('enable')"
            inactive-text=""
            :active-value="true"
            :inactive-value="false"
          />
          <span class="split">|</span>
          <span class="tip">{{ $ts('afterScriptTip') }}</span>
          <el-link type="primary" :underline="false" @click="openLink('/help?id=debug')">{{ $ts('document') }}</el-link>
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
const CryptoJS = require('crypto-js')
export default {
  name: 'DebugScript',
  components: { editor: require('vue2-ace-editor') },
  data() {
    return {
      activeName: 'pre',
      preEnable: true,
      preContent: '',
      afterEnable: true,
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
      const fn = new Function('CryptoJS', 'ctx', `return ${code}`)
      fn(CryptoJS, ctx)
      return ctx
    },
    runAfter(resp, ctx) {
      const script = this.afterContent
      if (!script) {
        return resp
      }
      const code = `(function() {
          ${script}
        }())`
      // eslint-disable-next-line no-eval
      // const data = eval(fn)
      const fn = new Function('CryptoJS', 'ctx', 'resp', `return ${code}`)
      fn(CryptoJS, ctx, resp)
      return resp
    },
    setData(data) {
      if (data) {
        this.preEnable = data.preEnable === undefined ? true : data.preEnable
        this.preContent = data.preContent || ''
        this.afterEnable = data.afterEnable === undefined ? true : data.afterEnable
        this.afterContent = data.afterContent || ''
      }
    },
    getData() {
      return {
        preEnable: this.preEnable,
        preContent: this.preContent,
        afterEnable: this.afterEnable,
        afterContent: this.afterContent
      }
    },
    getEnable() {
      return (this.preEnable && this.preContent.length > 0) || (this.afterEnable && this.afterContent.length > 0)
    }
  }
}
</script>
