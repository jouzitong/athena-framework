package org.arthena.common.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author zhouzhitong
 * @since 2023-11-20
 **/
public class FileUtils {

    public static BufferedWriter getFileOutputStream(String filePath) {
        File file = new File(filePath);
        // 判断文件是否存在, 不存在就创建, 如果存在就清空里面的内容
        if (file.exists()) {
            try {
                if (!file.delete()) {
                    throw new RuntimeException("清空文件失败: " + filePath);
                }
                if (!file.createNewFile()) {
                    throw new RuntimeException("创建文件失败: " + filePath);
                }
                return new BufferedWriter(new FileWriter(file));
            } catch (Exception e) {
                throw new RuntimeException("清空文件失败: " + filePath);
            }
        } else {
            try {
                if (!file.createNewFile()) {
                    throw new RuntimeException("创建文件失败: " + filePath);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                return new BufferedWriter(new FileWriter(file));
            } catch (Exception e) {
                throw new RuntimeException("创建文件失败: " + filePath);
            }
        }

    }

}
