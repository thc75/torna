import { KJUR, hextob64 } from 'jsrsasign'

const HASH_TYPE = {
  SHA256withRSA: 'SHA256withRSA',
  SHA1withRSA: 'SHA1withRSA'
}

const PEM_BEGIN = '-----BEGIN PRIVATE KEY-----\n'
const PEM_END = '\n-----END PRIVATE KEY-----'

export const RSA = {
  /**
   * rsa签名
   * @param content {string}  签名内容
   * @param privateKey {string} 私钥
   * @param hash {string} hash算法，SHA256withRSA，SHA1withRSA
   * @returns {string} 返回签名字符串，base64后的内容
   */
  signToB64: function(content, privateKey, hash) {
    const signData = this.signData(content, privateKey, hash)
    // 将内容转成base64
    return hextob64(signData)
  },
  /**
   * rsa签名
   * @param content {string}  签名内容
   * @param privateKey {string} 私钥
   * @param hash {string} hash算法，SHA256withRSA，SHA1withRSA
   * @returns {KJUR.crypto.Signature.hSign|void} 返回签名后的内容
   */
  signData: function(content, privateKey, hash) {
    privateKey = this._formatKey(privateKey)
    // 创建 Signature 对象
    const signature = new KJUR.crypto.Signature({
      alg: hash,
      // !这里指定 私钥 pem!
      prvkeypem: privateKey
    })
    signature.updateString(content)
    return signature.sign()
  },
  HASH_TYPE: HASH_TYPE,
  _formatKey: function(key) {
    if (!key.startsWith(PEM_BEGIN)) {
      key = PEM_BEGIN + key
    }
    if (!key.endsWith(PEM_END)) {
      key = key + PEM_END
    }
    return key
  }
}
