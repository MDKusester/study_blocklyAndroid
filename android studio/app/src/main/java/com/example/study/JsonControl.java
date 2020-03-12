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
 *封装一些操作json的方法
 * 利用正则表达式
 */
public class JsonControl {

    //返回用户自定义文件数量
    public int getJsonLength(File jsonFile) {
        String mJsonString = readfile(jsonFile);
        String[] jsonArray = mJsonString.split("\\}");//这里要加转义符
        if (jsonArray.length > 0) {
            return (jsonArray.length - 1);
        }
        return 0;
    }

    //写入一个用户自定义文件 type系统自+1 name用户输入
    //！！！要注意用户写入的名字不能和默认类型相同 否则会引起错误
    public void addJson(File file, String name) {
        StringBuilder mJsonBuilder = new StringBuilder();
        String mJsonString = readfile(file);
        mJsonBuilder.append(mJsonString.split("]")[0]);
        String temp = "";
//        int num = getJsonLength(file);//这里的策略改一下 用户删除可能导致重复
        int num = getTypeMax(file)+1;//改为拿到最大的值 然后+1
        if (num == 0) {//如果添加的是第一个 不加逗号
            temp = "\n" +
                    "{\n" +
                    "\"type\": \"" + "user_defined" + Integer.toString(num) + "\",\n" +
                    "\"message0\": \"" + name + "\",\n" +
                    "\"previousStatement\": null,\n" +
                    "\"nextStatement\": null,\n" +
                    "\"colour\": " + (int) (Math.random() * 360) + ",\n" +
                    "\"tooltip\": \"\",\n" +
                    "\"helpUrl\": \"\"\n" +
                    "}";
        } else if (num > 0) {
            temp = ",\n" +
                    "{\n" +
                    "\"type\": \"" + "user_defined" + Integer.toString(num) + "\",\n" +
                    "\"message0\": \"" + name + "\",\n" +
                    "\"previousStatement\": null,\n" +
                    "\"nextStatement\": null,\n" +
                    "\"colour\": " + (int) (Math.random() * 360) + ",\n" +
                    "\"tooltip\": \"\",\n" +
                    "\"helpUrl\": \"\"\n" +
                    "}";
        }
        mJsonBuilder.append(temp);
        mJsonBuilder.append("\n]");
        writefile(file, mJsonBuilder.toString());

    }

    //删除指定名字的文件
    public void deleteJson(File file, String name) {
        String outString;
        String fileString = readfile(file);
        Pattern pattern = Pattern.compile(",[^}]+\\{[^{]+"+name+"[^{]+\\}");
        Matcher matcher = pattern.matcher(fileString);
        if(matcher.find()){//如果发现 说明不是第一个 直接删除
            outString = matcher.replaceAll("");
            Log.d("Tag", "deleteJson: 删除了不是第一个的block");
            writefile(file,outString);
            return;
        }
        pattern = Pattern.compile("\\{[^{]+"+name+"[^{]+\\}[^{]+,");
        matcher = pattern.matcher(fileString);
        if(matcher.find()){//如果发现 说明是第一个 直接删除
            outString = matcher.replaceAll("");
            Log.d("Tag", "deleteJson: 删除了第一个block");
            writefile(file,outString);
            return;
        }
        Log.d("Tag", "deleteJson: 删除失败");
    }
    //根据名字重命名文件 toolbox用的是type 不用重命名
    public void renameJson(File file,String newName,String oldName){
        String fileString = readfile(file);
        Pattern pattern = Pattern.compile("\"message0\": \""+oldName+"\"");
        Matcher matcher = pattern.matcher(fileString);
        if (matcher.find()) {
            String outString = matcher.replaceAll("\"message0\": \""+newName+"\"");
            writefile(file,outString);
            Log.d("Tag", "renameJson: 重命名成功");
            return;
        }
        Log.d("Tag", "renameJson: 重命名失败");
    }


