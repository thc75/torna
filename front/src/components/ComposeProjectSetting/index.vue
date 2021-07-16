<template>
  <div style="padding: 20px">
    <h4>网关URL<span class="info-tip">所有接口显示同一个网关URL，自带URL将隐藏</span></h4>
    <div>
      <el-input v-model="setting.gatewayUrl" size="mini" style="width: 500px" />
      <el-button type="primary" size="mini" style="margin-top: 10px;">保存</el-button>
    </div>
    <h4>公共参数</h4>
    <el-tabs active-name="commonRequestParam">
      <el-tab-pane :label="$ts('commonRequest')" name="commonRequestParam">
        <common-param
          ref="commonRequestParamRef"
          list-url="/compose/project/setting/globalParams/list"
          add-url="/compose/project/setting/globalParams/add"
          update-url="/compose/project/setting/globalParams/update"
          delete-url="/compose/project/setting/globalParams/delete"
        />
      </el-tab-pane>
      <el-tab-pane :label="$ts('commonResponse')" name="commonResponseParam">
        <common-param
          ref="commonResponseRef"
          list-url="/compose/project/setting/returnParams/list"
          add-url="/compose/project/setting/returnParams/add"
          update-url="/compose/project/setting/returnParams/update"
          delete-url="/compose/project/setting/returnParams/delete"
        />
      </el-tab-pane>
    </el-tabs>
    <h4>文档附加页<span class="info-tip">可添加文档附加信息，如签名算法说明，状态码列表等</span></h4>
    <el-button
      type="primary"
      size="mini"
      style="margin-bottom: 10px"
      @click="onExtDocAdd"
    >
      {{ $ts('add') }}
    </el-button>
    <el-table
      border
      highlight-current-row
    >
      <el-table-column label="标题" prop="title" />
      <el-table-column label="内容" prop="content">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-tickets" @click="onExtDocContentView(scope.row)">查看</el-button>
        </template>
      </el-table-column>
      <el-table-column
        :label="$ts('operation')"
        :width="$width(200, { 'en': 240 })"
      >
        <template slot-scope="scope">
          <el-link type="primary" size="mini" @click="onExtDocUpdate(scope.row)">{{ $ts('update') }}</el-link>
          <el-popconfirm
            :title="$ts('deleteConfirm', scope.row.name)"
            @confirm="onExtDocDelete(scope.row)"
          >
            <el-link slot="reference" type="danger" size="mini">{{ $ts('delete') }}</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import CommonParam from './CommonParam'
export default {
  components: { CommonParam },
  props: {
    projectId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      setting: {
        gatewayUrl: ''

      },
      globalParams: []
    }
  },
  watch: {
    projectId(val) {
      this.$refs.commonRequestParamRef.reload(val)
    }
  }
}
</script>
