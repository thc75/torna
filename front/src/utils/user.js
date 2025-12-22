import { Enums } from './enums'

export function is_third_party_user(userInfo) {
  const sourceMap = Enums.SOURCE
  const source = userInfo.source
  return source === sourceMap.FORM || source === sourceMap.OAUTH
}

export function enable_mfa_auth(userInfo) {
  return userInfo && userInfo.mfaEnable
}
