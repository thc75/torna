import { BaseTranslator } from '../base'

// Mapping
const MAPPING = {
  'ok': '确定',
  'cancel': '取消',
  'home': '首页',
  'adminManage': '后台管理',
  'previewModel': '浏览模式',
  'helpCenter': '帮助中心',
  'userMsgReadAll': '全部标记已读',
  'userMsgCenter': '消息中心',
  'userMsgNoMsg': '暂没有新消息',
  'viewDoc': '查看文档',
  'setRead': '标记已读',
  'userCenter': '个人中心',
  'logout': '注销',
  'spaceList': '空间列表',
  'createSpace': '创建空间',
  'creator': '创建人',
  'createTime': '创建时间',
  'spaceName': '空间名称',
  'spaceAdmin': '空间管理员',
  'dlgCancel': '取 消',
  'dlgSave': '保 存',
  'canNotEmpty': '不能为空',
  'createSuccess': '创建成功',
  'filterWithUsername': '可根据登录账号筛选',
  'selectUser': '请选择用户',
}

export class Translator extends BaseTranslator {
  getMapping() {
    return MAPPING
  }
}

