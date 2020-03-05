package com.permission.utils.string;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.stream.Collectors.toList;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2018-04-17   *
 * * Time: 15:51        *
 * * to: lz&xm          *
 * **********************
 **/
public class StringUtil {

    /**
     * 字符串转 String List
     *
     * @param str       待转换字符串
     * @param delimiter 分隔符
     * @return List<String>
     */
    public static List<String> stringsToStringList(String str, StringUtil.Delimiter delimiter) {
        return Arrays.asList(str.split(delimiter.getName()));
    }

    /**
     * 字符串转 Integer List
     *
     * @param str       待转换字符串
     * @param delimiter 分隔符
     * @return List<Integer>
     */
    public static List<Integer> stringsToIntegerList(String str, StringUtil.Delimiter delimiter) {
        return stringsToStringList(str, delimiter).stream().map(Integer::parseInt).collect(toList());
    }

    /**
     * 字符串转 Long List
     *
     * @param str       待转换字符串
     * @param delimiter 分隔符
     * @return List<Long>
     */
    public static List<Long> stringsToLongList(String str, StringUtil.Delimiter delimiter) {
        return stringsToStringList(str, delimiter).stream().map(Long::parseLong).collect(toList());
    }

    /**
     * Integer List 转 逗号分隔 字符串
     *
     * @param integers 待转集合
     * @return String
     */
    public static String integerListToString(List<Integer> integers) {
        StringBuilder sb = new StringBuilder();
        for (Integer i : integers) {
            sb.append(i).append(Delimiter.COMMA.getName());
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * Long List 转 逗号分隔 字符串
     *
     * @param longs 待转集合
     * @return String
     */
    public static String longListToString(List<Long> longs) {
        StringBuilder sb = new StringBuilder();
        for (Long l : longs) {
            sb.append(l).append(Delimiter.COMMA.getName());
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * string List 转 逗号分隔 字符串
     *
     * @param strings 待转集合
     * @return String
     */
    public static String stringListToString(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s).append(Delimiter.COMMA.getName());
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * 删除字符串中指定字符 (字符串逗号分隔)
     *
     * @param strList 逗号分隔的字符串
     * @param chr     指定字符
     * @return String 删除后的字符串
     */
    public static String deleteChar(String strList, String chr) {
        List<String> strings = stringsToStringList(strList, Delimiter.COMMA);
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(strings);
        list.remove(chr);
        return stringListToString(list);
    }

    /**
     * 模糊查询字段
     *
     * @param str 需要模糊查询的字段名
     * @return 拼接后的字段
     */
    public static String blurryString(String str) {
        return "%" + str + "%";
    }

    /**
     * 模糊查询字段开始
     *
     * @param str 需要模糊查询的字段名
     * @return 拼接后的字段
     */
    public static String blurryStringBegin(String str) {
        return "%" + str;
    }

    /**
     * 模糊查询字段结束
     *
     * @param str 需要模糊查询的字段名
     * @return 拼接后的字段
     */
    public static String blurryStringEnd(String str) {
        return str + "%";
    }

    //大写随机字符串
    public static String randomStrUp() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    //小写随机字符串
    public static String randomStrLo() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

    public enum Delimiter {
        COMMA(","),
        SEMICOLON(";");

        private String name;

        Delimiter(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
