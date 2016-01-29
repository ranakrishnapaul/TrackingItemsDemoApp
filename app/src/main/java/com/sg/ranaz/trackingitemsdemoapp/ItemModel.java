/* Author: Rana
 * email id: ranakrishnapaul@gmail.com
 * skype id: rana.krishna.paul
 * File Name: ItemModel.java
 * This is a POJO Class for Item which has getter() & Setter()
 *
 *  */

package com.sg.ranaz.trackingitemsdemoapp;



public class ItemModel {

    //Variables,Objects Declaration
    private String title;
    private String thumbnail;
    private long id;
    private String desc;
    private String loc;
    private String cost;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getID() {
        return id;
    }
    public void setID(long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLoc() {
        return loc;
    }
    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getCost() {
        return cost;
    }
    public void setCost(String cost) {
        this.cost = cost;
    }

}
