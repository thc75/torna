# Velocity语法

在 Velocity 中所有的关键字都是以`#`开头的，而所有的变量则是以`$`开头。

## 注释

单行注释

`## 这是单行注释`

多行注释

```html
#*

xxx

 *#
```

## 变量

声明变量

```html
#set($name = "velocity")
#set($foo = 1)
```

使用：

```html
My name is ${name}
```

## 循环

```html
#foreach($item in $list)
 $item
 ${foreach.index}
#end
```

其中，`$item`代表遍历的每一项，`${foreach.index}`表示当前循环次数的计数器，从0开始


for循环跳出`#break`

```html
#foreach($item in $list)
 #if($item.name == "张三")
    #break
 #end
#end
```

## 条件控制

```html
#if(condition)
...dosonmething...
#elseif(condition)
...dosomething...
#else
...dosomething...
#end
```

如：

```html
#if( "${table.comment}" != "" )
/**
 * ${table.comment}
 */
#end
```

## 关系和逻操作符

```html
#if($foo && $bar)
    xxx
#end
```
