package com.jsonyao.io.fileio;

import java.io.*;

/**
 * 测试 FileReader: 字符流-节点流
 */
public class FileInputOutTest {

    public static void main(String[] args) {
        // 读取文件
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(Constant.FILE_READ_PATH);

            // 文件不存在时则创建, true代表追加式写入
            fileOutputStream = new FileOutputStream(Constant.FILE_WRITE_PATH, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            // 读取可用空间
            byte[] bytes = new byte[fileInputStream.available()];

            // 读取流内容到bytes数组中
            fileInputStream.read(bytes);

            // 输出bytes数组到另一个文件中
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 打印并读取输入后的文件结果: [哈喽BufferdWrite!, 哈喽BufferdWrite!, HelloWorld!, Nice!, 哈喽!]
        BufferedReaderTest bufferedReaderTest = new BufferedReaderTest();
        bufferedReaderTest.printStr(Constant.FILE_WRITE_PATH);
    }
}
