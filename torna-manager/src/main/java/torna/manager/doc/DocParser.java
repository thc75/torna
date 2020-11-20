package torna.manager.doc;

/**
 * @author tanghc
 */
public interface DocParser {
    /**
     * 解析swagger文档，将json转换成对象模式
     * @param swaggerJson swagger文档内容
     * @param config 配置
     * @return 返回对象
     */
    DocBean parseJson(String swaggerJson, ParseConfig config);

    default DocBean parseJson(String swaggerJson) {
        ParseConfig parseConfig = new ParseConfig();
        parseConfig.setAllowMethod("POST");
        return this.parseJson(swaggerJson, parseConfig);
    }
}
