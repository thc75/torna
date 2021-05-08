<template>
  <div class="app-container">
    <el-form ref="form" :model="form" class="text-form" label-width="110px">
      <el-form-item :label="$ts('spaceName')">
        {{ form.name }}
        <popover-update
          v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])"
          :on-show="() => {return form.name}"
          :on-save="onSaveName"
        />
      </el-form-item>
      <el-form-item :label="$ts('spaceAdmin')">
        {{ form.leader }}
      </el-form-item>
      <el-form-item :label="$ts('creator')">
        {{ form.creatorName }}
      </el-form-item>
      <el-form-item :label="$ts('createTime')">
        {{ form.gmtCreate }}
      </el-form-item>
      <el-form-item v-if="hasRole(`space:${spaceId}`, Role.admin)">
        <el-button type="danger" size="mini" @click="onSpaceDel">{{ $ts('deleteSpace') }}</el-button>
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
        creatorName: '',
        creatorId: '',
        gmtCreate: ''
      },
      popoverShow: false,
      updateForm: {
        id: '',
        name: ''
      },
      updateFormRules: {
        name: [
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' }
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
      this.confirm(this.$ts('deleteSpaceConfirm'), () => {
        this.post('/space/delete', { id: this.spaceId }, () => {
          this.setSpaceId('')
          this.tipSuccess(this.$ts('deleteSuccess'))
          this.goRoute('/')
        })
      })
    },
    loadInfo(spaceId) {
      if (spaceId) {
        this.get('/space/info', { spaceId: spaceId }, resp => {
          const data = resp.data
          const leaders = data.leaders.map(userInfo => {
            return userInfo.nickname
          })
          data.leader = leaders.join(' / ')
          this.form = data
        })
      }
    }
  }
}
</script>
