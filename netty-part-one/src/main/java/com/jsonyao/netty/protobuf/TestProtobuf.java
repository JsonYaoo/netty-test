package com.jsonyao.netty.protobuf;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Google Protocol Buffers快速入门: 测试生成的protobuf文件
 */
public class TestProtobuf {

    public static void main(String[] args) {
        byte[] data = serialUser2Bytes();
        UserModule.User user = deSerialBytes2User(data);
        System.err.println("userId: " + user.getUserId());
        System.err.println("age: " + user.getAge());
        System.err.println("userName: " + user.getUserName());
        System.err.println("favorite: " + user.getFavoriteList());

        // => userName: "\345\274\240\344\270\211" | favorite: "\347\257\256\347\220\203", 兼容性不好
//        System.err.println("user: " + user.toString());
    }

    static class User implements Serializable {
        private String userId;
        private int age;
        private String userName;
        private String[] favorite;

        public User(String userId, int age, String userName, String[] favorite) {
            this.userId = userId;
            this.age = age;
            this.userName = userName;
            this.favorite = favorite;
        }

        @Override
        public String toString() {
            return "User{" +
                    "userId='" + userId + '\'' +
                    ", age=" + age +
                    ", userName='" + userName + '\'' +
                    ", favorite=" + Arrays.toString(favorite) +
                    '}';
        }
    }

    /**
     * 序列化: Java Object => bytes
     * @return
     */
    /**
     *  序列化机制：
     *  1. java序列化 比如一个int类型(4个字节长度)
     *     eg: int a = 2   &  int a = 110000000
     *  => 实际: java的序列化无论真是的int类型数值大小实际占用多少个字节, 在内存中都是以4个长度(32位)
     *  => 所以, Java Serializable对于小的int数值编码是就浪费了内存
     *
     *  2. protobuf序列化机制：
     *  => 实际: 是按照实际的数据大小去动态伸缩的, 因此很多时候我们的int数据并没有实际占用到4个字节
     *  => 所以, protobuf序列化后一般都会比int类型(java序列化机制)的占用长度要小很多! 码流小, 传输性能高!
     *
     *  3. fastJson序列化机制：序列化后的字节数组比protobuf序列化方式的小得多, 传输性最高!
     */
    public static byte[] serialUser2Bytes() {
        // 1. java序列化机制: 可见, Java原生的序列化方式字节数组更大, 传输性能不高
        //      => User java serializable bytes: [85, 115, 101, 114, 123, 117, 115, 101, 114, 73, 100, 61, 39, 49, 48, 48, 49, 39, 44, 32, 97, 103, 101, 61, 51, 48, 44, 32, 117, 115, 101, 114, 78, 97, 109, 101, 61, 39, -27, -68, -96, -28, -72, -119, 39, 44, 32, 102, 97, 118, 111, 114, 105, 116, 101, 61, 91, -24, -74, -77, -25, -112, -125, 44, 32, -25, -81, -82, -25, -112, -125, 93, 125]
        User java_user = new User("1001", 30, "张三", new String[]{"足球", "篮球"});
        System.err.println("User java serializable bytes: " + Arrays.toString(java_user.toString().getBytes()));
        try {
            System.err.println("User java deSerializable" + new String(java_user.toString().getBytes(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 2. protobuf序列化机制：序列化后的字节数组比Java原生方式的小得多, 传输性能高
        //      => User protobuf bytes: [10, 4, 49, 48, 48, 49, 16, 30, 26, 6, -27, -68, -96, -28, -72, -119, 34, 6, -24, -74, -77, -25, -112, -125, 34, 6, -25, -81, -82, -25, -112, -125]
        UserModule.User.Builder userBuilder = UserModule.User.newBuilder();
        UserModule.User user = userBuilder
                .setUserId("1001")
                .setAge(30)
                .setUserName("张三")
                .addFavorite("足球")
                .addFavorite("篮球")
                .build();
        byte[] data = user.toByteArray();
        System.err.println("User protobuf bytes: " + Arrays.toString(data));

        // 3. fastJson序列化机制：序列化后的字节数组比protobuf序列化方式的小得多, 传输性最高
        //      => User fastJson bytes: [123, 125] => 这里测的FastJson码流更小
        System.err.println("User fastJson bytes: " + Arrays.toString(JSON.toJSONString(java_user).getBytes()));
        return data;
    }

    /**
     * 反序列化: bytes => Java Object
     * @return
     */
    public static UserModule.User deSerialBytes2User(byte[] data) {
        try {
            return UserModule.User.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return null;
    }
}
