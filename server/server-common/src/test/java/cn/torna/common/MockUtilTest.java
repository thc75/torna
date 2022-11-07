package cn.torna.common;

import cn.torna.common.util.MockUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;

/**
 * @author thc
 */
public class MockUtilTest {

    @Test
    public void test() throws ScriptException {
        String mock = "var data = Mock.mock({\n" +
                "   // 20条数据\n" +
                "   \"data|20\": [{\n" +
                "     // 商品种类\n" +
                "     \"goodsClass\": \"女装\",\n" +
                "     // 商品Id\n" +
                "     \"goodsId|+1\": 1,\n" +
                "     // 商品名称\n" +
                "     \"goodsName\": \"@ctitle(10)\",\n" +
                "     // 商品地址\n" +
                "     \"goodsAddress\": \"@county(true)\",\n" +
                "     // 商品等级评价★\n" +
                "     \"goodsStar|1-5\": \"★\",\n" +
                "     // 商品图片\n" +
                "     \"goodsImg\": \"@Image('100x100','@color','小甜甜')\",\n" +
                "     \"addTime\": moment().format(),\n" +
                "     // 商品售价\n" +
                "     \"goodsSale|30-500\": 30\n" +
                "   }]\n" +
                " })\n" +
                "// 最终返回数据\n" +
                "return data;";

        Object data = MockUtil.mock(mock);
        System.out.println(data);
        Object data2 = MockUtil.mock(mock);
        System.out.println(data2);
//        System.out.println(JSON.toJSONString(data, SerializerFeature.PrettyFormat));
    }