    //根据名字得到type值
    public String getTypeByName(File file, String name) {
        String fileString = readfile(file);
        Pattern pattern = Pattern.compile("\\{[^{]+" + name + "[^{]+\\}");//注意转义字符 只需要开头结尾加转义符\\
        Matcher matcher = pattern.matcher(fileString);
        if (matcher.find()) {
            String jsonString = matcher.group();//拿到单个block对象
//            Log.d("Tag", "单个block对象 " + jsonString);
            pattern = Pattern.compile("\"type\": \".+\"");//注意：后的空格
            matcher = pattern.matcher(jsonString);
            if (matcher.find()) {
                String typeString = matcher.group();//拿到type总字符串
//                Log.d("Tag", "type " + typeString);
                pattern = Pattern.compile("user_defined\\d+");//注意格式是固定的
                matcher = pattern.matcher(typeString);
                if (matcher.find()) {
                    String typeStringR = matcher.group();//拿到type字符串
                    Log.d("Tag", "typeR " + typeStringR);
                    return typeStringR;
                }
            }
        }
        Log.d("Tag", "getTypeByName: 正则表达式匹配失败");
        return null;
    }
    //根据type值的到name
    public String getNameByType(File file, String type) {
        String fileString = readfile(file);
        Pattern pattern = Pattern.compile("\\{[^{]+" + type + "[^{]+\\}");//注意转义字符 只需要开头结尾加转义符\\
        Matcher matcher = pattern.matcher(fileString);
        if (matcher.find()) {
            String jsonString = matcher.group();//拿到单个block对象
            pattern = Pattern.compile("\"message0\": \".+\"");//注意：后的空格
            matcher = pattern.matcher(jsonString);
            if (matcher.find()) {
                String nameString = matcher.group();
                pattern = Pattern.compile(": \".+\"");
                matcher = pattern.matcher(nameString);
                if (matcher.find()) {
                    nameString = matcher.group();
                    pattern = Pattern.compile(": \"");
                    matcher = pattern.matcher(nameString);
                    if(matcher.find()){
                        nameString = matcher.replaceAll("");
                        pattern = Pattern.compile("\"");
                        matcher = pattern.matcher(nameString);
                        if(matcher.find()){
                            String typeStringR = matcher.replaceAll("");
                            Log.d("Tag", "getTypeByName: 正则表达式匹配成功，得到name值"+typeStringR);
                            return typeStringR;
                        }
                    }
                }
            }
        }
        Log.d("Tag", "getTypeByName: 正则表达式匹配失败");
        return null;
    }
    //根据名字得到color值 -1表示无
    public int getColorByName(File file, String name) {
        String fileString = readfile(file);
        Pattern pattern = Pattern.compile("\\{[^{]+" + name + "[^{]+\\}");//注意转义字符 只需要开头结尾加转义符\\
        Matcher matcher = pattern.matcher(fileString);
        if (matcher.find()) {
            String jsonString = matcher.group();//拿到单个block对象
//            Log.d("Tag", "单个block对象 " + jsonString);
            pattern = Pattern.compile("\"colour\": .+");//注意：后的空格
            matcher = pattern.matcher(jsonString);
            if (matcher.find()) {
                String colorString = matcher.group();//拿到color总字符串
//                Log.d("Tag", "type " + colorString);
                pattern = Pattern.compile("\\d+");//拿到color数字
                matcher = pattern.matcher(colorString);
                if (matcher.find()) {
                    int colorInt = Integer.parseInt(matcher.group());//拿到type字符串
                    Log.d("Tag", "color " + colorInt);
                    return colorInt;
                }
            }
        }
        Log.d("Tag", "getColorByName: 正则表达式匹配失败");
        return -1;
    }

    //得到type值中最大的数字 -1表示无
    public int getTypeMax(File file) {
        int max = -1;
        String fileString = readfile(file);
        Pattern patternOut = Pattern.compile("\\{[^{]+\\}");//注意转义字符 只需要开头结尾加转义符\\
        Matcher matcherOut = patternOut.matcher(fileString);
        Pattern pattern;//内部循环
        Matcher matcher;
        while (matcherOut.find()) {//会自动向下查找
            String jsonString = matcherOut.group();//拿到单个block对象
//            Log.d("Tag", "单个block对象 " + jsonString);
            pattern = Pattern.compile("\"type\": \".+\"");//注意：后的空格
            matcher = pattern.matcher(jsonString);
            if (matcher.find()) {
                String typeString = matcher.group();//拿到type总字符串
//                Log.d("Tag", "type " + typeString);
                pattern = Pattern.compile("\\d+");
                matcher = pattern.matcher(typeString);
                if (matcher.find()) {
                    int typeInt = Integer.parseInt(matcher.group());//拿到type字符串
//                    Log.d("Tag", "typeInt " + typeInt);
                    if(typeInt > max){
                        max = typeInt;
                    }
                }
            }
        }
        Log.d("Tag", "最大值是"+max);
        return max;
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
