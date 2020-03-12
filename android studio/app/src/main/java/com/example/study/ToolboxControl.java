package com.example.study;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 *封装一些操作toolbox的方法
 * 利用正则表达式
 */
public class ToolboxControl {

    //在自定义category中根据type添加blockly
    public void addBlock(File file, String type) {//需要再规范一下 换行 不重复什么的
        String outString;
        String fileString = readfile(file);
        Pattern pattern;
        Matcher matcher;
        Pattern outPattern = Pattern.compile("<category name=\"自定义\">[\\s\\S]*</category>");
        Matcher outMatcher = outPattern.matcher(fileString);
        if (outMatcher.find()) {
            String ruleString = outMatcher.group();//拿到自定义部分字符串
            pattern = Pattern.compile("<category name=\"自定义\">");
            matcher = pattern.matcher(ruleString);
            if (matcher.find()) {
                String leftString = matcher.replaceAll("");
                pattern = Pattern.compile("</category>");
                matcher = pattern.matcher(leftString);
                if (matcher.find()) {
                    String userString = matcher.replaceAll("");//得到用户自定义部分
                    String newString = "<category name=\"自定义\">" + "\n"
                            + userString + "\n"
                            + "<block type=\"" + type + "\"></block>" + "\n"
                            + "</category>";
                    outString = outMatcher.replaceAll(newString);
                    writefile(file, outString);
                    Log.d("Tag", "addBlock: 添加操作成功");
                    return;
                }
            }
            Log.d("Tag", "addBlock: 添加操作失败");
        }
    }

    //在自定义category中根据type删除blockly
    public void deleteBlock(File file, String type) {
        String outString;
        String fileString = readfile(file);
        Pattern pattern;
        Matcher matcher;
        Pattern outPattern = Pattern.compile("<category name=\"自定义\">[\\s\\S]*</category>");
        Matcher outMatcher = outPattern.matcher(fileString);
        if (outMatcher.find()) {
            String ruleString = outMatcher.group();//拿到自定义部分字符串
            pattern = Pattern.compile("<category name=\"自定义\">");
            matcher = pattern.matcher(ruleString);
            if (matcher.find()) {
                String leftString = matcher.replaceAll("");
                pattern = Pattern.compile("</category>");
                matcher = pattern.matcher(leftString);
                if (matcher.find()) {
                    String userString = matcher.replaceAll("");//得到用户自定义部分
                    pattern = Pattern.compile("<block type=\"" + type + "\"></block>");
                    matcher = pattern.matcher(userString);
                    if(matcher.find()) {
                        userString = matcher.replaceAll("");//删除
                        String newString = "<category name=\"自定义\">" + "\n"
                                + userString + "\n"
                                + "</category>";
                        outString = outMatcher.replaceAll(newString);
                        writefile(file, outString);
                        Log.d("Tag", "deleteBlock: 删除操作成功");
                        return;
                    }
                }
            }
            Log.d("Tag", "deleteBlock: 删除操作失败");
        }
    }

    //读取文件
    public String readfile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fos = new FileInputStream(file);
            InputStreamReader inReader = new InputStreamReader(fos);
            BufferedReader bufreader = new BufferedReader(inReader);
            String line = bufreader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = bufreader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            String contents = stringBuilder.toString();
            return contents;
        }

    }

    //写入文件
    public void writefile(File file, String s) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(s.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
