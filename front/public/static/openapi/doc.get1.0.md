
# 获取文档详情


<table>
    <tr>
        <th>接口名</th>
        <td>doc.get</td>
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
        <th>最大长度</th>
        <th>示例值</th>
        <th>描述</th>
    </tr>
        <tr><td>id</td><td class="param-type">string</td><td><strong>是</strong></td><td>9VXEyXvg</td><td>文档id<br/></td></tr>
    </table>

**参数示例**

```json
{
	"id":"9VXEyXvg"
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
                    <th>最大长度</th>
                    <th>示例值</th>
                    <th>描述</th>
                </tr>
                                <tr><td>id</td><td>long</td><td>je24ozLJ</td><td>文档id<br/></td></tr>
                                <tr><td>name</td><td>string</td><td>获取商品信息</td><td>文档名称<br/></td></tr>
                                <tr><td>description</td><td>string</td><td>获取商品信息</td><td>文档概述<br/></td></tr>
                                <tr><td>url</td><td>string</td><td>/goods/get</td><td>url<br/></td></tr>
                                <tr><td>httpMethod</td><td>string</td><td>GET</td><td>http方法<br/></td></tr>
                                <tr><td>contentType</td><td>string</td><td>application/json</td><td>contentType<br/></td></tr>
                                <tr><td>parentId</td><td>string</td><td></td><td>父节点, 没有填空字符串<br/></td></tr>
                                <tr><td>isShow</td><td>byte</td><td>1</td><td>是否显示，1：显示，0：不显示<br/></td></tr>
                                <tr><td>gmtCreate</td><td>date</td><td></td><td>创建时间<br/></td></tr>
                                <tr><td>gmtModified</td><td>date</td><td></td><td>最后修改时间<br/></td></tr>
                                <tr><td>headerParams</td><td>array</td><td><table><tr><th>名称</th><th>类型</th><th>示例值</th><th>描述</th></tr><tr><td>id</td><td>string</td><td>asdf</td><td>参数id<br/></td></tr><tr><td>name</td><td>string</td><td>token</td><td>参数名<br/></td></tr><tr><td>required</td><td>byte</td><td>1</td><td>是否必须，1：是，0：否<br/></td></tr><tr><td>example</td><td>string</td><td>asdfasdfd</td><td>示例值<br/></td></tr><tr><td>description</td><td>string</td><td>请求token</td><td>描述<br/></td></tr><tr><td>gmtCreate</td><td>date</td><td></td><td>创建时间<br/></td></tr><tr><td>gmtModified</td><td>date</td><td></td><td>最后修改时间<br/></td></tr></table></td><td>请求头<br/></td></tr>
                                <tr><td>requestParams</td><td>array</td><td><table><tr><th>名称</th><th>类型</th><th>示例值</th><th>描述</th></tr><tr><td>id</td><td>string</td><td>asdf</td><td>参数id<br/></td></tr><tr><td>name</td><td>string</td><td>goodsName</td><td>参数名<br/></td></tr><tr><td>type</td><td>string</td><td>string</td><td>字段类型<br/></td></tr><tr><td>required</td><td>byte</td><td>1</td><td>是否必须，1：是，0：否<br/></td></tr><tr><td>maxLength</td><td>string</td><td>128</td><td>最大长度<br/></td></tr><tr><td>example</td><td>string</td><td>iphone12</td><td>示例值<br/></td></tr><tr><td>description</td><td>string</td><td>商品名称描述</td><td>描述<br/></td></tr><tr><td>enumId</td><td>long</td><td>L42GEXWG</td><td>字典id<br/></td></tr><tr><td>docId</td><td>string</td><td></td><td>文档id<br/></td></tr><tr><td>parentId</td><td>string</td><td></td><td>父节点, 没有填空字符串<br/></td></tr><tr><td>creator</td><td>string</td><td></td><td>创建人<br/></td></tr><tr><td>modifier</td><td>string</td><td></td><td>最后修改人<br/></td></tr><tr><td>gmtCreate</td><td>date</td><td></td><td>创建时间<br/></td></tr><tr><td>gmtModified</td><td>date</td><td></td><td>最后修改时间<br/></td></tr></table></td><td>请求参数<br/></td></tr>
                                <tr><td>responseParams</td><td>array</td><td><table><tr><th>名称</th><th>类型</th><th>示例值</th><th>描述</th></tr><tr><td>id</td><td>string</td><td>asdf</td><td>参数id<br/></td></tr><tr><td>name</td><td>string</td><td>goodsName</td><td>参数名<br/></td></tr><tr><td>type</td><td>string</td><td>string</td><td>字段类型<br/></td></tr><tr><td>required</td><td>byte</td><td>1</td><td>是否必须，1：是，0：否<br/></td></tr><tr><td>maxLength</td><td>string</td><td>128</td><td>最大长度<br/></td></tr><tr><td>example</td><td>string</td><td>iphone12</td><td>示例值<br/></td></tr><tr><td>description</td><td>string</td><td>商品名称描述</td><td>描述<br/></td></tr><tr><td>enumId</td><td>long</td><td>L42GEXWG</td><td>字典id<br/></td></tr><tr><td>docId</td><td>string</td><td></td><td>文档id<br/></td></tr><tr><td>parentId</td><td>string</td><td></td><td>父节点, 没有填空字符串<br/></td></tr><tr><td>creator</td><td>string</td><td></td><td>创建人<br/></td></tr><tr><td>modifier</td><td>string</td><td></td><td>最后修改人<br/></td></tr><tr><td>gmtCreate</td><td>date</td><td></td><td>创建时间<br/></td></tr><tr><td>gmtModified</td><td>date</td><td></td><td>最后修改时间<br/></td></tr></table></td><td>返回参数<br/></td></tr>
                                <tr><td>errorCodeParams</td><td>array</td><td><table><tr><th>名称</th><th>类型</th><th>示例值</th><th>描述</th></tr><tr><td>id</td><td>string</td><td>asdf</td><td>参数id<br/></td></tr><tr><td>code</td><td>string</td><td>10001</td><td>参数名<br/></td></tr><tr><td>msg</td><td>string</td><td>token错误</td><td>错误描述<br/></td></tr><tr><td>solution</td><td>string</td><td>请填写token</td><td>解决方案<br/></td></tr></table></td><td>错误码<br/></td></tr>
                            </table>
        </td>
    </tr>
    </table>

**返回示例**

```json
{
	"code":"0",
	"data":{
		"gmtModified":"",
		"description":"获取商品信息",
		"requestParams":[
			{
				"enumId":"L42GEXWG",
				"creator":"",
				"gmtModified":"",
				"docId":"",
				"modifier":"",
				"description":"商品名称描述",
				"type":"string",
				"gmtCreate":"",
				"required":"1",
				"parentId":"",
				"example":"iphone12",
				"name":"goodsName",
				"id":"asdf",
				"maxLength":"128"
			}
		],
		"httpMethod":"GET",
		"gmtCreate":"",
		"url":"/goods/get",
		"parentId":"",
		"isShow":"1",
		"errorCodeParams":[
			{
				"msg":"token错误",
				"code":"10001",
				"solution":"请填写token",
				"id":"asdf"
			}
		],
		"headerParams":[
			{
				"gmtModified":"",
				"name":"token",
				"description":"请求token",
				"id":"asdf",
				"gmtCreate":"",
				"required":"1",
				"example":"asdfasdfd"
			}
		],
		"name":"获取商品信息",
		"responseParams":[
			{
				"enumId":"L42GEXWG",
				"creator":"",
				"gmtModified":"",
				"docId":"",
				"modifier":"",
				"description":"商品名称描述",
				"type":"string",
				"gmtCreate":"",
				"required":"1",
				"parentId":"",
				"example":"iphone12",
				"name":"goodsName",
				"id":"asdf",
				"maxLength":"128"
			}
		],
		"id":"je24ozLJ",
		"contentType":"application/json"
	},
	"msg":""
}
```


