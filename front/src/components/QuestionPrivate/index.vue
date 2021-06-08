<template>
  <el-popover
    placement="right"
    trigger="hover"
  >
    <div>
      <p>{{ $ts('privateDesc') }}</p>
      <p>{{ $ts('publicDesc') }}</p>
    </div>
    <i slot="reference" class="el-icon-question" style="margin-left: 10px"></i>
  </el-popover>
</template>
