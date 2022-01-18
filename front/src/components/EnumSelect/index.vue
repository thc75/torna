<template>
  <el-select v-model="row.example">
    <el-option
      v-for="enumItem in row.enumInfo.items"
      :key="enumItem.value"
      :label="enumItem.label"
      :value="enumItem.value"
    >
      <div v-if="enumItem.name === enumItem.value">
        <span style="float: left">{{ enumItem.value }}</span>
      </div>
      <div v-else>
        <span style="float: left">{{ enumItem.name }}</span>
        <span style="float: right; color: #8492a6; font-size: 13px">{{ enumItem.value }}</span>
      </div>
    </el-option>
  </el-select>
</template>
<script>
export default {
  name: 'EnumItemView',
  props: {
    row: {
      type: Object,
      require: true,
      default: () => {}
    }
  }
}
</script>
