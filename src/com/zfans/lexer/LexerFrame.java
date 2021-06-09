package com.zfans.lexer;

import com.zfans.lexer.entity.UnidentifiableWord;
import com.zfans.lexer.entity.Word;
import com.zfans.ll1.SyntaxAnalyzer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

/**
 * 词法分析器界面类
 */
public class LexerFrame extends JFrame implements ActionListener {


    private JToolBar toolBar;


    private JButton openButton;


    //词法分析按钮
    private JButton lexerButton;

    //语法分析按钮
    private JButton syntaxButton;

    private JButton clearButton;


    //文件内容文本域
    private final JTextArea fileContentTextArea;

    //表格
    private JTable resultTable;

    //控制台文本域
    private final JTextArea consoleTextArea;

    Analyst analyst = new Analyst();

    // token流
    ArrayList<TokenItem> token = new ArrayList<>();

    public static class TokenItem {
        public Integer tokenNum;
        public Integer row;

        public TokenItem(Integer tokenNum, Integer row) {
            this.tokenNum = tokenNum;
            this.row = row;
        }
    }

    String[] title = {"行", "标识符", "符种", "符号类型", "符值"};
    String[][] values = {};


    /**
     * 创建工具栏
     */
    public void createToolBar() {
        toolBar = new JToolBar();
        // 不可拽出
        toolBar.setFloatable(false);

        //工具栏上的按钮
        openButton = new JButton(new ImageIcon("image/打开文件.png"));
        openButton.setText("导入");
        openButton.setFocusPainted(false);

        lexerButton = new JButton(new ImageIcon("image/词法分析.png"));
        lexerButton.setText("词法分析");
        lexerButton.setFocusPainted(false);

        syntaxButton = new JButton(new ImageIcon("image/语法.png"));
        syntaxButton.setText("语法分析");
        syntaxButton.setFocusPainted(false);

        clearButton = new JButton(new ImageIcon("image/清空.png"));
        clearButton.setText("清空");
        clearButton.setFocusPainted(false);

        //添加事件监听器
        openButton.addActionListener(this);
        lexerButton.addActionListener(this);
        syntaxButton.addActionListener(this);
        clearButton.addActionListener(this);

        //添加按钮到工具栏
        toolBar.add(openButton);
        toolBar.add(lexerButton);
        toolBar.add(syntaxButton);
        toolBar.add(clearButton);

    }


