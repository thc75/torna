<template>
  <div class="navbar">
    <div class="right-menu">
      <div class="right-menu-item">
        <el-button type="primary" size="mini" @click="goRoute('/')">{{ $ts('docManagement') }}</el-button>
      </div>
      <div class="right-menu-item">
        <el-tooltip placement="bottom" :content="$ts('helpCenter')">
          <el-button type="text" class="el-icon-question navbar-btn" style="color: #5a5e66" @click="openLink('/help')" />
        </el-tooltip>
      </div>
      <div class="right-menu-item">
        <user-message />
      </div>
      <div class="right-menu-item">
        <right-dropdown />
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import RightDropdown from '@/components/RightDropdown'
import UserMessage from '@/components/UserMessage'

export default {
  components: {
    RightDropdown, UserMessage
  },
  data() {
    return {
      isShowDefault: false,
      spaceData: []
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar'
    ])
  }
}
</script>
