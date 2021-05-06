import { BaseTranslator } from '../base'

// Mapping
const MAPPING = {
  'ok': 'OK',
  'cancel': 'Cancel',
  'home': 'Home',
  'adminManage': 'Admin Management',
  'previewModel': 'Preview Model',
  'helpCenter': 'Help Center',
  'userMsgReadAll': 'Set all read',
  'userMsgCenter': 'Message Center',
  'userMsgNoMsg': 'No Message',
  'viewDoc': 'View Doc',
  'setRead': 'Set read',
  'userCenter': 'User Center',
  'logout': 'Logout',
  'spaceList': 'Space',
  'createSpace': 'Create Space',
  'creator': 'Creator',
  'createTime': 'Create Time',
  'spaceName': 'Space Name',
  'spaceAdmin': 'Space Administrator',
  'dlgCancel': 'Cancel',
  'dlgSave': 'Save',
  'canNotEmpty': 'Can not empty',
  'createSuccess': 'Create Success',
  'filterWithUsername': 'Filter with username',
  'selectUser': 'Please select user',
}

export class Translator extends BaseTranslator {
  getMapping() {
    return MAPPING
  }
}

