package cn.zmdo.web.common.util;

import cn.zmdo.web.common.util.exception.StringFormatSignException;

public class StringFormatUtils {

    private static final int FLAG_BITS = 0xFF; // 标志所占的 bit 位

    private static final int COMMA_BIT   = 0x01; // 逗号
    private static final int PLUS_BIT    = 0x02; // 加号符号
    private static final int SPACE_BIT   = 0x04; // 是否补空格
    private static final int ZERO_BIT    = 0x08; // 是否补 0
    private static final int DOT_BIT     = 0x10; // 小数点符号
    private static final int POUND_BIT   = 0x20; // #号
    private static final int BRACKET_BIT = 0x40; // 括号


    public static String createRegularExpressionByFormatString(String formatExpression) {
        StringBuilder regularExpressionBuilder = new StringBuilder("^");
        char[] chars = formatExpression.toCharArray();

        for (int i = 0; i < chars.length ; i ++) {
            char ch = chars[i];
            switch (ch) {

                // 如果碰见 % 这个是格式符号
                case '%':
                    i += analysisSign(chars,i + 1,regularExpressionBuilder);
                    break;

                // 特殊字符串
                case '\\':
                case '^':
                case '$':
                case '.':
                case '*':
                case '+':
                case '?':
                case '|':
                case '{':
                case '}':
                case '[':
                case ']':
                case '(':
                case ')':
                    regularExpressionBuilder.append("\\").append(ch);
                    break;

                default:
                    regularExpressionBuilder.append(ch);
            }
        }
        regularExpressionBuilder.append("$");
        return regularExpressionBuilder.toString();
    }

    private static int analysisSign(char[] chars,int start,StringBuilder regularExpressionBuilder) {
        boolean first = true;  // 初始位记录
        boolean numberTyped = false;
        boolean dotFirst = false; // 小数点后必须跟数字，这个是遇到点的第一个字符的标志

        int flags = 0x00;
        int number = 0;  // 整数位/特殊符号标志位数
        int decimal = 0; // 小数位
        int i;
        loop : for (i = start; i < chars.length ; i ++) {
            char ch = chars[i];

            // 数字开头的，是保留的
            if ( '0' <= ch && ch <= '9') { // 数字
                if ((!numberTyped) && ch =='0' && (flags & SPACE_BIT) == 0) {
                    flags = flags | ZERO_BIT;
                } else {
                    int n = ch - '0';
                    // 检查点标志位
                    if ((flags & DOT_BIT) != 0) {
                        decimal = decimal*10 + n;
                        dotFirst = false;
                    } else  {
                        number = number*10 + n;
                    }
                }
                numberTyped  = true;
            } else if (ch == '.') { // 小数点
                if ((flags & DOT_BIT) == 0) { // 小数点只能出现一次
                    if (flags == 0) { // 如果其他标志位都没有被定义
                        flags = flags | DOT_BIT ;
                        dotFirst = true;
                    } else {
                        throw new StringFormatSignException("标志位冲突");
                    }
                } else {
                    throw new StringFormatSignException("小数点只能在'%'后出现一次");
                }
            } else if (ch == ' ') { // 空格
                if (!numberTyped && (flags & SPACE_BIT) == 0) {
                    flags = flags | SPACE_BIT ;
                } else {
                    throw new StringFormatSignException("' '符号解析错误");
                }
            } else if (ch == '+') { // 正负号标志
                if (!numberTyped && (flags & PLUS_BIT) == 0) {
                    flags = flags | PLUS_BIT ;
                } else {
                    throw new StringFormatSignException("'+'符号解析错误");
                }
            } else if (ch == '(') { // 括号标志
                if (!numberTyped && (flags & BRACKET_BIT) == 0) {
                    flags = flags | BRACKET_BIT;
                } else {
                    throw new StringFormatSignException("'('符号解析错误");
                }
            } else if (ch == ',') { // 逗号分隔符标志
                if (!numberTyped && (flags & COMMA_BIT) == 0) {
                    flags = flags | COMMA_BIT;
                } else {
                    throw new StringFormatSignException("','符号解析错误");
                }
            } else if (ch == '#') { // 浮点数及16/8进制表示
                if (first) {
                    flags = flags | POUND_BIT;
                } else {
                    throw new StringFormatSignException("'#'号标志只能出现在'%'后的首位");
                }
            } else if (ch == '<') { // 表示前一个格式符的表示 ，这里直接跳过
                if (first) {
                    continue;
                } else {
                    throw new StringFormatSignException("'<'号标志只能出现在'%'后的首位");
                }
            } else {
                if (dotFirst) {
                    throw new StringFormatSignException("'.'号后无有效数字");
                }
                // 标志字符
                switch (ch) {
                    case '%':
                        regularExpressionBuilder.append("%");
                        break loop;
                    case 'n':
                        regularExpressionBuilder.append("\n");
                        break loop;
                    case 's':
                    case 'S':
                        // 构建字符串正则表达式
                        regularExpressionBuilder.append(buildStringExpression(flags,number,decimal,ch == 'S'));
                        break loop;
                    case 'c':
                    case 'C':
                        // 构建字符正则表达式
                        regularExpressionBuilder.append(buildCharExpression(flags,number,decimal,ch == 'C'));
                        break loop;
                    case 'b':
                    case 'B':
                        // 构建布尔值正则表达式
                        regularExpressionBuilder.append(buildBooleanExpression(flags,number,decimal,ch == 'B'));
                        break loop;
                    case 'd':
                        // TODO 构建十进制整数正则表达式
                        regularExpressionBuilder.append(buildIntegerExpression(flags,number,decimal,false));
                        break loop;
                    case 'x':
                    case 'X':
                        // 构建十六进制整数正则表达式
                        regularExpressionBuilder.append(buildHexIntegerExpression(flags,number,decimal,ch == 'X'));
                        break loop;
                    case 'o':
                        // 构建八进制整数正则表达式
                        regularExpressionBuilder.append(buildOctIntegerExpression(flags,number,decimal,false));
                        break loop;
                    case 'f':
                        // TODO 构建小数正则表达式
                        regularExpressionBuilder.append(buildFloatExpression(flags,number,decimal,false));
                        break loop;
                    case 'e':
                    case 'E':
                        // TODO 构建指数形式描述的数值
                        throw new StringFormatSignException("暂不支持指数形式的正则表达式转换");
                        // break loop;
                    case 'a':
                        // TODO 构建十六进制小数正则表达式
                        throw new StringFormatSignException("暂不支持十六进制小数的正则表达式转换");
                        // break loop;
                    default:
                        throw new StringFormatSignException(String.format("无效的占位符'%c'",ch));
                }
            }

            first = false;
        }
        return i + 1 - start;
    }

