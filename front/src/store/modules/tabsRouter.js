const state = {
  visitedTabs: [],
  showTabsView: false
}

const actions = {
  addVisitedTabs({ commit, dispatch }, tabInfo) {
    return new Promise(resolve => {
      const index = state.visitedTabs.findIndex(tab => tab.path === tabInfo.path)
      if (index !== -1) {
        resolve({ position: index, length: state.visitedTabs.length })
      } else {
        commit('ADD_VISITED_TABS', tabInfo)
        resolve({ position: state.visitedTabs.length - 1, length: state.visitedTabs.length })
      }
      if (state.visitedTabs.length === 1) {
        dispatch('setting', true)
      }
    })
  },
  deleteVisitedTabs({ commit, dispatch, state }, view) {
    return new Promise(resolve => {
      for (const [index, v] of state.visitedTabs.entries()) {
        if (v.path === view.path) {
          commit('DELETE_VISITED_TABS', index)
          resolve({
            visitedTabs: [...state.visitedTabs], index
          })
          break
        }
      }
      if (state.visitedTabs.length === 0) {
        dispatch('setting', false)
      }
    })
  },
  deleteOthersVisitedTabs({ commit, state }, view) {
    return new Promise(resolve => {
      commit('DELETE_OTHERS_VISITED_TABS', view)
      resolve([...state.visitedTabs])
    })
  },
  deleteAllVisitedTabs({ commit, dispatch, state }) {
    return new Promise(resolve => {
      commit('DELETE_ALL_VISITED_TABS')
      dispatch('setting', false)
      resolve([...state.visitedTabs])
    })
  },
  deleteLeftTabs({ commit }, view) {
    return new Promise(resolve => {
      commit('DELETE_LEFT_TABS', view)
      resolve([...state.visitedTabs])
    })
  },
  deleteRightTabs({ commit }, view) {
    return new Promise(resolve => {
      commit('DELETE_RIGHT_TABS', view)
      resolve([...state.visitedTabs])
    })
  },
  setting: ({ commit }, value) => {
    commit('SETTING', !!value)
  }
}

const mutations = {
  ADD_VISITED_TABS: (state, tabInfo) => {
    state.visitedTabs.push(
      Object.assign({}, tabInfo, {
        title: tabInfo.title
      })
    )
  },
  DELETE_VISITED_TABS: (state, index) => {
    state.visitedTabs.splice(index, 1)
  },
  DELETE_OTHERS_VISITED_TABS: (state, view) => {
    state.visitedTabs = state.visitedTabs.filter(v => v.path === view.path)
  },
  DELETE_ALL_VISITED_TABS: state => {
    state.visitedTabs = []
  },
  DELETE_LEFT_TABS: (state, view) => {
    const index = state.visitedTabs.findIndex(v => v.path === view.path)
    if (index === -1) {
      return
    }
    state.visitedTabs = state.visitedTabs.filter((item, idx) => idx >= index)
  },
  DELETE_RIGHT_TABS: (state, view) => {
    const index = state.visitedTabs.findIndex(v => v.path === view.path)
    if (index === -1) {
      return
    }
    state.visitedTabs = state.visitedTabs.filter((item, idx) => idx <= index)
  },
  SETTING: (state, value) => {
    state.showTabsView = value
  }
}

export default {
  namespaced: true,
  state,
  actions,
  mutations
}

