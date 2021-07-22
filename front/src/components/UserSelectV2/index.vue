<template>
  <el-form ref="userSelectForm" :model="formData" :rules="rules" size="mini">
    <el-form-item prop="value">
      <el-select
        v-model="formData.value"
        filterable
        :filter-method="dataFilter"
        :multiple="multiple"
        :placeholder="$ts('filterWithUsername')"
        style="width: 100%"
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
  name: 'UserSelectV2',
  props: {
    multiple: {
      type: Boolean,
      default: false
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
      userOptions: [],
      userOptionsCopy: [],
      formData: {
        value: ''
      }
    }
  },
  methods: {
    getValue() {
      return this.formData.value
    },
    resetForm() {
      this.$refs.userSelectForm.resetFields()
    },
    setData(data) {
      this.userOptions = data
      this.userOptionsCopy = Object.assign(data)
    },
    setValue(value) {
      this.formData.value = value
    },
    validate() {
      return this.$refs.userSelectForm.validate()
    },
    dataFilter(val) {
      if (val) {
        val = val.toLowerCase()
        this.userOptions = this.userOptionsCopy.filter((item) => {
          if (item.username.toLowerCase().indexOf(val) > -1 ||
            item.nickname.toLowerCase().indexOf(val) > -1 ||
            item.email.toLowerCase().indexOf(val) > -1) {
            return true
          }
        })
      } else {
        this.userOptions = this.userOptionsCopy
      }
    }
  }
}
</script>
