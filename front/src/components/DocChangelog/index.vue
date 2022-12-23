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
        <el-divider content-position="center">{{ $ts('changeContent') }}</el-divider>
        <div>
          <div v-for="detail in item.docDiffDetails" :key="detail.id" class="changelog-item">
            <div class="changelog-item-label">
              <el-tag size="mini" :type="getTagType(detail.modifyType)" :closabl="false">{{ getModifyTypeName(detail.modifyType) }}</el-tag>
              <span class="position-type">{{ getI18nName(detail.positionType) }}</span>
            </div>
            <div v-if="isDocInfoChange(detail.positionType)" class="inline">
              <doc-changelog-simple-diff :value="detail.content.value" />
            </div>
            <div v-else>
              <doc-changelog-param-diff :content="detail.content" />
            </div>
          </div>
        </div>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>
<style>
.changelog-item-label {
  margin: 5px 0;
  display: inline-block;
}
.position-type {
  font-weight: bold;
}
</style>
<script>
import { Enums, PositionNameMap } from '@/utils/enums'
import DocChangelogSimpleDiff from '@/components/DocChangelogSimpleDiff'
import DocChangelogParamDiff from '@/components/DocChangelogParamDiff'
const POSITION_TYPE = Enums.POSITION_TYPE
// { '0': 'DOC_NAME' }
const positionConfig = {}
for (const key in POSITION_TYPE) {
  const value = POSITION_TYPE[key]
  positionConfig['' + value] = key
}

export default {
  name: 'DocChangelog',
  components: { DocChangelogSimpleDiff, DocChangelogParamDiff },
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
    getModifyTypeName(type) {
      // 变更类型，0：修改，1：新增，2：删除
      switch (type) {
        case 0:
          return $ts('update')
        case 1:
          return $ts('add')
        case 2:
          return $ts('delete')
      }
      return ''
    },
    getTagType(type) {
      // 变更类型，0：修改，1：新增，2：删除
      switch (type) {
        case 0:
          return 'warning'
        case 1:
          return 'success'
        case 2:
          return 'danger'
      }
      return ''
    },
    isDocInfoChange(positionType) {
      return positionType <= 7
    },
    getI18nName(positionType) {
      const name = positionConfig['' + positionType]
      return $ts(PositionNameMap[name])
    }
  }
}
</script>
