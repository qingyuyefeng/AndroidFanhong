package com.fanhong.cn.util;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
     * @param c
     * @return 返回开头及之后每隔4位添加一个空格的新字符串
     */
    public static String addChar(String string, char c) {
        return addChar(4, string, c);
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
        string = string.replaceAll("-", "");
        StringBuffer sb = new StringBuffer(string);
        String str = "";
        for (int i = 0; i < string.length(); i++) {
            str += sb.charAt(i);
            if (i != string.length() - 1 && (i - length + 1) >= 0) {
                if (i - length + 1 == 0) {
                    str += c;
                } else if (i != string.length() - 1 && (i - length + 1) % 4 == 0) {
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

    /********************* 格式验证 *********************/

    /**
     * 校验邮箱格式
     */
    public static boolean checkEmail(String value) {
        boolean flag = false;
        Pattern p1 = null;
        Matcher m = null;
        p1 = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        m = p1.matcher(value);
        flag = m.matches();
        return flag;
    }

    /**
     * @param checkType 校验类型：0校验手机号码，1校验座机号码，2两者都校验满足其一就可
     * @param phoneNum
     */
    public static boolean validPhoneNum(String checkType, String phoneNum) {
        boolean flag = false;
        Pattern p1 = null;
        Pattern p2 = null;
        Matcher m = null;
        p1 = Pattern.compile("^(((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[0-9])|(18[0-9]))+\\d{8})?$");
        p2 = Pattern.compile("^(0[0-9]{2,3}\\-)?([1-9][0-9]{6,7})$");
        if ("0".equals(checkType)) {
            System.out.println(phoneNum.length());
            if (phoneNum.length() != 11) {
                return false;
            } else {
                m = p1.matcher(phoneNum);
                flag = m.matches();
            }
        } else if ("1".equals(checkType)) {
            if (phoneNum.length() < 11 || phoneNum.length() >= 16) {
                return false;
            } else {
                m = p2.matcher(phoneNum);
                flag = m.matches();
            }
        } else if ("2".equals(checkType)) {
            if (!((phoneNum.length() == 11 && p1.matcher(phoneNum).matches()) || (phoneNum.length() < 16 && p2.matcher(phoneNum).matches()))) {
                return false;
            } else {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 功能：身份证的有效验证
     */
    public static boolean IDCardValidate(String IDStr) throws ParseException {
        IDStr = IDStr.trim().toUpperCase();
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            //身份证号码长度应该为15位或18位
            return false;
        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            //身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。
            return false;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
            //身份证生日无效。
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
            //身份证生日不在有效范围。
            return false;
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            //身份证月份无效
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            //身份证日期无效
            return false;
        }
        // =====================(end)=====================

        // ================ 地区码时候有效 ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            //身份证地区编码错误。
            return false;
        }
        // ==============================================

        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                //身份证无效，不是合法的身份证号码
                return false;
            }
        } else {
            return true;
        }
        // =====================(end)=====================
        return true;
    }

    /**
     * 功能：设置地区编码
     */
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 验证日期字符串是否是YYYY-MM-DD格式
     */
    public static boolean isDataFormat(String str) {
        boolean flag = false;
        // String
        // regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1 = Pattern.compile(regxStr);
        Matcher isNo = pattern1.matcher(str);
        if (isNo.matches()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 功能：判断字符串是否为数字
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }
// 身份证号码验证：end
}
