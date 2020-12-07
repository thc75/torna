
# 获取文档列表


<table>
    <tr>
        <th>接口名</th>
        <td>doc.list</td>
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
    </table>

**参数示例**

```json
{}
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
                                <tr><td>code</td><td>string</td><td></td><td>0</td><td>状态值，"0"表示成功，其它都是失败<br/></td></tr>
                                <tr><td>msg</td><td>string</td><td></td><td></td><td>错误信息，出错时显示<br/></td></tr>
                                <tr><td>data</td><td>array</td><td></td><td><table><tr><th>名称</th><th>类型</th><th>最大长度</th><th>示例值</th><th>描述</th></tr><tr><td>id</td><td>long</td><td></td><td>9VXEyXvg</td><td>文档id<br/></td></tr><tr><td>name</td><td>string</td><td></td><td>获取商品信息</td><td>文档名称<br/></td></tr><tr><td>description</td><td>string</td><td></td><td>根据ID查询商品信息</td><td>文档概述<br/></td></tr><tr><td>url</td><td>string</td><td></td><td>/goods/get</td><td>url<br/></td></tr><tr><td>httpMethod</td><td>string</td><td></td><td>GET</td><td>http方法<br/></td></tr><tr><td>contentType</td><td>string</td><td></td><td>application/json</td><td>contentType<br/></td></tr><tr><td>isFolder</td><td>byte</td><td></td><td>0</td><td>是否是分类，0：不是，1：是<br/></td></tr><tr><td>parentId</td><td>string</td><td></td><td>Br2jqzG7</td><td>父节点<br/></td></tr><tr><td>isShow</td><td>byte</td><td></td><td>1</td><td>是否显示，1：显示，0：不显示<br/></td></tr></table></td><td>文档数组<br/></td></tr>
                            </table>
        </td>
    </tr>
    </table>

**返回示例**

```json
{
	"msg":"",
	"code":"0",
	"data":[
		{
			"isFolder":"0",
			"name":"获取商品信息",
			"description":"根据ID查询商品信息",
			"id":"9VXEyXvg",
			"httpMethod":"GET",
			"contentType":"application/json",
			"url":"/goods/get",
			"parentId":"Br2jqzG7",
			"isShow":"1"
		}
	]
}
```


