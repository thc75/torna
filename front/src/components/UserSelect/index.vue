<template>
  <el-form ref="userSelectForm" :model="formData" :rules="rules" size="mini">
    <el-form-item prop="value">
      <el-select
        v-model="formData.value"
        filterable
        remote
        :multiple="multiple"
        :remote-method="remoteMethod"
        :placeholder="$ts('filterWithUsername')"
        style="width: 100%"
        :loading="loading"
      >
        <el-option
          v-for="item in userOptions"
          :key="item.id"
          :label="item.nickname"
          :value="item.id"
        >
          {{ item.nickname }}<span v-show="item.email">({{ item.email }})</span>
        </el-option>
      </el-select>
    </el-form-item>
  </el-form>
</template>
<script>
export default {
  name: 'UserSelect',
  props: {
    multiple: {
      type: Boolean,
      default: false
    },
    url: {
      type: String,
      default: '/user/search'
    },
    loader: {
      type: Function,
      default: null
    },
    value: {
      type: Array,
      default: () => []
    },
    rules: {
      type: Object,
      default() {
        return {
          value: [
            { required: true, message: this.$ts('selectUser'), trigger: ['blur', 'change'] }
          ]
        }
      }
    }
  },
  data() {
    return {
      loading: false,
      userOptions: [],
      formData: {
        value: ''
      },
      searchFormData: {
        username: ''
      }
    }
  },
  watch: {
    value(newVal) {
      this.formData.value = newVal
    }
  },
  mounted() {
    this.formData.value = this.value
    if (this.value.length > 0) {
      this.loadByUserIds(this.value)
    }
  },
  methods: {
    remoteMethod(val) {
      this.searchFormData.username = val
      this.loadUser()
    },
    loadByUserIds(userIds) {
      this.loading = true
      this.post('/user/list', { userIds: userIds }, resp => {
        this.loading = false
        this.userOptions = resp.data
      }, () => {
        this.loading = false
      })
    },
    loadUser() {
      const that = this
      if (this.loader) {
        const promise = this.loader(this.searchFormData)
        promise.then((data) => {
          that.userOptions = data
        })
      } else {
        this.loading = true
        this.post('/user/search', this.searchFormData, resp => {
          this.loading = false
          this.userOptions = resp.data
        }, () => {
          this.loading = false
        })
      }
    },
    getValue() {
      return this.formData.value
    },
    resetForm() {
      this.$refs.userSelectForm.resetFields()
    },
    setValue(value) {
      this.formData.value = value
    },
    validate() {
      return this.$refs.userSelectForm.validate()
    }
  }
}
</script>
