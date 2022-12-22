<template>
  <div>
    <el-timeline>
      <el-timeline-item
        v-for="(item, index) in list"
        :key="index"
        :timestamp="item.gmtCreate"
        placement="top"
      >
        <span class="doc-modify-info">
          {{ $ts('modifier') }}：{{ item.modifyNickname }}
          <el-button style="float: right; padding: 0;margin-right: 10px" type="text">还原</el-button>
          <el-button style="float: right; padding: 0;margin-right: 10px" type="text">对比</el-button>
        </span>
        <div>
          <div v-for="detail in item.docDiffDetails" :key="detail.id">
            <div v-if="detail.positionType < 50">
              {{ getI18nName(detail.positionType) }}：{{ detail }}
            </div>
            <div v-else></div>

          </div>
        </div>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>
<script>
import { Enums } from '@/utils/enums'
const POSITION_TYPE = Enums.POSITION_TYPE
// { '0': 'DOC_NAME' }
const positionConfig = {}
for (const key in POSITION_TYPE) {
  const value = POSITION_TYPE[key]
  positionConfig['' + value] = key
}
const i18nNameMap = {
  DOC_NAME: 'docName',
  DOC_HTTP_METHOD: 'method',
  DOC_URL: 'requestUrl',
  CONTENT_TYPE: 'contentType',
  DOC_DESCRIPTION: 'docDesc',
  DEPRECATED: 'deprecated',
  IS_SHOW: 'isShow',
  ORDER_INDEX: 'orderIndex',
  PATH_PARAM: 'pathVariable',
  HEADER_PARAM: 'requestHeader',
  QUERY_PARAM: 'queryParam',
  REQUEST_PARAM: 'requestParams',
  RESPONSE_PARAM: 'responseParam',

  PARAM_NAME: 'paramName',
  PARAM_TYPE: 'type',
  PARAM_REQUIRED: 'required',
  PARAM_MAXLENGTH: 'maxLength',
  PARAM_DESCRIPTION: 'description',
  PARAM_EXAMPLE: 'example'
}

export default {
  name: 'DocChangelog',
  data() {
    return {
      list: []
    }
  },
  methods: {
    show(docId) {
      if (docId) {
        this.get('/doc/changelog/list', { docId: docId }, resp => {
          this.list = resp.data
        })
      }
    },
    getI18nName(positionType) {
      const name = positionConfig['' + positionType]
      return $ts(i18nNameMap[name])
    }
  }
}
</script>
