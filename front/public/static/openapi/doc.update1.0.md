
# 修改文档


<table>
    <tr>
        <th>接口名</th>
        <td>doc.update</td>
        <th>版本号</th>
        <td>1.0</td>
    </tr>
</table>

**请求参数**

<table>
    <tr>
        <th>名称</th>
        <th>类型</th>
        <th>是否必须</th>
        <th>示例值</th>
        <th>描述</th>
    </tr>
        <tr><td>id</td><td class="param-type">long</td><td><strong>是</strong></td><td>je24ozLJ</td><td>文档id<br/></td></tr>
        <tr><td>name</td><td class="param-type">string</td><td><strong>是</strong></td><td>获取商品信息</td><td>文档名称<br/></td></tr>
        <tr><td>description</td><td class="param-type">string</td><td>否</td><td>获取商品信息</td><td>文档概述<br/></td></tr>
        <tr><td>url</td><td class="param-type">string</td><td>否</td><td>/goods/get</td><td>url<br/></td></tr>
        <tr><td>httpMethod</td><td class="param-type">string</td><td>否</td><td>GET</td><td>http方法<br/></td></tr>
        <tr><td>contentType</td><td class="param-type">string</td><td>否</td><td>application/json</td><td>contentType<br/></td></tr>
        <tr><td>parentId</td><td class="param-type">string</td><td>否</td><td></td><td>父节点, 没有填空字符串<br/></td></tr>
        <tr><td>isShow</td><td class="param-type">byte</td><td>否</td><td>1</td><td>是否显示，1：显示，0：不显示<br/></td></tr>
        <tr><td>headerParams</td><td class="param-type">array</td><td>否</td><td><table parentname="headerParams"><tr><th>名称</th><th>类型</th><th>是否必须</th><th>示例值</th><th>描述</th></tr><tr><td>id</td><td class="param-type">string</td><td>否</td><td>asdf</td><td>参数id<br/></td></tr><tr><td>name</td><td class="param-type">string</td><td><strong>是</strong></td><td>goodsName</td><td>参数名<br/></td></tr><tr><td>type</td><td class="param-type">string</td><td><strong>是</strong></td><td>string</td><td>字段类型<br/></td></tr><tr><td>required</td><td class="param-type">byte</td><td><strong>是</strong></td><td>1</td><td>是否必须，1：是，0：否<br/></td></tr><tr><td>maxLength</td><td class="param-type">string</td><td>否</td><td>128</td><td>最大长度<br/></td></tr><tr><td>example</td><td class="param-type">string</td><td>否</td><td>iphone12</td><td>示例值<br/></td></tr><tr><td>description</td><td class="param-type">string</td><td>否</td><td>商品名称描述</td><td>描述<br/></td></tr><tr><td>enumContent</td><td class="param-type">string</td><td>否</td><td></td><td>参数枚举值,json数组格式<br/></td></tr><tr><td>docId</td><td class="param-type">string</td><td>否</td><td></td><td>文档id<br/></td></tr><tr><td>parentId</td><td class="param-type">string</td><td>否</td><td></td><td>父节点, 没有填空字符串<br/></td></tr><tr><td>style</td><td class="param-type">byte</td><td><strong>是</strong></td><td>1</td><td>参数形式，0：header, 1：请求参数，2：返回参数，3：错误码<br/></td></tr></table></td><td>请求头<br/></td></tr>
        <tr><td>requestParams</td><td class="param-type">array</td><td>否</td><td><table parentname="requestParams"><tr><th>名称</th><th>类型</th><th>是否必须</th><th>示例值</th><th>描述</th></tr><tr><td>id</td><td class="param-type">string</td><td>否</td><td>asdf</td><td>参数id<br/></td></tr><tr><td>name</td><td class="param-type">string</td><td><strong>是</strong></td><td>goodsName</td><td>参数名<br/></td></tr><tr><td>type</td><td class="param-type">string</td><td><strong>是</strong></td><td>string</td><td>字段类型<br/></td></tr><tr><td>required</td><td class="param-type">byte</td><td><strong>是</strong></td><td>1</td><td>是否必须，1：是，0：否<br/></td></tr><tr><td>maxLength</td><td class="param-type">string</td><td>否</td><td>128</td><td>最大长度<br/></td></tr><tr><td>example</td><td class="param-type">string</td><td>否</td><td>iphone12</td><td>示例值<br/></td></tr><tr><td>description</td><td class="param-type">string</td><td>否</td><td>商品名称描述</td><td>描述<br/></td></tr><tr><td>enumContent</td><td class="param-type">string</td><td>否</td><td></td><td>参数枚举值,json数组格式<br/></td></tr><tr><td>docId</td><td class="param-type">string</td><td>否</td><td></td><td>文档id<br/></td></tr><tr><td>parentId</td><td class="param-type">string</td><td>否</td><td></td><td>父节点, 没有填空字符串<br/></td></tr><tr><td>style</td><td class="param-type">byte</td><td><strong>是</strong></td><td>1</td><td>参数形式，0：header, 1：请求参数，2：返回参数，3：错误码<br/></td></tr></table></td><td>请求参数<br/></td></tr>
        <tr><td>responseParams</td><td class="param-type">array</td><td>否</td><td><table parentname="responseParams"><tr><th>名称</th><th>类型</th><th>是否必须</th><th>示例值</th><th>描述</th></tr><tr><td>id</td><td class="param-type">string</td><td>否</td><td>asdf</td><td>参数id<br/></td></tr><tr><td>name</td><td class="param-type">string</td><td><strong>是</strong></td><td>goodsName</td><td>参数名<br/></td></tr><tr><td>type</td><td class="param-type">string</td><td><strong>是</strong></td><td>string</td><td>字段类型<br/></td></tr><tr><td>required</td><td class="param-type">byte</td><td><strong>是</strong></td><td>1</td><td>是否必须，1：是，0：否<br/></td></tr><tr><td>maxLength</td><td class="param-type">string</td><td>否</td><td>128</td><td>最大长度<br/></td></tr><tr><td>example</td><td class="param-type">string</td><td>否</td><td>iphone12</td><td>示例值<br/></td></tr><tr><td>description</td><td class="param-type">string</td><td>否</td><td>商品名称描述</td><td>描述<br/></td></tr><tr><td>enumContent</td><td class="param-type">string</td><td>否</td><td></td><td>参数枚举值,json数组格式<br/></td></tr><tr><td>docId</td><td class="param-type">string</td><td>否</td><td></td><td>文档id<br/></td></tr><tr><td>parentId</td><td class="param-type">string</td><td>否</td><td></td><td>父节点, 没有填空字符串<br/></td></tr><tr><td>style</td><td class="param-type">byte</td><td><strong>是</strong></td><td>1</td><td>参数形式，0：header, 1：请求参数，2：返回参数，3：错误码<br/></td></tr></table></td><td>返回参数<br/></td></tr>
        <tr><td>errorCodeParams</td><td class="param-type">array</td><td>否</td><td><table parentname="errorCodeParams"><tr><th>名称</th><th>类型</th><th>是否必须</th><th>示例值</th><th>描述</th></tr><tr><td>id</td><td class="param-type">string</td><td>否</td><td>asdf</td><td>参数id<br/></td></tr><tr><td>name</td><td class="param-type">string</td><td><strong>是</strong></td><td>goodsName</td><td>参数名<br/></td></tr><tr><td>type</td><td class="param-type">string</td><td><strong>是</strong></td><td>string</td><td>字段类型<br/></td></tr><tr><td>required</td><td class="param-type">byte</td><td><strong>是</strong></td><td>1</td><td>是否必须，1：是，0：否<br/></td></tr><tr><td>maxLength</td><td class="param-type">string</td><td>否</td><td>128</td><td>最大长度<br/></td></tr><tr><td>example</td><td class="param-type">string</td><td>否</td><td>iphone12</td><td>示例值<br/></td></tr><tr><td>description</td><td class="param-type">string</td><td>否</td><td>商品名称描述</td><td>描述<br/></td></tr><tr><td>enumContent</td><td class="param-type">string</td><td>否</td><td></td><td>参数枚举值,json数组格式<br/></td></tr><tr><td>docId</td><td class="param-type">string</td><td>否</td><td></td><td>文档id<br/></td></tr><tr><td>parentId</td><td class="param-type">string</td><td>否</td><td></td><td>父节点, 没有填空字符串<br/></td></tr><tr><td>style</td><td class="param-type">byte</td><td><strong>是</strong></td><td>1</td><td>参数形式，0：header, 1：请求参数，2：返回参数，3：错误码<br/></td></tr></table></td><td>错误码<br/></td></tr>
    </table>

