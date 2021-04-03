package com.jsonyao.netty.marshalling;

import java.io.Serializable;

/**
 * JBoss Marshalling快速入门: 请求对象
 */
public class MarshallingRequestData implements Serializable {

    private static final long serialVersionUID = 5977835490688258851L;

    private String id;

    private String name;

    private String requestMessage;

    private byte[] attachment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }
}
