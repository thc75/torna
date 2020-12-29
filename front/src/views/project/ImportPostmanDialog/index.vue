<template>
  <el-dialog
    title="导入Postman文档"
    :visible.sync="importPostmanDlgShow"
    @close="onHide"
  >
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
        <el-button slot="trigger" class="choose-file" type="primary">选择文件</el-button>
        <div slot="tip" class="el-upload__tip">选择Postman导出文件(Collection v2.1)</div>
      </el-upload>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="importPostmanDlgShow = false">取 消</el-button>
      <el-button :disabled="fileList.length === 0" type="primary"  @click="submitUpload">导 入</el-button>
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
      importPostmanDlgShow: false,
      importPostmanRule: {
        file: [
          { required: true, message: '请上传文件', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    show() {
      this.onImportPostman()
    },
    getAction() {
      return get_full_url('/module/import/postman')
    },
    getHeaders() {
      return get_headers()
    },
    onSelectFile(f, fileList) {
      this.fileList = fileList
    },
    onExceed(files, fileList) {
      this.$message.warning('只能选择一个文件')
    },
    onBeforeUpload() {
      this.extData.projectId = this.projectId
    },
    onImportPostman() {
      this.importPostmanDlgShow = true
      this.fileList = []
    },
    onHide() {
      this.$refs.upload.clearFiles()
    },
    onSuccess(resp) {
      if (resp.code !== '0') {
        this.tipError(resp.msg)
      } else {
        this.importPostmanDlgShow = false
        this.success && this.success(resp)
      }
    },
    submitUpload() {
      this.$refs.upload.submit()
    }
  }
}
</script>
