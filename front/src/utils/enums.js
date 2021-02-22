export const Enums = {
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
    '*/*',
    'application/x-www-form-urlencoded',
    'multipart/form-data',
    'application/json',
    'application/xml',
    'text/plain'
  ],
  USER_STATUS: {
    SET_PASSWORD: 2
  }
}
