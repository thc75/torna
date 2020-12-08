
# 修改分类名称


<table>
    <tr>
        <th>接口名</th>
        <td>doc.category.name.update</td>
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
        <tr><td>id</td><td class="param-type">string</td><td><strong>是</strong></td><td></td><td>9VXEyXvg</td><td>文档id<br/></td></tr>
        <tr><td>name</td><td class="param-type">string</td><td><strong>是</strong></td><td>50</td><td>商品分类</td><td>分类名称<br/></td></tr>
    </table>

**参数示例**

```json
{
	"name":"商品分类",
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
        <td>返回的数据，没有则返回{}。详情见data部分</td>
    </tr>
    </table>

**data部分**

<table>
    <tr>
        <th>名称</th>
        <th>类型</th>
        <th>最大长度</th>
        <th>示例值</th>
        <th>描述</th>
    </tr>
    </table>


**返回示例**

```json
{
	"code":"0",
	"data":{},
	"msg":""
}
```


