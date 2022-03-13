const state = {
  visitedTabs: [],
  showTabsView: false
}

const actions = {
  addVisitedTabs({ commit, dispatch }, view) {
    return new Promise(resolve => {
      const index = state.visitedTabs.findIndex(tab => tab.path === view.path)
      if (index !== -1) {
        resolve({ position: index, length: state.visitedTabs.length })
      } else {
        commit('ADD_VISITED_TABS', view)
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
  setting: ({ commit }, value) => {
    commit('SETTING', !!value)
  }
}

const mutations = {
  ADD_VISITED_TABS: (state, view) => {
    state.visitedTabs.push(
      Object.assign({}, view, {
        title: view.meta.title || view.query.meta_title || ''
      })
    )
  },
  DELETE_VISITED_TABS: (state, index) => {
    state.visitedTabs.splice(index, 1)
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

