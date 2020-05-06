package com.miui.networkassistant.model;

public class VirtualSimInfo {
    private long mAssistBalance = Long.MIN_VALUE;
    private String mAssistBalanceTitle;
    private long mAssistCenter = -1;
    private String mAssistCenterTitle;
    private long mAssistKey1 = -1;
    private String mAssistKey1Title;
    private long mAssistKey2 = -1;
    private String mAssistKey2Title;

    public long getAssistBalance() {
        return this.mAssistBalance;
    }

    public String getAssistBalanceTitle() {
        return this.mAssistBalanceTitle;
    }

    public long getAssistCenter() {
        return this.mAssistCenter;
    }

    public String getAssistCenterTitle() {
        return this.mAssistCenterTitle;
    }

    public long getAssistKey1() {
        return this.mAssistKey1;
    }

    public String getAssistKey1Title() {
        return this.mAssistKey1Title;
    }

    public long getAssistKey2() {
        return this.mAssistKey2;
    }

    public String getAssistKey2Title() {
        return this.mAssistKey2Title;
    }

    public void setAssistBalance(long j) {
        this.mAssistBalance = j;
    }

    public void setAssistBalanceTitle(String str) {
        this.mAssistBalanceTitle = str;
    }

    public void setAssistCenter(long j) {
        this.mAssistCenter = j;
    }

    public void setAssistCenterTitle(String str) {
        this.mAssistCenterTitle = str;
    }

    public void setAssistKey1(long j) {
        this.mAssistKey1 = j;
    }

    public void setAssistKey1Title(String str) {
        this.mAssistKey1Title = str;
    }

    public void setAssistKey2(long j) {
        this.mAssistKey2 = j;
    }

    public void setAssistKey2Title(String str) {
        this.mAssistKey2Title = str;
    }
}
