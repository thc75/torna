<template>
  <div>
    <el-drawer
      :title="$ts('dictInfo')"
      :visible.sync="drawer"
      :direction="direction"
      size="35%"
      :with-header="true"
      :before-close="handleClose"
    >
      <div v-if="list.length > 0" style="margin: 10px;">
        <div v-for="enumInfo in list" :key="enumInfo.id">
          <h3>{{ enumInfo.name }}</h3>
          <el-alert v-if="enumInfo.description" :closable="false" :title="enumInfo.description" style="margin-bottom: 10px;" />
          <enum-item-view :list="enumInfo.items" />
        </div>
      </div>
      <div v-else style="text-align: center;margin-top: 30px;">
        <span class="info-tip">{{ $ts('emptyDictData') }}</span>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import EnumItemView from '@/components/EnumItemView'
export default {
  components: { EnumItemView },
  data() {
    return {
      drawer: false,
      direction: 'rtl',
      list: []
    }
  },
  methods: {
    show(moduleId) {
      if (moduleId) {
        this.get('/doc/enum/info/all', { moduleId: moduleId }, resp => {
          this.list = resp.data
          this.drawer = true
          document.body.style.overflowY = 'hidden'
        })
      }
    },
    handleClose() {
      this.drawer = false
      document.body.style.overflowY = 'auto'
    }
  }
}
</script>
