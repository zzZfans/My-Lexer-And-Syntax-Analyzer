package com.zfans.lexer;

import com.zfans.lexer.entity.Word;

import java.util.ArrayList;

/**
 * 封装进行词法分析的方法
 * 1~20 号为关键字,用下标表示, i+1 就是其机器码; 21~40 号为操作符,用下标表示, i+21 就是其机器码; 41~51 号为分界符,
 * 用下标表示, i+41 就是其机器码;用户自定义的标识符，其机器码为 52 ;常数的机器码为 53 ；不可以识别的标识符，其机器码为 0
 */
public class Analyst {

    //关键字 1~20
    private final String[] keyword = {"int", "long", "char", "if", "else", "for", "while", "return", "break", "continue",
            "switch", "case", "default", "float", "double", "void", "struct", "static", "do", "short"};

    //运算符 21~40
    private final String[] operator = {"+", "-", "*", "/", "%", "=", ">", "<", "!", "==", "!=", ">=", "<=", "++", "--", "&", "&&", "||", "[", "]"};

    //分界符 41~51
    private final String[] delimiter = {",", ";", "(", ")", "{", "}", "'", "\"", ":", "#", "."};

    public Analyst() {

    }

    /**
     * 判断是否是数字
     */
    public boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * 判断是否是字母的函数
     */
    public boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    /**
     * 判断是否由两个运算符组成
     */
    public boolean isTwoOperator(String str, char ch) {
        char lc;
        //字符数大于2和无字符的情况
        if (str.length() != 1) {
            return false;
        } else {//字符数等于2的情况
            lc = str.charAt(0);
            if (ch == '=' && (lc == '>' || lc == '<' || lc == '=' || lc == '!')) {

            } else if (ch == '+' && lc == '+') {

            } else if (ch == '-' && lc == '-') {

            } else if (ch == '|' && lc == '|') {

            } else if (ch == '&' && lc == '&') {

            } else {
                //否就返回false
                return false;
            }
            //其它符号的情况都返回true
            return true;
        }
    }

    /**
     * 获取关键字的机器码
     */
    public int getKeywordOpcodes(String str) {

        int i;
        for (i = 0; i < keyword.length; i++) {
            if (str.equals(keyword[i])) {
                break;
            }
        }
        if (i < keyword.length) {
            return i + 1;//返回关键字的机器码
        } else {
            return 0;
        }
    }

    /**
     * 获取操作符的机器码
     */
    public int getOperatorOpcodes(String str) {
        int i;
        for (i = 0; i < operator.length; i++) {
            if (str.equals(operator[i])) {
                break;
            }
        }

        if (i < operator.length) {
            return i + 21;//返回操作符的机器码
        } else {
            return 0;
        }
    }

    /**
     * 获取分界符的机器码
     */
    public int getDelimiterOpcodes(String str) {
        int i;
        for (i = 0; i < delimiter.length; i++) {
            if (str.equals(delimiter[i])) {
                break;
            }
        }

        if (i < delimiter.length) {
            return i + 41;//返回分界符的机器码
        } else {
            return 0;
        }
    }

    /**
     * 判断字符是否可以识别
     */
    public boolean isIdent(String str) {
        char ch;
        int i;
        for (i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            //非数字串的情况和非由英文字母组成的字符串
            if ((i == 0 && !isLetter(ch)) || (!isDigit(ch) && !isLetter(ch))) {
                break;
            }
        }

        return i >= str.length();
    }

    /**
     * 预处理函数
     * 去除多余间隔
     * '\n' 替换为 '$'
     */
    public String preFunction(String str) {
        StringBuilder ret = new StringBuilder();

        char curChar, nextChar;
        for (int i = 0; i < str.length() - 1; i++) {
            curChar = str.charAt(i);
            nextChar = str.charAt(i + 1);
            //如果字符是换行符,将\n换成$
            if (curChar == '\n') {
                curChar = '$';
                ret.append(curChar);
            } else if (curChar == ' ' || curChar == '\r' || curChar == '\t') {
                if (!(nextChar == ' ' || nextChar == '\r' || curChar == '\t')) {
                    //一个' '或者'\t'或者'\r'的情况，将这些字符换成' '
                    curChar = ' ';
                    ret.append(curChar);
                }
            } else {
                ret.append(curChar);//将字符连起来
            }
        }

        // 处理最后一个字符
        curChar = str.charAt(str.length() - 1);
        if (curChar != ' ' && curChar != '\r' && curChar != '\t' && curChar != '\n') {
            ret.append(curChar);
        }
        return ret.toString();
    }

