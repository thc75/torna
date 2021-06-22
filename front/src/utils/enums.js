export const Enums = {
  STATUS: {
    ENABLE: 1,
    DISABLE: 0
  },
  /**
   * 0：path, 1：header， 2：请求参数，3：返回参数，4：错误码
   */
  PARAM_STYLE: {
    path: 0,
    header: 1,
    request: 2,
    response: 3,
    code: 4
  },
  CONTENT_TYPE: [
    'application/json',
    'application/x-www-form-urlencoded',
    'multipart/form-data',
    'application/xml',
    'text/plain',
    '*/*'
  ],
  USER_STATUS: {
    SET_PASSWORD: 2
  },
  /**
   * 注册来源
   */
  SOURCE: {
    REGISTER: 'register',
    BACKEND: 'backend',
    FORM: 'form',
    OAUTH: 'oauth'
  },
  /**
   * 分享类型
   */
  SHARE_TYPE: {
    /** 公开 */
    PUBLIC: 1,
    /** 加密 */
    ENCRYPT: 2
  },
  /**
   * 聚合项目类型
   */
  COMPOSE_PROJECT_TYPE: {
    /** 公开 */
    PUBLIC: 1,
    /** 加密 */
    ENCRYPT: 2
  },
  /**
   * 协议
   */
  DOC_TYPE: {
    HTTP: 0,
    DUBBO: 1
  },
  /**
   * 扩展属性
   */
  PROP_TYPE: {
    DEBUG: 10
  },
  /**
   * 文件夹类型
   */
  FOLDER_TYPE: {
    PROJECT: 0,
    TYPE_MODULE: 1,
    TYPE_FOLDER: 2,
    TYPE_DOC: 3
  },
  /**
   * 初始orderIndex
   */
  INIT_ORDER_INDEX: 10000
}
