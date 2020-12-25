/**
 * 构建返回结果例子
 * @param params 返回结果定义
 * @returns {{}} 返回json对象
 */
export function createResponseExample(params) {
  const responseJson = {}
  params.forEach(row => {
    let val
    // 如果有子节点
    if (row.children && row.children.length > 0) {
      const childrenValue = createResponseExample(row.children)
      // 如果是数组
      if (row.type.toLowerCase() === 'array') {
        val = [childrenValue]
      } else {
        val = childrenValue
      }
    } else {
      // 单值
      val = row.example
    }
    responseJson[row.name] = val
  })
  return responseJson
}
