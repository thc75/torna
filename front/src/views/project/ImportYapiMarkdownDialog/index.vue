<template>
  <el-dialog
    :title="$t('ImportYapiMarkdownDoc.importYapiMarkdownDoc')"
    :visible.sync="importYapiMarkdownDlgShow"
    @close="onHide"
  >
    <h3>{{ $t('ImportYapiMarkdownDoc.step1') }}</h3>
    <p>
      <img :src="`${getBaseUrl()}/static/images/export-yapi.png`" style="width: 80%" />
    </p>
    <h3>{{ $t('ImportYapiMarkdownDoc.step2') }}</h3>
    <div style="text-align: center">
      <el-upload
        ref="upload"
        name="file"
        :action="getAction()"
        :data="extData"
        :headers="getHeaders()"
        accept="json"
        :multiple="false"
        :auto-upload="false"
        :limit="1"
        :before-upload="onBeforeUpload"
        :on-exceed="onExceed"
        :on-change="(file, files) => onSelectFile(file, files)"
        :on-remove="(file, files) => onSelectFile(file, files)"
        :on-success="onSuccess"
      >
        <el-button slot="trigger" class="choose-file" type="primary">{{ $t('chooseFile') }}</el-button>
      </el-upload>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="importYapiMarkdownDlgShow = false">{{ $t('dlgCancel') }}</el-button>
      <el-button :disabled="fileList.length === 0" type="primary" @click="submitUpload">{{ $t('dlgImport') }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
import { get_full_url, get_headers } from '@/utils/http'
export default {
  name: 'ImportPostmanDialog',
  props: {
    projectId: {
      type: String,
      default: ''
    },
    success: {
      type: Function,
      default: () => {}
    }
  },
  data() {
    return {
      extData: {},
      fileList: [],
      importYapiMarkdownDlgShow: false,
      importYapiMarkdownRule: {
        file: [
          { required: true, message: this.$t('pleaseUploadFile'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    show() {
      this.onImportPostman()
    },
    getAction() {
      return get_full_url('/module/import/yapi/markdown')
    },
    getHeaders() {
      return get_headers()
    },
    onSelectFile(f, fileList) {
      this.fileList = fileList
    },
    onExceed(files, fileList) {
      this.$message.warning(this.$t('onlyChooseOneFile'))
    },
    onBeforeUpload() {
      this.extData.projectId = this.projectId
    },
    onImportPostman() {
      this.importYapiMarkdownDlgShow = true
      this.fileList = []
    },
    onHide() {
      this.$refs.upload.clearFiles()
    },
    onSuccess(resp) {
      if (resp.code !== '0') {
        this.tipError(resp.msg)
      } else {
        this.importYapiMarkdownDlgShow = false
        this.success && this.success(resp)
      }
    },
    submitUpload() {
      this.$refs.upload.submit()
    }
  }
}
</script>