    private static String buildStringExpression(int flags,int number,int decimal,boolean upper) {

        // 检查符号冲突组合
        // 只允许 "." 存在
        final int[] conflicts = new int[]{
                COMMA_BIT,
                PLUS_BIT,
                SPACE_BIT,
                ZERO_BIT,
                POUND_BIT,
                BRACKET_BIT,
        };
        checkConflict(conflicts,flags);

        // 获取字符串表达式
        String stringExp = ".";
        if (upper) {
            // 如果是大写的s占位符，那么就不能存在小写字母
            stringExp = "[^a-z]";
        }

        // 如果对字符有限制
        if (decimal > 0) {
            // 如果 最大填充空格数 大于 字串最大长度
            if (number > decimal) {
                return String.format("[ ]{%d}%s{%d}",number - decimal,stringExp,decimal);
            } else {
                // 如果 填充空格数 大于 0
                if (number > 0) {
                    StringBuilder nd = new StringBuilder("(");
                    // 首先匹配到所有小于等于 空格数 的情况
                    for (int i = 1 ; i < number ; i ++) {
                        nd.append(String.format("([ ]{%d}%s{%d})",number - i,stringExp,i));
                        nd.append("|");
                    }
                    // 匹配到超过空格数的情况
                    nd.append(String.format("(%s{%d,%d})",stringExp,number,decimal));
                    nd.append(")");
                    return nd.toString();
                } else {
                    return String.format("%s{%d}",stringExp,decimal);
                }
            }
        }

        // 下面的是没有标注字串最大长度的情况，这种情况下无法判断
        // 直接返回一个通配符
        return stringExp + "*";
    }

    private static String buildCharExpression(int flags,int number,int decimal,boolean upper) {

        // 字符输出不支持任何标点符号
        // 检查符号冲突组合
        final int[] conflicts = new int[]{
                COMMA_BIT,
                PLUS_BIT,
                SPACE_BIT,
                ZERO_BIT,
                DOT_BIT,
                POUND_BIT,
                BRACKET_BIT,
        };
        checkConflict(conflicts,flags);

        String charExp = ".";
        if (upper) {
            // 如果是大写的c占位符，那么就不能存在小写字母
            charExp = "[^a-z]";
        }

        // 如果填充 > 1
        if (number > 1) {
            return String.format("[ ]{%d}%s{1}",number - 1,charExp);
        }

        return ".{1}";
    }

