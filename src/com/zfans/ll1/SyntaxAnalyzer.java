package com.zfans.ll1;

import com.zfans.lexer.LexerFrame;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 语法分析器
 */
public class SyntaxAnalyzer {
    private static Integer lastRow;
    private static final ArrayList<String> stack = new ArrayList<>();
    private static ArrayList<LexerFrame.TokenItem> tokenList;
    // 产生式数组
    private static final Production[] productions = new Production[42];
    // 种别码 Map，种别码为键，单词为值
    private static HashMap<Integer, String> i2S;
    // 种别码 Map，单词为键，种别码为值
    private static HashMap<String, Integer> s2I;

    public static Integer doSyntaxAnalyzer(ArrayList<LexerFrame.TokenItem> token) {
        stack.clear();
        SyntaxAnalyzer.tokenList = token;
        initMap(); // 初始化种别码 Map
        initProductions(); // 产生式初始化

        stack.add(String.valueOf(s2I.get("#"))); // 在 stack 底部加上 #
        stack.add("PROGRAM"); // 将 PROGRAM 压入栈

        int stackTopPointer = 1;

        // 在 tokenList 末尾加上 #
        tokenList.add(new LexerFrame.TokenItem(s2I.get("#"), tokenList.get(tokenList.size() - 1).row));

        // 当前步骤数
        int index = 0;
        System.out.println("语法分析过程：");
        while (stackTopPointer >= 0) {

            System.out.printf("%-6s", "第" + ++index + "步：");
            System.out.printf("%-10s", "当前栈：");
            // 引入StringBuffer仅为控制在控制台的输出格式对齐
            StringBuilder sbui = new StringBuilder();
            for (int i = 0; i <= stackTopPointer; i++) {
                String str;
                try {
                    str = i2S.get(Integer.valueOf(stack.get(i)));
                    if (str != null) {
                        sbui.append(str).append(" ");
                    }
                } catch (NumberFormatException e) {
                    sbui.append(stack.get(i)).append(" ");
                }
            }
            System.out.printf("%-30s", sbui);
            System.out.print("待读队列：");

            sbui = new StringBuilder();
            for (LexerFrame.TokenItem item : tokenList) {
                sbui.append(i2S.get(item.tokenNum)).append(" ");
            }
            System.out.printf("%-55s", sbui);

            if (match(stackTopPointer)) {
                stackTopPointer--;
                System.out.print("\n");
            } else {
                int i = ll1Table(stackTopPointer);
                Production production;
                try {
                    production = productions[i];
                } catch (Exception e) {
                    System.out.printf("\n语法错误发生在第 %s 行！", lastRow);
                    return lastRow;
                }
                // 压栈
                int stackLenDelta = stackPush(stackTopPointer, production);
                stackTopPointer += stackLenDelta;
                System.out.printf("%-30s", "下一步所用产生式：" + production.prodStr);
                System.out.println();
            }
        }
        if (stackTopPointer == -1) {
            System.out.println("代码符合当前语法规则！");
        }
        return 0;
    }

    private static int stackPush(int stackTopPointer, Production production) {
        int len = production.rStrArray.length;
        stack.remove(stackTopPointer);
        if (!"ε".equals(production.rStrArray[0])) {
            for (int i = len - 1; i >= 0; i--) {
                stack.add(production.rStrArray[i]);
            }
            return len - 1;
        }
        return -1;
    }

