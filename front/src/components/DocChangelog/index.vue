<template>
  <div>
    <el-timeline>
      <el-timeline-item
        v-for="(item, index) in list"
        :key="index"
      >
        <h3>{{ item.gmtCreate }}</h3>
        <el-card style="margin-right: 10px">
          <span class="doc-modify-info">
            <el-tag v-if="item.modifyType === 1" size="medium" :type="getTagType(item.modifyType)" :closabl="false">
              {{ getDocModifyTypeName(item.modifyType) }}
            </el-tag>
            {{ item.modifyType === 1 ? $ts('creator') : $ts('modifier') }}：{{ item.modifyNickname }}
            <div style="display: inline;">
  <!--            <el-popconfirm-->
              <!--              :title="$ts('confirmRestore')"-->
              <!--            >-->
              <!--              <el-button slot="reference" style="float: right; padding: 0;margin-left: 10px" type="text" @click="restoreDoc(item)">-->
              <!--                {{ $ts('restore') }}-->
              <!--              </el-button>-->
              <!--            </el-popconfirm>-->
              <el-button style="float: right; padding: 0;margin-left: 10px" type="text" @click="showCompare(item)">
                {{ $ts('viewDoc') }}
              </el-button>
            </div>
          </span>
          <div v-show="item.docDiffWrappers && item.docDiffWrappers.length > 0">
            <el-divider content-position="center">{{ $ts('changeContent') }}</el-divider>
            <div v-for="wrapper in item.docDiffWrappers" :key="wrapper.positionType">
              <div class="changelog-item-label">
                <span class="position-type">{{ getI18nName(wrapper.positionType) }}</span>
              </div>
              <div v-for="detail in wrapper.docDiffDetails" :key="detail.id" class="changelog-item">
                <div v-if="isDocInfoChange(detail.positionType)" class="inline">
                  <doc-changelog-simple-diff :value="detail.content.value" />
                </div>
                <div v-else>
                  <doc-changelog-param-diff :detail="detail" />
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    <doc-compare ref="docCompare" />
  </div>
</template>
<style>
.changelog-item-label {
  margin: 8px 0;
  display: inline-block;
}
.position-type {
  font-weight: bold;
}
.changelog-item {
  margin: 5px 0;
}
</style>
<script>
import { Enums, PositionNameMap } from '@/utils/enums'
import DocChangelogSimpleDiff from '@/components/DocChangelogSimpleDiff'
import DocChangelogParamDiff from '@/components/DocChangelogParamDiff'
import DocCompare from '@/components/DocCompare'
const POSITION_TYPE = Enums.POSITION_TYPE
// { '0': 'DOC_NAME' }
const positionConfig = {}
for (const key in POSITION_TYPE) {
  const value = POSITION_TYPE[key]
  positionConfig['' + value] = key
}

$addI18n({
  confirmRestore: { zh: '确认还原到此版本吗？', en: 'Restore this version?' },
  currentVersion: { zh: '当前版本', en: 'Current version' }
})

export default {
  name: 'DocChangelog',
  components: { DocChangelogSimpleDiff, DocChangelogParamDiff, DocCompare },
  data() {
    return {
      list: [],
      docId: ''
    }
  },
  methods: {
    show(docId) {
      if (docId) {
        this.docId = docId
        this.get('/doc/changelog/list', { docId: docId }, resp => {
          this.list = resp.data
        })
      }
    },
    getDocModifyTypeName(type) {
      // 变更类型，0：修改，1：创建
      switch (type) {
        case 0:
          return $ts('update')
        case 1:
          return $ts('create')
      }
      return ''
    },
    getParamModifyTypeName(type) {
      // 变更类型，0：修改，1：新增，2：删除
      switch (type) {
        case 0:
          return $ts('update')
        case 1:
          return $ts('newAdd')
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
    },
    showCompare(record) {
      this.$refs.docCompare.show(record.md5New, this.docId)
    },
    restoreDoc(item) {
    }
  }
}
</script>
