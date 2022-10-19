/*!40101 SET NAMES utf8 */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`torna` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `torna`;

/*Table structure for table `compose_additional_page` */

DROP TABLE IF EXISTS `compose_additional_page`;

CREATE TABLE `compose_additional_page` (
                                           `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                           `project_id` bigint(20) unsigned NOT NULL COMMENT 'compose_project.id',
                                           `title` varchar(64) NOT NULL COMMENT '文档标题',
                                           `content` text COMMENT '文档内容',
                                           `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序值',
                                           `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1:启用，0：禁用',
                                           `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                           `gmt_create` datetime ,
                                           `gmt_modified` datetime  ,
                                           PRIMARY KEY (`id`) USING BTREE,
                                           KEY `idx_projectid` (`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='聚合文档附加页';

/*Data for the table `compose_additional_page` */

/*Table structure for table `compose_common_param` */

DROP TABLE IF EXISTS `compose_common_param`;

CREATE TABLE `compose_common_param` (
                                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                        `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，md5(doc_id:parent_id:style:name)',
                                        `name` varchar(64) NOT NULL DEFAULT '' COMMENT '字段名称',
                                        `type` varchar(64) NOT NULL DEFAULT 'String' COMMENT '字段类型',
                                        `required` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否必须，1：是，0：否',
                                        `max_length` varchar(64) NOT NULL DEFAULT '-' COMMENT '最大长度',
                                        `example` varchar(1024) NOT NULL DEFAULT '' COMMENT '示例值',
                                        `description` text NOT NULL COMMENT '描述',
                                        `enum_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'enum_info.id',
                                        `compose_project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'compose_project.id',
                                        `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                        `style` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0：path, 1：header， 2：请求参数，3：返回参数，4：错误码',
                                        `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                                        `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                        `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                        `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE KEY `uk_dataid` (`data_id`) USING BTREE,
                                        KEY `idx_compose_project_id` (`compose_project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='聚合文档公共参数';

/*Data for the table `compose_common_param` */

/*Table structure for table `compose_doc` */

DROP TABLE IF EXISTS `compose_doc`;

CREATE TABLE `compose_doc` (
                               `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                               `doc_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'doc_info.id',
                               `project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'compose_project.id',
                               `is_folder` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否文件夹',
                               `folder_name` varchar(64) NOT NULL DEFAULT '' COMMENT '文件夹名称',
                               `parent_id` bigint(20) NOT NULL DEFAULT '0',
                               `origin` varchar(128) NOT NULL DEFAULT '' COMMENT '文档来源',
                               `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                               `creator` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
                               `order_index` int(10) unsigned NOT NULL DEFAULT '0',
                               `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                               `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                               PRIMARY KEY (`id`) USING BTREE,
                               KEY `idx_projectid` (`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='文档引用';

/*Data for the table `compose_doc` */

/*Table structure for table `compose_project` */

DROP TABLE IF EXISTS `compose_project`;

CREATE TABLE `compose_project` (
                                   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                   `name` varchar(64) NOT NULL DEFAULT '' COMMENT '项目名称',
                                   `description` varchar(128) NOT NULL DEFAULT '' COMMENT '项目描述',
                                   `space_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '所属空间，space.id',
                                   `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '访问形式，1：公开，2：加密',
                                   `password` varchar(64) NOT NULL DEFAULT '' COMMENT '访问密码',
                                   `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建者userid',
                                   `creator_name` varchar(64) NOT NULL DEFAULT '',
                                   `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                   `modifier_name` varchar(64) NOT NULL DEFAULT '',
                                   `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                                   `show_debug` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否显示调试',
                                   `gateway_url` varchar(128) NOT NULL DEFAULT '' COMMENT '网关url',
                                   `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1：有效，0：无效',
                                   `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                   `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `idx_spaceid` (`space_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='组合项目表';

/*Data for the table `compose_project` */

/*Table structure for table `doc_info` */

DROP TABLE IF EXISTS `doc_info`;

CREATE TABLE `doc_info` (
                            `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                            `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，接口规则：md5(module_id:parent_id:url:http_method)。分类规则：md5(module_id:parent_id:name)',
                            `md5` varchar(32) NOT NULL DEFAULT '' COMMENT '文档内容的md5值',
                            `name` varchar(128) NOT NULL DEFAULT '' COMMENT '文档名称',
                            `description` text NOT NULL COMMENT '文档描述',
                            `author` varchar(64) NOT NULL DEFAULT '' COMMENT '维护人',
                            `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:http,1:dubbo',
                            `url` varchar(256) NOT NULL DEFAULT '' COMMENT '访问URL',
                            `http_method` varchar(12) NOT NULL DEFAULT '' COMMENT 'http方法',
                            `content_type` varchar(128) NOT NULL DEFAULT '' COMMENT 'contentType',
                            `deprecated` varchar(128) DEFAULT '$false$' COMMENT '废弃信息',
                            `is_folder` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否是分类，0：不是，1：是',
                            `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '父节点',
                            `module_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '模块id，module.id',
                            `is_use_global_headers` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否使用全局请求参数',
                            `is_use_global_params` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否使用全局请求参数',
                            `is_use_global_returns` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否使用全局返回参数',
                            `is_request_array` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否请求数组',
                            `is_response_array` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否返回数组',
                            `request_array_type` varchar(16) NOT NULL DEFAULT 'object' COMMENT '请求数组时元素类型',
                            `response_array_type` varchar(16) NOT NULL DEFAULT 'object' COMMENT '返回数组时元素类型',
                            `create_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
                            `modify_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
                            `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建人',
                            `creator_name` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者昵称user_info.nickname',
                            `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '修改人',
                            `modifier_name` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者昵称user_info.realname',
                            `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                            `remark` text COMMENT '备注',
                            `is_show` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否显示',
                            `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                            `is_locked` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否锁住',
                            `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                            `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE KEY `uk_data_id` (`data_id`) USING BTREE,
                            KEY `idx_moduleid` (`module_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='文档信息';

/*Data for the table `doc_info` */

insert  into `doc_info`(`id`,`data_id`,`md5`,`name`,`description`,`author`,`type`,`url`,`http_method`,`content_type`,`deprecated`,`is_folder`,`parent_id`,`module_id`,`is_use_global_headers`,`is_use_global_params`,`is_use_global_returns`,`is_request_array`,`is_response_array`,`request_array_type`,`response_array_type`,`create_mode`,`modify_mode`,`creator_id`,`creator_name`,`modifier_id`,`modifier_name`,`order_index`,`remark`,`is_show`,`is_deleted`,`is_locked`,`gmt_create`,`gmt_modified`) values (40,'0762df7ae4a3adbd6db1d3f7e066d0a4','','商城产品','<p><br></p>','',0,'','','','$false$',1,0,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:22','2022-08-02 18:07:18'),(41,'edc09831d127452509526348b17e245e','b75627f978dc6648d4919cc5f7b90b12','查询产品','<p><br></p>','',0,'/shop/product','GET','application/x-www-form-urlencoded','$false$',0,40,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:22','2022-08-02 18:07:18'),(42,'742a336acb1fd0f18574b4fd71b7e966','1b164a8d01428aceecd1b364fca5ecbc','修改产品','<p><br></p>','',0,'/shop/product','PUT','application/json','$false$',0,40,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:22','2022-08-02 18:07:18'),(43,'16efeddc8b66ba84435b4d527c833cd4','14165f091ae8f9955989a7852d18744d','新增产品','<p><br></p>','',0,'/shop/product','POST','application/json','$false$',0,40,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:22','2022-08-02 18:07:18'),(44,'593246d119b946366686ab99072d641a','','测试用例202111','<p><br></p>','',0,'','','','$false$',1,0,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',-1,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(45,'fb1e118d7be63ee40894cae7b185c0c5','988c505b72fe712c204e5b11925300ae','继承第三方类','<p><br></p>','',0,'/extendThirdClass','POST','application/json','$false$',0,44,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(46,'b2c6cf679409187bafda42d85f61eae1','9b5e47fc3e2cc355c2ff20401a6907d0','普通字符串','<p><br></p>','',0,'/string','GET','','$false$',0,44,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(47,'aa8ed5e969c75a035f904cb1c27d6608','ddec5709bd5f6d258730648465237fc7','参数内容很长','<p><br></p>','',0,'/longParam','GET','application/x-www-form-urlencoded','$false$',0,44,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(48,'d32477fda51550e0acb2ee16d684f773','9c6d56d9f39dc062b0fc5711288649d8','示例hutool JSONObject','<p><br></p>','',0,'/hutoolJSONObject','GET','application/x-www-form-urlencoded','$false$',0,44,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(49,'020595711e965df1917c9dfc063f307e','','测试用例','<p><br></p>','',0,'','','','$false$',1,0,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',-1,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(50,'54ef223cf9624b153956fc060cbb17d4','778e39add6225729aa2b07237c78c868','delete method with body','<p><br></p>','',0,'/2021111/delete','DELETE','application/x-www-form-urlencoded','$false$',0,49,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(51,'9e2fe0f861e4606412feb02d6b3611b5','688341faca98439f1e4eef59d41c495e','example2021922','<p><br></p>','',0,'/example2021922','POST','application/json','$false$',0,49,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(52,'a5aff543d0aba6028a956b6e392c0f55','56c45a940a046cfc10e9ee7fc550e806','泛型3','<p><br></p>','',0,'/test/item/{id}','GET','application/x-www-form-urlencoded','$false$',0,49,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(53,'ed8c1799f14160e49e7181778c2a2e56','5311b2a6d9a6ef0cc06f87f12d96ef76','泛型','<p><br></p>','',0,'/example72021923','GET','','$false$',0,49,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(54,'ed52f0e76673593ff1e2b80d9bb45d49','c45409553677090e985aeb36e23f562e','泛型2','<p><br></p>','',0,'/example6220219232','GET','','$false$',0,49,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(55,'bf21a13fa7964a457286e5852b1a7fe5','14ccd1140bd9a84b9abcdd5e466159c3','问号泛型','<p><br></p>','',0,'/test/generic/q','GET','application/x-www-form-urlencoded','$false$',0,49,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(56,'3de1d74e77f918bbd3e30a26dc5fa34f','','订单模块','<p><br></p>','',0,'','','','$false$',1,0,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(57,'aca639ea880bd2e8b90cb64fd979195a','9089347bdd4bd483d664aca280c67362','获取某个产品下的订单','<p><strong>注意：</strong>html演示。。</p>\n','',0,'/order/get2/{productNo}','GET','application/x-www-form-urlencoded','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(58,'376d45d2aa7f0451f59ccbc4ffa17c97','16982252ca137f60d0e4f92067c743fc','嵌套泛型，2层泛型','<p><br></p>','',0,'/order/moreGeneric','POST','application/json','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(59,'82085820d98bf1ea9af2e7aeb31f9eed','43c945adbc19c3896d95175baf69e686','只1层泛型','<p><br></p>','',0,'/order/moreGeneric2','POST','application/json','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(60,'f059106b7f7f4952e31648ced3ca8676','85e53af1000c94db3369d3fa5fce2716','没有泛型','<p><br></p>','',0,'/order/moreGeneric3','POST','application/json','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(61,'d9d558676742ec01828e0d4899a2fcb2','d1b2d0decb4058bc2c51495536a2ff6f','演示query参数,不加@RequestParam','<p>演示query参数,不加@RequestParam</p>\n','',0,'/order/get','GET','application/x-www-form-urlencoded','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(62,'7de95802c704f9e9c21f83085da55879','e391d3651dbc0be505541cb71404cc71','提交订单信息，application/json','<p>提交订单信息，application/json</p>\n','Tom',0,'/order/create','POST','application/json','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(63,'c17fd68995214a79ff9fb7548051a418','b43612e4e0b996fcb9dd7ad34db0fb65','演示path参数+query参数+body参数+header','<p>提交订单信息说明。。</p>\n','',0,'/order/submit/{productNo}','POST','application/json','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(64,'1e8636f22682ebf654592c9e13c583b4','084c8687764d38b0efc8ba97dc48432f','演示query参数,加@RequestParam','<p>演示query参数,加@RequestParam</p>\n','',0,'/order/get2','GET','application/x-www-form-urlencoded','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(65,'470dd942878f5bab1a5ad12aad8d4e5c','669466e302f4030b2531e3b9905ac08a','演示query参数,放在对象里面','<p><br></p>','',0,'/order/get4','GET','application/x-www-form-urlencoded','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(66,'98fe231300e6e6864c4a973fb7642a68','a42284159e9daa0108b1fc48393d8e4d','提交订单信息，application/x-www-form-urlencoded','<p>提交订单信息，application/x-www-form-urlencoded</p>\n','',0,'/order/create2','POST','application/x-www-form-urlencoded','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(67,'aee3ee906c576fe1c1b4a02713f5db5e','83ea9dd4adec0387aa63dc2523e53ee4','演示path参数','<p>演示path参数</p>\n','',0,'/order/get/{orderNo}','GET','application/x-www-form-urlencoded','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(68,'b8b62abb78b49b80e4df377488699d61','32bdc14ab3b6502e565a4bde680465a2','演示query参数,放在对象里面+外面一个','<p><br></p>','',0,'/order/get5','GET','application/x-www-form-urlencoded','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(69,'4992d04305f0874f0f37708629e5b4b9','0f93adc67bcc7771bf2cb1929d08cd4c','全部参数form，全部由@ApiImplicitParams实现','<p>全部由@ApiImplicitParams实现</p>\n','',0,'/order/submit3/{productNo}','POST','application/x-www-form-urlencoded','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(70,'451c2f27e852ebe50de4ef67aee8af95','32d200e5e13c2310644deff366fa1983','全部参数upload，全部由@ApiImplicitParams实现','<p>全部由@ApiImplicitParams实现</p>\n','',0,'/order/submit4/{productNo}','POST','multipart/form-data','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(71,'5d12627081670014e321c86b39a69fe1','9e278f9777880b6c6eb605e3c680b446','全部参数json，全部由@ApiImplicitParams实现','<p>全部由@ApiImplicitParams实现</p>\n','',0,'/order/submit2/{productNo}','POST','application/json','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(72,'0019cac17c98c23a1dbb176642993605','9344778e21d782ec69c08ecc34144324','返回树','<p><br></p>','',0,'/order/cycle2','GET','','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(73,'78b1174d012d2f3aa9e596ca71223b2a','1f7ff36fc81f84fcb26f7ed419e48775','循环属性','<p><br></p>','',0,'/order/cycle','GET','','$false$',0,56,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:23','2022-08-02 18:07:18'),(74,'02be37808d57b221dcfa25a31b759a33','82a08cef5045a07ab886b54da4f2fb1b','参数/返回都是List<OrderDTO>','<p>List方式</p>\n','',0,'/order/postArr4','POST','application/json','$false$',0,56,4,1,1,1,1,1,'object','object',1,1,99999,'Tom',99999,'Tom',100,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(75,'02caa73195d72baa6b5ce43cf9c473da','3bd0fdc03e30c7d3de1721da68b66932','参数/返回都是OrderDTO[]','<p>参数/返回都是OrderDTO[]</p>\n','',0,'/order/postArr','POST','application/json','$false$',0,56,4,1,1,1,1,1,'object','object',1,1,99999,'Tom',99999,'Tom',101,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(76,'6b3cc6a9b2be32fe3e1d3d27bdffa779','09ec797f5469b37733ecfd01a850230a','参数Integer[],返回OrderDTO[]','<p>参数Integer[],返回OrderDTO[]</p>\n','',0,'/order/postArr2','POST','application/json','$false$',0,56,4,1,1,1,1,1,'integer','object',1,1,99999,'Tom',99999,'Tom',102,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(77,'961c71f676583968440858586b70ad84','6895f74bd1b53cfc96a3beffdb729fc4','参数OrderDTO[],返回Integer[]','<p><br></p>','',0,'/order/postArr3','POST','application/json','$false$',0,56,4,1,1,1,1,1,'object','integer',1,1,99999,'Tom',99999,'Tom',103,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(78,'457778b845342f45f914a92dcbc9bb80','a01416eb95af4c2552f95b39f2656cc9','参数/返回都是List<Integer>','<p>List方式</p>\n','',0,'/order/postArr5','POST','application/json','$false$',0,56,4,1,1,1,1,1,'integer','integer',1,1,99999,'Tom',99999,'Tom',104,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(79,'de4e4c5c71d0dfb1974b09ad8fc90820','3d0d65236667c69de3ab6d90d5c4748d','参数/返回都是List<OrderDTO>2','<p>List方式</p>\n','',0,'/order/postArr6','POST','application/json','$false$',0,56,4,1,1,1,1,1,'object','object',1,1,99999,'Tom',99999,'Tom',105,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(80,'a5a2da7089fdb4f48044199b15d94613','','产品模块','<p><br></p>','',0,'','','','$false$',1,0,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',-1,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(81,'32090c2b9fc2c24ac3716330880177d3','06695b07d80aaee6eb9d9b5150c52939','返回树','<p><br></p>','',0,'/product/getTree','GET','','$false$',0,80,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(82,'f415ed3eb5ecc4ec028a7a9bb68807b8','6f312643ac4faaefa7ed6a808d8db7b2','获取产品','<p>获取产品说明。。</p>\n','Tom,Jim',0,'/product/get/{productNo}','GET','application/x-www-form-urlencoded','$false$',0,80,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(83,'f71959131c0d5123c030efffb02ba590','318f15e2dfd4adbad5fafc9a325c1c49','导出excel2','<p><br></p>','',0,'/exportExcel2','GET','application/x-www-form-urlencoded','$false$',0,80,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(84,'98debf34dd736b1bd1ccd7ae7cbcfb24','bcf59f70c3a7c6d7ae514d64c14893d3','导出zip','<p><br></p>','',0,'/exportZip','GET','','$false$',0,80,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(85,'00f3f43b7293208a1157a0c2a54c2c12','1fa082957e200dfd279c70fb761d3d3a','导出','<p><br></p>','',0,'/export','GET','','$false$',0,80,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(86,'8c9b1dcee1d2d46363cbc152d89a2fbc','a63c02296b43263aec562fc7195960f1','导出excel','<p><br></p>','',0,'/exportExcel','GET','','$false$',0,80,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(87,'4ec90f5e27c3fd03da4d1adf220c0f70','1b23d1d108f679b9af39991a238c0796','获取图片','<p>返回一个图片流，页面展示图片</p>\n','',0,'/getImage','GET','application/x-www-form-urlencoded','$false$',0,80,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(88,'366a646e90404243ffe12e1502794a7f','499bbfc6fb0d3a5e0376537263aeb46f','导出图片','<p><br></p>','',0,'/exportPng','GET','','$false$',0,80,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(89,'cf2d8816bcc110f486812833fd775115','6277b9d30a61ac058f2d77f179962270','上传文件','<p><br></p>','',0,'/product/upload','POST','multipart/form-data','$false$',0,80,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',11,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(90,'9d698e13de92b03b38dd7fcdd481a235','05c0cc077e006401e4103945dd98bbc6','上传文件2','<p><br></p>','',0,'/product/upload2','POST','multipart/form-data','$false$',0,80,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',12,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(91,'42dd5856602b0aed4c06c1e5d20c74bf','3caa2949447a922934b6b984dc63e142','上传文件3','<p><br></p>','',0,'/product/upload3','POST','multipart/form-data','$false$',0,80,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',13,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(92,'8c8cd8072318ca2bc842b5006e2f3a99','','登录模块','<p><br></p>','',0,'','','','$false$',1,0,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',2,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18'),(93,'03883fbd1846daa152a8e3fbcf06dc38','bd69f2a4bc68985fa922f6be0fa5c135','登录','<p>登录</p>\n','',0,'/login/login','POST','application/json','$false$',0,92,4,1,1,1,0,0,'object','object',1,1,99999,'Tom',99999,'Tom',0,'<p><br></p>',1,0,0,'2022-02-27 15:09:24','2022-08-02 18:07:18');

/*Table structure for table `doc_param` */

DROP TABLE IF EXISTS `doc_param`;

CREATE TABLE `doc_param` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                             `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，md5(doc_id:parent_id:style:name)',
                             `name` varchar(64) NOT NULL DEFAULT '' COMMENT '字段名称',
                             `type` varchar(64) NOT NULL DEFAULT 'String' COMMENT '字段类型',
                             `required` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否必须，1：是，0：否',
                             `max_length` varchar(64) NOT NULL DEFAULT '-' COMMENT '最大长度',
                             `example` varchar(1024) NOT NULL DEFAULT '' COMMENT '示例值',
                             `description` text NOT NULL COMMENT '描述',
                             `enum_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'enum_info.id',
                             `doc_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'doc_info.id',
                             `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                             `style` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0：path, 1：header， 2：body参数，3：返回参数，4：错误码, 5：query参数',
                             `create_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
                             `modify_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
                             `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                             `creator_name` varchar(64) NOT NULL DEFAULT '',
                             `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                             `modifier_name` varchar(64) NOT NULL DEFAULT '',
                             `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                             `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                             `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                             `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `uk_dataid` (`data_id`) USING BTREE,
                             KEY `idx_docid` (`doc_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=697 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='文档参数';

/*Data for the table `doc_param` */

insert  into `doc_param`(`id`,`data_id`,`name`,`type`,`required`,`max_length`,`example`,`description`,`enum_id`,`doc_id`,`parent_id`,`style`,`create_mode`,`modify_mode`,`creator_id`,`creator_name`,`modifier_id`,`modifier_name`,`order_index`,`is_deleted`,`gmt_create`,`gmt_modified`) values (271,'fc4cff99091ca5a949494d6e033c17a6','productNo','integer',1,'-','123','产品id',0,41,0,0,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(272,'e10383abfeae6c3cbedc3eb705e3637a','productNo','String',0,'-','aaaa','产品id',0,41,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(273,'18c23e88a161988e13e62c8c536c2d4e','remark','String',0,'-','小米','备注',0,41,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(274,'1daac5bf66901a9f14585683acc0c584','productDetailVO','object',0,'','','产品详情',0,41,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(275,'911229a606bdb7ec0f8c951856e638c1','productNo','String',0,'-','aaaa','产品id',0,41,274,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(276,'def964156597e8362ee4d43b0bbb8bdb','remark','String',0,'-','小米','备注',0,41,274,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(277,'733385eacca3d5e23f76bbc459a3b01f','productNo','String',0,'-','aaaa','产品id',0,42,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(278,'da3a5b78940e80e1670fcd8cf7f4f3de','remark','String',0,'-','小米','备注',0,42,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(279,'23b61dc8b4ba29ca25f17bdd466dbf42','productDetailVO','object',0,'','','产品详情',0,42,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(280,'8d7efeb0efef5dbfa862961c2d8fbb03','productNo','String',0,'-','aaaa','产品id',0,42,279,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(281,'ae1bc788266397d446b90b905179748b','remark','String',0,'-','小米','备注',0,42,279,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(282,'774e5db72e299bbce4359a3c8c28138a','productNo','String',0,'-','aaaa','产品id',0,42,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(283,'5bc223bce61b3397a728545a4fe94c7f','remark','String',0,'-','小米','备注',0,42,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(284,'6d81fe2271a37fe5e96045f149c61d71','productDetailVO','object',0,'','','产品详情',0,42,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(285,'932265373fe22aa7d5dd62d8f8720d3c','productNo','String',0,'-','aaaa','产品id',0,42,284,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(286,'472e4f6193b421e989978a61e0d50c8f','remark','String',0,'-','小米','备注',0,42,284,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(287,'2addbdc3cccf43b88ff3255069a17aef','productNo','String',0,'-','aaaa','产品id',0,43,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(288,'d3b3ba58ffe5da0cf32fb68e529328c4','remark','String',0,'-','小米','备注',0,43,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(289,'c9ace9ddd6660c1df0c86afaa5efe91a','productDetailVO','object',0,'','','产品详情',0,43,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(290,'cd9a1b544326eb0de9c9263fd3c141cc','productNo','String',0,'-','aaaa','产品id',0,43,289,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(291,'7c7d925ab4ae27bb5ac6fca49ca55e3e','remark','String',0,'-','小米','备注',0,43,289,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(292,'3c043a2c364d0ad4a1571c95e28a8d10','productNo','String',0,'-','aaaa','产品id',0,43,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(293,'c60f58b835c8cfa14e043ce53b57fe41','remark','String',0,'-','小米','备注',0,43,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(294,'6bc5cc7a49f27eb8801afff94819a278','productDetailVO','object',0,'','','产品详情',0,43,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(295,'238e6444466a7a38fe8fadd0e6d88ab7','productNo','String',0,'-','aaaa','产品id',0,43,294,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:22','2022-02-27 15:09:22'),(296,'105636eaefef923d824fe4041bc9ef87','remark','String',0,'-','小米','备注',0,43,294,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(297,'9383e1a4dc98e48ae351ae2d6d4ba1b8','remark','String',1,'-','','描述',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(298,'6a0dac468a11cb2d0a75e83c808ae593','version','String',1,'-','1.0','版本',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(299,'1a9f7d9a56e8ea19f7cacebc589eaff6','records','List',0,'-','','查询数据列表',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(300,'0f7efd85f242c9d0a22377fe18d78764','total','long',0,'-','100','总数',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(301,'9adce0909dda290bdef79adf10ef18df','size','long',0,'-','10','页数',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(302,'afe10b1415869a300ea63dc9c44273eb','current','long',0,'-','1','当前页',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(303,'ef1a515c7b7e652d170e53bea7785091','orders','List',0,'','','',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(304,'b630e3954b07bd1d2d64eb67e97c08f8','column','String',0,'-','','',0,45,303,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(305,'7504b0bdbcbb46f42640424338550b31','asc','boolean',0,'-','','',0,45,303,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(306,'2db3977081627efcc15c4c2c293a9b2e','optimizeCountSql','boolean',0,'-','','',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(307,'9c59aada9ef0bd3c669d1b024357d153','isSearchCount','boolean',0,'-','','',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(308,'de3062819af0a3e34fe3cc2f83b73bd8','hitCount','boolean',0,'-','','',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(309,'17bdf3fd35b0817607183d5842f662f0','countId','String',0,'-','','',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(310,'091bb08b24797f18f727bda1c4c6c6f5','maxLimit','Long',0,'-','','',0,45,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(311,'11db639fc5ac7fc560d13f689cf9f23e','code','Integer',0,'-','0','状态码，0：成功',0,45,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(312,'2c650f80dc6632d4fe95f1f1ceef8a01','data','object',0,'','','数据',0,45,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(313,'7d5d548f0671d12d0dcdc0ff6f651eef','userId','number',0,'-','123','用户id',0,45,312,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(314,'fc4fd9cd0172598d1003e58a5cdaa537','orderNo','String',0,'-','xxx','订单号',0,45,312,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(315,'aa8bd2792d4c7b2a0bfdd1039fedad5f','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,45,312,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(316,'ef565bf07cca615f3b497ffdd6761606','remark','String',0,'-','','',0,45,312,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(317,'d008976f613a5572fbec40964d4d421f','msg','String',0,'-','','消息',0,45,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(318,'7f91ef2d2afc26ff2ca3012200c25fd7','content','string',0,'-','','参数内容很长，<br>换行，参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长参数内容很长',0,47,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(319,'0505e099b617becd88c86cd537f93c03','code','Integer',0,'-','0','状态码，0：成功',0,47,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(320,'4acf4a41d02d193ae6003d532e03de1b','data','object',0,'','','数据',0,47,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(321,'e13ab0e1a2a4b1005827066d1c46870b','userId','number',0,'-','123','用户id',0,47,320,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(322,'476a5b0474903b572ef479951a5cb874','orderNo','String',0,'-','xxx','订单号',0,47,320,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(323,'133e0dce9ac75d61b92a13584cfaaf77','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,47,320,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(324,'2d5f7f25ef4070d34427666d5c32161f','remark','String',0,'-','','',0,47,320,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(325,'da92bac3409ded18e733a2bc9f0356ea','msg','String',0,'-','','消息',0,47,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(326,'1563abcde0f5b4b8ba2de6bc6f37c46e','type','String',0,'-','','',0,48,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(327,'51a388f1454725fe2d3b92d4c7d9e13d','title','String',0,'-','','',0,48,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(328,'76d4db1e4e675e264f863b75c1f6b2c1','param','object',0,'-','','',0,48,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(329,'5d835a4c9efac3de93b165d862b1e659','code','Integer',0,'-','0','状态码，0：成功',0,48,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(330,'a34da0d8cae00d7bedd8c9f91cb7fd77','data','object',0,'-','','数据',0,48,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(331,'aff90ff7378d21b760b6fe4771581eac','msg','String',0,'-','','消息',0,48,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(332,'8a31a56ab980cd52070b1ca3e758a4eb','code','Integer',0,'-','0','状态码，0：成功',0,50,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(333,'b00afe4251ece9a9abf529fc16738544','data','Object',0,'-','','数据',0,50,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(334,'71bf2b7799d8566addf1680a02649591','msg','String',0,'-','','消息',0,50,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(335,'1fa2da4b14e7fb252a1bab0b1ec9adbc','taskName','String',1,'-','','必填,任务名称',0,51,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(336,'facde3be2436cb977ba72cc35325342c','streamUrl','String',1,'-','','取流 url',0,51,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(337,'85bdc633c7f48cb88bc8f574df8e3baf','streamUserName','String',0,'-','','流地址用户名',0,51,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(338,'4f995e0f9cfa7880c6444e0a6802397d','streamPassword','String',0,'-','','取流密码',0,51,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(339,'49ef37c90edb3ed153897aceec5de5cb','taskType','enum',0,'-','PLAN','PLAN、TEMP',0,51,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(340,'44a92978e67ffbe54bedaa0c8d4fded4','exeTimeList','List',0,'','','taskType为计划任务-plan时的时间布控列表',0,51,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(341,'aa8210a3eb3607a56a541b56ee9a65af','timeRange','List',1,'','','计划日时间段列表',0,51,340,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(342,'3cc572281a2a8b746208efb5858ec8c5','startTime','String',0,'-','17:30:08','必填,布控开始时间,ISO8601_time 格式,只需要时间',0,51,341,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(343,'02d6142dce529b41f6410fa97ff13d7b','endTime','String',0,'-','17:30:08','必填,布控结束时间, ISO8601_time 格式',0,51,341,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(344,'4ad6e249cc51a15193e5dd4abe7d6440','this$0','object',0,'','','',0,51,340,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(345,'7f6817f461e32bfba0bb73ddd0d09629','taskName','String',1,'-','','必填,任务名称',0,51,344,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(346,'b453bdb20538dc1b57bf5558e4f12a23','streamUrl','String',1,'-','','取流 url',0,51,344,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(347,'48d4e489b60385a2f2327171bbbaf3be','streamUserName','String',0,'-','','流地址用户名',0,51,344,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(348,'cfc51f72063479d84c9604f0c110f20f','streamPassword','String',0,'-','','取流密码',0,51,344,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(349,'8d12fe1f4b62b017cd27d27a49c9dc40','taskType','enum',0,'-','PLAN','PLAN、TEMP',0,51,344,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(350,'f355fe89146edc4809045669c75b9299','exeTimeList','List',0,'-','','taskType为计划任务-plan时的时间布控列表',0,51,344,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(351,'8eab62b083c2fb6051122e8875a0d755','dataPullAdd','String',0,'-','','数据分析完成后的保存地址',0,51,344,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(352,'ccd8ab1473575f19f5015a48fde8526f','dataPullAdd','String',0,'-','','数据分析完成后的保存地址',0,51,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(353,'93eb2b324c181df9dc75ce1bce13e6e4','id','string',1,'-','','',0,52,0,0,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(354,'c0f68ddc15537f61229f649a35500aac','code','Integer',0,'-','0','状态码，0：成功',0,52,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(355,'66fdc564527e1d4d3c50829fb70b3747','data','object',0,'','','数据',0,52,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(356,'96353337ab70a5596e68be6cd806ddee','question','object',0,'','','问题',0,52,355,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(357,'cdc554f0f6380904cf717c450d30dce1','id','Long',0,'-','','问题id',0,52,356,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(358,'cf32de5da3fd31336f3111617f585f36','surveyId','Integer',0,'-','','问卷的id',0,52,356,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(359,'09f7e3a09bba291560f5868173e0f302','questionType','String',0,'-','','问题的类型（1是单选，2是多选，3是填空）',0,52,356,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(360,'3798c39454ee3f93d7011a57a05d4add','optionMin','Integer',0,'-','','当问题是多选时答案的数量，最少个数',0,52,356,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(361,'ace9afd79c115ba19e25860b52a91626','optionMax','Integer',0,'-','','当问题是多选时答案的数量，最多个数',0,52,356,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(362,'754ddf8a0df9c4d32260ca88f41148be','questionDesc','String',0,'-','','问题描述',0,52,356,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(363,'42aa98baac35a439d255b0536cfeb78a','sortNum','Integer',0,'-','','问题顺序号',0,52,356,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(364,'1ef5864d9c9683d2c6ddad96ed35f872','options','List',0,'','','问题所有的可选项',0,52,355,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(365,'62665ed969a10a571cc06e14d52f7ade','id','Long',0,'-','','选项的id',0,52,364,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(366,'d0b50233b78c5455d5a0fafbcefdc9c8','questionId','Long',0,'-','','选项对应问题的id',0,52,364,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(367,'d6475b0dda9f96760708f190dc8df14a','optionDesc','String',0,'-','','选项的描述',0,52,364,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(368,'7a01f0e847f7884132effe56b56dd1a6','optionImg','String',0,'-','','选项的图片(只能有1张)',0,52,364,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(369,'955ef40b423c737c8817035478cbac7a','optionInput','String',0,'-','','选项是否要求录入答案，0不可录入，1可以录入，2必须录入',0,52,364,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(370,'6cad2480b1ab430ec425ab78969ad112','sortNum','Integer',0,'-','','选项的顺序号',0,52,364,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(371,'36cb49281234e3c5ae9832606559ae1f','selectedCount','Integer',0,'-','','选择此项目的总人数',0,52,364,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(372,'f720f264e7b526002391986eaf0c2bc2','userSelected','boolean',0,'-','','当前用户是否选择了此答案',0,52,364,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(373,'d2e4d0e8fe69830e4269debabbe8f3c9','optionText','String',0,'-','','当前用户输入的文本答案',0,52,364,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(374,'607e8b399a67097ae372c851ee99bccf','optionText','String',0,'-','','当前用户输入的文本答案/只有填空题才有',0,52,355,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(375,'395e4f07e8938d2863495c3b8e74418f','msg','String',0,'-','','消息',0,52,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(376,'f2d987468ab3ad8045489e9fee90b8dc','values','List<List<String>>',0,'-','','坐标值',0,53,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(377,'476b0c03e135d9150cb5347806b3e825','x_aix','List',0,'-','','X名称',0,53,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(378,'5935691ff0097371163639c3fae4be66','y_aix','List',0,'-','','Y名称',0,53,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(379,'5a7be921b6dd351b33c261421c1259c3','id','String',0,'-','','id',0,54,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(380,'60db8b48f029bc4e042ec55bc723b42a','orderVOS','List<List<OrderVO>>',0,'-','','orders',0,54,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(381,'a873449c0f0dd42d15276154d5c05012','id','string',0,'-','','',0,55,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(382,'a256272e13dcfc818ec24f0f2dfa9fca','code','Integer',0,'-','0','状态码，0：成功',0,55,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(383,'5f725b24c6e20cae3db04593bc19d6f4','data','object',0,'-','','数据',0,55,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(384,'2fd159635e61081dbfd04299046c6eca','msg','String',0,'-','','消息',0,55,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(385,'8fe67ba663ec50bd5481c3a239cbb4fc','productNo','integer',1,'-','','',0,57,0,0,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(386,'5fe32abb6555b323cd3104844817a1a1','token','String',1,'-','','',0,57,0,1,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(387,'2a87ff8429a9559b69420f93b0331ee9','qid','string',1,'-','','',0,57,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(388,'8e3f190b648e81ed4f638cf9cf352c0e','id','Integer',1,'-','1','查询id',0,57,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(389,'c9633e657bab224e17aa7aededb52a37','orderNo','Integer',0,'-','xxx','订单id',0,57,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(390,'dd1e45596c46fbcbc4be4db8224a6f37','code','Integer',0,'-','0','状态码，0：成功',0,57,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(391,'15689ef71662bc44c9a4ef595ac5664f','data','array',0,'','','数据',0,57,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(392,'6e89ea7ae46c848a837f64489ea2fbef','userId','number',0,'-','123','用户id',0,57,391,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(393,'f8826064b462c4c4dc96cf116b349eaa','orderNo','String',0,'-','xxx','订单号',0,57,391,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(394,'444d3cd9ed456c737e4eaf6212b3725e','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,57,391,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(395,'64bbf35490adf70fa78d2d21cdbedb53','remark','String',0,'-','','',0,57,391,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(396,'5352c6cf1225795e200cff16bd598e34','msg','String',0,'-','','消息',0,57,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(397,'1a4a385f1f1ca4974997531b771082f8','pagger','object',0,'','','',0,58,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(398,'8fd01f1981ba3100ce80b56c60bf8cd9','records','List',0,'','','查询数据列表',0,58,397,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(399,'93e608519daa7240868a134389fce1df','productNo','String',0,'-','aaaa','产品id',0,58,398,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(400,'8200494d98bf42ba0303019e7572cde4','remark','String',0,'-','小米','备注',0,58,398,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(401,'89bdaf4b2825bd3a607297fa5177278c','productDetailVO','object',0,'','','产品详情',0,58,398,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(402,'e618389af96b1fb023c7baf3e06ef360','productNo','String',0,'-','aaaa','产品id',0,58,401,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(403,'b3058480b7662aa11ab2c785b2a245ac','remark','String',0,'-','小米','备注',0,58,401,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(404,'4fcd6f06f9b18db62c5d1b88f1192a05','total','long',0,'-','100','总数',0,58,397,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(405,'40a044952383999a1e8c56b7e2723449','size','long',0,'-','10','页数',0,58,397,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(406,'99c0bbe6de63c9c7fa03c082b7c5405b','current','long',0,'-','1','当前页',0,58,397,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(407,'84641fa89614fd63f066375181e0a4f2','optimizeCountSql','boolean',0,'-','','',0,58,397,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(408,'f9ecb5dc5a36de37f350d2db0af776e1','isSearchCount','boolean',0,'-','','',0,58,397,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(409,'860cdf145a8c19e79c683f221d84d799','hitCount','boolean',0,'-','','',0,58,397,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(410,'7fa47eff70f0e1a8a5579f7e679d6dc1','maxLimit','Long',0,'-','','',0,58,397,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(411,'c5d90dfdcaf13aa7c1f5bfaad1533476','condition','object',0,'','','',0,58,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(412,'86ede6e1d963c22e6e6b77e1636a7002','id','Integer',1,'-','1','查询id',0,58,411,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(413,'4a48b9b3265afbe550157b59c6e4db1c','orderNo','Integer',0,'-','xxx','订单id',0,58,411,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(414,'61ba97daa0e4ef23f00f4e050edc5a0e','code','Integer',0,'-','0','状态码，0：成功',0,58,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(415,'9c5ec06f8a5309e8bfb81ab253e42e41','data','object',0,'','','数据',0,58,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(416,'268ec8abb4ba7d59649e4808e14c6700','records','List',0,'','','查询数据列表',0,58,415,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(417,'9cec1e6a149c3c653e90e113cc7ad426','userId','number',0,'-','123','用户id',0,58,416,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(418,'ec1244d36a9399e0c21adcd20b239546','orderNo','String',0,'-','xxx','订单号',0,58,416,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(419,'4263c12f1af5b48aa2bde97b7097c81c','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,58,416,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(420,'2d35319e25409ba8bb14bf3f7b5e32bb','remark','String',0,'-','','',0,58,416,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(421,'9cf52e011c3bad2e9b0d3d21f02703ff','total','long',0,'-','100','总数',0,58,415,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(422,'4161b546ae6bf5aa732e0a91aab12313','size','long',0,'-','10','页数',0,58,415,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(423,'3a2b99c0e5c5cd5069fb05a4a3e90de1','current','long',0,'-','1','当前页',0,58,415,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(424,'6deb6113dbf812f84b9c3f30b534b5ba','optimizeCountSql','boolean',0,'-','','',0,58,415,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(425,'df1fe52ec57e800e20f3217756405211','isSearchCount','boolean',0,'-','','',0,58,415,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(426,'d4670a89fffecd79d172fd27e38065dd','hitCount','boolean',0,'-','','',0,58,415,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(427,'761f5b3f6ba79902373765697edbe146','maxLimit','Long',0,'-','','',0,58,415,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(428,'bf094560a3e64ada07d35097d2b1520a','msg','String',0,'-','','消息',0,58,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(429,'f3de15ada6c3e84826b8a9d49ebfe05a','records','List',0,'','','查询数据列表',0,59,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(430,'e5da56dae02fac7183db77c3b604db4e','productNo','String',0,'-','aaaa','产品id',0,59,429,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(431,'16afd7faed6074acaacac9cce155411a','remark','String',0,'-','小米','备注',0,59,429,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(432,'33434ecac5053c9ddcbf2a7303fcdbcd','productDetailVO','object',0,'','','产品详情',0,59,429,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(433,'faffc8e6907b9eec4b7e7d9962525aa7','productNo','String',0,'-','aaaa','产品id',0,59,432,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(434,'19fbb721ab52d8a7b057bb772f6d4dd1','remark','String',0,'-','小米','备注',0,59,432,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(435,'1d77bc761000c10bf3488f6800ef21ef','total','long',0,'-','100','总数',0,59,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(436,'a5f5de12c6b8ff07df708a7e92a5aa7e','size','long',0,'-','10','页数',0,59,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(437,'36540e825a4269d246ee8197b1eabd86','current','long',0,'-','1','当前页',0,59,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(438,'71883dbc32f8d642fd60028954bd0b5a','optimizeCountSql','boolean',0,'-','','',0,59,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(439,'8cd0921213e448db26e1066598ed3365','isSearchCount','boolean',0,'-','','',0,59,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(440,'81e11569887ed1ba5b6b451f6e4329ef','hitCount','boolean',0,'-','','',0,59,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(441,'ca0842737ffcff536ad646bbd7cc2881','maxLimit','Long',0,'-','','',0,59,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(442,'d9ebda1baa33de76fd2b00d563f7ea59','code','Integer',0,'-','0','状态码，0：成功',0,59,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(443,'a4c260403d7f3d3872c298594653f823','data','object',0,'','','数据',0,59,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(444,'f14808c806f3ef3b2820b9801d8230d1','userId','number',0,'-','123','用户id',0,59,443,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(445,'46cfff96c06b95023ef0f70a80fd80d0','orderNo','String',0,'-','xxx','订单号',0,59,443,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(446,'caf010ea4803ccacbc95990fcb687dad','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,59,443,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(447,'21c55f53f1a2ee768197892d15744e6c','remark','String',0,'-','','',0,59,443,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(448,'0e3263c38350a61450605a2322be9560','msg','String',0,'-','','消息',0,59,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(449,'a8d11c6a605589faec5968c9eb805cfa','productNo','String',0,'-','aaaa','产品id',0,60,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(450,'7ecc7388f7f17e76444fb1455cabd205','remark','String',0,'-','小米','备注',0,60,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(451,'d903b5669cd5970805b1c2cdde1fd367','productDetailVO','object',0,'','','产品详情',0,60,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(452,'cc33ca406a90a531dd83c27f4552dc64','productNo','String',0,'-','aaaa','产品id',0,60,451,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(453,'b27a2b099a0e05d5d295fa7bc62d26cf','remark','String',0,'-','小米','备注',0,60,451,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(454,'6f685f2495e2d442affdd16d01b30685','code','Integer',0,'-','0','状态码，0：成功',0,60,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(455,'07244d3ebc8a2f54d8b013804f6e8615','data','Object',0,'-','','数据',0,60,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(456,'b7b2ebe4b999af024cd49e987a8f879d','msg','String',0,'-','','消息',0,60,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(457,'55c3fb7416ccfa56f952661291e35b3c','orderNo','string',1,'-','xxx','订单id',0,61,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(458,'4df4814359aa843cc54d64185189a1d7','code','Integer',0,'-','0','状态码，0：成功',0,61,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(459,'3254213e545c1dbb1bdb16fdf3c40385','data','object',0,'','','数据',0,61,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(460,'0633389bd915b647c9c04fc269289ce9','userId','number',0,'-','123','用户id',0,61,459,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(461,'bcdb4fff7324c6f28ddcdff85bbe82ec','orderNo','String',0,'-','xxx','订单号',0,61,459,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(462,'ac341a8d5a14b7270c7d53cfd032e08c','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,61,459,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(463,'41aad8f4f48f8de48daf623217fc45f7','remark','String',0,'-','','',0,61,459,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(464,'6ae6d5d1d8e3eece5dc370121d925f0e','msg','String',0,'-','','消息',0,61,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(465,'75b73addd3f2864293c1571eb8c7b8fb','orderNo','String',0,'-','aaaa','订单id',0,62,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(466,'ed299d9177839091b36393b781c41bbc','remark','String',0,'-','订单','备注',0,62,0,2,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(467,'f6f71464fdf6de818d4285888ca6bb12','code','Integer',0,'-','0','状态码，0：成功',0,62,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(468,'3ec21f516fb1b623e9de6c223b342d83','data','object',0,'','','数据',0,62,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(469,'c85959e707342c4ee294aa324b912e81','orderNo','String',0,'-','aaaa','订单id',0,62,468,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(470,'beab5164e53d11813453896f1966eb89','remark','String',0,'-','订单','备注',0,62,468,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(471,'f42bf9fcf8f4ee588585731b4fbe3782','msg','String',0,'-','','消息',0,62,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(472,'288ba08607af915a52819338930acecb','100001','String',0,'-','','id错误',0,62,0,4,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(473,'7d580dc64be377746f223f28fc929cef','100002','String',0,'-','','错误描述2',0,62,0,4,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(474,'d519f7c58c045651433de4e912041169','100003','String',0,'-','','错误描述3',0,62,0,4,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(475,'3cad0ab48af1979ff352d8ed53109c3b','productNo','integer',1,'-','11','产品id',0,63,0,0,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(476,'f01ff9460532d0cfe9610025118e9143','token','String',1,'-','asdfe','token',0,63,0,1,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(477,'ceabe766b0c9fc67b19c79707d72d874','qid','string',1,'-','xx','查询id',0,63,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(478,'f4201e9746cd51dae2a6fa289e89fa3e','id','Integer',0,'-','123','id',0,63,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(479,'bbc20959c89560d7621bd2409073c317','orderNo','String',0,'-','aaaa','订单id',0,63,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(480,'976d4aa3c60367f0fc09563e30def828','code','Integer',0,'-','0','状态码，0：成功',0,63,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(481,'7da68668e7ad6f4ec03fd669c33265e0','data','object',0,'','','数据',0,63,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(482,'17b0104dbadca9ca97834e0d51e9443d','orderNo','String',0,'-','aaaa','订单id',0,63,481,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(483,'f88be63c0b18440da55c51bf771d6cbd','remark','String',0,'-','订单','备注',0,63,481,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(484,'de5098f20b175539e1caaed112db56d5','orderDetail','object',0,'','','订单详情',0,63,481,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(485,'b78454d4905fc873b46f74778038dba6','userId','number',0,'-','123','用户id',0,63,484,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(486,'93587d1417fa570a9a9430ad8e839f97','orderNo','String',0,'-','xxx','订单号',0,63,484,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(487,'7dd521617b28bd4333171c243debcb5c','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,63,484,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(488,'c2471ed9d53dc68e9087a20d36ea7c89','remark','String',0,'-','','',0,63,484,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(489,'a564877cc394b8cd7ca567878c3a9094','products','List',0,'','','产品详情',0,63,481,3,1,1,99999,'Tom',99999,'Tom',3,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(490,'0ef71d742f6e94d93cd645f13296c9e7','productNo','String',0,'-','aaaa','产品id',0,63,489,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(491,'48815f076790c183657e75ef05d4117b','remark','String',0,'-','小米','备注',0,63,489,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(492,'c992dda98dcf2b5ff28a7021632ad95b','productDetailVO','object',0,'','','产品详情',0,63,489,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(493,'b60d8e5b12b8b65206b92ca59f472511','productNo','String',0,'-','aaaa','产品id',0,63,492,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(494,'b9d304343aeb0e38638ad756d233d8b2','remark','String',0,'-','小米','备注',0,63,492,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(495,'f4cffca43fba3ae36090292ef95f027f','products2','List',0,'','','产品详情2',0,63,481,3,1,1,99999,'Tom',99999,'Tom',4,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(496,'0056f081b1f7975111e075489ad48c90','productNo','String',0,'-','aaaa','产品id',0,63,495,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(497,'c4dda06fdf732eb99dd683ea5a57cace','remark','String',0,'-','小米','备注',0,63,495,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(498,'177948386f569e43346b9061adac91ac','productDetailVO','object',0,'-','','产品详情',0,63,495,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(499,'fe09539d8e520db60f4700ff697f8f0f','products3','List',0,'','','产品详情3',0,63,481,3,1,1,99999,'Tom',99999,'Tom',5,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(500,'0fa7977198b9feafabe42361615fa229','productNo','String',0,'-','aaaa','产品id',0,63,499,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(501,'5b0f799187ef076a437af65fa70668fc','remark','String',0,'-','小米','备注',0,63,499,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(502,'796155f110a4ddb3f32a93b7774dce1e','productDetailVO','object',0,'-','','产品详情',0,63,499,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(503,'e4dde7283fb3c8d9137c37515d90a274','productArr','ProductVO[]',0,'','','产品详情arr',0,63,481,3,1,1,99999,'Tom',99999,'Tom',6,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(504,'3cfa4a544238c2dc23368fa8eb8b486b','productNo','String',0,'-','aaaa','产品id',0,63,503,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(505,'69575a23cf30ccd0db04665a41e26d47','remark','String',0,'-','小米','备注',0,63,503,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(506,'ed3b8b8f4c03236c384999543bf2cce7','productDetailVO','object',0,'-','','产品详情',0,63,503,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(507,'59e17875e45e196e3417bc4cc99bfba4','msg','String',0,'-','','消息',0,63,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(508,'37c46bcd88d0f6c35e6a4658249a4029','orderNo','string',1,'-','xxx','订单id',0,64,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(509,'17ad5222b0b522f43fbaca1c3e8a1adf','code','Integer',0,'-','0','状态码，0：成功',0,64,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(510,'033671a4c22013d7a628b5aa378f56e5','data','object',0,'','','数据',0,64,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(511,'9334cba0c59130cc2d4d85a577a95873','userId','number',0,'-','123','用户id',0,64,510,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(512,'6b2ee6ee96e6eb7b18fe2e841e1e2e35','orderNo','String',0,'-','xxx','订单号',0,64,510,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(513,'0bb05cb05aaf86282c5690ea8f97c34f','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,64,510,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(514,'470db38117a7fdf132984a048327ede2','remark','String',0,'-','','',0,64,510,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(515,'870da3309bedc54fb9cfb09776f6b9a3','msg','String',0,'-','','消息',0,64,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(516,'4a95b3462d5638b8ca470925c970815e','id','Integer',1,'-','1','查询id',0,65,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(517,'9a05476db6a9994603375360e0dd1e66','orderNo','Integer',0,'-','xxx','订单id',0,65,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(518,'1a23f5d24a1bd39d828054b40069e06e','code','Integer',0,'-','0','状态码，0：成功',0,65,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(519,'c05bd6b39f3db0ac5949d81ee7df282f','data','object',0,'','','数据',0,65,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(520,'de9711a34cf449fdddaccd0a428adf57','userId','number',0,'-','123','用户id',0,65,519,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(521,'0cc7d8cefd1ea9ccb6c2fae9afe37762','orderNo','String',0,'-','xxx','订单号',0,65,519,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(522,'55f9568d14bd084b544bc31255c939ba','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,65,519,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(523,'e6f4a3ced8aed7af6ed501c987e660e0','remark','String',0,'-','','',0,65,519,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(524,'548ccb811dc80e238a21ad2d030e0f4e','msg','String',0,'-','','消息',0,65,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(525,'342d73b8c2df44584fa86f6f1cb89432','orderNo','String',0,'-','aaaa','订单id',0,66,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(526,'210ec971c61126b18dc36ab82172b27f','remark','String',0,'-','订单','备注',0,66,0,2,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(527,'d593314b8b3c4b21ee768cf90b99fb58','code','Integer',0,'-','0','状态码，0：成功',0,66,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(528,'5fcbf6084bbeaff956ab864aafb1bef3','data','object',0,'','','数据',0,66,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(529,'da0a0c198e92110f6e62afc88bcb7e7b','orderNo','String',0,'-','aaaa','订单id',0,66,528,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(530,'0d98810691f267cd550427e036b493b3','remark','String',0,'-','订单','备注',0,66,528,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(531,'1cd70eeaa781f16d84c52892b1ec4f2a','msg','String',0,'-','','消息',0,66,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(532,'7ae047690a840884642b813e0cd1b05e','orderNo','string',1,'-','xxx','订单id',0,67,0,0,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(533,'4df7fb01e005f6ad10b9495fab6480f0','code','Integer',0,'-','0','状态码，0：成功',0,67,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(534,'8be19b06d35b4922df2215d182a27eb4','data','object',0,'','','数据',0,67,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(535,'6e8122d8dd72d6f0caef1f0058273ab3','userId','number',0,'-','123','用户id',0,67,534,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(536,'31940e0eabe9a2df2d50609e4bde274d','orderNo','String',0,'-','xxx','订单号',0,67,534,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(537,'6d108520819df5ab5eafd1d083bd2372','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,67,534,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(538,'609d7ac9c782dfef6c60405253c22570','remark','String',0,'-','','',0,67,534,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(539,'1ab3bde4827c4569dd6da39517dafe96','msg','String',0,'-','','消息',0,67,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(540,'1e9a4871c5cb64070b29fa184cf4291c','id','Integer',1,'-','1','查询id',0,68,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(541,'4775a1027ec9007467dfc9d4df931149','orderNo','Integer',0,'-','xxx','订单id',0,68,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(542,'edd6a3dc6f7c5a22dae399d2d42b650b','pid','string',0,'-','111','产品id，外面的',0,68,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(543,'34a6da7fbd078e455642498955144e0f','code','Integer',0,'-','0','状态码，0：成功',0,68,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(544,'2180b02ec88ef76ca2972a8597878990','data','object',0,'','','数据',0,68,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(545,'2191c459d35a2731fcd5dbfffa6c2534','userId','number',0,'-','123','用户id',0,68,544,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(546,'cee2610e08f8ccfb96e2687f6b98c05f','orderNo','String',0,'-','xxx','订单号',0,68,544,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(547,'9df405a73364988dfdc6aefc60a479d4','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,68,544,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(548,'b39b17fcadabd46f18591d312758950d','remark','String',0,'-','','',0,68,544,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(549,'25b5a818d28763601593a2657b8c9286','msg','String',0,'-','','消息',0,68,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(550,'f1fb0c11a87afc1476d644a62dc8a95d','productNo','integer',1,'-','11','产品id',0,69,0,0,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(551,'4bf0fe62fce6dec53abdbe1aec44648e','token','String',1,'-','asdfe','token',0,69,0,1,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(552,'8355346d115b830ad39919ffa4786bbe','qid','string',1,'-','xx','查询id',0,69,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(553,'0c51908286b882d68d413f58b3ea4c25','id','Integer',0,'-','123','id',0,69,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(554,'39fbf1f04ea5e3ab72413224befbad34','orderNo','String',0,'-','aaaa','订单id',0,69,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(557,'c22d439dcb6858489fee71a81def6d67','code','Integer',0,'-','0','状态码，0：成功',0,69,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(558,'11a3fbe1040dd5713cf3db20fe24320c','data','Object',0,'-','','数据',0,69,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(559,'23671b5e7eaacf6e20efbc70dd47b295','msg','String',0,'-','','消息',0,69,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(560,'54dc55cdd3ccd2a947abbd7838b0e9a3','productNo','integer',1,'-','11','产品id',0,70,0,0,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(561,'8fed34bae052d0dbdd66d0c3540fbe1e','token','String',1,'-','asdfe','token',0,70,0,1,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(562,'0d656f9e3d76ff133524969bce910adc','qid','string',1,'-','xx','查询id',0,70,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(563,'d4175253f43895d7a376a81232af1c2d','file','file',1,'-','','文件',0,70,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(564,'1354c255bb4977853ea4798c8b5d95f5','code','Integer',0,'-','0','状态码，0：成功',0,70,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(565,'1dc3e6ac78882c41bcec764f34630d6b','data','Object',0,'-','','数据',0,70,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(566,'128bbe51fa8b443eeec04eab6bf2ddca','msg','String',0,'-','','消息',0,70,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(567,'37f2a8ef0c290eb05e8eae86f5733022','productNo','integer',1,'-','11','产品id',0,71,0,0,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(568,'316e8e7147429b2d49870a0ac57d2668','token','String',1,'-','asdfe','token',0,71,0,1,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(569,'a5f31711ab5daa21fb5d390a7d0c7efc','qid','string',1,'-','xx','查询id',0,71,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(570,'d280905cc1bac8b51e48146e0c09be0c','id','Integer',0,'-','123','id',0,71,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(571,'e98c5bd85fcf14a5572c10806316def1','orderNo','String',0,'-','aaaa','订单id',0,71,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(574,'7e4dcfed3321604ddb154ebd25ca2f5b','code','Integer',0,'-','0','状态码，0：成功',0,71,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(575,'e1896246b9065ba9fb52beb8a87e4443','data','object',0,'','','数据',0,71,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(576,'6fa4ac45592367612d74fa4ff0832c2f','orderNo','String',0,'-','aaaa','订单id',0,71,575,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(577,'1b65e337d0f89798e0dfcd596a6ad330','remark','String',0,'-','订单','备注',0,71,575,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(578,'1cf72e7793fad4cfc212ae5e4ffed06e','orderDetail','object',0,'','','订单详情',0,71,575,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(579,'465cf4115ac5f352df332e7bc03cbdb9','userId','number',0,'-','123','用户id',0,71,578,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(580,'774831685377127e2afd000031650738','orderNo','String',0,'-','xxx','订单号',0,71,578,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(581,'01f661ffc66227005fa84f4036cba34c','payTime','Date',0,'-','2021-05-24 19:59:51','订单id',0,71,578,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(582,'6e5dc340d5e6c94afc60bbe94e31a57a','remark','String',0,'-','','',0,71,578,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(583,'61c7046344ce9643727c3e8da11c4708','products','List',0,'','','产品详情',0,71,575,3,1,1,99999,'Tom',99999,'Tom',3,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(584,'62e57e0c0bd010fc319c53f843cf1e68','productNo','String',0,'-','aaaa','产品id',0,71,583,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(585,'7e93830da8903d003acb617c2011e9b7','remark','String',0,'-','小米','备注',0,71,583,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(586,'4a33e9b48efb834a9197fab6f93877cf','productDetailVO','object',0,'','','产品详情',0,71,583,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(587,'49df17749f7b17a0ce16177924cea846','productNo','String',0,'-','aaaa','产品id',0,71,586,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(588,'466d5f73d7640bb1f4f149444c93841e','remark','String',0,'-','小米','备注',0,71,586,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(589,'1e13e115431ef58d0198bcc41d0bb78f','products2','List',0,'','','产品详情2',0,71,575,3,1,1,99999,'Tom',99999,'Tom',4,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(590,'209d07af1d6dd388c322207776b848c1','productNo','String',0,'-','aaaa','产品id',0,71,589,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(591,'29ace4a898dff883249e19e43f4a1244','remark','String',0,'-','小米','备注',0,71,589,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(592,'0289b1669635b5d42d60dca767afd253','productDetailVO','object',0,'-','','产品详情',0,71,589,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(593,'fe1c4856a4711aef88a2013689208e05','products3','List',0,'','','产品详情3',0,71,575,3,1,1,99999,'Tom',99999,'Tom',5,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(594,'c9b9ee1cc7a5dd0a34163f85760c215a','productNo','String',0,'-','aaaa','产品id',0,71,593,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(595,'f8b9d02ee7197805b52c33ca3c0bf28c','remark','String',0,'-','小米','备注',0,71,593,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(596,'159d6a90515908f6a7ce912f242d0f72','productDetailVO','object',0,'-','','产品详情',0,71,593,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(597,'7f0e15ad55dfe5f69b3f8a3c03b899ce','productArr','ProductVO[]',0,'','','产品详情arr',0,71,575,3,1,1,99999,'Tom',99999,'Tom',6,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(598,'0cb15cd261a1e16e271fcd81da07a120','productNo','String',0,'-','aaaa','产品id',0,71,597,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(599,'18035ae37f1c0a994ac39383b404c90e','remark','String',0,'-','小米','备注',0,71,597,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(600,'7a84a63a69d5db2b27829a434a536856','productDetailVO','object',0,'-','','产品详情',0,71,597,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(601,'246baa01907332561b2fe960d3c34ba7','msg','String',0,'-','','消息',0,71,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(602,'104dca71e9cd944a5349192ece3e312a','code','Integer',0,'-','0','状态码，0：成功',0,72,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(603,'21f5838e13e24ce308ee9ce0a7fa7d2b','data','object',0,'','','数据',0,72,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(604,'b9309598175b91c540f76f82ea48c2db','id','Integer',0,'-','','',0,72,603,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(605,'48c0a9f4fbb1f180deb1669a7e438c4c','name','String',0,'-','','名称',0,72,603,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(606,'c5a90c65e650196bc3d38c2914c23b1b','parentId','Integer',0,'-','','父id',0,72,603,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(607,'7040cfaadd77f540db99037bf97b527a','children','List',0,'','','子节点',0,72,603,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(608,'6205a80a8dce923e76ca8d59d373d19e','id','Integer',0,'-','','',0,72,607,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(609,'8b5676813e3c30421ec5b8721530dda1','name','String',0,'-','','名称',0,72,607,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(610,'522e32b0ee9d491b1c03a398a7f98607','parentId','Integer',0,'-','','父id',0,72,607,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(611,'96b952f24c14cb097aca48cdaed7024c','children','List',0,'-','','子节点',0,72,607,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(612,'c52d939fd9f99660574743c949e4c2f3','msg','String',0,'-','','消息',0,72,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(613,'6aa4103aaff2c561d190e0c8ba7047bc','orderNo','String',0,'-','bbbb','订单id',0,73,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(614,'ed799dc71959d33564ff2e4d54647808','cycleProduct','object',0,'','','产品详情',0,73,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(615,'00755785429e574c7facd1b88adf046f','productNo','String',0,'-','aaaa','产品id',0,73,614,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(616,'c4d40198d54f3f9112ba4c28004602c7','cycleOrder','object',0,'','','订单详情',0,73,614,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(617,'5ccd8788262ee971ad119feb49435320','orderNo','String',0,'-','bbbb','订单id',0,73,616,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(618,'8ae1e7731304fa84b6f5422848ca50e0','cycleProduct','object',0,'-','','产品详情',0,73,616,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(619,'80ca989a36fe1947887c5d2b4a6a037d','cycleProductB','object',0,'','','产品详情B',0,73,616,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(620,'fd6a0e7d47a02ac070906b80b5c9ee64','productNo','String',0,'-','aaaa','产品id',0,73,619,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(621,'375fc96f198a3aadaab1cf7be48648e0','cycleOrder','object',0,'-','','订单详情',0,73,619,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(622,'dbb7f0c02f7c733680d8a9030709ad8a','cycleProductB','object',0,'','','产品详情B',0,73,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(623,'49f25e4042d60ab92cbb5170667ec4b4','productNo','String',0,'-','aaaa','产品id',0,73,622,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(624,'097d6eb410f29b9cfcdbad55cd7a9b5e','cycleOrder','object',0,'','','订单详情',0,73,622,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(625,'11cf4602d407c8b840b55ce4d57d7570','orderNo','String',0,'-','bbbb','订单id',0,73,624,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(626,'2265aa5e79c043d700a7b2811aa6de75','cycleProduct','object',0,'','','产品详情',0,73,624,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(627,'267df12faf3c76053f058ac501fccb7a','productNo','String',0,'-','aaaa','产品id',0,73,626,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(628,'5407e5d06e1c3564d47a36e7652c793d','cycleOrder','object',0,'-','','订单详情',0,73,626,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(629,'c02ce9b0279711149c4e73e744b7b94a','cycleProductB','object',0,'-','','产品详情B',0,73,624,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(630,'4b1fe836103c50c949a7682f5fa9c69e','orderNo','String',0,'-','aaaa','订单id',0,74,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(631,'fd1b1c2514b4660d55bbebc2b246dd6d','remark','String',0,'-','订单','备注',0,74,0,2,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(632,'a32db487b1723ef4b6d9774e7299101a','orderNo','String',0,'-','aaaa','订单id',0,74,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(633,'b7647ed1200915ebdbb2113bb336f281','remark','String',0,'-','订单','备注',0,74,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(634,'49f331dacf23f4e33a33b72b20e792be','orderNo','String',0,'-','aaaa','订单id',0,75,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(635,'80db6b80de1dddf3b44755aad57ddd1c','remark','String',0,'-','订单','备注',0,75,0,2,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(636,'4acc4bdd1a5e6e197ab7bf4d94015cb2','orderNo','String',0,'-','aaaa','订单id',0,75,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(637,'9c46558df8cb920f8d2d296cab4b63e1','remark','String',0,'-','订单','备注',0,75,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(638,'c9ef4f3d768de98b7d9d2a0a40959dc5','','integer',0,'-','1,2,3','订单id',0,76,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(639,'88f204bff5ea216de46f2c434232db7d','orderNo','String',0,'-','aaaa','订单id',0,76,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(640,'5d3265d4d49a22a4d1c294448066fef4','remark','String',0,'-','订单','备注',0,76,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(641,'bb163b1c0d9e1b78a58f8c18527bab18','orderNo','String',0,'-','aaaa','订单id',0,77,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(642,'dfb55d77329e08d900fbf4ea4943aa4d','remark','String',0,'-','订单','备注',0,77,0,2,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(643,'80a49b87d0ca09f135fa37ee5f0da18f','','integer',0,'-','1,2,3','订单id',0,77,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(644,'8d1ce6b0ac1d6b570a69161a205d5aa3','','integer',0,'-','1,2,3','订单id',0,78,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(645,'96ccd4755b5624a0e4bda68d6dbe1403','','integer',0,'-','4,5,6','返回订单id',0,78,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(646,'8e94984adc20cda2a781aa09429e2690','id','string',1,'-','xxx','id',0,79,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(647,'d465a13f7ad24bc64b9ac8b5d65467d7','orderNo','String',0,'-','aaaa','订单id',0,79,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(648,'f00933a9801f259bf00935d71ab0fa71','remark','String',0,'-','订单','备注',0,79,0,2,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(649,'04fd8db276069171155fb00a1edbc072','orderNo','String',0,'-','aaaa','订单id',0,79,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(650,'378c9b572b239db7360bec34f4ca88e9','remark','String',0,'-','订单','备注',0,79,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(651,'17a33b76c03267330d189dba29a5ff22','code','Integer',0,'-','0','状态码，0：成功',0,81,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(652,'3f16107cfc3ba23738c2ab9c599ab8a1','data','object',0,'','','数据',0,81,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(653,'fa82230c6371549369f01ad5fc1100e9','id','Integer',0,'-','','',0,81,652,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(654,'5506349ba3fa12e68d8f2e1a085cf6c5','name','String',0,'-','','名称',0,81,652,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(655,'7284b6100f2d6e05579d3b98af09bb7f','parentId','Integer',0,'-','','父id',0,81,652,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(656,'5940e3632ba01baf4e591910bb0103da','children','List',0,'','','子节点',0,81,652,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(657,'0f9a1553d382da0ae09e2d389bdfced7','id','Integer',0,'-','','',0,81,656,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(658,'164af4773aa93ab8b04cff716175b15a','name','String',0,'-','','名称',0,81,656,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(659,'e1d0e2edc428cb437db76ce32ab9b045','parentId','Integer',0,'-','','父id',0,81,656,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(660,'4df47e830e9552e927b0c1ca1d7b15a2','children','List',0,'-','','子节点',0,81,656,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(661,'db8cf23e44c6ea95483644d0803ba872','msg','String',0,'-','','消息',0,81,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(662,'7d9cbb5110bb583ec0a1f5addec1d379','productNo','integer',1,'-','123','产品id',0,82,0,0,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(663,'542ac6c4c833e79aebe857f2071a8e03','productNo','String',0,'-','aaaa','产品id',0,82,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(664,'75634022f52753a4ef3aca0b0bcf3c9b','remark','String',0,'-','小米','备注',0,82,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(665,'589d2af083f9b498d30816a51ae1c1e0','productDetailVO','object',0,'','','产品详情',0,82,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(666,'5b22d001724e814fe57c5955fbde58fe','productNo','String',0,'-','aaaa','产品id',0,82,665,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(667,'c558d82fd166fdb56e8bb6bafc72a02c','remark','String',0,'-','小米','备注',0,82,665,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(668,'2f1ba82dd979f45e2c383e22806dbb76','100001','String',0,'-','','id错误',0,82,0,4,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(669,'4bb3d4e75f58136f9375c03ba461c415','100002','String',0,'-','','错误描述2',0,82,0,4,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(670,'ca29c19d3db5e3b4412790997a144139','100003','String',0,'-','','错误描述3',0,82,0,4,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(671,'e8802fa3b3b58fabe19ab00697aa5626','type','string',1,'-','png','文件类型，png/jpeg',0,87,0,5,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(672,'bad7a881c73114a5175efec9ef400a33','file','file',1,'-','','产品模板文件',0,89,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(673,'bff3784c181139dc20a99ac814a99697','productNo','string',1,'-','111','产品id',0,89,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(674,'bf3192d191fd2768171d20ca709f09bc','code','Integer',0,'-','0','状态码，0：成功',0,89,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(675,'7c40a853270acbf6239477faaf23744d','data','Object',0,'-','','数据',0,89,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(676,'daf2e533ce23c4bac8429fe4c1d36ee1','msg','String',0,'-','','消息',0,89,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(677,'16c9ed5346b9c587f7ba97fcc4a783a3','file','MultipartFile',0,'-','','产品模板文件',0,90,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(678,'91bc05e8317d4ae8c22b305c2d0bf47e','files','MultipartFile[]',0,'-','','产品模板文件Array',0,90,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(679,'6db614ea5e3bfd3541ce45af6d74fcbb','fileList','List',0,'-','','产品模板文件List',0,90,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(680,'449c640e5c145bc5b28630bb23311f33','productNo','String',1,'-','111','产品id',0,90,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(681,'d90e9f88c27f62e7145a17580a25b27d','code','Integer',0,'-','0','状态码，0：成功',0,90,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(682,'6c2406d812d2d4fa433127b9beb6221d','data','Object',0,'-','','数据',0,90,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(683,'ac7b865f2fd98ee152b99b0a005b51f0','msg','String',0,'-','','消息',0,90,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(684,'0e2c396c09b4a1dc4d31df1ab00da60f','files','file[]',1,'-','','产品模板文件',0,91,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(685,'b71dfcd55d824deeeafc511ac3962fd2','productNo','string',1,'-','111','产品id',0,91,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(686,'e67eede9884e536f5297ba59f4a47780','code','Integer',0,'-','0','状态码，0：成功',0,91,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(687,'bf184588e7943e46e971c4b97337bc1a','data','Object',0,'-','','数据',0,91,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(688,'833862fb777c64ac881f70f37a5b0b56','msg','String',0,'-','','消息',0,91,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(689,'5b76345fda517f07c18a2c01fabdf2ab','username','String',1,'-','jim','用户名',0,93,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(690,'bf7876d98d2c1f3df6b5d4d8f1048d19','password','String',1,'-','123','密码',0,93,0,2,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(691,'3b5657fc11215e00714d321379cf9739','code','Integer',0,'-','0','状态码，0：成功',0,93,0,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(692,'19cfb8a2e10d23e22a518b6b7ee7cad3','data','object',0,'','','数据',0,93,0,3,1,1,99999,'Tom',99999,'Tom',1,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(693,'03376025094d959f3dc3c8e7de64c3cb','userId','Integer',1,'-','11','用户id',0,93,692,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(694,'324bce9151dfd7f02d5c59016bfa8cd8','username','String',1,'-','jim','用户名',0,93,692,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(695,'8bf3549e0a98b0d69c7fb3063f95e6ec','token','String',1,'-','xx','token',0,93,692,3,1,1,99999,'Tom',99999,'Tom',0,0,'2022-02-27 15:09:24','2022-02-27 15:09:24'),(696,'fc598e914f59f9f387ce8477daf2bf71','msg','String',0,'-','','消息',0,93,0,3,1,1,99999,'Tom',99999,'Tom',2,0,'2022-02-27 15:09:24','2022-02-27 15:09:24');

/*Table structure for table `enum_info` */

DROP TABLE IF EXISTS `enum_info`;

CREATE TABLE `enum_info` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                             `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，md5(module_id:name)',
                             `name` varchar(128) NOT NULL DEFAULT '' COMMENT '枚举名称',
                             `description` varchar(128) NOT NULL DEFAULT '' COMMENT '枚举说明',
                             `module_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'module.id',
                             `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                             `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                             `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                             PRIMARY KEY (`id`) USING BTREE,
                             KEY `idx_moduleid` (`module_id`) USING BTREE,
                             KEY `idx_dataid` (`data_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='枚举信息';

/*Data for the table `enum_info` */

insert  into `enum_info`(`id`,`data_id`,`name`,`description`,`module_id`,`is_deleted`,`gmt_create`,`gmt_modified`) values (2,'e743329c70009ed5cfa591856a1c0739','错误码','这里是全局错误码',4,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(3,'1a72fa2c1cdd8bfac8fb7ea5b1d94fca','订单状态枚举','',4,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(4,'bca608b86a8e7d6e8371f62640111d91','用户状态','',4,0,'2022-02-27 15:09:23','2022-02-27 15:09:23');

/*Table structure for table `enum_item` */

DROP TABLE IF EXISTS `enum_item`;

CREATE TABLE `enum_item` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                             `enum_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'enum_info.id',
                             `name` varchar(128) NOT NULL DEFAULT '' COMMENT '名称，字面值',
                             `type` varchar(64) NOT NULL DEFAULT '' COMMENT '类型',
                             `value` varchar(64) NOT NULL DEFAULT '' COMMENT '枚举值',
                             `description` varchar(128) NOT NULL DEFAULT '' COMMENT '枚举描述',
                             `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                             `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                             `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                             `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                             PRIMARY KEY (`id`) USING BTREE,
                             KEY `idx_enumid` (`enum_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='枚举详情';

/*Data for the table `enum_item` */

insert  into `enum_item`(`id`,`enum_id`,`name`,`type`,`value`,`description`,`order_index`,`is_deleted`,`gmt_create`,`gmt_modified`) values (1,2,'W_10001','string','W_10001','参数错误',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(2,2,'W_10002','string','W_10002','缺少token',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(3,2,'10000','number','10000','缺少参数',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(4,3,'WAIT_PAY','number','0','未支付',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(5,3,'HAS_PAY','number','1','已支付',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(6,3,'CANCEL','number','2','取消支付',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(7,4,'ENABLE','number','1','启用',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23'),(8,4,'DISABLE','number','0','禁用',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23');

/*Table structure for table `mock_config` */

DROP TABLE IF EXISTS `mock_config`;

CREATE TABLE `mock_config` (
                               `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                               `name` varchar(64) NOT NULL DEFAULT '' COMMENT '名称',
                               `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT 'md5(path+query+body)',
                               `path` varchar(128) NOT NULL DEFAULT '',
                               `ip` varchar(64) NOT NULL DEFAULT '' COMMENT '过滤ip',
                               `request_data` text NOT NULL COMMENT '请求参数',
                               `request_data_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '参数类型，0：KV形式，1：json形式',
                               `http_status` int(11) NOT NULL DEFAULT '200' COMMENT 'http状态',
                               `delay_mills` int(11) NOT NULL DEFAULT '0' COMMENT '延迟时间，单位毫秒',
                               `result_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '返回类型，0：自定义内容，1：脚本内容',
                               `response_headers` text NOT NULL COMMENT '响应header，数组结构',
                               `response_body` text NOT NULL COMMENT '响应结果',
                               `mock_script` text COMMENT 'mock脚本',
                               `mock_result` text COMMENT 'mock结果',
                               `doc_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '文档id',
                               `remark` varchar(128) NOT NULL DEFAULT '' COMMENT '备注',
                               `creator_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建人id',
                               `creator_name` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人姓名',
                               `modifier_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改人id',
                               `modifier_name` varchar(64) DEFAULT NULL COMMENT '修改人',
                               `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                               `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                               `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                               PRIMARY KEY (`id`) USING BTREE,
                               KEY `idx_docid` (`doc_id`) USING BTREE,
                               KEY `idx_dataid` (`data_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='mock配置';

/*Data for the table `mock_config` */

/*Table structure for table `module` */

DROP TABLE IF EXISTS `module`;

CREATE TABLE `module` (
                          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                          `name` varchar(64) NOT NULL DEFAULT '' COMMENT '模块名称',
                          `project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'project.id',
                          `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '模块类型，0：自定义添加，1：swagger导入，2：postman导入',
                          `import_url` varchar(128) NOT NULL DEFAULT '' COMMENT '导入url',
                          `basic_auth_username` varchar(128) NOT NULL DEFAULT '' COMMENT 'basic认证用户名',
                          `basic_auth_password` varchar(128) NOT NULL DEFAULT '' COMMENT 'basic认证密码',
                          `token` varchar(128) NOT NULL DEFAULT '' COMMENT '开放接口调用token',
                          `create_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
                          `modify_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
                          `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                          `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                          `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                          `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                          `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                          `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                          PRIMARY KEY (`id`) USING BTREE,
                          UNIQUE KEY `uk_token` (`token`) USING BTREE,
                          KEY `idx_projectid` (`project_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='项目模块';

/*Data for the table `module` */

insert  into `module`(`id`,`name`,`project_id`,`type`,`import_url`,`basic_auth_username`,`basic_auth_password`,`token`,`create_mode`,`modify_mode`,`creator_id`,`modifier_id`,`order_index`,`is_deleted`,`gmt_create`,`gmt_modified`) values (4,'测试模块',4,0,'','','','78488946f9494242bb079f3acba907a6',0,0,15,15,0,0,'2022-02-27 15:08:45','2022-02-27 15:08:45');

/*Table structure for table `module_config` */

DROP TABLE IF EXISTS `module_config`;

CREATE TABLE `module_config` (
                                 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                 `module_id` bigint(11) unsigned NOT NULL DEFAULT '0',
                                 `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '配置类型，1：全局header',
                                 `config_key` varchar(128) NOT NULL DEFAULT '' COMMENT '配置key',
                                 `config_value` varchar(128) NOT NULL DEFAULT '' COMMENT '配置值',
                                 `extend_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                 `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
                                 `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                 `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `idx_moduleid_type` (`module_id`,`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='系统配置';

/*Data for the table `module_config` */

/*Table structure for table `module_environment` */

DROP TABLE IF EXISTS `module_environment`;

CREATE TABLE `module_environment` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `module_id` bigint(20) NOT NULL COMMENT 'module.id',
                                      `name` varchar(64) NOT NULL DEFAULT '' COMMENT '环境名称',
                                      `url` varchar(255) NOT NULL DEFAULT '' COMMENT '调试路径',
                                      `is_public` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否公开',
                                      `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                      `gmt_create` datetime ,
                                      `gmt_modified` datetime  ,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `uk_moduleid_name` (`module_id`,`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='模块调试环境';

/*Data for the table `module_environment` */

insert  into `module_environment`(`id`,`module_id`,`name`,`url`,`is_public`,`is_deleted`,`gmt_create`,`gmt_modified`) values (2,4,'local','http://127.0.0.1:8088',0,0,'2022-02-27 15:09:23','2022-02-27 15:09:23');

/*Table structure for table `module_environment_param` */

DROP TABLE IF EXISTS `module_environment_param`;

CREATE TABLE `module_environment_param` (
                                            `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                            `data_id` varchar(64) NOT NULL DEFAULT '' COMMENT '唯一id，md5(doc_id:parent_id:style:name)',
                                            `name` varchar(64) NOT NULL DEFAULT '' COMMENT '字段名称',
                                            `type` varchar(64) NOT NULL DEFAULT 'String' COMMENT '字段类型',
                                            `required` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否必须，1：是，0：否',
                                            `max_length` varchar(64) NOT NULL DEFAULT '-' COMMENT '最大长度',
                                            `example` varchar(1024) NOT NULL DEFAULT '' COMMENT '示例值',
                                            `description` text NOT NULL COMMENT '描述',
                                            `enum_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'enum_info.id',
                                            `environment_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'module_environment.id',
                                            `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                            `style` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0：path, 1：header， 2：请求参数，3：返回参数，4：错误码',
                                            `create_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '新增操作方式，0：人工操作，1：开放平台推送',
                                            `modify_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '修改操作方式，0：人工操作，1：开放平台推送',
                                            `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                            `creator_name` varchar(64) NOT NULL DEFAULT '',
                                            `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                                            `modifier_name` varchar(64) NOT NULL DEFAULT '',
                                            `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                                            `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                            `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                            `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                            PRIMARY KEY (`id`) USING BTREE,
                                            UNIQUE KEY `uk_dataid` (`data_id`) USING BTREE,
                                            KEY `idx_environmentid` (`environment_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='模块公共参数';

/*Data for the table `module_environment_param` */

/*Table structure for table `module_swagger_config` */

DROP TABLE IF EXISTS `module_swagger_config`;

CREATE TABLE `module_swagger_config` (
                                         `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                         `module_id` bigint(20) NOT NULL COMMENT 'module.id',
                                         `url` varchar(256) NOT NULL DEFAULT '' COMMENT 'swagger url',
                                         `content` longtext NOT NULL COMMENT 'swagger内容',
                                         `auth_username` varchar(128) NOT NULL DEFAULT '' COMMENT '认证用户名',
                                         `auth_password` varchar(128) NOT NULL DEFAULT '' COMMENT '认证密码',
                                         `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                         `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                         PRIMARY KEY (`id`),
                                         KEY `idx_moduleid` (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='swagger配置表';

/*Data for the table `module_swagger_config` */

/*Table structure for table `open_user` */

DROP TABLE IF EXISTS `open_user`;

CREATE TABLE `open_user` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                             `app_key` varchar(100) NOT NULL COMMENT 'appKey',
                             `secret` varchar(200) DEFAULT NULL COMMENT 'secret',
                             `status` tinyint(4) unsigned NOT NULL DEFAULT '1' COMMENT '1启用，0禁用',
                             `applicant` varchar(64) NOT NULL DEFAULT '',
                             `space_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'space.id',
                             `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                             `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                             `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `uk_app_key` (`app_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='开放用户';

/*Data for the table `open_user` */

/*Table structure for table `project` */

DROP TABLE IF EXISTS `project`;

CREATE TABLE `project` (
                           `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                           `name` varchar(64) NOT NULL DEFAULT '' COMMENT '项目名称',
                           `description` varchar(128) NOT NULL DEFAULT '' COMMENT '项目描述',
                           `space_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '所属空间，space.id',
                           `is_private` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否私有项目，1：是，0：否',
                           `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建者userid',
                           `creator_name` varchar(64) NOT NULL DEFAULT '',
                           `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0',
                           `modifier_name` varchar(64) NOT NULL DEFAULT '',
                           `order_index` int(11) NOT NULL DEFAULT '0' COMMENT '排序索引',
                           `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                           `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                           `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                           PRIMARY KEY (`id`) USING BTREE,
                           KEY `uk_name` (`creator_id`,`name`) USING BTREE,
                           KEY `idx_spaceid` (`space_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='项目表';

/*Data for the table `project` */

insert  into `project`(`id`,`name`,`description`,`space_id`,`is_private`,`creator_id`,`creator_name`,`modifier_id`,`modifier_name`,`order_index`,`is_deleted`,`gmt_create`,`gmt_modified`) values (4,'商城项目','商城项目示例',19,0,15,'超级管理员',15,'超级管理员',0,0,'2022-02-27 15:08:32','2022-02-27 15:08:32');

/*Table structure for table `project_user` */

DROP TABLE IF EXISTS `project_user`;

CREATE TABLE `project_user` (
                                `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                `project_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'project.id',
                                `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
                                `role_code` varchar(64) NOT NULL DEFAULT '0' COMMENT '角色，guest：访客，dev：开发者，admin：项目管理员',
                                `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE KEY `uk_projectid_userid` (`project_id`,`user_id`) USING BTREE,
                                KEY `idx_userid` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='项目用户关系表';

/*Data for the table `project_user` */

insert  into `project_user`(`id`,`project_id`,`user_id`,`role_code`,`is_deleted`,`gmt_create`,`gmt_modified`) values (10,4,15,'admin',0,'2022-02-27 15:08:32','2022-02-27 15:08:32');

/*Table structure for table `prop` */

DROP TABLE IF EXISTS `prop`;

CREATE TABLE `prop` (
                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                        `ref_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '关联id',
                        `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '类型，0：doc_info属性',
                        `name` varchar(64) NOT NULL DEFAULT '',
                        `val` text NOT NULL,
                        `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                        `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE KEY `uk_docid_name` (`ref_id`,`type`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='属性表';

/*Data for the table `prop` */

/*Table structure for table `share_config` */

DROP TABLE IF EXISTS `share_config`;

CREATE TABLE `share_config` (
                                `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '分享形式，1：公开，2：加密',
                                `password` varchar(128) NOT NULL DEFAULT '' COMMENT '密码',
                                `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态，1：有效，0：无效',
                                `module_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'module.id',
                                `is_all` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为全部文档',
                                `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                `remark` varchar(128) NOT NULL DEFAULT '' COMMENT '备注',
                                `creator_name` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
                                `is_show_debug` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否显示调试',
                                `is_all_selected_debug` tinyint(4) NOT NULL DEFAULT '1' COMMENT '调试环境是否全选， 1-全选， 0-不选',
                                `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `idx_moduleid` (`module_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='分享配置表';

/*Data for the table `share_config` */

/*Table structure for table `share_content` */

DROP TABLE IF EXISTS `share_content`;

CREATE TABLE `share_content` (
                                 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                 `share_config_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'share_config.id',
                                 `doc_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '文档id',
                                 `parent_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '父id',
                                 `is_share_folder` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否分享整个分类',
                                 `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                 `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `idx_shareconfigid_docid` (`share_config_id`,`doc_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='分享详情';

/*Data for the table `share_content` */

/*Table structure for table `share_environment` */

DROP TABLE IF EXISTS `share_environment`;

CREATE TABLE `share_environment` (
                                     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                     `share_config_id` bigint(20) unsigned DEFAULT '0' COMMENT '分享配置id',
                                     `module_environment_id` bigint(20) unsigned DEFAULT '0' COMMENT '模块环境id',
                                     PRIMARY KEY (`id`),
                                     KEY `share_environment_share_config_id_index` (`share_config_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='分享环境关联表';

/*Data for the table `share_environment` */

/*Table structure for table `space` */

DROP TABLE IF EXISTS `space`;

CREATE TABLE `space` (
                         `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                         `name` varchar(64) NOT NULL DEFAULT '' COMMENT '空间名称',
                         `creator_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建者userid',
                         `creator_name` varchar(64) NOT NULL DEFAULT '',
                         `modifier_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '创建者userid',
                         `modifier_name` varchar(64) NOT NULL DEFAULT '',
                         `is_compose` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否组合空间',
                         `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                         `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                         `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='分组表';

/*Data for the table `space` */

insert  into `space`(`id`,`name`,`creator_id`,`creator_name`,`modifier_id`,`modifier_name`,`is_compose`,`is_deleted`,`gmt_create`,`gmt_modified`) values (19,'研发中心',15,'超级管理员',15,'超级管理员',0,0,'2022-02-27 15:08:03','2022-02-27 15:08:03');

/*Table structure for table `space_user` */

DROP TABLE IF EXISTS `space_user`;

CREATE TABLE `space_user` (
                              `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                              `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
                              `space_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'space.id',
                              `role_code` varchar(64) NOT NULL DEFAULT '' COMMENT '角色，guest：访客，dev：开发者，admin：空间管理员',
                              `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                              `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                              `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE KEY `uk_groupid_userid` (`space_id`,`user_id`) USING BTREE,
                              KEY `idx_userid` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='分组用户关系表';

/*Data for the table `space_user` */

insert  into `space_user`(`id`,`user_id`,`space_id`,`role_code`,`is_deleted`,`gmt_create`,`gmt_modified`) values (25,15,19,'admin',0,'2022-02-27 15:08:02','2022-02-27 15:08:02');

/*Table structure for table `system_config` */

DROP TABLE IF EXISTS `system_config`;

CREATE TABLE `system_config` (
                                 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                 `config_key` varchar(64) NOT NULL DEFAULT '',
                                 `config_value` varchar(256) NOT NULL DEFAULT '',
                                 `remark` varchar(128) NOT NULL DEFAULT '',
                                 `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                 `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE KEY `uk_configkey` (`config_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='系统配置表';

/*Data for the table `system_config` */

insert  into `system_config`(`id`,`config_key`,`config_value`,`remark`,`is_deleted`,`gmt_create`,`gmt_modified`) values (2,'torna.version','1160','当前内部版本号。不要删除这条记录！！',0,'2022-02-27 14:56:51','2022-08-02 18:07:19');

/*Table structure for table `user_dingtalk_info` */

DROP TABLE IF EXISTS `user_dingtalk_info`;

CREATE TABLE `user_dingtalk_info` (
                                      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                      `nick` varchar(64) NOT NULL DEFAULT '' COMMENT '用户在钉钉上面的昵称',
                                      `name` varchar(64) NOT NULL DEFAULT '' COMMENT '员工名称。',
                                      `email` varchar(128) NOT NULL DEFAULT '' COMMENT '员工邮箱。',
                                      `userid` varchar(128) NOT NULL DEFAULT '' COMMENT '员工的userid。',
                                      `unionid` varchar(128) NOT NULL DEFAULT '' COMMENT '用户在当前开放应用所属企业的唯一标识。',
                                      `openid` varchar(128) NOT NULL DEFAULT '' COMMENT '用户在当前开放应用内的唯一标识。',
                                      `user_info_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
                                      `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                      `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE KEY `uk_unionid` (`unionid`) USING BTREE,
                                      KEY `idx_openid` (`openid`) USING BTREE,
                                      KEY `idx_userid` (`user_info_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='钉钉开放平台用户';

/*Data for the table `user_dingtalk_info` */

/*Table structure for table `user_info` */

DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                             `username` varchar(128) NOT NULL DEFAULT '' COMMENT '登录账号/邮箱',
                             `password` varchar(128) NOT NULL DEFAULT '' COMMENT '登录密码',
                             `nickname` varchar(64) NOT NULL DEFAULT '' COMMENT '昵称',
                             `is_super_admin` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否是超级管理员',
                             `source` varchar(64) NOT NULL DEFAULT 'register',
                             `email` varchar(128) NOT NULL DEFAULT '',
                             `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0：禁用，1：启用，2：重设密码',
                             `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                             `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                             `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户表';

/*Data for the table `user_info` */

insert  into `user_info`(`id`,`username`,`password`,`nickname`,`is_super_admin`,`source`,`email`,`status`,`is_deleted`,`gmt_create`,`gmt_modified`) values (15,'admin','f9560048604e55186198a4a02ba1b9a9','超级管理员',1,'register','',1,0,'2022-02-27 14:56:51','2022-02-27 14:56:51');

/*Table structure for table `user_message` */

DROP TABLE IF EXISTS `user_message`;

CREATE TABLE `user_message` (
                                `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
                                `message` varchar(256) NOT NULL DEFAULT '',
                                `is_read` tinyint(4) NOT NULL DEFAULT '0',
                                `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '同user_subscribe.type',
                                `source_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '同user_subscribe.source_id',
                                `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `idx_userid` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='站内消息';

/*Data for the table `user_message` */

/*Table structure for table `user_subscribe` */

DROP TABLE IF EXISTS `user_subscribe`;

CREATE TABLE `user_subscribe` (
                                  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                  `user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'user_info.id',
                                  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '类型，1：订阅接口，2：订阅项目',
                                  `source_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '关联id，当type=1对应doc_info.id；type=2对应project.id',
                                  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
                                  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
                                  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ,
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE KEY `uk_userid_type_sourceid` (`user_id`,`type`,`source_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户订阅表';

/*Data for the table `user_subscribe` */