    @Test
    public void test2() throws ScriptException {
        String s = "function getDateBetween(start, end) {\n" +
                "                var result = [];\n" +
                "                //使用传入参数的时间\n" +
                "                var startTime = new Date(start);\n" +
                "                var endTime = new Date(end);\n" +
                "                while (endTime - startTime >= 0) {\n" +
                "                    var year = startTime.getFullYear();\n" +
                "                    var month = startTime.getMonth();\n" +
                "                    month = month<9?'0'+(month+1):month+1;\n" +
                "                    var day = startTime.getDate().toString().length == 1 ? \"0\" + startTime.getDate() : startTime.getDate();\n" +
                "                    //加入数组\n" +
                "                    result.push(year + \"-\" + month + \"-\" + day);\n" +
                "                    //更新日期\n" +
                "                    startTime.setDate(startTime.getDate() + 1);\n" +
                "                }\n" +
                "                return result;\n" +
                "            }\n" +
                "\n" +
                "Date.prototype.format = function(fmt) { \n" +
                "     var o = { \n" +
                "        \"M+\" : this.getMonth()+1,                 //月份 \n" +
                "        \"d+\" : this.getDate(),                    //日 \n" +
                "        \"h+\" : this.getHours(),                   //小时 \n" +
                "        \"m+\" : this.getMinutes(),                 //分 \n" +
                "        \"s+\" : this.getSeconds(),                 //秒 \n" +
                "        \"q+\" : Math.floor((this.getMonth()+3)/3), //季度 \n" +
                "        \"S\"  : this.getMilliseconds()             //毫秒 \n" +
                "    }; \n" +
                "    if(/(y+)/.test(fmt)) {\n" +
                "            fmt=fmt.replace(RegExp.$1, (this.getFullYear()+\"\").substr(4 - RegExp.$1.length)); \n" +
                "    }\n" +
                "     for(var k in o) {\n" +
                "        if(new RegExp(\"(\"+ k +\")\").test(fmt)){\n" +
                "             fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : ((\"00\"+ o[k]).substr((\"\"+ o[k]).length)));\n" +
                "         }\n" +
                "     }\n" +
                "    return fmt; \n" +
                "}\n" +
                "\t\t\t\n" +
                "function getMonthBetween(start,end){  \n" +
                "                        //初始化数组\n" +
                "\t\t\t\t\t\tvar startStr = start.format('yyyy-MM-dd');\n" +
                "\t\t\t\t\t\tvar endStr = end.format('yyyy-MM-dd');\n" +
                "                        var result = [];  \n" +
                "                        //切割起始年月\n" +
                "                        var s = startStr.split(\"-\");  \n" +
                "                        //切割结束年月\n" +
                "                        var e = endStr.split(\"-\");  \n" +
                "                        //获取时间对象\n" +
                "                        var min = new Date();  \n" +
                "                        var max = new Date();  \n" +
                "                        //设置起始时间\n" +
                "                        min.setFullYear(s[0],s[1]);  \n" +
                "                        //设置结束时间\n" +
                "                        max.setFullYear(e[0],e[1]);  \n" +
                "                        \n" +
                "                        //复制一份起始时间对象\n" +
                "                        var curr = min;  \n" +
                "                        //定义字符串\n" +
                "                        var str = \"\";\n" +
                "                        //起始时间在结束时间之前\n" +
                "                        while(curr <= max){  \n" +
                "                            //获取此时间的月份\n" +
                "                            var month = curr.getMonth();  \n" +
                "                            //如果月份为0，也就是代表12月份\n" +
                "                            if(month===0){\n" +
                "                                str=(curr.getFullYear()-1)+\"-\"+12;\n" +
                "                            }else{//正常月份\n" +
                "                                str=curr.getFullYear()+\"-\"+(month<10?(\"0\"+month):month);\n" +
                "                            }\n" +
                "                            //将此年月加入数组\n" +
                "                            result.push(str);  \n" +
                "                            //更新此时间月份\n" +
                "                            curr.setMonth(month+1);  \n" +
                "                        }  \n" +
                "                        return result;  \n" +
                "                   } \t\t\t\n" +
                "function getYearBetween(start,end){\n" +
                "                    var result = [];\n" +
                "                    //使用传入参数的时间\n" +
                "                    var startTime = new Date(start);\n" +
                "                    var endTime = new Date(end);\n" +
                "                    while(endTime - startTime>=0 ) {\n" +
                "                        //获取年份\n" +
                "                        var year = startTime.getFullYear();\n" +
                "                        //加入数组\n" +
                "                        result.push(year);\n" +
                "                        //更新日期\n" +
                "                        startTime.setFullYear(startTime.getFullYear()+1);\n" +
                "                    }\n" +
                "                return result;\n" +
                "           }\n" +
                "           \n" +
                "           \tfunction fomatFloat(src,pos){    \n" +
                "       \t\treturn Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);   \n" +
                "    \t}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\tvar type = 'day';\n" +
                "\tvar d1 = new Date().setDate(1);\n" +
                "\tvar d2 = new Date();\n" +
                "\tvar result = [];\n" +
                "\tif(type == 'day'){\n" +
                "\t  \n" +
                "\t\tresult = d1<d2?getDateBetween(d1, d2):getDateBetween(d2, d1);\n" +
                "\t} else if(type == 'month'){\n" +
                "\t\n" +
                "\t\tresult = d1<d2?getMonthBetween(d1, d2):getDateBetween(d2, d1);\t\n" +
                "\t} else if(type == 'year'){\n" +
                "\t\t\n" +
                "\t\tresult = d1<d2?getYearBetween(d1, d2):getDateBetween(d2, d1);\n" +
                "\t}\n" +
                "\t\n" +
                "\tvar arr = [];\n" +
                "\t\n" +
                "\t for(var i=0;i<result.length;i++){\n" +
                "        var d = result[i];\n" +
                "        var yeaterday_amount = 0.0;\n" +
                "        var randomAmount = 0.0;\n" +
                "        if(i>0){\n" +
                "            yeaterday_amount = arr[i-1].total_amount;\n" +
                "        }\n" +
                "        var today_v = Mock.mock('@float(0, 100,2,2)');\n" +
                "        var total_v= fomatFloat(today_v +  yeaterday_amount,2);\n" +
                "        var r = Mock.mock({\n" +
                "            'id':i+1,\n" +
                "            'date':d,\n" +
                "\t\t\t'today_amount':  today_v,\n" +
                "            'total_amount':  total_v\n" +
                "        });\n" +
                "        arr.push(r);\n" +
                "    }\n" +
                "    return arr;\n" +
                "\t\n" +
                "   ";
        Object data = MockUtil.mock(s);
        System.out.println(data);
    }

    @Test
    public void test3() throws ScriptException {
        String s = "var data = Mock.mock({\n" +
                "        'patientId': '@natural( 1000000, 6000000 )',\n" +
                "        'name': '@cname',\n" +
                "        'age': '@natural( 1, 100 )',\n" +
                "        'gender': '@cword(男女)',\n" +
                "        'bedNo': '@natural( 1, 120 )',\n" +
                "        'diag': '@csentence'\n" +
                "    });" +
                "return data\n";
        Object data = MockUtil.mock(s);
        System.out.println(data);
    }

}
