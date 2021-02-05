<template>
  <el-popover
    v-model="show"
    placement="bottom"
    :title="title"
    width="250"
    trigger="click"
  >
    <el-form
      ref="updateForm"
      size="mini"
      label-width="100px;"
      @submit.native.prevent
    >
      <el-form-item>
        {{ msg }}
      </el-form-item>
      <div style="text-align: right; margin: 0">
        <el-button type="primary" native-type="submit" @click="onUpdateNameSave">确定</el-button>
      </div>
    </el-form>
    <el-link
      slot="reference"
      type="danger"
      icon="el-icon-delete"
      @click.stop="() => {}"
    />
  </el-popover>
</template>
<script>
export default {
  name: 'PopoverDelete',
  props: {
    title: {
      type: String,
      default: '删除确认'
    },
    msg: {
      type: String,
      default: '确认要删除该项吗？'
    },
    onSave: {
      type: Function,
      default: () => {}
    }
  },
  data() {
    return {
      show: false,
      msg: ''
    }
  },
  methods: {
    onUpdateNameSave() {
      this.onSave(() => {
        this.hide()
      })
    },
    hide() {
      this.show = false
    }
  }
}
</script>
