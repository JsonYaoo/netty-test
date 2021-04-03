package com.jsonyao.netty.common.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;

/**
 * Netty最佳实践: 创建请求和回送响应的一个数据封装类(必须是MessageModule.Message对象)
 */
public class MessageBuilder {

    /**
     * 冗余校验编码(随便起的)
     */
    private static final int CRC_CODE = 0xabef0101;

    /**
     * 构造Protobuf响应Message
     *
     * @param module
     * @param cmd
     * @param resultType
     * @param data
     *
     * @return
     */
    public static MessageModule.Message getResponseMessage(String module,
                                                           String cmd,
                                                           MessageModule.ResultType resultType,
                                                           GeneratedMessageV3 data) {
        return MessageModule.Message.newBuilder()
                .setCrcCode(CRC_CODE)
                .setMessageType(MessageModule.MessageType.RESPONSE)
                .setModule(module)
                .setCmd(cmd)
                .setResultType(resultType)
                .setBody(ByteString.copyFrom(data.toByteArray()))
                .build();
    }

    /**
     * 构造Protobuf请求Message
     *
     * @param module
     * @param cmd
     * @param data
     *
     * @return
     */
    public static MessageModule.Message getRequestMessage(String module,
                                                          String cmd,
                                                          GeneratedMessageV3 data) {
        return MessageModule.Message.newBuilder()
                .setCrcCode(CRC_CODE)
                .setMessageType(MessageModule.MessageType.REQUEST)
                .setModule(module)
                .setCmd(cmd)
                .setBody(ByteString.copyFrom(data.toByteArray()))
                .build();
    }
}
