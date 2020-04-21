package com.wayzim.wayzimpda.tools;

/**
 * Created by Jay on 2015/9/25 0025.
 */
public class Item {

    private String iId;//任务号
    private String iName;//物料名称

    private String iMCode;//物料编码
    private String iCount; //数量


    public Item() {
    }

    public Item(String iId, String iName) {
        this.iId = iId;
        this.iName = iName;
    }

    public Item(String iId, String iName, String iMCode, String iCount) {
        this.iId = iId;
        this.iName = iName;
        this.iMCode = iMCode;
        this.iCount = iCount;
    }

    public String getiId() {
        return iId;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public String getiName() {
        return iName;
    }

    public void setiName(String iName) {
        this.iName = iName;
    }

    public String getiMCode() {
        return iMCode;
    }

    public void setiMCode(String iMCode) {
        this.iMCode = iMCode;
    }

    public String getiCount() {
        return iCount;
    }

    public void setiCount(String iCount) {
        this.iCount = iCount;
    }
}
