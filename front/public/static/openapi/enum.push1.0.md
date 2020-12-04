
# 推送字典


<table>
    <tr>
        <th>接口名</th>
        <td>enum.push</td>
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
        <tr><td>name</td><td class="param-type">string</td><td><strong>是</strong></td><td>支付状态</td><td>字典分类名称<br/></td></tr>
        <tr><td>description</td><td class="param-type">string</td><td>否</td><td>支付状态</td><td>字典分类描述<br/></td></tr>
        <tr><td>items</td><td class="param-type">array</td><td><strong>是</strong></td><td><table parentname="items"><tr><th>名称</th><th>类型</th><th>是否必须</th><th>示例值</th><th>描述</th></tr><tr><td>name</td><td class="param-type">string</td><td><strong>是</strong></td><td>WAIT_PAY</td><td>枚举名称<br/></td></tr><tr><td>type</td><td class="param-type">string</td><td><strong>是</strong></td><td>string</td><td>枚举类型，string/number/boolean三选一<br/></td></tr><tr><td>value</td><td class="param-type">string</td><td><strong>是</strong></td><td>0</td><td>枚举值<br/></td></tr><tr><td>description</td><td class="param-type">string</td><td><strong>是</strong></td><td>未支付</td><td>枚举值描述<br/></td></tr></table></td><td>枚举项<br/></td></tr>
    </table>

**参数示例**

```json
{
	"name":"支付状态",
	"description":"支付状态",
	"items":[
		{
			"name":"WAIT_PAY",
			"description":"未支付",
			"type":"string",
			"value":"0"
		}
	]
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
                                <tr><td>id</td><td>string</td><td>Br2jqzG7</td><td>字典分类id<br/></td></tr>
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


