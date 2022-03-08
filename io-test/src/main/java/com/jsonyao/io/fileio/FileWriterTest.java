package com.jsonyao.io.fileio;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 测试 FileWriter: 字符流-节点流
 */
public class FileWriterTest {

    public static void main(String[] args) {
        // 读取文件
        FileWriter fileWriter = null;

        try {
            // 文件不存在时则创建, true代表追加式写入
            fileWriter = new FileWriter(Constant.FILE_WRITE_PATH, true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 写入内容到文件
        try {
            fileWriter.write("哈喽Write!\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭流
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 读取并打印文件内容: [哈喽Write!, 哈喽Write!]
        BufferedReaderTest bufferedReaderTest = new BufferedReaderTest();
        bufferedReaderTest.printStr(Constant.FILE_WRITE_PATH);
    }
}
