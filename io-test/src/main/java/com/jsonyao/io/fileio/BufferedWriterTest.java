package com.jsonyao.io.fileio;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 测试 BufferedWriter: 字符流-处理流
 */
public class BufferedWriterTest {

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

        // 节点流对接处理流: 缓冲区的作用的主要目的是, 避免每次和硬盘打交道，提高数据访问的效率
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        // 写入内容到文件
        try {
            bufferedWriter.write("哈喽BufferdWrite!\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭流
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 读取并打印文件内容: [哈喽BufferdWrite!, 哈喽BufferdWrite!]
        BufferedReaderTest bufferedReaderTest = new BufferedReaderTest();
        bufferedReaderTest.printStr(Constant.FILE_WRITE_PATH);
    }
}
