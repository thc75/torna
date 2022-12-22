import { BaseTranslator } from '../base'

// Mapping
const MAPPING = {
  'changeHistory': 'Change History',
  'canUseScope': 'Use Scope',
  'dlgClose': 'Close',
  'preRequestScript': 'Pre-request Script',
  'afterResponseScript': 'After Response Script',
  'composeProject': 'Compose Project',
  'setting': 'Setting',
  'lockDoc': 'Lock Doc',
  'lockOn': 'Lock',
  'unlock': 'Unlock',
  'lockDocDesc': 'Locked doc can\'t be modified by push.',
  'nameValue': 'Name(Value)',
  'ok': 'OK',
  'cancel': 'Cancel',
  'home': 'Home',
  'adminManage': 'Backend Management',
  'previewModel': 'Preview Model',
  'helpCenter': 'Help Center',
  'help': 'Help',
  'userMsgReadAll': 'Set all read',
  'userMsgCenter': 'Message Center',
  'userMsgNoMsg': 'No Message',
  'viewDoc': 'View Doc',
  'setRead': 'Set read',
  'userCenter': 'User Center',
  'logout': 'Logout',
  'spaceList': 'Spaces',
  'createSpace': 'New Space',
  'isComposeSpace': 'Is Composed Space',
  'composeSpaceTip': 'Compose documents from other spaces',
  'creator': 'Creator',
  'createTime': 'Create Time',
  'spaceName': 'Space Name',
  'spaceAdmin': 'Space Admin',
  'dlgCancel': 'Cancel',
  'dlgSave': 'Save',
  'notEmpty': 'Can not empty',
  'createSuccess': 'Create Success',
  'filterWithUsername': 'Filter with nick/email',
  'selectUser': 'Please select user',
  'projectList': 'Projects',
  'spaceInfo': 'Space Info',
  'spaceMember': 'Members',
  'openUser': 'Open User',
  'apiDoc': 'API Doc',
  'projectInfo': 'Project Info',
  'projectMember': 'Project Member',
  'createProject': 'New Project',
  'noProject': 'No project',
  'privateProject': 'Private Project',
  'projectDesc': 'Description',
  'projectName': 'Project Name',
  'visitPermission': 'Visit Permission',
  'private': 'Private',
  'privateDesc': 'Private：Only project members can visit',
  'public': 'Public',
  'publicDesc': 'Public：All Space users can visit',
  'addSuccess': 'Create Success',
  'deleteSpace': 'Delete Space',
  'deleteSpaceConfirm': 'Delete this space?',
  'deleteSuccess': 'Delete Success',
  'tip': 'Tip',
  'loginAccount': 'Login Account',
  'password': 'Password',
  'passwordConfirm': 'Confirm password',
  'loginSubmit': 'Login',
  'search': 'Search',
  'addMember': 'Add Member',
  'member': 'Member',
  'me': 'Me',
  'role': 'Role',
  'joinTime': 'Join Time',
  'operation': 'Operation',
  'removeConfirm': 'Remove {0} ？',
  'remove': 'Remove',
  'addUser': 'Add User',
  'user': 'User',
  'pleaseSelect': 'Please select',
  'removeSuccess': 'Remove success',
  'updateSuccess': 'Update success',
  'visitor': 'Guest',
  'developer': 'Developer',
  'createAccount': 'New Account',
  'applicant': 'Applicant',
  'status': 'Status',
  'enable': 'Enable',
  'disable': 'Disable',
  'addTime': 'Add Time',
  'enableAccountConfirm': 'Enable this account？',
  'disableAccountConfirm': 'Disable this account？',
  'resetSecretConfirm': 'Reset secret？',
  'resetSecret': 'Reset Secret',
  'operateSuccess': 'Operate success',
  'notEmptyLengthLimit': 'Can not empty and max-length is {0}',
  'apiList': 'API List',
  'dictionaryManagement': 'Dictionary Management',
  'errorCodeManagement': 'Error Code Management',
  'moduleSetting': 'Module Setting',
  'shareManagement': 'Share Management',
  'createFolder': 'New Folder',
  'apiFilter': 'Filter with ID/Name/URL',
  'refreshTable': 'Refresh Table',
  'export': 'Export',
  'docName': 'Name',
  'hidden': 'Hidden',
  'modifierName': 'Modifier',
  'updateTime': 'Modify Time',
  'createDoc': 'New Doc',
  'preview': 'Preview',
  'update': 'Update',
  'more': 'More',
  'copy': 'Copy',
  'delete': 'Delete',
  'refreshSuccess': 'Refresh success',
  'inputFolderMsg': 'Folder name',
  'updateFolderTitle': 'Update Folder',
  'newFolderTitle': 'New Folder',
  'deleteConfirm': 'Delete {0} ？',
  'exportDoc': 'Export Doc',
  'exportAs': 'Export As',
  'singlePage': 'Single Page',
  'multiPage': 'Multi Pages',
  'fileType': 'File Type',
  'selectDoc': 'Choose Doc',
  'selectEnv': 'Select Environment',
  'allDocs': 'All Docs',
  'partDocs': 'Parts Docs',
  'dlgExport': 'Export',
  'pleaseCheckDoc': 'Please choose doc',
  'filterNameUrl': 'Filter with name/url',
  'appendShare': 'Append Share',
  'appendShareTip': 'Checked: New docs under this folder can be visited',
  'newDict': 'New Dictionary',
  'newItem': 'New Item',
  'name': 'Name',
  'type': 'Type',
  'value': 'Value',
  'description': 'Description',
  'categoryName': 'Category Name',
  'lengthLimit': 'Max length is {0}',
  'updateDictCategory': 'Update Dictionary Category',
  'updateDict': 'Update Dictionary',
  'moduleList': 'Application List',
  'newModule': 'New Application',
  'importSwaggerDoc': 'Import Swagger',
  'importPostmanDoc': 'Import Postman',
  'syncSwaggerDoc': 'Sync Swagger',
  'inputModuleName': 'Input Application Name',
  'appName': 'Application Name',
  'syncSuccess': 'Sync success',
  'importSwaggerPlaceholder': 'url, such as: http://xxx:8080/swagger/doc.json',
  'basicAuth': 'Basic Auth',
  'optionalUsername': 'Optional，username',
  'optionalPassword': 'Optional，password',
  'dlgImport': 'Import',
  'errorUrl': 'Error Url',
  'importSuccess': 'Import success',
  'chooseFile': 'Choose File',
  'importPostmanTip': 'Choose Postman export file(Collection v2.1)',
  'pleaseUploadFile': 'Please update file',
  'onlyChooseOneFile': 'Only one file',
  'deleteModule': 'Delete Application',
  'commonHeader': 'Common Headers',
  'commonRequest': 'Common Request',
  'commonResponse': 'Common Response',
  'debugEnv': 'Debug Environment',
  'swaggerSetting': 'Swagger Setting',
  'deleteModuleConfirm': 'Delete this Application?',
  'add': 'New',
  'errorParam': 'Error Content, support letter/numer/-/_',
  'newHeader': 'New Header',
  'updateHeader': 'Update Header',
  'paramName': 'Param Name',
  'example': 'Example',
  'addChildNode': 'Add Child Node',
  'isDataNode': 'Data Node',
  'addParam': 'New Param',
  'updateParam': 'Update Param',
  'addChildTitle': 'New Param - Child of {0}',
  'linkDict': 'Link Dict',
  'helpBook': 'Help Book',
  'addEnv': 'New Environment',
  'updateEnv': 'Update Environment',
  'envName': 'Environment Name',
  'baseUrl': 'Base Url',
  'envNamePlaceholder': 'Such as: Test Env',
  'baseUrlPlaceholder': 'Such as: http://10.0.1.31:8080',
  'whatsOpenApi': 'What\'s OpenAPI',
  'whatsOpenApiText': 'Request API to operate documents, third-party application can update documents by this way.',
  'useStep': 'Use steps',
  'useStep1': '1、Project depend SDK',
  'useStep2': '2、Config settings',
  'requestUrl': 'Url',
  'refreshTokenConfirm': 'Refresh token? Old token will not available',
  'refreshToken': 'Refresh Token',
  'openApiLink': 'OpenAPI Doc',
  'newShare': 'New Share',
  'shareUrl': 'Share Url',
  'shareDoc': 'Share Doc',
  'shareStyle': 'Share Style',
  'encryption': 'Encryption',
  'deleteRowConfirm': 'Delete record?',
  'remark': 'Remark',
  'optional': 'optional',
  'wholeModule': '(whole module)',
  'updateShare': 'Update Share',
  'look': 'See',
  'pwdShow': 'Password',
  'remarkShow': '[Remark]',
  'projectAdmin': 'Project Admin',
  'updateProject': 'Update Project',
  'deleteProject': 'Delete Project',
  'deleteProjectConfirm': 'Delete project?',
  'accountInfo': 'Account Info',
  'baseInfo': 'Base Info',
  'overview': 'Overview',
  'updatePassword': 'Update Password',
  'baseSetting': 'Base Setting',
  'subscription': 'Subscription',
  'subscribeApi': 'Subscribe API',
  'messageCenter': 'Message Center',
  'myMessage': 'My Messages',
  'nickname': 'Nickname',
  'email': 'Email',
  'regTime': 'Sign in time',
  'saveSuccess': 'Save success',
  'oldPassword': 'Old Password',
  'newPassword': 'New Password',
  'newPasswordConfirm': 'Confirm Password',
  'dlgUpdate': 'Update',
  'notSamePassword': 'Password not same',
  'updatePasswordSuccess': 'Update success，go login',
  'mySubscribeApi': 'My Subscription',
  'apiName': 'API Name',
  'cancelSubscribeConfirm': 'Unsubscribe {0} ？',
  'cancelSubscribe': 'Unsubscribe',
  'content': 'Content',
  'unread': 'Unread',
  'read': 'Read',
  'pushTime': 'Push Time',
  'addNewUser': 'New User',
  'superAdmin': 'Super Admin',
  'origin': 'Origin',
  'normal': 'OK',
  'inactive': 'Inactive',
  'resetPasswordConfirm': 'Reset {0} password？',
  'resetPassword': 'Reset Password',
  'disableConfirm': 'Disable {0} ?',
  'enableConfirm': 'Enable {0} ?',
  'suggestUseEmail': 'suggest email',
  'suggestUseRealName': '',
  'isSuperAdmin': 'Is super admin',
  'selfReg': 'Self sign in',
  'thirdPartyLogin': 'Third-party login',
  'backendAdd': 'Backend create',
  'unknown': 'Unknown',
  'resetPasswordSuccess': 'New Password：{0}',
  'resetSuccess': 'Reset success',
  'addUserSuccess': 'Login Account:{0}\nPassword:{1}',
  'docManagement': 'Doc Management',
  'userManagement': 'User Management',
  'docTitle': 'Title',
  'docDesc': 'Description',
  'pathVariable': 'PathVariable',
  'sourceFolder': 'Folder',
  'parentNode': 'Parent Node',
  'empty': 'Empty',
  'isShow': 'Show',
  'requestHeader': 'Header',
  'importHeader': 'Import Header',
  'useCommonHeader': 'Use Common Header',
  'requestParams': 'Request',
  'newQueryParam': 'New Query Parameter',
  'importQueryParam': 'Import Query Parameter',
  'newBodyParam': 'New Body Parameter',
  'importBodyParam': 'Import Body Parameter',
  'useCommonParam': 'Use Common Parameter',
  'isRootArray': 'Root Array',
  'responseParam': 'Response',
  'newResponseParam': 'New Response Parameter',
  'importResponseParam': 'Import Response Parameter',
  'useCommonResponse': 'Use Common Response',
  'errorCode': 'Error Code',
  'newErrorCode': 'New Error Code',
  'errorDesc': 'Error Description',
  'solution': 'Solution',
  'currentUpdateRemark': 'Update remark',
  'back': 'Back',
  'save': 'Save',
  'importJson': 'Import Json',
  'importByQueryParam': 'Query Parameter',
  'importByBulk': 'Bulk Mode（Postman Bulk Edit）',
  'importResponseParamTip': 'Enter the complete response results to fill in the test data. [Pay attention to sensitive information, do not import online data]',
  'pleaseInputJson': 'Enter json',
  'jsonType': 'Json Type',
  'require': 'Require',
  'required': 'Require',
  'maxLength': 'Max Length',
  'clickRestore': 'Restore',
  'noData': 'No data',
  'clickSubscribe': 'Click Subscribe',
  'exportMarkdown': 'As markdown',
  'exportHtml': 'As html',
  'exportWord': 'As word',
  'createdOn': 'Created on',
  'lastModifiedBy': 'last modified on',
  'noHeader': 'No header',
  'requestExample': 'Request Example',
  'responseExample': 'Response Example',
  'emptyErrorCode': 'No error code',
  'updateRemark': 'Update Remark',
  'subscribeSuccess': 'Subscribe success',
  'unsubscribeSuccess': 'Unsubscribe success',
  'apiInfo': 'API Info',
  'debugApi': 'Debug',
  'method': 'Method',
  'invokeParam': 'Invoke Parameter',
  'returnResult': 'Result',
  'proxyForward': 'Proxy Forward',
  'proxyForwardOn': 'Checked：Request by server proxy',
  'proxyForwardOff': 'Uncheck：Request by axios, need cors',
  'debugSend': 'Send',
  'noDebugEvnTip1': 'No debug environment, please go',
  'noDebugEvnTip2': 'to add.',
  'noDebugEvnTip3': 'No debug environment support',
  'referenceDoc': 'Reference Doc',
  'uploadMultiFiles': 'Upload Multi Files',
  'text': 'Text',
  'file': 'File',
  'sendErrorTip': 'Send failure, press F12 see Console',
  'parseError': 'Type convert error',
  'emptyParam': 'Empty param',
  'clickSee': 'Detail',
  'checkDict': 'Dictionary',
  'elType': 'Element Type:{0}',
  'no': 'No',
  'yes': 'Yes',
  'newConfig': 'New Config',
  'helpDoc': 'Help Doc',
  'copyCurrentConfig': 'Copy current config',
  'deleteConfigConfirm': 'Delete current config?',
  'mockUrl': 'Mock Url',
  'showAfterSave': 'Show after save',
  'param': 'Parameter',
  'response': 'Response',
  'responseDelay': 'Response Delay',
  'responseHeader': 'Response Header',
  'responseContent': 'Response Content',
  'mockScript': 'Mock Script',
  'run': 'Run',
  'baseOnMockjs': 'Base on mockjs',
  'runResult': 'Run result',
  'runSuccess': 'Run success',
  'runError': 'Run error',
  'noResultTip': 'No data return，miss return',
  'mockScriptError': 'Mock script error',
  'pleaseFinishForm': 'Please finish form',
  'maintainer': 'Maintainer',
  'managementModel': 'Management Model',
  'document': 'Document',
  'systemSetting': 'System Setting',
  'language': 'Language',
  'docTabView': 'Tabs View',
  'nickEmail': 'Nickname/Email',
  'visitStyle': 'Visit Style',
  'updateName': 'Update Name',
  'visitUrl': 'Visit Url',
  'composeSpace': 'Compose Space',
  'btnOk': ' OK ',
  'visitPassword': 'Password',
  'elementType': 'Element Type',
  'rootArrayTip': 'Body is an array, such as',
  'mustArray': 'Must array',
  'arrayMustHasElement': 'Array need element',
  'objectArrayReqTip': 'Request array, the element in below table',
  'objectArrayRespTip': 'Response array, the element in below table',
  'supportHtml': 'html supported',
  'newParam': 'New Param',
  'importParam': 'Import Param',
  'isPublic': 'Is Public',
  'debugEnvPublicTip': 'Public: debug url show in compose page',
  'orderIndex': 'Order',
  'expand': 'Expand',
  'collapse': 'Collapse',
  'script': 'Script',
  'preScriptTip': 'Modify request info before send request',
  'afterScriptTip': 'Modify response info after response',
  'userLogin': 'User Login',
  'signUp': 'Sign Up',
  'goLogin': 'Go Login',
  'forgetPwd': 'Forget Password',
  'plzInputLoginAccount': 'Required login account',
  'plzInputPassword': 'Required password',
  'askSuperAdminRestPwd': 'Ask super admin reset password',
  'accountLogin': 'Account Login',
  'ldapLogin': 'LDAP Login',
  'thirdpartyLogin': 'Third-party Login',
  'useSmartDoc': 'Use smart-doc push documents',
  'moreOperation': 'More Operations',
  'copySuccess': 'Copy Success',
  'deprecated': 'Deprecated',
  'closeOthers': 'Close Others',
  'closeAll': 'Close All',
  'closeLeft': 'Close Left',
  'closeRight': 'Close Right',
  'inputContent': 'Input content',
  'interface': 'Interface',
  'version': 'Version',
  'protocol': 'Protocol',
  'author': 'Author',
  'dependency': 'Dependency',
  'viewDict': 'View Dictionary',
  'dictInfo': 'Dictionary Information',
  'emptyDictData': 'Empty dictionary data',
  'grid': 'Grid',
  'card': 'Card',
  'constManager': 'Constant Management',
  'projectConstant': 'Project Constant',
  'applicationConstant': 'Application Constant',
  'viewConst': 'View Constant',
  'applicationManagement': 'Application Management',
  'selectAll': 'Select All',
  'recommend': 'Recommend',
  'standardDocument': 'Standard Document',
  'customDocument': 'Custom Document'

}

export class Translator extends BaseTranslator {
  getMapping() {
    return MAPPING
  }
  addMapping(anotherMapping) {
    Object.assign(this.getMapping(), anotherMapping)
  }
}

