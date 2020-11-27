<template>
  <el-popover
    v-model="show"
    placement="bottom"
    :title="title"
    width="250"
    trigger="click"
    @show="showHandler"
  >
    <el-form
      ref="updateForm"
      :model="updateForm"
      :rules="updateFormRules"
      size="mini"
      label-width="100px;"
      @submit.native.prevent
    >
      <el-form-item :label="label" prop="value">
        <el-input ref="input" v-model="updateForm.value" show-word-limit maxlength="50" />
      </el-form-item>
      <div style="text-align: right; margin: 0">
        <el-button type="primary" native-type="submit" @click="onUpdateNameSave">确定</el-button>
      </div>
    </el-form>
    <el-button
      slot="reference"
      type="text"
      icon="el-icon-edit"
      @click.stop="() => {}"
    />
  </el-popover>
</template>
<script>
export default {
  name: 'PopoverUpdate',
  props: {
    title: {
      type: String,
      default: '修改名称'
    },
    label: {
      type: String,
      default: ''
    },
    onShow: {
      type: Function,
      default: () => {}
    },
    onSave: {
      type: Function,
      default: () => {}
    }
  },
  data() {
    return {
      show: false,
      updateForm: {
        value: ''
      },
      updateFormRules: {
        value: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    onUpdateNameSave() {
      this.$refs.updateForm.validate(valid => {
        if (valid) {
          this.onSave(this.updateForm.value, () => {
            this.hide()
          })
        }
      })
    },
    showHandler() {
      const value = this.onShow(this.updateForm)
      if (value) {
        this.updateForm.value = value
      }
      this.$nextTick(() => {
        this.$refs.input.focus()
      })
    },
    hide() {
      this.show = false
    }
  }
}
</script>
