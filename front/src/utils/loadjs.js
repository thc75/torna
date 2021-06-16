const cache = {}

/**
 * 加载JS
 * @param url js全路径
 * @param success 加载成功后回调函数
 */
export function loadJS(url, success) {
  if (cache[url]) {
    success()
    return
  }
  const doc = document
  const des_dom = (doc.body || doc.getElementsByTagName('body')[0]) || doc.getElementsByTagName('HEAD')[0]
  const script = doc.createElement('script')
  script.type = 'text/javascript'
  script.src = url
  if (script.readyState) {
    script.onreadystatechange = function() {
      if (script.readyState === 'loaded' || script.readyState === 'complete') {
        success()
      }
    }
  } else {
    script.onload = function() {
      success()
    }
  }
  des_dom.appendChild(script)
  cache[url] = true
}
