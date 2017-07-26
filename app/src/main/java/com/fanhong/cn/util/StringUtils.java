package com.fanhong.cn.util;

public class StringUtils {

    /**
     * 此方法用于替换字符串中间部分的字符并保持长度不变，如：将电话号码替换为 139****9876
     *
     * @param oldStr 将要替换的原字符串
     * @param start  开始替换的位置（即：前面保留的字符数）
     * @param end    结束替换的位置（即：后面保留的字符数）
     * @param reChar 用来替换中间部分的字符
     * @return 返回用所传的字符替换中间部分后的字符串
     */
    public static String replaceString(String oldStr, int start, int end, String reChar) {
        StringBuilder sBuilder = new StringBuilder(oldStr);
        String restr = "";
        for (int i = 0; i < oldStr.length() - start - end; i++) {
            restr += reChar;
        }
        sBuilder.replace(start, oldStr.length() - end, restr);
        return sBuilder.toString();
    }

    /**
     * 此方法适用于各种数字账号显示的格式化，因为会去除所有原有空格，如用于文本的话，可能会导致排版出现混乱
     *
     * @param string 默认开头保留的长度为4 需要格式化的字符串
     * @return 返回开头及之后每隔4位添加一个空格的新字符串
     */
    public static String addChar(String string) {
        return addChar(4, string, '-');
    }

    /**
     * 此方法适用于各种数字账号显示的格式化，因为会去除所有原有空格，如用于文本的话，可能会导致排版出现混乱
     *
     * @param length 开头保留的长度，此参数如不填，则默认为4
     * @param string 需要格式化的字符串
     * @return 返回开头及之后每隔4位添加一个空格的新字符串
     */
    public static String addChar(int length, String string, char c) {
        string = string.replaceAll(" ", "");
        StringBuffer sb = new StringBuffer(string);
        String str = "";
        for (int i = 0; i < string.length(); i++) {
            str += sb.charAt(i);
            if ((i - length + 1) >= 0) {
                if (i - length + 1 == 0) {
                    str += c;
                } else if ((i - length + 1) % 4 == 0) {
                    str += c;
                }
            }
        }
        return str;
    }

    public static boolean isEmpty(String str) {
        if (null == str || str.length() == 0 || str == "" || str.equals("")) return true;
        return false;
    }

}
