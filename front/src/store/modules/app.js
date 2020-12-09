import Cookies from 'js-cookie'

const state = {
  sidebar: {
    opened: Cookies.get('sidebarStatus') ? !!+Cookies.get('sidebarStatus') : true,
    withoutAnimation: false
  },
  sidebarView: {
    opened: Cookies.get('sidebarViewStatus') ? !!+Cookies.get('sidebarViewStatus') : true,
    withoutAnimation: false
  },
  device: 'desktop'
}

const mutations = {
  TOGGLE_SIDEBAR: state => {
    state.sidebar.opened = !state.sidebar.opened
    state.sidebar.withoutAnimation = false
    if (state.sidebar.opened) {
      Cookies.set('sidebarStatus', 1)
    } else {
      Cookies.set('sidebarStatus', 0)
    }
  },
  TOGGLE_SIDEBAR_VIEW: state => {
    state.sidebarView.opened = !state.sidebarView.opened
    state.sidebarView.withoutAnimation = false
    if (state.sidebarView.opened) {
      Cookies.set('sidebarViewStatus', 1)
    } else {
      Cookies.set('sidebarViewStatus', 0)
    }
  },
  CLOSE_SIDEBAR: (state, withoutAnimation) => {
    Cookies.set('sidebarStatus', 0)
    state.sidebar.opened = false
    state.sidebar.withoutAnimation = withoutAnimation
  },
  CLOSE_SIDEBAR_VIEW: (state, withoutAnimation) => {
    Cookies.set('sidebarViewStatus', 0)
    state.sidebarView.opened = false
    state.sidebarView.withoutAnimation = withoutAnimation
  },
  TOGGLE_DEVICE: (state, device) => {
    state.device = device
  }
}

const actions = {
  toggleSideBar({ commit }) {
    commit('TOGGLE_SIDEBAR')
  },
  toggleSideBarView({ commit }) {
    commit('TOGGLE_SIDEBAR_VIEW')
  },
  closeSideBarView({ commit }, { withoutAnimation }) {
    commit('CLOSE_SIDEBAR_VIEW', withoutAnimation)
  },
  closeSideBar({ commit }, { withoutAnimation }) {
    commit('CLOSE_SIDEBAR', withoutAnimation)
  },
  toggleDevice({ commit }, device) {
    commit('TOGGLE_DEVICE', device)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
