<template>
  <el-dropdown trigger="click" class="user-message" @command="handleCommand">
    <el-badge :value="messages.length" :max="99" :hidden="!hasMessage()">
      <el-button type="text" class="navbar-btn" icon="el-icon-bell" style="color: #606266" />
    </el-badge>
    <el-dropdown-menu slot="dropdown" class="user-message-menu user-drop-menu">
      <el-dropdown-item style="padding: 0 20px">
        <el-button v-show="hasMessage()" type="text" icon="el-icon-circle-check" style="margin-right: 10px" @click="setReadAll">全部标记已读</el-button>
        <router-link to="/user/message">
          <el-button type="text" icon="el-icon-message-solid">消息中心</el-button>
        </router-link>
      </el-dropdown-item>
      <el-dropdown-item v-for="item in messages" :key="item.id" class="user-message-item" divided>
        <div>
          {{ item.message }}
        </div>
        <div>
          <router-link :to="`/view/doc/${item.sourceId}`">
            <el-link type="primary" @click="setRead(item)">查看文档</el-link>
          </router-link>
          |
          <el-link type="primary" @click.stop="setRead(item)">标记已读</el-link>
        </div>
      </el-dropdown-item>
      <el-dropdown-item v-show="!hasMessage()">
        <span>暂没有新消息</span>
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>
<style lang="scss">
.user-message {
  .el-button {
    padding: 0 !important;
  }
  .el-badge__content {
    top: 12px !important;
  }
}
.user-message-menu {
  padding: 5px 0 !important;
  a {
    display: initial;
  }
  .user-message-item {
    line-height: 25px !important;
    .user-message-item-btn {
      float: right;
    }
  }
}
</style>
<script>
export default {
  data() {
    return {
      list: [],
      inter: null
    }
  },
  computed: {
    messages() {
      return this.list.filter(row => row.isRead === 0)
    }
  },
  created() {
    this.loadMessage()
    // 每30秒拉取一次
    this.inter = setInterval(this.loadMessage, 1000 * 30)
  },
  destroyed() {
    if (this.inter) {
      clearInterval(this.inter)
    }
  },
  methods: {
    loadMessage: function() {
      this.get('/user/message/unread', {}, resp => {
        this.list = resp.data
      }, () => {})
    },
    hasMessage() {
      return this.messages.length > 0
    },
    setRead(item) {
      item.isRead = true
      this.post('/user/message/setRead', { id: item.id }, () => {
      })
    },
    setReadAll() {
      this.post('/user/message/setReadAll', { }, () => {
        this.loadMessage()
      })
    }
  }
}
</script>
