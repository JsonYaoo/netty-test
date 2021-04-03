package com.jsonyao.netty.common.protobuf;

import com.google.protobuf.GeneratedMessageV3;
import lombok.Data;

/**
 * Netty最佳实践: Server服务调用统一返回结果包装类 => 用于包装后返回给Netty Server, Netty Server再封装成Message响应给客户端
 */
@Data
public class Result<T extends GeneratedMessageV3> {

    private MessageModule.ResultType resultType;

    private T content;

    /**
     * 返回空的成功的GeneratedMessageV3
     * @param <T>
     * @return
     */
    public static <T extends GeneratedMessageV3> Result<T> SUCCESS(){
        Result<T> result = new Result<>();
        result.resultType = MessageModule.ResultType.SUCCESS;
        return result;
    }

    /**
     * 返回成功结果的GeneratedMessageV3
     * @param content
     * @param <T>
     * @return
     */
    public static <T extends GeneratedMessageV3> Result<T> SUCCESS(T content){
        Result<T> result = new Result<>();
        result.resultType = MessageModule.ResultType.SUCCESS;
        result.setContent(content);
        return result;
    }


    /**
     * 返回空的失败的GeneratedMessageV3
     * @param <T>
     * @return
     */
    public static <T extends GeneratedMessageV3> Result<T> FAILURE(){
        Result<T> result = new Result<>();
        result.resultType = MessageModule.ResultType.FAILURE;
        return result;
    }

    /**
     * 返回失败结果的GeneratedMessageV3
     * @param content
     * @param <T>
     * @return
     */
    public static <T extends GeneratedMessageV3> Result<T> FAILURE(T content){
        Result<T> result = new Result<>();
        result.resultType = MessageModule.ResultType.FAILURE;
        result.setContent(content);
        return result;
    }
}
