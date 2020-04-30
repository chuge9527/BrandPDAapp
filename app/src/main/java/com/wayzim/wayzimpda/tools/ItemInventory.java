package com.wayzim.wayzimpda.tools;

public class ItemInventory {
    private String iTaskId;
    private String ibinId;//库位号
    private String iTrayCode;
    private String iName;//物料名称
    private String iMCode;//物料编码
    private String iCount; //数量

    public String getiTaskId() {
        return iTaskId;
    }

    public void setiTaskId(String iTaskId) {
        this.iTaskId = iTaskId;
    }

    public String getIbinId() {
        return ibinId;
    }

    public String getiTrayCode() {
        return iTrayCode;
    }

    public void setiTrayCode(String iTrayCode) {
        this.iTrayCode = iTrayCode;
    }

    public void setIbinId(String ibinId) {
        this.ibinId = ibinId;
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

    @Override
    public String toString() {
        return "ItemInventory{" +
                "iTaskId='" + iTaskId + '\'' +
                ", ibinId='" + ibinId + '\'' +
                ", iTrayCode='" + iTrayCode + '\'' +
                ", iName='" + iName + '\'' +
                ", iMCode='" + iMCode + '\'' +
                ", iCount='" + iCount + '\'' +
                '}';
    }
}
