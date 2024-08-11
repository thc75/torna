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
    CUSTOM: 2,
    MARKDOWN: 3
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
    DOC_NAME: { value: 0, label: 'docName' },
    DOC_HTTP_METHOD: { value: 1, label: 'method' },
    DOC_URL: { value: 2, label: 'requestUrl' },
    CONTENT_TYPE: { value: 3, label: 'contentType' },
    DOC_DESCRIPTION: { value: 4, label: 'docDesc' },
    DEPRECATED: { value: 5, label: 'deprecated' },
    IS_SHOW: { value: 6, label: 'isShow' },
    ORDER_INDEX: { value: 7, label: 'orderIndex' },
    STATUS: { value: 13, label: 'status' },
    AUTHOR: { value: 14, label: 'maintainer' },
    USE_GLOBAL_HEADERS: { value: 15, label: 'useCommonHeader' },
    USE_GLOBAL_PARAMS: { value: 16, label: 'useCommonParam' },
    USE_GLOBAL_RETURNS: { value: 17, label: 'useCommonResponse' },
    IS_REQUEST_ARRAY: { value: 18, label: 'isRequestArray' },
    IS_RESPONSE_ARRAY: { value: 19, label: 'isResponseArray' },
    REQUEST_ARRAY_TYPE: { value: 20, label: 'requestArrayType' },
    RESPONSE_ARRAY_TYPE: { value: 21, label: 'responseArrayType' },
    //
    PATH_PARAM: { value: 40, label: 'pathVariable' },
    HEADER_PARAM: { value: 41, label: 'requestHeader' },
    QUERY_PARAM: { value: 42, label: 'queryParam' },
    REQUEST_PARAM: { value: 43, label: 'requestParams' },
    RESPONSE_PARAM: { value: 44, label: 'responseParam' },

    PARAM_NAME: { value: 50, label: 'paramName' },
    PARAM_TYPE: { value: 51, label: 'type' },
    PARAM_REQUIRED: { value: 52, label: 'required' },
    PARAM_MAXLENGTH: { value: 53, label: 'maxLength' },
    PARAM_DESCRIPTION: { value: 54, label: 'description' },
    PARAM_EXAMPLE: { value: 55, label: 'example' }
  },
  DOC_STATUS: [
    { label: 'todo', value: 0, type: 'info' },
    { label: 'doing', value: 5, type: 'warning' },
    { label: 'done', value: 10, type: 'success' }
  ],
  ModuleConfig: {
    TORNA_PUSH_PRINT_CONTENT: 'torna.push.print-content',
    TORNA_PUSH_OVERRIDE: 'torna.push.override',
    TORNA_PUSH_DOC_DEFAULT_STATUS: 'torna.push.doc-default-status'
  },
  /**
   * 来源方式
   */
  MODIFY_SOURCE: {
    /**
     * 推送
     */
    PUSH: 0,
    /**
     * 表单编辑
     */
    FORM: 1,
    /**
     * 文本编辑,Markdown或富文本
     */
    TEXT: 2
  }
}
