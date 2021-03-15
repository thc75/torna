const initList = []
let hasInit = false

export function add_init(fn) {
  if (hasInit) {
    fn.call(this)
  }
  if (fn) {
    initList.push(fn)
  }
}

export function run_init() {
  hasInit = true
  for (const el of initList) {
    el.call(this)
  }
}
