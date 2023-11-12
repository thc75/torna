<template>
  <el-popover
    placement="right"
    trigger="hover"
  >
    <div>
      <p>{{ $t('privateDesc') }}</p>
      <p>{{ $t('publicDesc') }}</p>
    </div>
    <i slot="reference" class="el-icon-question" style="margin-left: 10px"></i>
  </el-popover>
</template>
