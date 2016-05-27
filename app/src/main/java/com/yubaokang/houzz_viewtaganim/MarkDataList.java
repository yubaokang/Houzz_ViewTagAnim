package com.yubaokang.houzz_viewtaganim;

/**
 * 锚点列表对象
 * Created by Hank on 2016/4/22.
 */
public class MarkDataList {


    /**
     * id : 8
     * relativeX : 462
     * relativeY : 369
     */

    private int id;
    private int relativeX;
    private int relativeY;

    public MarkDataList(int id, int relativeX, int relativeY) {
        this.id = id;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }

    /**
     * id : 1030701
     * text : 物料标记
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRelativeX() {
        return relativeX;
    }

    public void setRelativeX(int relativeX) {
        this.relativeX = relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }

    public void setRelativeY(int relativeY) {
        this.relativeY = relativeY;
    }



}
