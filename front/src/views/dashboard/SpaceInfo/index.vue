<template>
  <div>
    <el-form ref="form" :model="form" class="text-form" label-width="100px">
      <el-form-item label="空间名称">
        {{ form.name }}
        <el-popover
          v-model="popoverShow"
          placement="bottom"
          title="修改名称"
          width="250"
          trigger="click"
          @show="onPopoverShow"
        >
          <el-form
            ref="updateNameForm"
            :model="updateForm"
            :rules="updateFormRules"
            size="mini"
            label-width="100px;"
          >
            <el-form-item prop="name">
              <el-input v-model="updateForm.name" show-word-limit maxlength="50" />
            </el-form-item>
          </el-form>
          <div style="text-align: right; margin: 0">
            <el-button type="primary" size="mini" @click="onUpdateNameSave">确定</el-button>
          </div>
          <el-button slot="reference" type="text" icon="el-icon-edit"></el-button>
        </el-popover>
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
      <el-form-item>
        <el-button type="danger" size="mini" @click="onSpaceDel">删除空间</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  name: 'SpaceInfo',
  props: {
    spaceId: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      form: {
        id: 0,
        name: '',
        creator: '',
        gmtCreate: ''
      },
      popoverShow: false,
      updateForm: {
        id: 0,
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
    onPopoverShow() {
      Object.assign(this.updateForm, this.form)
    },
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
