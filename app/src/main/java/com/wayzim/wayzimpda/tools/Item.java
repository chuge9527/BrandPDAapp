package com.wayzim.wayzimpda.tools;

/**
 * Created by Jay on 2015/9/25 0025.
 */
public class Item {

    private String iId;//任务号
    private String iName;//物料名称

    public Item() {
    }

    public Item(String iId, String iName) {
        this.iId = iId;
        this.iName = iName;
    }

    public String getiId() {
        return iId;
    }

    public String getiName() {
        return iName;
    }

    public void setiId(String iId) {
        this.iId = iId;
    }

    public void setiName(String iName) {
        this.iName = iName;
    }
}
