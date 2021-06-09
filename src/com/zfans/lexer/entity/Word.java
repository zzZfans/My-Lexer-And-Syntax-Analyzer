package com.zfans.lexer.entity;

/**
 * 可以识别的标识符
 */
public class Word {

    //标识符所在的行
    private final int row;

    //获取到的标识符
    private final String word;

    public Word(int row, String word) {
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