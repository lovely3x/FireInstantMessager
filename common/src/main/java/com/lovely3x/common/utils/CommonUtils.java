package com.lovely3x.common.utils;

import android.text.TextUtils;

/**
 * Created by lovely3x on 15-9-22.
 */
public class CommonUtils {

    /**
     * @param string        需要处理的字符串
     * @param start         处理开始的位置
     * @param end           处理结束的位置
     * @param replaceLength 星号区域的长度,-1表示和替换的区域长度相同
     * @return 处理之后的字符串
     */
    public static String getAsteriskString(String string, int start, int end, int replaceLength) {
        if (TextUtils.isEmpty(string) || string.length() < 11) {
            return string;
        }
        int asteriskLen = end - start + 1;
        asteriskLen = replaceLength != -1 ? replaceLength : asteriskLen;
        StringBuilder asterisk = new StringBuilder();
        for (int i = 0; i < asteriskLen; i++) asterisk.append('*');
        StringBuilder sb = new StringBuilder().append(string);
        sb.replace(start, end, asterisk.toString());
        return sb.toString();
    }

    /**
     * 将指定的时间转换为 多少天多少时 例如 12日30天6时
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {

        long second = 1;//one second
        long minute = second * 60;//one minute
        long hour = minute * 60;//one hour
        long day = hour * 24;//one day
        String result;
        time /= 1000;
        if (time < minute) {
            result = String.format("%d秒", time / second);
        } else if (time < hour) {
            int m = (int) (time / minute);
            int s = (int) ((time % minute) / second);
            result = String.format("%d分%d秒", m, s);
        } else if (time < day) {
            int h = (int) (time / hour);
            int m = (int) ((time % hour) / minute);
            int s = (int) ((time % hour % minute) / second);
            result = String.format("%d时%d分%d秒", h, m, s);
        } else {
            int d = (int) (time / day);
            int h = (int) ((time % day) / hour);
            int m = (int) (time % day % hour / minute);//
            int s = (int) ((time % day % hour % minute) / second);
            result = String.format("%d天%d时%d分%d秒", d, h, m, s);
        }
        return result;

    }
}
