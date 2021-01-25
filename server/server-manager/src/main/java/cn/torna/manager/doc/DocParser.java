package cn.torna.manager.doc;

/**
 * @author tanghc
 */
public interface DocParser<T> {
    /**
     * 解析swagger文档，将json转换成对象模式
     * @param json 文档内容
     * @param config 配置
     * @return 返回对象
     */
    default T parseJson(String json, ParseConfig config) {
        return null;
    }

    default T parseJson(String json) {
        ParseConfig parseConfig = new ParseConfig();
        parseConfig.setAllowMethod("POST");
        return this.parseJson(json, parseConfig);
    }
}
