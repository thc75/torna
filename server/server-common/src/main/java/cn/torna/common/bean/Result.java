package cn.torna.common.bean;

public class Result<T> {

    private static final String CODE_SUCCESS = "0";
    private static final String CODE_ERROR = "100";

    private static Result ok = new Result();
    static {
        ok.setCode(CODE_SUCCESS);
    }

    private String code;
    private T data;
    private String msg;

    public static Result ok() {
        return ok;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<T>();
        result.setCode(CODE_SUCCESS);
        result.setData(data);
        return result;
    }

    public static Result err(String msg) {
        Result result = new Result();
        result.setCode(CODE_ERROR);
        result.setMsg(msg);
        return result;
    }

    public static Result err(String code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}