<template>
  <div class="navbar">
    <hamburger :is-active="sidebarView.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <el-dropdown v-show="spaceData.length > 0" trigger="click" style="padding: 10px;" @command="handleCommand">
      <span class="el-dropdown-link" style="font-size: 16px;">
        <el-tag>{{ currentSpace.name }} <i class="el-icon-caret-bottom el-icon--right" style="font-size: 16px;"></i></el-tag>
      </span>
      <el-dropdown-menu slot="dropdown">
        <div class="dropdown-operation">
          <el-checkbox v-model="isShowDefault">显示个人空间</el-checkbox>
        </div>
        <el-dropdown-item v-for="(item, index) in spaceData" v-show="item.isDefault ? isShowDefault : true" :key="item.id" :divided="index === 0" :command="function(){ onSpaceSelect(item) }">
          {{ item.name }}
          <span v-if="item.isDefault" class="space-user">
            <el-tooltip placement="top" content="个人空间">
              <i v-if="item.isDefault" class="el-icon-user"></i>
            </el-tooltip>
            ({{ item.creatorName }})
          </span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
    <el-button type="primary" size="mini" icon="el-icon-monitor" @click="goAdminPage">管理模式</el-button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Hamburger from '@/components/Hamburger'

export default {
  components: {
    Hamburger
  },
  data() {
    return {
      currentSpace: {
        id: ''
      },
      isShowDefault: false,
      spaceData: []
    }
  },
  computed: {
    ...mapGetters([
      'sidebarView',
      'avatar'
    ])
  },
  created() {
    this.initSpace()
  },
  methods: {
    initSpace() {
      this.get('/space/list', {}, resp => {
        this.spaceData = resp.data
        if (this.spaceData.length === 0) {
          return
        }
        let selected = false
        const cacheId = this.getSpaceId()
        if (cacheId) {
          for (let i = 0; i < this.spaceData.length; i++) {
            if (cacheId === this.spaceData[i].id) {
              this.doSelectSpace(this.spaceData[i])
              selected = true
              break
            }
          }
        }
        // 没有选中就选择第一个
        if (!selected) {
          this.doSelectSpace(this.spaceData[0])
        }
      })
    },
    onSpaceSelect(item) {
      this.doSelectSpace(item)
    },
    doSelectSpace(item) {
      this.currentSpace = item
      this.setSpaceId(item.id)
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBarView')
    },
    goAdminPage() {
      this.goRoute('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.dropdown-operation {
  padding: 0 10px;
}
.space-user {
  color: #909399;
}
.el-dropdown-link {
  cursor: pointer;
  //color: #409EFF;
}
.el-icon-arrow-down {
  font-size: 12px;
}

.navbar-div {
  float: right;
  margin-right: 12px;
}
.user-head {
  cursor: pointer;
  margin-top: 6px;margin-right: 10px;
}
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 40px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
