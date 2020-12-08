#set($jin="#")

$jin ${docItem.description}

#if("$!{docItem.remark}" != "")
${docItem.remark}
#end

<table>
    <tr>
        <th>接口名</th>
        <td>${docItem.name}</td>
        <th>版本号</th>
        <td>${docItem.version}</td>
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
    #foreach($paramDefinition in ${docItem.paramDefinitions})
    ${paramDefinition.paramMarkdownHtml}
    #end
</table>

**参数示例**

```json
${docItem.paramData}
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
    #if(${docItem.singleReturn})
        <tr>
            <td>data</td>
            <td>${docItem.apiDocReturnDefinition.dataType}</td>
            <td>返回的数据，没有则返回{}。详情见data部分</td>
        </tr>
    #else
    <tr>
        <td>data</td>
        <td>object</td>
        <td>返回的数据，没有则返回{}。详情见data部分</td>
    </tr>
    #end
</table>

**data部分**

#if(${docItem.singleReturn})
${docItem.apiDocReturnDefinition.description}。#if(${docItem.apiDocReturnDefinition.example} != "")示例值：${docItem.apiDocReturnDefinition.example}#end
#else
<table>
    <tr>
        <th>名称</th>
        <th>类型</th>
        <th>最大长度</th>
        <th>示例值</th>
        <th>描述</th>
    </tr>
    #foreach($resultDefinition in ${docItem.resultDefinitions})
    ${resultDefinition.resultHtml}
    #end
</table>
#end


**返回示例**

```json
${docItem.resultData}
```


