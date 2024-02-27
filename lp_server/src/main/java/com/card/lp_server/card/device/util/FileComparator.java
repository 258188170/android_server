package com.card.lp_server.card.device.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileComparator {
    public static boolean compareTextFiles(String file1Path, String file2Path) {
        try (BufferedReader br1 = new BufferedReader(new FileReader(file1Path));
             BufferedReader br2 = new BufferedReader(new FileReader(file2Path))) {
            String line1, line2;
            while ((line1 = br1.readLine()) != null) {
                line2 = br2.readLine();
                // 如果两行不相等，文件内容不一致
                if (!line1.equals(line2)) {
                    return false;
                }
            }
            // 确保文件2也没有多余的行
            return br2.readLine() == null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        String file1Path = "D:\\test\\lptest10.txt";
        String file2Path = "D:\\test\\out4_1.txt";
        boolean areFilesEqual = compareTextFiles(file1Path, file2Path);
        if (areFilesEqual) {
            System.out.println("两个文件内容完全一致。");
        } else {
            System.out.println("两个文件内容不一致。");
        }
    }
}
