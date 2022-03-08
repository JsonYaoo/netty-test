package com.jsonyao.io.fileio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试 BufferedReader: 字符流-处理流
 */
public class BufferedReaderTest {

    public static void main(String[] args) {
        BufferedReaderTest bufferedReaderTest = new BufferedReaderTest();
        bufferedReaderTest.printStr(Constant.FILE_READ_PATH);
    }

    public void printStr(String path) {
        // 读取文件
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // 节点流对接处理流: 缓冲区的作用的主要目的是, 避免每次和硬盘打交道，提高数据访问的效率
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        // 读取文件内容
        String str = null;
        List<String> stringList = new ArrayList<>();
        while (true) {
            try {
                str = bufferedReader.readLine();
                if(str == null) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            stringList.add(str);
        }

        // 打印文件内容
        // [HelloWorld!, Nice!, 哈喽!]
        System.err.println(stringList);

        // 关闭流
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
