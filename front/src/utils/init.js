const initList = []

export function add_init(fn) {
  if (fn) {
    initList.push(fn)
  }
}

export function run_init() {
  for (const el of initList) {
    el.call(this)
  }
}
