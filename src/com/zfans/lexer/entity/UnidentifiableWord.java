package com.zfans.lexer.entity;

/**
 * 不可以识别的标识符
 */
public class UnidentifiableWord {

    //不可以识别的标识符行
    private final int row;

    //获取到的不可以识别的标识符
    private final String word;

    public UnidentifiableWord(int row, String word) {
        this.row = row;
        this.word = word;
    }

    public int getRow() {
        return row;
    }


    public String getWord() {
        return word;
    }


}
