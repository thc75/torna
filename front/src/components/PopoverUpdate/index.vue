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
        <el-input-number v-if="isNumber" ref="inputNumber" v-model="updateForm.value" controls-position="right" style="width: 150px;" />
        <el-input v-else ref="input" v-model="updateForm.value" show-word-limit maxlength="50" />
      </el-form-item>
      <div style="text-align: right; margin: 0">
        <el-button type="text" size="mini" @click="hide">{{ $ts('cancel') }}</el-button>
        <el-button type="primary" size="mini" native-type="submit" @click="onUpdateNameSave">{{ $ts('ok') }}</el-button>
      </div>
    </el-form>
    <el-button
      v-if="showIcon"
      slot="reference"
      type="text"
      icon="el-icon-edit"
      @click.stop="() => {}"
    />
    <el-button
      v-else
      slot="reference"
      type="text"
      @click.stop="() => {}"
    >
      {{ value }}
    </el-button>
  </el-popover>
</template>
<script>
export default {
  name: 'PopoverUpdate',
  props: {
    isNumber: {
      type: Boolean,
      default: false
    },
    showIcon: {
      type: Boolean,
      default: true
    },
    title: {
      type: String,
      default: $ts('updateName')
    },
    label: {
      type: String,
      default: ''
    },
    value: {
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
      this.updateForm.value = this.onShow(this.updateForm)
      this.$nextTick(() => {
        this.$refs.input && this.$refs.input.focus()
        this.$refs.inputNumber && this.$refs.inputNumber.focus()
      })
    },
    hide() {
      this.show = false
    }
  }
}
</script>
