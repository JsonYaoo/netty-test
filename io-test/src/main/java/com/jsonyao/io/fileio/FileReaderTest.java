package com.jsonyao.io.fileio;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试 FileReader: 字符流-节点流
 */
public class FileReaderTest {

    public static void main(String[] args) {
        FileReaderTest fileReaderTest = new FileReaderTest();
        fileReaderTest.printStr(Constant.FILE_READ_PATH);
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

        // 读取文件内容
        int res = -1;
        List<Character> characterList = new ArrayList<>();
        while (true) {
            try {
                res = fileReader.read();
                if(res == -1) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            characterList.add((char) res);
        }

        // 打印文件内容
        StringBuilder sb = new StringBuilder();
        for (Character character : characterList) {
            if(character != '\r' && character != '\n') {
                sb.append(character);
            }
            if(character == '\n') {
                // HelloWorld!
                // Nice!
                // 哈喽!
                System.err.println(sb.toString());
                sb = new StringBuilder();
            }
        }

        // 关闭流
        try {
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