    private static String buildBooleanExpression(int flags, int number, int decimal,boolean upper) {

        // 检查符号冲突组合
        // 只允许 "." 存在
        final int[] conflicts = new int[]{
                COMMA_BIT,
                PLUS_BIT,
                SPACE_BIT,
                ZERO_BIT,
                POUND_BIT,
                BRACKET_BIT,
        };
        checkConflict(conflicts,flags);

        String trueStr = upper?"TRUE":"true";
        String falseStr = upper?"FALSE":"false";
        final int maxLen = 5;

        // 如果是 字串最大长度 > 0
        // 字串最大长度在大于等于5的情况下进行单独处理，再大就没任何意义了
        if (0 < decimal && decimal < maxLen) {
            String subExpression = String.format("(%s|%s)",trueStr.substring(0,decimal),falseStr.substring(0,decimal));
            if (decimal > number) {
                return subExpression;
            } else {
                return String.format("[ ]{%d}%s",number - decimal,subExpression);
            }
        }

        if (number == maxLen) {
            return String.format("([ ]%s|%s)",trueStr,falseStr);
        } else if (number > maxLen) {
            return String.format("[ ]{%d}([ ]%s|%s)",number - maxLen,trueStr,falseStr);
        }

        return String.format("(%s|%s)",trueStr,falseStr);
    }

    private static String buildIntegerExpression(int flags, int number, int decimal, boolean upper) {

        // 检查符号冲突组合
        final int[] conflicts = new int[]{
                DOT_BIT,
                POUND_BIT,
                PLUS_BIT | SPACE_BIT,
        };
        checkConflict(conflicts,flags);

        // 匹配 0 的表达式
        String zeroNumberExp = "0";
        // 匹配非0的表达式
        String nonzeroNumberExp ;

        // 如果出现逗号，那么数字的构造格式就不一样了
        if ((flags & COMMA_BIT) != 0) {
            nonzeroNumberExp = "[1-9][0-9]{0,2}(,[0-9]{3})*";
        } else {
            nonzeroNumberExp = "[1-9][0-9]*";
        }

        if ((flags & ZERO_BIT) != 0) {
            nonzeroNumberExp = String.format("0{0,%d}%s",number - 1,nonzeroNumberExp);
            zeroNumberExp = String.format("0{%d}",number);
        }

        String spaceFillExp = "";
        // 如果需要填充空格
        // 即 整数部分大于1 且 填充位不为0 的情况下，可以认为就是需要填充空格
        // 只要满足上述条件，其实就无关是否有空格标志了
        if (number > 1 && (flags & ZERO_BIT) == 0) {
            spaceFillExp = String.format("[ ]{0,%d}",number - 1);
            zeroNumberExp = String.format("[ ]{%d}0",number - 1);
        }

        // 如果同时设置了加号和括号
        String expression ;
        if ((flags & PLUS_BIT) != 0 && (flags & BRACKET_BIT) != 0) {
            // 这种情况下负数会用括号括住，而正数则需要用加号标识，0没有任何标识
            expression = String.format("((%s)|(%s\\(%s\\))|(%s\\+%s))",
                    zeroNumberExp,
                    spaceFillExp,
                    nonzeroNumberExp,
                    spaceFillExp,
                    nonzeroNumberExp);
        } else if ((flags & PLUS_BIT) != 0) { // 如果仅设置了加号
            expression = String.format("((%s)|(%s[\\+-]%s))",
                    zeroNumberExp,
                    spaceFillExp,
                    nonzeroNumberExp);
        } else if ((flags & BRACKET_BIT) != 0) { // 如果仅设置了括号
            expression = String.format("((%s)|(%s\\(%s\\))|(%s%s))",
                    zeroNumberExp,
                    spaceFillExp,
                    nonzeroNumberExp,
                    spaceFillExp,
                    nonzeroNumberExp);
        } else {
            expression = String.format("((%s)|%s[-]?(%s))",
                    zeroNumberExp,
                    spaceFillExp,
                    nonzeroNumberExp);
        }

        return expression;
    }

