package com.android.be_gain.models;

public class ModelQuiz {

    String question;
    String QA;
    String QB;
    String QC;
    String QD;
    String ans;

    public ModelQuiz(String question, String QA, String QB, String QC, String QD, String ans) {
        this.question = question;
        this.QA = QA;
        this.QB = QB;
        this.QC = QC;
        this.QD = QD;
        this.ans = ans;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQA() {
        return QA;
    }

    public void setQA(String QA) {
        this.QA = QA;
    }

    public String getQB() {
        return QB;
    }

    public void setQB(String QB) {
        this.QB = QB;
    }

    public String getQC() {
        return QC;
    }

    public void setQC(String QC) {
        this.QC = QC;
    }

    public String getQD() {
        return QD;
    }

    public void setQD(String QD) {
        this.QD = QD;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }
}
