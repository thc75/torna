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
    DUBBO: 1,
    CUSTOM: 2
  },
  /**
   * 扩展属性
   */
  PROP_TYPE: {
    DEBUG: 10,
    DEBUG_PROXY: 11
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
  INIT_ORDER_INDEX: 10000,
  /**
   * 调试脚本类型
   */
  DEBUG_SCRIPT_TYPE: {
    /** 前置脚本 */
    PRE: 0,
    /** 后置脚本 */
    AFTER: 1
  },
  /**
   * 调试脚本作用域
   */
  DEBUG_SCRIPT_SCOPE: {
    DOC: 0,
    MODULE: 1,
    PROJECT: 2
  },
  /**
   * 文档元素位置
   */
  POSITION_TYPE: {
    DOC_NAME: 0,
    DOC_HTTP_METHOD: 1,
    DOC_URL: 2,
    CONTENT_TYPE: 3,
    DOC_DESCRIPTION: 4,
    DEPRECATED: 5,
    IS_SHOW: 6,
    ORDER_INDEX: 7,
    PATH_PARAM: 8,
    HEADER_PARAM: 9,
    QUERY_PARAM: 10,
    REQUEST_PARAM: 11,
    RESPONSE_PARAM: 12,

    PARAM_NAME: 50,
    PARAM_TYPE: 51,
    PARAM_REQUIRED: 52,
    PARAM_MAXLENGTH: 53,
    PARAM_DESCRIPTION: 54,
    PARAM_EXAMPLE: 55
  }
}

export const PositionNameMap = {
  DOC_NAME: 'docName',
  DOC_HTTP_METHOD: 'method',
  DOC_URL: 'requestUrl',
  CONTENT_TYPE: 'contentType',
  DOC_DESCRIPTION: 'docDesc',
  DEPRECATED: 'deprecated',
  IS_SHOW: 'isShow',
  ORDER_INDEX: 'orderIndex',
  PATH_PARAM: 'pathVariable',
  HEADER_PARAM: 'requestHeader',
  QUERY_PARAM: 'queryParam',
  REQUEST_PARAM: 'requestParams',
  RESPONSE_PARAM: 'responseParam',

  PARAM_NAME: 'paramName',
  PARAM_TYPE: 'type',
  PARAM_REQUIRED: 'required',
  PARAM_MAXLENGTH: 'maxLength',
  PARAM_DESCRIPTION: 'description',
  PARAM_EXAMPLE: 'example'
}
