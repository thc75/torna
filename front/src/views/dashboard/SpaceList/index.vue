<template>
  <div>
    <h3>{{ $ts('spaceList') }}</h3>
    <p>
      <el-button type="primary" @click="onSpaceAdd">{{ $ts('createSpace') }}</el-button>
    </p>
    <div v-for="(space) in data" :key="space.id" class="torna-card" @click="enterSpace(space)">
      <el-card shadow="hover" class="box-card">
        <div slot="header" class="clearfix">
          <span>{{ space.name }}</span>
          <el-tooltip v-if="space.isCompose" content="聚合空间" placement="top">
            <i style="float: right; padding: 3px 0" class="el-icon-files"></i>
          </el-tooltip>
        </div>
        <el-form ref="form" :model="space" class="text-form" label-width="100px">
          <el-form-item :label="$ts('creator')">
            {{ space.creatorName }}
          </el-form-item>
          <el-form-item :label="$ts('createTime')">
            {{ space.gmtCreate }}
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    <space-create-dialog ref="spaceCreateDlg" :success="onSpaceAddSuccess" />
  </div>
</template>
<script>
import SpaceCreateDialog from '@/components/SpaceCreateDialog'
export default {
  name: 'SpaceList',
  components: { SpaceCreateDialog },
  data() {
    return {
      showDefault: true,
      data: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.get('/space/list', {}, resp => {
        this.data = resp.data
      })
    },
    enterSpace(space) {
      const url = space.isCompose ? `/space/compose/${space.id}` : `/space/project/${space.id}`
      this.goRoute(url)
    },
    onSpaceAdd() {
      this.$refs.spaceCreateDlg.show()
    },
    onSpaceAddSuccess() {
      this.loadData()
    }
  }
}
</script>