    private static String buildHexIntegerExpression(int flags, int number, int decimal, boolean upper) {
        // 禁止 空格 逗号 括号 小数点 加号
        final int[] conflicts = new int[]{
                COMMA_BIT,
                PLUS_BIT,
                SPACE_BIT,
                DOT_BIT,
                BRACKET_BIT,
        };
        checkConflict(conflicts,flags);

        String nonzeroNumberSegmentExp ;
        if (upper) {
            nonzeroNumberSegmentExp = "[1-9A-F][0-9A-F]";
        } else {
            nonzeroNumberSegmentExp = "[1-9a-f][0-9a-f]";
        }

        String nonzeroNumberExp = null;
        String zeroNumberExp = "0";

        String hexPrefix = "0x";
        if (upper) {
            hexPrefix = "0X";
        }

        // 检查 zero 标志位
        if ((flags & ZERO_BIT) != 0) {

            // 检查 '#’ 键是否存在，存在的话需要加上 "0x" 或 "0X" 标记
            StringBuilder builder = new StringBuilder("(");
            if((flags & POUND_BIT) != 0) {
                builder.append(hexPrefix).append("(");
                // 这里处理的是 0标志位的个数 大于 整数总位数 的情况
                for (int i = 0 ; i < number - 3 ; i ++) {
                    builder.append(String.format("(0{%d}%s{%d})|",number - 3 - i,nonzeroNumberSegmentExp,i));
                }
                // 这里处理的是 0标志位的个数 小于 整数总位数 的情况
                builder.append(String.format("%s{%d,}",nonzeroNumberSegmentExp,number - 3));
                builder.append(")");

                zeroNumberExp = String.format("%s0{%d}",hexPrefix,number - 2);
            } else {
                // 这里处理的是 0标志位的个数 大于 整数总位数 的情况
                for (int i = 0 ; i < number - 1 ; i ++) {
                    builder.append(String.format("(0{%d}%s{%d})|",number - 1 - i,nonzeroNumberSegmentExp,i));
                }
                // 这里处理的是 0标志位的个数 小于 整数总位数 的情况
                builder.append(String.format("%s{%d,}",nonzeroNumberSegmentExp,number - 1));

                zeroNumberExp = String.format("0{%d}",number);
            }
            nonzeroNumberExp = builder.append(")").toString();
        }

        // 如果需要填充空格
        // 即 整数部分大于1 且 填充位不为0 的情况下，可以认为就是需要填充空格
        // 注意 ”补零“ 和 ”补空格“ 是相互排斥的，所以里面还是要判断一次 ”#“
        if (number > 1 && (flags & ZERO_BIT) == 0) {

            // 检查 '#’ 键是否存在，存在的话需要加上 "0x" 或 "0X" 标记
            StringBuilder builder = new StringBuilder("(");
            if((flags & POUND_BIT) != 0) {
                // 这里处理的是 空格标志位的个数 大于 整数总位数 的情况
                for (int i = 0 ; i < number - 3 ; i ++) {
                    builder.append(String.format("([ ]{%d}%s%s{%d})|",number - 3 - i,hexPrefix,nonzeroNumberSegmentExp,i));
                }
                // 这里处理的是 空格标志位的个数 小于 整数总位数 的情况
                builder.append(String.format("%s%s{%d,}",hexPrefix,nonzeroNumberSegmentExp,number - 3));

                zeroNumberExp = String.format("[ ]{%d}%s0",number - 3,hexPrefix);
            } else {
                // 这里处理的是 空格标志位的个数 大于 整数总位数 的情况
                for (int i = 0 ; i < number - 1 ; i ++) {
                    builder.append(String.format("([ ]{%d}%s{%d})|",number - 1 - i,nonzeroNumberSegmentExp,i));
                }
                // 这里处理的是 空格标志位的个数 小于 整数总位数 的情况
                builder.append(String.format("%s{%d,}",nonzeroNumberSegmentExp,number - 1));

                zeroNumberExp = String.format("[ ]{%d}0",number - 1);
            }
            nonzeroNumberExp = builder.append(")").toString();
        }

        return String.format("((%s)|(%s))",zeroNumberExp,nonzeroNumberExp);
    }