    private static boolean match(int stackTopPointer) {
        try {
            // 未抛出异常说明是终结符
            int stackTopVal = Integer.parseInt(stack.get(stackTopPointer));

            if (stackTopVal == tokenList.get(0).tokenNum) {
                stack.remove(stackTopPointer);
                lastRow = tokenList.get(0).row;
                tokenList.remove(0);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            // 抛出异常说明是非终结符
            return false;
        }
    }

    // 利用LL(1)预测分析表进行分析
    private static int ll1Table(int stackTopPointer) {
        if ("PROGRAM".equals(stack.get(stackTopPointer))) {
            if ("int".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 0;
            } else {
                return -1;
            }
        } else if ("CS".equals(stack.get(stackTopPointer))) {
            if ("int".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 1;
            } else if ("}".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 2;
            } else if ("id".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 1;
            } else if ("if".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 1;
            } else if ("while".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 1;
            } else if ("return".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 1;
            } else if ("char".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 1;
            } else if ("short".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 1;
            } else if ("long".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 1;
            } else if ("float".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 1;
            } else if ("double".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 1;
            } else {
                return -1;
            }
        } else if ("S".equals(stack.get(stackTopPointer))) {
            if ("int".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 3;
            } else if ("id".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 4;
            } else if ("if".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 5;
            } else if ("while".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 6;
            } else if ("return".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 7;
            } else if ("char".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 3;
            } else if ("short".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 3;
            } else if ("long".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 3;
            } else if ("float".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 3;
            } else if ("double".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 3;
            } else {
                return -1;
            }
        } else if ("FA".equals(stack.get(stackTopPointer))) {
            if ("(".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 8;
            } else if ("=".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 9;
            } else {
                return -1;
            }
        } else if ("IT".equals(stack.get(stackTopPointer))) {
            if ("id".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 10;
            } else {
                return -1;
            }
        } else if ("IT'".equals(stack.get(stackTopPointer))) {
            if (")".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 12;
            } else if (";".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 12;
            } else if (",".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 11;
            } else {
                return -1;
            }
        } else if ("ELSE".equals(stack.get(stackTopPointer))) {
            if ("int".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 14;
            } else if ("}".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 14;
            } else if ("id".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 14;
            } else if ("if".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 14;
            } else if ("while".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 14;
            } else if ("return".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 14;
            } else if ("else".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 13;
            } else if ("char".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 14;
            } else if ("short".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 14;
            } else if ("long".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 14;
            } else if ("float".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 14;
            } else if ("double".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 14;
            } else {
                return -1;
            }
        } else if ("CE".equals(stack.get(stackTopPointer))) {
            if ("(".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 15;
            } else if ("id".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 15;
            } else if ("ui".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 15;
            } else if ("+".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 15;
            } else if ("-".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 15;
            } else {
                return -1;
            }
        } else if ("E".equals(stack.get(stackTopPointer))) {
            if ("(".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 16;
            } else if ("id".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 16;
            } else if ("ui".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 16;
            } else if ("+".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 17;
            } else if ("-".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 17;
            } else {
                return -1;
            }
        } else if ("E'".equals(stack.get(stackTopPointer))) {
            if (")".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 19;
            } else if (";".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 19;
            } else if ("+".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 18;
            } else if ("-".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 18;
            } else if (">".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 19;
            } else if (">=".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 19;
            } else if ("<".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 19;
            } else if ("<=".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 19;
            } else if ("==".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 19;
            } else if ("!=".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 19;
            } else {
                return -1;
            }
        } else if ("W".equals(stack.get(stackTopPointer))) {
            if ("+".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 20;
            } else if ("-".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 21;
            } else {
                return -1;
            }
        } else if ("X".equals(stack.get(stackTopPointer))) {
            if ("(".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 22;
            } else if ("id".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 22;
            } else if ("ui".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 22;
            } else {
                return -1;
            }
        } else if ("X'".equals(stack.get(stackTopPointer))) {
            if (")".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 24;
            } else if (";".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 24;
            } else if ("+".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 24;
            } else if ("-".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 24;
            } else if ("*".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 23;
            } else if ("/".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 23;
            } else if (">".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 24;
            } else if (">=".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 24;
            } else if ("<".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 24;
            } else if ("<=".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 24;
            } else if ("==".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 24;
            } else if ("!=".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 24;
            } else {
                return -1;
            }
        } else if ("Y".equals(stack.get(stackTopPointer))) {
            if ("*".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 25;
            } else if ("/".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 26;
            } else {
                return -1;
            }
        } else if ("Z".equals(stack.get(stackTopPointer))) {
            if ("(".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 29;
            } else if ("id".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 27;
            } else if ("ui".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 28;
            } else {
                return -1;
            }
        } else if ("CO".equals(stack.get(stackTopPointer))) {
            if (">".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 30;
            } else if (">=".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 31;
            } else if ("<".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 32;
            } else if ("<=".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 33;
            } else if ("==".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 34;
            } else if ("!=".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 35;
            } else {
                return -1;
            }
        } else if ("BT".equals(stack.get(stackTopPointer))) {
            if ("int".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 38;
            } else if ("char".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 36;
            } else if ("short".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 37;
            } else if ("long".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 39;
            } else if ("float".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 40;
            } else if ("double".equals(i2S.get(tokenList.get(0).tokenNum))) {
                return 41;
            } else {
                return -1;
            }
        } else {
            System.out.println("\n没有匹配的非终结符！");
        }
        return -1;
    }

    private static void initProductions() {

        productions[0] = new Production("PROGRAM",
                new String[]{s2I.get("int").toString(),
                        s2I.get("id").toString(),
                        s2I.get("(").toString(),
                        s2I.get(")").toString(),
                        s2I.get("{").toString(),
                        "CS",
                        s2I.get("}").toString()},
                "PROGRAM -> int id ( ) { CS }");

        productions[1] = new Production("CS",
                new String[]{"S", "CS"},
                "CS -> S CS");

        productions[2] = new Production("CS",
                new String[]{"ε"},
                "CS -> ε");

        productions[3] = new Production("S",
                new String[]{"BT", "IT", s2I.get(";").toString()},
                "S -> BT IT ;");

        productions[4] = new Production("S",
                new String[]{s2I.get("id").toString(), "FA"},
                "S -> id FA");

        productions[5] = new Production("S",
                new String[]{s2I.get("if").toString(),
                        s2I.get("(").toString(),
                        "CE",
                        s2I.get(")").toString(),
                        s2I.get("{").toString(),
                        "CS",
                        s2I.get("}").toString(),
                        "ELSE"},
                "S -> if ( CE ) { CS } ELSE");

        productions[6] = new Production("S",
                new String[]{s2I.get("while").toString(),
                        s2I.get("(").toString(),
                        "CE",
                        s2I.get(")").toString(),
                        s2I.get("{").toString(),
                        "CS",
                        s2I.get("}").toString()},
                "S -> while ( CE ) { CS }");

        productions[7] = new Production("S",
                new String[]{s2I.get("return").toString(),
                        s2I.get("ui").toString(),
                        s2I.get(";").toString()},
                "S -> return ui ;");

        productions[8] = new Production("FA",
                new String[]{String.valueOf(s2I.get("(")),
                        "IT",
                        String.valueOf(s2I.get(")")),
                        String.valueOf(s2I.get(";")),
                        "L'"},
                "FA -> ( IT ) ;");

        productions[9] = new Production("FA",
                new String[]{String.valueOf(s2I.get("=")),
                        "E",
                        String.valueOf(s2I.get(";"))},
                "FA -> = E ;");

        productions[10] = new Production("IT",
                new String[]{String.valueOf(s2I.get("id")),
                        "IT'"},
                "IT -> id IT'");

        productions[11] = new Production("IT'",
                new String[]{String.valueOf(s2I.get(",")),
                        String.valueOf(s2I.get("id")), "IT'"},
                "IT' -> , id IT'");

        productions[12] = new Production("IT'",
                new String[]{"ε"},
                "IT' -> ε");

        productions[13] = new Production("ELSE",
                new String[]{String.valueOf(s2I.get("else")),
                        String.valueOf(s2I.get("{")),
                        "CS",
                        String.valueOf(s2I.get("}"))},
                "ELSE -> else { CS }");

        productions[14] = new Production("ELSE",
                new String[]{"ε"},
                "ELSE -> ε");

        productions[15] = new Production("CE",
                new String[]{"E", "CO", "E"},
                "CE -> E CO E");

        productions[16] = new Production("E",
                new String[]{"X", "E'"},
                "E -> X E'");

        productions[17] = new Production("E",
                new String[]{"W", "E'"},
                "E -> W E'");

        productions[18] = new Production("E'",
                new String[]{"W", "E'"},
                "E' -> W E'");

        productions[19] = new Production("E'",
                new String[]{"ε"},
                "E' -> ε");

        productions[20] = new Production("W",
                new String[]{String.valueOf(s2I.get("+")),
                        "X"},
                "W -> + X");

        productions[21] = new Production("W",
                new String[]{String.valueOf(s2I.get("-")), "X"},
                "W -> - X");

        productions[22] = new Production("X",
                new String[]{"Z", "X'"},
                "X -> Z X'");

        productions[23] = new Production("X'",
                new String[]{"Y", "X'"},
                "X' -> Y X'");

        productions[24] = new Production("X'",
                new String[]{"ε"},
                "X' -> ε");

        productions[25] = new Production("Y",
                new String[]{String.valueOf(s2I.get("*")), "Z"},
                "Y -> * Z");

        productions[26] = new Production("Y",
                new String[]{String.valueOf(s2I.get("/")), "Z"},
                "Y -> / Z");

        productions[27] = new Production("Z",
                new String[]{String.valueOf(s2I.get("id"))},
                "Z -> id");

        productions[28] = new Production("Z",
                new String[]{String.valueOf(s2I.get("ui"))},
                "Z -> ui");

        productions[29] = new Production("Z",
                new String[]{String.valueOf(s2I.get("(")),
                        "E",
                        String.valueOf(s2I.get(")"))},
                "Z -> ( E )");

        productions[30] = new Production("CO",
                new String[]{String.valueOf(s2I.get(">"))},
                "CO -> >");

        productions[31] = new Production("CO",
                new String[]{String.valueOf(s2I.get(">="))},
                "CO -> >=");

        productions[32] = new Production("CO",
                new String[]{String.valueOf(s2I.get("<"))},
                "CO -> <");

        productions[33] = new Production("CO",
                new String[]{String.valueOf(s2I.get("<="))},
                "CO -> <=");

        productions[34] = new Production("CO",
                new String[]{String.valueOf(s2I.get("=="))},
                "CO -> ==");

        productions[35] = new Production("CO",
                new String[]{String.valueOf(s2I.get("!="))},
                "CO -> !=");

        productions[36] = new Production("BT",
                new String[]{String.valueOf(s2I.get("char"))},
                "BT -> char");

        productions[37] = new Production("BT",
                new String[]{String.valueOf(s2I.get("short"))},
                "BT -> short");

        productions[38] = new Production("BT",
                new String[]{String.valueOf(s2I.get("int"))},
                "BT -> int");

        productions[39] = new Production("BT",
                new String[]{String.valueOf(s2I.get("long"))},
                "BT -> long");

        productions[40] = new Production("BT",
                new String[]{String.valueOf(s2I.get("float"))},
                "BT -> float");

        productions[41] = new Production("BT",
                new String[]{String.valueOf(s2I.get("double"))},
                "BT -> double");
    }

    private static void initMap() {
        s2I = new HashMap<>();
        s2I.put("int", 1);
        s2I.put("(", 43);
        s2I.put(")", 44);
        s2I.put("}", 46);
        s2I.put(";", 42);
        s2I.put("id", 52);
        s2I.put("if", 4);
        s2I.put("while", 7);
        s2I.put("return", 8);
        s2I.put("ui", 53);
        s2I.put("=", 26);
        s2I.put(",", 41);
        s2I.put("else", 5);
        s2I.put("+", 21);
        s2I.put("-", 22);
        s2I.put("*", 23);
        s2I.put("/", 24);
        s2I.put(">", 27);
        s2I.put(">=", 32);
        s2I.put("<", 28);
        s2I.put("<=", 33);
        s2I.put("==", 30);
        s2I.put("!=", 31);
        s2I.put("char", 3);
        s2I.put("short", 20);
        s2I.put("long", 2);
        s2I.put("float", 14);
        s2I.put("double", 15);
        s2I.put("{", 45);
        s2I.put("#", 50);

        i2S = new HashMap<>();
        i2S.put(1, "int");
        i2S.put(43, "(");
        i2S.put(44, ")");
        i2S.put(46, "}");
        i2S.put(42, ";");
        i2S.put(52, "id");
        i2S.put(4, "if");
        i2S.put(7, "while");
        i2S.put(8, "return");
        i2S.put(53, "ui");
        i2S.put(26, "=");
        i2S.put(41, ",");
        i2S.put(5, "else");
        i2S.put(21, "+");
        i2S.put(22, "-");
        i2S.put(23, "*");
        i2S.put(24, "/");
        i2S.put(27, ">");
        i2S.put(32, ">=");
        i2S.put(28, "<");
        i2S.put(33, "<=");
        i2S.put(30, "==");
        i2S.put(31, "!=");
        i2S.put(3, "char");
        i2S.put(20, "short");
        i2S.put(2, "long");
        i2S.put(14, "float");
        i2S.put(15, "double");
        i2S.put(45, "{");
        i2S.put(50, "#");
    }

    /**
     * 产生式类
     */
    private static class Production {
        String lStr;
        String[] rStrArray;
        String prodStr;

        public Production(String lStr, String[] rStrArray, String prodStr) {
            this.lStr = lStr;
            this.rStrArray = rStrArray;
            this.prodStr = prodStr;
        }
    }
}