    /**
     * 将字符串分成一个个单词，存放在数组列表
     */
    public ArrayList<Word> divide(String str) {
        ArrayList<Word> wordArrayList = new ArrayList<>();

        String curStr = "";
        int row = 1;

        for (int i = 0; i < str.length(); i++) {

            char curCh = str.charAt(i);

            if (i == 0 && curCh == ' ') {//字符串的第一个字符
                continue;
            }
            if (curCh == ' ') {//' '或者'\t'或者'\r'的情况
                // 新 word
                if (!"".equals(curStr)) {
                    wordArrayList.add(new Word(row, curStr));
                    curStr = "";//置空
                }
            } else if (isDigit(curCh) || isLetter(curCh)) {
                if (!"".equals(curStr) && !isDigit(curStr.charAt(curStr.length() - 1)) && !isLetter(curStr.charAt(curStr.length() - 1))) {
                    wordArrayList.add(new Word(row, curStr));
                    curStr = "";
                }
                curStr = curStr + curCh;
            } else {
                if (isTwoOperator(curStr, curCh)) { //两个运算符的情况
                    curStr = curStr + curCh;
                } else {
                    if ("".equals(curStr) && curCh != '$') {
                        curStr = curStr + curCh;
                    } else if ("".equals(curStr)) {//若检测到$符号，就换行
                        row++;//行数加一
                    } else {
                        wordArrayList.add(new Word(row, curStr));
                        curStr = "";
                        if (curCh != '$') {
                            curStr = curStr + curCh;
                        } else {
                            row++;
                        }
                    }
                }
            }
        }
        if (!"".equals(curStr)) {
            wordArrayList.add(new Word(row, curStr));
        }
        return wordArrayList;
    }

    /**
     * 判断字符串是数字串，单个字符，还是一个字符串
     */
    public int check(String str) {
        char ch;
        ch = str.charAt(0);
        if (ch >= '0' && ch <= '9') {
            return 1;//数字串
        }
        if (str.length() == 1) {
            return 2;//单个字符
        } else {
            return 3;//一个字符串
        }
    }

    /**
     * 检查字符串是否为数字串，返回其机器码
     */
    public int checkDigit(String str) {
        int i;
        char ch;
        for (i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (ch > '9' || ch < '0') {
                break;
            }
        }
        if (i < str.length()) {
            return 0;//不可识别的情况
        } else {
            return 53;//常数
        }
    }

    /**
     * 检查字符串是否为单个字符，返回其机器码
     */
    public int checkChar(String str) {
        if (getOperatorOpcodes(str) != 0) {//操作符
            return getOperatorOpcodes(str);
        } else if (getDelimiterOpcodes(str) != 0) {//分界符
            return getDelimiterOpcodes(str);
        } else if (isIdent(str)) {
            return 52;//用户自定义标识符的机器码
        } else {
            return 0;//不可以被识别的标识符，机器码为 0
        }
    }

    /**
     * 检查字符串是否为字符串，返回其机器码
     */
    public int checkString(String str) {
        //操作符
        if (getOperatorOpcodes(str) != 0) {
            return getOperatorOpcodes(str);
        } else if (getKeywordOpcodes(str) != 0) {//关键字
            return getKeywordOpcodes(str);
        } else if (isIdent(str)) {
            return 52;//用户自定义标识符的机器码
        } else {
            return 0;//不可以被识别的标识符，机器码为 0
        }
    }

    public String[] getKeyword() {
        return keyword;
    }

    public String[] getOperator() {
        return operator;
    }

    public String[] getDelimiter() {
        return delimiter;
    }


}
