<template>
  <div>
    <el-timeline>
      <el-timeline-item
        v-for="(item, index) in list"
        :key="index"
      >
        <h3>{{ item.modifyTime }}</h3>
        <el-card style="margin-right: 10px">
          <span class="doc-modify-info">
            <el-tag size="medium" :type="getTagType(item.modifyType)" :closable="false">
              {{ getDocModifyTypeName(item.modifyType) }}
            </el-tag>
            {{ item.modifyType === 1 ? $t('creator') : $t('modifier') }}：{{ item.modifyNickname }}
            <div style="display: inline;">
  <!--            <el-popconfirm-->
              <!--              :title="$t('DocChangelog.confirmRestore')"-->
              <!--            >-->
              <!--              <el-button slot="reference" style="float: right; padding: 0;margin-left: 10px" type="text" @click="restoreDoc(item)">-->
              <!--                {{ $t('restore') }}-->
              <!--              </el-button>-->
              <!--            </el-popconfirm>-->

              <el-button
                v-show="item.modifySource === getEnums().MODIFY_SOURCE.TEXT && item.md5Old && item.md5New"
                style="float: right; padding: 0;margin-left: 10px"
                type="text"
                @click="showDiff(item)"
              >
                {{ $t('compare') }}
              </el-button>
              <el-button style="float: right; padding: 0;margin-left: 10px" type="text" @click="showCompare(item)">
                {{ $t('viewDoc') }}
              </el-button>
            </div>
          </span>
          <div v-show="item.docDiffWrappers && item.docDiffWrappers.length > 0">
            <el-divider content-position="center">{{ $t('changeContent') }}</el-divider>
            <div v-for="wrapper in item.docDiffWrappers" :key="wrapper.positionType">
              <div class="changelog-item-label">
                <span class="position-type">{{ getI18nName(wrapper.positionType) }}</span>
              </div>
              <div v-for="detail in wrapper.docDiffDetails" :key="detail.id" class="changelog-item">
                <div v-if="isDocInfoChange(detail.positionType)" class="inline">
                  <doc-changelog-simple-diff :detail="detail" />
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
    <el-dialog
      ref="compareDlg"
      :title="$t('compare')"
      :visible.sync="compareDlgShow"
      fullscreen
      :modal="false"
    >
      <doc-diff ref="docDiff" />
    </el-dialog>
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
import { Enums } from '@/utils/enums'
import DocChangelogSimpleDiff from '@/components/DocChangelogSimpleDiff'
import DocChangelogParamDiff from '@/components/DocChangelogParamDiff'
import DocCompare from '@/components/DocCompare'
import DocDiff from '@/components/DocDiff'
const POSITION_TYPE = Enums.POSITION_TYPE
// { '0': { label: '', value: 0 } }
const positionConfig = {}
for (const key in POSITION_TYPE) {
  // { label: '', value: 0 }
  const obj = POSITION_TYPE[key]
  positionConfig['' + obj.value] = obj
}

export default {
  name: 'DocChangelog',
  components: { DocChangelogSimpleDiff, DocChangelogParamDiff, DocCompare, DocDiff },
  data() {
    return {
      list: [],
      compareDlgShow: false
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
    getDocModifyTypeName(type) {
      // 变更类型，0：修改，1：创建
      switch (type) {
        case 0:
          return $t('update')
        case 1:
          return $t('create')
      }
      return ''
    },
    getParamModifyTypeName(type) {
      // 变更类型，0：修改，1：新增，2：删除
      switch (type) {
        case 0:
          return $t('update')
        case 1:
          return $t('newAdd')
        case 2:
          return $t('delete')
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
      return positionType < 40
    },
    getI18nName(positionType) {
      const obj = positionConfig['' + positionType]
      return $t(obj.label)
    },
    showCompare(record) {
      this.$refs.docCompare.show(record.md5New)
    },
    showDiff(record) {
      this.compareDlgShow = true
      this.$nextTick(() => {
        this.$refs.docDiff.compareWithMd5(record.md5Old, record.md5New)
      })
    },
    restoreDoc(item) {
    }
  }
}
</script>
