package org.athena.framework.data.jdbc.utils;

/**
 * @author zhouzhitong
 * @since 2025/7/13
 **/
public class CamelCaseUtils {

    /**
     * 转换为小驼峰（camelCase）
     * 例子: "user_name" -> "userName"
     */
    public static String toCamelCase(String input) {
        return convert(input, false);
    }

    /**
     * 转换为大驼峰（PascalCase）
     * 例子: "user_name" -> "UserName"
     */
    public static String toPascalCase(String input) {
        return convert(input, true);
    }

    /**
     * 将驼峰命名（camelCase 或 PascalCase）转换为下划线命名（snake_case）
     * 例子: "userName" -> "user_name", "UserName" -> "user_name"
     */
    public static String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (Character.isUpperCase(c)) {
                // 如果不是第一个字符，前面加下划线
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }


    /**
     * 将字符串按照分隔符（下划线、短横线、空格等）转换为驼峰格式
     *
     * @param input       原始字符串
     * @param capitalizeFirst 是否首字母大写（PascalCase）
     * @return 转换后的字符串
     */
    private static String convert(String input, boolean capitalizeFirst) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = capitalizeFirst;

        for (char c : input.toCharArray()) {
            if (c == '_' || c == '-' || c == ' ') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }

        return result.toString();
    }

    // 测试方法
    public static void main(String[] args) {
        String[] examples = {
                "userName",
                "username",
                "user_name",
                "user-name",
                "user name",
                "User_Name",
                "USER-NAME"
        };

        System.out.println("小驼峰命名：");
        for (String s : examples) {
            System.out.println(s + " -> " + toCamelCase(s));
        }

        System.out.println("\n下划线命名：");
        for (String s : examples) {
            System.out.println(s + " -> " + toSnakeCase(s));
        }

        System.out.println("\n大驼峰命名：");
        for (String s : examples) {
            System.out.println(s + " -> " + toPascalCase(s));
        }
    }
}
