<template>
  <div>
    <el-form ref="form" :model="form" class="text-form" label-width="100px">
      <el-form-item label="空间名称">
        {{ form.name }}
        <popover-update
          v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])"
          :on-show="() => {return form.name}"
          :on-save="onSaveName"
        />
      </el-form-item>
      <el-form-item label="空间管理员">
        {{ form.leader }}
      </el-form-item>
      <el-form-item label="创建人">
        {{ form.creator }}
      </el-form-item>
      <el-form-item label="创建时间">
        {{ form.gmtCreate }}
      </el-form-item>
      <el-form-item v-if="hasRole(`space:${spaceId}`, Role.admin)">
        <el-button type="danger" size="mini" @click="onSpaceDel">删除空间</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import PopoverUpdate from '@/components/PopoverUpdate'
export default {
  name: 'SpaceInfo',
  components: { PopoverUpdate },
  props: {
    spaceId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      form: {
        id: '',
        name: '',
        creator: '',
        gmtCreate: ''
      },
      popoverShow: false,
      updateForm: {
        id: '',
        name: ''
      },
      updateFormRules: {
        name: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    spaceId(spaceId) {
      this.loadInfo(spaceId)
    }
  },
  methods: {
    onUpdateNameSave() {
      this.$refs.updateNameForm.validate(valid => {
        if (valid) {
          this.updateForm.id = this.form.id
          this.post('/space/updateName', this.updateForm, () => {
            this.popoverShow = false
            location.reload()
          })
        }
      })
    },
    onSaveName(value, done) {
      const data = {
        id: this.spaceId,
        name: value
      }
      this.post('/space/updateName', data, () => {
        done()
        location.reload()
      })
    },
    onSpaceDel() {
      this.confirm('确认要删除该空间吗？', () => {
        this.post('/space/delete', { id: this.spaceId }, () => {
          this.setSpaceId(0)
          alert('删除成功')
          location.reload()
        })
      })
    },
    loadInfo(spaceId) {
      if (spaceId) {
        this.get('/space/info', { spaceId: spaceId }, resp => {
          const data = resp.data
          const leaders = data.leaders.map(userInfo => {
            return userInfo.realname
          })
          data.leader = leaders.join(' / ')
          this.form = data
        })
      }
    }
  }
}
</script>
