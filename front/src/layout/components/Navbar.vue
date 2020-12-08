<template>
  <div class="navbar">
    <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

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

    <div class="right-menu">
      <!--<el-button v-if="isIsp()" type="text" style="margin-right: 10px" @click="doLogout">退出</el-button>-->
      <el-dropdown trigger="click" @command="handleCommand">
        <el-avatar
          class="user-head"
          shape="square"
          size="medium"
          icon="el-icon-user-solid"
        />
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item :command="onResetPwd">
            <span>修改密码</span>
          </el-dropdown-item>
          <el-dropdown-item :command="doLogout" divided>
            <span style="display: block;">注销</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <div class="navbar-div">
      <el-dropdown trigger="click" @command="handleCommand">
        <span class="el-dropdown-link">
          <el-button type="text" class="el-icon-circle-plus" style="font-size: 24px;"></el-button>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="el-icon-house" :command="onSpaceCreate">创建空间</el-dropdown-item>
          <el-dropdown-item
            v-if="hasRole(`space:${currentSpace.id}`, [Role.dev, Role.admin])"
            icon="el-icon-notebook-1"
            :command="onProjectCreate"
          >
            创建项目
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <!-- 添加空间dialog -->
    <space-create-dialog ref="spaceCreateDlg" :success="initSpace" />
    <!-- 添加项目dialog -->
    <project-create-dialog ref="projectCreateDlg" />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Hamburger from '@/components/Hamburger'
import SpaceCreateDialog from '@/components/SpaceCreateDialog'
import ProjectCreateDialog from '@/components/ProjectCreateDialog/index'

export default {
  components: {
    Hamburger, SpaceCreateDialog, ProjectCreateDialog
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
      'sidebar',
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
      this.goHome()
    },
    doSelectSpace(item) {
      this.currentSpace = item
      this.setSpaceId(item.id)
    },
    onSpaceCreate() {
      this.$refs.spaceCreateDlg.show()
    },
    onProjectCreate() {
      this.$refs.projectCreateDlg.show()
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    onResetPwd: function() {
      this.goRoute('/updatePassword')
    },
    doLogout() {
      this.logout()
      // this.$router.push(`/login?redirect=${this.$route.fullPath}`)
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
