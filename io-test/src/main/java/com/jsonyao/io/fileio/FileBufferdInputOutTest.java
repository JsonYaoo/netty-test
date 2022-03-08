package com.jsonyao.io.fileio;

import java.io.*;

/**
 * 测试 FileReader: 字符流-处理流
 */
public class FileBufferdInputOutTest {

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

        // 节点流对接处理流: 缓冲区的作用的主要目的是, 避免每次和硬盘打交道，提高数据访问的效率
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        while (true) {
            try {
                int read = bufferedInputStream.read();
                if(read == -1) {
                    break;
                }

                bufferedOutputStream.write(read);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        try {
            bufferedOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
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
        }

        // 打印并读取输入后的文件结果: [哈喽BufferdWrite!, 哈喽BufferdWrite!, HelloWorld!, Nice!, 哈喽!, HelloWorld!, Nice!, 哈喽!, HelloWorld!, Nice!, 哈喽!]
        BufferedReaderTest bufferedReaderTest = new BufferedReaderTest();
        bufferedReaderTest.printStr(Constant.FILE_WRITE_PATH);
    }
}
