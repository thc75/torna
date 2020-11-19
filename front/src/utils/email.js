const emailMap = {
  'qq.com': 'http://mail.qq.com',
  'gmail.com': 'http://mail.google.com',
  'sina.com': 'http://mail.sina.com.cn',
  '163.com': 'http://mail.163.com',
  '126.com': 'http://mail.126.com',
  'yeah.net': 'http://www.yeah.net/',
  'sohu.com': 'http://mail.sohu.com/',
  'tom.com': 'http://mail.tom.com/',
  'sogou.com': 'http://mail.sogou.com/',
  '139.com': 'http://mail.10086.cn/',
  'hotmail.com': 'http://www.hotmail.com',
  'live.com': 'http://login.live.com/',
  'live.cn': 'http://login.live.cn/',
  'live.com.cn': 'http://login.live.com.cn',
  '189.com': 'http://webmail16.189.cn/webmail/',
  'yahoo.com.cn': 'http://mail.cn.yahoo.com/',
  'yahoo.cn': 'http://mail.cn.yahoo.com/',
  'eyou.com': 'http://www.eyou.com/',
  '21cn.com': 'http://mail.21cn.com/',
  '188.com': 'http://www.188.com/',
  'dingtalk.com': 'https://mail.dingtalk.com/',
  'outlook.com': 'https://outlook.live.com/',
  'foxmail.com': 'http://www.foxmail.com'
}

export function goEmailSite(email, callback) {
  processEmail(email, (url) => {
    callback(url)
  })
}

export function encodeEmail(email) {
  if (email && email.indexOf('@') > -1) {
    let ret = ''
    const arr = email.split('@')
    const account = arr[0]
    if (account.length <= 3) {
      ret = `${account.substring(0, 1)}***@${arr[1]}`
    } else {
      ret = `${account.substring(0, 3)}***@${arr[1]}`
    }
    return ret
  } else {
    return ''
  }
}

export function processEmail(email, callback) {
  try {
    const arr = email.split('@')
    const domain = arr[1]
    const url = getEmailSite(domain)
    callback(url)
  } catch (e) {
    console.log('解析邮箱失败, email:' + email, e)
  }
}

export function getEmailSite(domain) {
  return emailMap[domain]
}
