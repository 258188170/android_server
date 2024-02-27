package com.card.lp_server.card.device.util;


//刷屏幕对象
public class HidScreenInfo {
    public HidScreenInfo() {
    }

    public HidScreenInfo(String dyNumber, String dyvModel, String acpetDate, String qualityLevel, String sn) {
        this.dyNumber = dyNumber;
        this.dyvModel = dyvModel;
        this.acpetDate = acpetDate;
        this.qualityLevel = qualityLevel;
        this.sn = sn;
    }

    //编号
    private String dyNumber;
    //型号
    private String dyvModel;
    //军检验收日期
    private String acpetDate;
    //质量等级
    private String qualityLevel;
    //状态
    private String sn;

    public String getDyNumber() {
        return dyNumber;
    }

    public void setDyNumber(String dyNumber) {
        this.dyNumber = dyNumber;
    }

    public String getDyvModel() {
        return dyvModel;
    }

    public void setDyvModel(String dyvModel) {
        this.dyvModel = dyvModel;
    }

    public String getAcpetDate() {
        return acpetDate;
    }

    public void setAcpetDate(String acpetDate) {
        this.acpetDate = acpetDate;
    }

    public String getQualityLevel() {
        return qualityLevel;
    }

    public void setQualityLevel(String qualityLevel) {
        this.qualityLevel = qualityLevel;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
