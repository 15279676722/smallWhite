package com.example.smallwhite.jvm.chapter08;

public class EscapeAnalysis {
    private EscapeAnalysis escapeAnalysis;

    /**
     * 方法返回EscapeAnalysis对象发生逃逸
     * */
    public EscapeAnalysis getInstance(){
        return escapeAnalysis == null?new EscapeAnalysis():escapeAnalysis;
    }
    /**
     * 为成员属性赋值发生逃逸
     * */
    public void setEscapeAnalysis(){
        escapeAnalysis = new EscapeAnalysis();
    }
    /**
     * 对象作用域只在当前方法有效没有发生逃逸
     * */
    public void useEscapeAnalysis(){
        EscapeAnalysis escapeAnalysis = new EscapeAnalysis();
    }

}
