package ink.ikx.cfp.utils;

import cn.hutool.core.util.StrUtil;

import java.io.File;

public class Utils {

    public static String getPath(String... path) {
        StringBuilder sb = new StringBuilder();
        for (String s1 : path) {
            if (!StrUtil.isBlank(sb.toString()))
                sb.append(sb.toString().endsWith(File.separator) ? s1 : (File.separator + s1));
            else sb.append(s1).append(File.separator);
        }
        return sb.toString();
    }

    public static String singleWordToCamel(String str) {
        String first = str.toLowerCase().substring(0, 1);
        return str.toLowerCase().replace(first, first.toUpperCase());
    }
}
