package org.tinycloud.paginate.utils;

/**
 * <p>
 * SQL排序片段优化去除工具类
 * </p>
 *
 * @author liuxingyu01
 * @since 2026-05-24
 **/
public class SqlOrderByUtils {

    private SqlOrderByUtils() {
    }

    /**
     * 移除最外层ORDER BY片段，解析失败时返回原始SQL
     *
     * @param sql 原始SQL
     * @return 移除最外层ORDER BY后的SQL
     */
    public static String removeOuterOrderBy(String sql) {
        if (sql == null || sql.isEmpty()) {
            return sql;
        }
        try {
            int orderByIndex = findOuterOrderByIndex(sql);
            if (orderByIndex < 0) {
                return sql;
            }
            String result = rtrim(sql.substring(0, orderByIndex));
            if (result.endsWith(";")) {
                result = rtrim(result.substring(0, result.length() - 1));
            }
            return result;
        } catch (Exception ex) {
            return sql;
        }
    }

    /**
     * 查找最外层ORDER BY开始位置
     *
     * @param sql 原始SQL
     * @return 最外层ORDER BY开始位置，找不到返回-1
     */
    private static int findOuterOrderByIndex(String sql) {
        int depth = 0;
        int orderByIndex = -1;
        boolean singleQuote = false;
        boolean doubleQuote = false;
        boolean backQuote = false;
        boolean bracketQuote = false;
        boolean lineComment = false;
        boolean blockComment = false;
        boolean invalidParentheses = false;

        for (int i = 0; i < sql.length(); i++) {
            char current = sql.charAt(i);
            char next = i + 1 < sql.length() ? sql.charAt(i + 1) : '\0';

            if (lineComment) {
                if (current == '\n' || current == '\r') {
                    lineComment = false;
                }
                continue;
            }
            if (blockComment) {
                if (current == '*' && next == '/') {
                    blockComment = false;
                    i++;
                }
                continue;
            }
            if (singleQuote) {
                if (current == '\'' && next == '\'') {
                    i++;
                } else if (current == '\'') {
                    singleQuote = false;
                }
                continue;
            }
            if (doubleQuote) {
                if (current == '"' && next == '"') {
                    i++;
                } else if (current == '"') {
                    doubleQuote = false;
                }
                continue;
            }
            if (backQuote) {
                if (current == '`') {
                    backQuote = false;
                }
                continue;
            }
            if (bracketQuote) {
                if (current == ']') {
                    bracketQuote = false;
                }
                continue;
            }

            if (current == '-' && next == '-') {
                lineComment = true;
                i++;
                continue;
            }
            if (current == '/' && next == '*') {
                blockComment = true;
                i++;
                continue;
            }
            if (current == '\'') {
                singleQuote = true;
                continue;
            }
            if (current == '"') {
                doubleQuote = true;
                continue;
            }
            if (current == '`') {
                backQuote = true;
                continue;
            }
            if (current == '[') {
                bracketQuote = true;
                continue;
            }
            if (current == '(') {
                depth++;
                continue;
            }
            if (current == ')') {
                if (depth > 0) {
                    depth--;
                } else {
                    invalidParentheses = true;
                }
                continue;
            }
            if (depth == 0 && startsWithOrderBy(sql, i)) {
                orderByIndex = i;
                i += 7;
            }
        }
        if (depth != 0 || invalidParentheses || singleQuote || doubleQuote || backQuote || bracketQuote || blockComment) {
            return -1;
        }
        return orderByIndex;
    }

    /**
     * 判断指定位置是否为ORDER BY关键字
     *
     * @param sql   原始SQL
     * @param index 当前检查位置
     * @return true是，false不是
     */
    private static boolean startsWithOrderBy(String sql, int index) {
        if (!regionMatches(sql, index, "order")) {
            return false;
        }
        int nextIndex = index + 5;
        if (nextIndex >= sql.length() || !Character.isWhitespace(sql.charAt(nextIndex))) {
            return false;
        }
        nextIndex = skipWhitespaces(sql, nextIndex);
        if (!regionMatches(sql, nextIndex, "by")) {
            return false;
        }
        int endIndex = nextIndex + 2;
        return isWordBoundary(sql, index - 1) && isWordBoundary(sql, endIndex);
    }

    /**
     * 忽略大小写匹配指定片段
     *
     * @param sql      原始SQL
     * @param index    当前检查位置
     * @param expected 期望片段
     * @return true匹配，false不匹配
     */
    private static boolean regionMatches(String sql, int index, String expected) {
        if (index < 0 || index + expected.length() > sql.length()) {
            return false;
        }
        return sql.regionMatches(true, index, expected, 0, expected.length());
    }

    /**
     * 跳过空白字符
     *
     * @param sql   原始SQL
     * @param index 当前检查位置
     * @return 第一个非空白字符位置
     */
    private static int skipWhitespaces(String sql, int index) {
        int current = index;
        while (current < sql.length() && Character.isWhitespace(sql.charAt(current))) {
            current++;
        }
        return current;
    }

    /**
     * 判断指定位置是否为单词边界
     *
     * @param sql   原始SQL
     * @param index 当前检查位置
     * @return true是，false不是
     */
    private static boolean isWordBoundary(String sql, int index) {
        if (index < 0 || index >= sql.length()) {
            return true;
        }
        char ch = sql.charAt(index);
        return !Character.isLetterOrDigit(ch) && ch != '_';
    }

    /**
     * 删除右侧空白字符
     *
     * @param value 输入字符串
     * @return 删除右侧空白后的字符串
     */
    private static String rtrim(String value) {
        int end = value.length();
        while (end > 0 && Character.isWhitespace(value.charAt(end - 1))) {
            end--;
        }
        return value.substring(0, end);
    }
}