**参数示例**

```json
{
	"errorCodeParams":[
		{
			"docId":"",
			"enumContent":"",
			"name":"goodsName",
			"description":"商品名称描述",
			"style":"1",
			"id":"asdf",
			"type":"string",
			"required":"1",
			"maxLength":"128",
			"parentId":"",
			"example":"iphone12"
		}
	],
	"headerParams":[
		{
			"docId":"",
			"enumContent":"",
			"name":"goodsName",
			"description":"商品名称描述",
			"style":"1",
			"id":"asdf",
			"type":"string",
			"required":"1",
			"maxLength":"128",
			"parentId":"",
			"example":"iphone12"
		}
	],
	"name":"获取商品信息",
	"description":"获取商品信息",
	"requestParams":[
		{
			"docId":"",
			"enumContent":"",
			"name":"goodsName",
			"description":"商品名称描述",
			"style":"1",
			"id":"asdf",
			"type":"string",
			"required":"1",
			"maxLength":"128",
			"parentId":"",
			"example":"iphone12"
		}
	],
	"responseParams":[
		{
			"docId":"",
			"enumContent":"",
			"name":"goodsName",
			"description":"商品名称描述",
			"style":"1",
			"id":"asdf",
			"type":"string",
			"required":"1",
			"maxLength":"128",
			"parentId":"",
			"example":"iphone12"
		}
	],
	"id":"je24ozLJ",
	"httpMethod":"GET",
	"contentType":"application/json",
	"url":"/goods/get",
	"parentId":"",
	"isShow":"1"
}
```

**返回结果**

<table>
    <tr>
        <th>名称</th>
        <th>类型</th>
        <th>描述</th>
    </tr>
    <tr>
        <td>code</td>
        <td>string</td>
        <td>状态值，"0"表示成功，其它都是失败</td>
    </tr>
    <tr>
        <td>msg</td>
        <td>string</td>
        <td>错误信息，出错时显示</td>
    </tr>
        <tr>
        <td>data</td>
        <td>object</td>
        <td>返回的数据，没有则返回{}
            <table>
                <tr>
                    <th>名称</th>
                    <th>类型</th>
                    <th>示例值</th>
                    <th>描述</th>
                </tr>
                                <tr><td>id</td><td>string</td><td>Br2jqzG7</td><td>文档id<br/></td></tr>
                            </table>
        </td>
    </tr>
    </table>

**返回示例**

```json
{
	"code":"0",
	"data":{
		"id":"Br2jqzG7"
	},
	"msg":""
}
```


