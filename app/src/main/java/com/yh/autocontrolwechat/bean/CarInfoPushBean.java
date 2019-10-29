package com.yh.autocontrolwechat.bean;

import java.util.List;

public class CarInfoPushBean {

    /**
     * title : 阿斯顿·马丁 阿斯顿马丁Rapide 2014款 6.0L S 百年纪念版
     * iamges : ["http://test.appcar.checheng.net/uploads/201910/img_2019102416360067.jpg","http://test.appcar.checheng.net/uploads/201910/img_2019102416355585.png"]
     * buy_date : 2019-10-24 00:00:00
     * mile_age : 11
     * color : 白色
     * transfer_time : 22
     * tellphone : 18514204494
     * city : 江苏省/无锡市/锡山区
     * price : 11
     * source : 二手车淘车
     */

    private String title;
    private String buy_date;
    private int mile_age;
    private String color;
    private int transfer_time;
    private String tellphone;
    private String city;
    private String price;
    private String source;
    private List<String> iamges;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBuy_date() {
        return buy_date;
    }

    public void setBuy_date(String buy_date) {
        this.buy_date = buy_date;
    }

    public int getMile_age() {
        return mile_age;
    }

    public void setMile_age(int mile_age) {
        this.mile_age = mile_age;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getTransfer_time() {
        return transfer_time;
    }

    public void setTransfer_time(int transfer_time) {
        this.transfer_time = transfer_time;
    }

    public String getTellphone() {
        return tellphone;
    }

    public void setTellphone(String tellphone) {
        this.tellphone = tellphone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getIamges() {
        return iamges;
    }

    public void setIamges(List<String> iamges) {
        this.iamges = iamges;
    }
}