    /**
     * 打开文件
     */
    public void openFile() {
        JFileChooser fileChooser = new JFileChooser("打开文件");
        //设置默认目录
        fileChooser.setCurrentDirectory(new File("c-language-source-file"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".c") || f.getName().endsWith(".cpp");
            }

            @Override
            public String getDescription() {
                return "C/C++源代码文件(*.c/*.cpp)";
            }
        });
        int isOpen = fileChooser.showOpenDialog(null);
        fileChooser.setDialogTitle("打开文件");
        if (isOpen == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getPath();
            fileContentTextArea.setText(readFromFile(path));
        }
    }

    /**
     * 读取文件
     */
    public String readFromFile(String path) {
        File file = new File(path);
        String s = null;
        try {
            FileInputStream fin = new FileInputStream(file);

            int length = fin.available();

            byte[] arr = new byte[length];

            int len = fin.read(arr);

            s = new String(arr, 0, len);


        } catch (IOException e) {

            e.printStackTrace();
        }
        return s;
    }


    /**
     * 清除文本框内容
     */
    public void doClear() {
        // 清空代码
        fileContentTextArea.setText(null);
        // 清空表
        DefaultTableModel defaultTableModel = (DefaultTableModel) resultTable.getModel();
        int rowCount = defaultTableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            defaultTableModel.removeRow(0);
        }
        // 清空输出
        consoleTextArea.setText(null);
    }

    /**
     * 词法分析
     */
    public void doTokenizing() {
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        Date date = new Date();
        System.out.println("\n时间：" + sdf.format(date));
        System.out.println("词法分析输出：");

        token.clear();

        ArrayList<Word> wordArrayList;
        ArrayList<UnidentifiableWord> ulist = new ArrayList<>();

        String codeStr, pretreatedStr, str;
        Word word;

        int opcodes = -1;
        int errorNum = 0;

        int count = 0;

        codeStr = fileContentTextArea.getText();
        if (codeStr.length() > 0) {
            consoleTextArea.setText(null);
            pretreatedStr = analyst.preFunction(codeStr);
            wordArrayList = analyst.divide(pretreatedStr);
            values = new String[wordArrayList.size()][5];

            ArrayList<String> tokenList = new ArrayList<>();

            while (wordArrayList.size() > 0) {
                word = wordArrayList.remove(0);
                str = word.getWord();
                int type = analyst.check(str);
                switch (type) {
                    // 数字串
                    case 1:
                        // 0：不可识别 52：常数
                        opcodes = analyst.checkDigit(str);
                        break;
                    // 单个字符
                    case 2:
                        opcodes = analyst.checkChar(str);
                        break;
                    // 其他字符串
                    case 3:
                        opcodes = analyst.checkString(str);
                        break;
                    default:
                        break;
                }

                if (opcodes == 0) {
                    UnidentifiableWord u = new UnidentifiableWord(word.getRow(), str);
                    ulist.add(u);
                    errorNum++;
                }

                if (opcodes >= 1 && opcodes <= 20) {
                    System.out.printf("<%2d, >\n", opcodes);
                    values[count][3] = "关键字";
                    values[count][4] = String.format("keyword[%s]", opcodes - 1);
                } else if (opcodes >= 21 && opcodes <= 40) {
                    System.out.printf("<%2d, >\n", opcodes);
                    values[count][3] = "运算符";
                    values[count][4] = String.format("operator[%s]", opcodes - 21);
                } else if (opcodes >= 41 && opcodes <= 51) {
                    System.out.printf("<%2d, >\n", opcodes);
                    values[count][3] = "分界符";
                    values[count][4] = String.format("delimiter[%s]", opcodes - 41);
                } else if (opcodes == 52) {

                    int strIndexInToken = tokenList.indexOf(str);
                    if (strIndexInToken != -1) {
                        System.out.printf("<%2d,token[%s]>\n", opcodes, strIndexInToken);
                    } else {
                        tokenList.add(str);
                        System.out.printf("<%2d,token[%s]>\n", opcodes, tokenList.size() - 1);
                    }

                    values[count][3] = "自定义标识符";
                    values[count][4] = "-";
                } else if (opcodes == 53) {
                    System.out.printf("<%2d,%s>\n", opcodes, Integer.toBinaryString(Integer.parseInt(str)));
                    values[count][3] = "常数";
                    values[count][4] = Integer.toBinaryString(Integer.parseInt(str));
                } else {
                    System.out.printf("<%2d,unknown>\n", opcodes);
                    values[count][3] = "未知";
                    values[count][4] = "-";
                }

                values[count][0] = String.valueOf(word.getRow());
                values[count][1] = str;
                values[count][2] = String.valueOf(opcodes);
                count++;
                this.token.add(new TokenItem(opcodes, word.getRow()));
            }

            //更新表格内容
            DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
            while (model.getRowCount() > 0) {
                model.removeRow(model.getRowCount() - 1);
            }

            model.setDataVector(values, title);

            resultTable = new JTable(model);

            consoleTextArea.append("共有 " + errorNum + " 处错误！" + "\n");
            while (ulist.size() > 0) {
                int r;
                String string;
                UnidentifiableWord uni = ulist.remove(0);
                r = uni.getRow();
                string = uni.getWord();
                consoleTextArea.append("第 " + r + " 行错误：" + string + "\n");
            }

        } else {
            JOptionPane.showConfirmDialog(this, "请输入 C/C++ 源代码！", "提示", JOptionPane.DEFAULT_OPTION);
        }

    }


    /**
     * 点击事件
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            openFile();
        } else if (e.getSource() == lexerButton) {
            doTokenizing();
        } else if (e.getSource() == syntaxButton) {
            doTokenizing();
            Integer row = SyntaxAnalyzer.doSyntaxAnalyzer(token);
            if (row == 0) {
                consoleTextArea.setText("代码符合当前语法规则！");
            } else {
                consoleTextArea.setText("语法错误发生在第 " + row + " 行！");
            }
        } else if (e.getSource() == clearButton) {
            doClear();
        }
    }

    /**
     * 构造方法
     */
    public LexerFrame() {
        //改变swing控件的风格为当前系统(windwos 10)风格的代码
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // 全局字体
        FontUIResource fontRes = new FontUIResource(new Font("微软雅黑", Font.PLAIN, 13));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }

        setTitle("词法语法分析器 - Designed by Zfans");

        //创建工具栏
        createToolBar();

        //左面板
        //左右面板
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("C/C++ 源代码"));

        fileContentTextArea = new JTextArea();
        fileContentTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        //滚动面板
        JScrollPane scrollPane1 = new JScrollPane(fileContentTextArea);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.getViewport().add(fileContentTextArea);
        scrollPane1.getViewport().setPreferredSize(new Dimension(350, 600));
        leftPanel.add(scrollPane1);

        //右面板
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());


        //分析后的内容放在表格里

        //表格模型
        DefaultTableModel model = new DefaultTableModel(values, title);
        resultTable = new JTable(model);
        resultTable.setEnabled(false);
        resultTable.setRowHeight(22);
        resultTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JScrollPane scrollPane2 = new JScrollPane(resultTable);
        scrollPane2.setBorder(BorderFactory.createTitledBorder("词法分析输出表"));
        scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane2.getViewport().add(resultTable);
        scrollPane2.getViewport().setPreferredSize(new Dimension(100, 400));

        //控制台
        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);
        consoleTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JScrollPane scrollPane3 = new JScrollPane(consoleTextArea);
        scrollPane3.setBorder(BorderFactory.createTitledBorder("提示输出"));
        scrollPane3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane3.getViewport().add(consoleTextArea);
        scrollPane3.getViewport().setPreferredSize(new Dimension(300, 100));

        //创建一个垂直分割面板（一）
        // 垂直分割面板（一）
        JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setTopComponent(scrollPane2);
        verticalSplitPane.setBottomComponent(scrollPane3);
        rightPanel.add("Center", verticalSplitPane);

        //创建一个水平分割面板（|）
        // 水平分割面板（|）
        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        //左面板
        horizontalSplitPane.setLeftComponent(leftPanel);

        //右面板
        horizontalSplitPane.setRightComponent(rightPanel);

        JPanel lexerPanel = new JPanel();

        lexerPanel.setLayout(new BorderLayout());
        lexerPanel.add("North", toolBar);
        lexerPanel.add("Center", horizontalSplitPane);

        //基本符号表开始---------------------------------------------------------------------------------------------------
        JPanel basicSymbolTablePanel = new JPanel();
        String[][] basicSymbolTableValues = new String[54][3];
        int row = 0;
        basicSymbolTableValues[row++] = new String[]{"0", "不可识别的标识符", "不可识别的标识符"};
        //关键字
        String[] keyWord = analyst.getKeyword();
        for (String s : keyWord) {
            basicSymbolTableValues[row] = new String[]{String.valueOf(row++), s, "关键字"};
        }
        //运算符
        String[] operator = analyst.getOperator();
        for (String s : operator) {
            basicSymbolTableValues[row] = new String[]{String.valueOf(row++), s, "运算符"};
        }
        //分界符
        String[] delimiter = analyst.getDelimiter();
        for (String s : delimiter) {
            basicSymbolTableValues[row] = new String[]{String.valueOf(row++), s, "分界符"};
        }
        basicSymbolTableValues[row] = new String[]{String.valueOf(row++), "用户自定义的标识符", "用户自定义的标识符"};
        basicSymbolTableValues[row] = new String[]{String.valueOf(row++), "常数", "常数"};
        DefaultTableModel basicSymbolTableModel = new DefaultTableModel(basicSymbolTableValues, new String[]{"符种", "标识符", "符号类型"});
        JTable basicSymbolTableTable = new JTable(basicSymbolTableModel);
        basicSymbolTableTable.setEnabled(false);
        basicSymbolTableTable.setRowHeight(22);
        basicSymbolTableTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JScrollPane scrollPane4 = new JScrollPane(basicSymbolTableTable);
        scrollPane4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane4.getViewport().add(basicSymbolTableTable);
        scrollPane4.getViewport().setPreferredSize(new Dimension(400, 400));
        basicSymbolTablePanel.setLayout(new BorderLayout());
        basicSymbolTablePanel.add(scrollPane4);
        //基本符号表结束---------------------------------------------------------------------------------------------------

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("分析", new ImageIcon("image/词法分析.png"), lexerPanel);
        tabbedPane.addTab("基本符号表", new ImageIcon("image/基本符号表.png"), basicSymbolTablePanel);
        tabbedPane.setFocusable(false);
        add("Center", tabbedPane);

        //设置运行时窗口的位置
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        setLocation((screenSize.width - frameSize.width) / 6, (screenSize.height - frameSize.height) / 6);


        setIconImage(Toolkit.getDefaultToolkit().getImage("image/词法分析器.png"));
        setSize(1000, 700);
        // setResizable(false);
        setVisible(true);
    }

    /**
     * 主方法
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        new LexerFrame();
    }
}