    private static String buildOctIntegerExpression(int flags, int number, int decimal, boolean upper) {

        // 禁止 空格 逗号 括号 小数点 加号
        final int[] conflicts = new int[]{
                COMMA_BIT,
                PLUS_BIT,
                SPACE_BIT,
                DOT_BIT,
                BRACKET_BIT,
        };
        checkConflict(conflicts,flags);

        String nonzeroNumberExp = "[1-7][0-7]*";
        String zeroNumberExp = "0";

        // 检查 zero 标志位
        if ((flags & ZERO_BIT) != 0) {
            // 检查 '#’ 键是否存在，存在的话需要加上 "0" 标记
            StringBuilder builder = new StringBuilder("(");
            if((flags & POUND_BIT) != 0) {
                builder.append("0(");
                // 这里处理的是 0标志位的个数 大于 整数总位数 的情况
                for (int i = 0; i < number - 1; i ++) {
                    builder.append(String.format("(0{%d}[1-7][0-7]{%d})|",number - 2 - i,i));
                }
                // 这里处理的是 0标志位的个数 小于 整数总位数 的情况
                builder.append(String.format("[1-7][0-7]{%d,}",number - 1));
                builder.append(")");
            } else {
                // 这里处理的是 0标志位的个数 大于 整数总位数 的情况
                for (int i = 0; i < number - 1; i ++) {
                    builder.append(String.format("(0{%d}[1-7][0-7]{%d})|",number - 1 - i,i));
                }
                // 这里处理的是 0标志位的个数 小于 整数总位数 的情况
                builder.append(String.format("[1-7][0-7]{%d,}",number - 1));
            }
            nonzeroNumberExp = builder.append(")").toString();
            zeroNumberExp = String.format("0{%d}",number);
        }

        // 如果需要填充空格
        // 即 整数部分大于1 且 填充位不为0 的情况下，可以认为就是需要填充空格
        // 注意 ”补零“ 和 ”补空格“ 是相互排斥的，所以里面还是要判断一次 ”#“
        if (number > 1 && (flags & ZERO_BIT) == 0) {
            // 检查 '#’ 键是否存在，存在的话需要加上 "0" 标记
            StringBuilder builder = new StringBuilder("(");
            if((flags & POUND_BIT) != 0) {
                // 这里处理的是 空格标志位的个数 大于 整数总位数 的情况
                for (int i = 0; i < number - 1; i ++) {
                    builder.append(String.format("([ ]{%d}0[1-7][0-7]{%d})|",number - 2 - i,i));
                }
                // 这里处理的是 空格标志位的个数 小于 整数总位数 的情况
                builder.append(String.format("0[1-7][0-7]{%d,}",number - 1));

                zeroNumberExp = String.format("[ ]{%d}00",number - 2);
            } else {
                // 这里处理的是 空格标志位的个数 大于 整数总位数 的情况
                for (int i = 0; i < number - 1; i ++) {
                    builder.append(String.format("([ ]{%d}[1-7][0-7]{%d})|",number - 1 - i,i));
                }
                // 这里处理的是 空格标志位的个数 小于 整数总位数 的情况
                builder.append(String.format("[1-7][0-7]{%d,}",number - 1));

                zeroNumberExp = String.format("[ ]{%d}0",number - 1);
            }
            nonzeroNumberExp = builder.append(")").toString();
        }

        return String.format("((%s)|(%s))",zeroNumberExp,nonzeroNumberExp);
    }

    private static String buildFloatExpression(int flags, int number, int decimal, boolean upper) {
        // 禁止 空格 和 加号 同时出现
        final int[] conflicts = new int[]{
                SPACE_BIT | PLUS_BIT,
        };
        checkConflict(conflicts,flags);

        // 忽略 填充0 和 “#”


        throw new StringFormatSignException("暂不支持小数形式的正则表达式转换");
        // return null;
    }

    /**
     * 检查冲突
     * @param conflicts 冲突列表
     * @param flags 符号标记值
     */
    public static void checkConflict(int[] conflicts,int flags) {
        for (int conflictFlag : conflicts) {
            if ((flags & conflictFlag) == conflictFlag) {
                throw new StringFormatSignException("标志符号组合冲突或标志不适用");
            }
        }
    }

    /**
     * 构建单词匹配
     * @param word 要匹配的的单词
     * @param min 匹配的位数
     * @param max 匹配的最大位数
     * @return 正则表达式
     */
    public static String buildWordMatchExpression(String word,int min,int max) {
        StringBuilder builder = new StringBuilder("(");
        for (int i = min ; i <= max; i ++) {
            builder.append(word, 0, i).append("|");
        }
        // 删掉最后一个"|"符号
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        return builder.toString();
    }

}
