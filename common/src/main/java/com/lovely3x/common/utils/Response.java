package com.lovely3x.common.utils;

/**
 * 网络请求响应
 *
 * @author lenovo
 */
public class Response {
    /**
     * 是否请求成功
     */
    public boolean isSuccessful;

    /**
     * 请求返回携带的数据
     */
    public Object obj;

    /**
     * 错误原因
     */
    public String errorMsg;


    /**
     * 错误的代码
     */
    public int errorCode;

    /**
     * 附加值
     */
    public int addtional;


}
