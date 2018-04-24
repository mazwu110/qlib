package qlibtest.com.libtest.bean;


import com.qjsonlib.annotation.JsonName;

import java.io.Serializable;

/**
 * Created by mzw on 2018/3/24.
 */

public class IndexBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonName("content")
    private String content;
    @JsonName("coverImg")
    private String coverImg;
    @JsonName("id")
    private String id;
    @JsonName("dummyPrice")
    private int dummyPrice;
    @JsonName("name")
    private String name;
    @JsonName("sellPrice")
    private String sellPrice;
    @JsonName("storeNum")
    private String storeNum;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDummyPrice() {
        return dummyPrice;
    }

    public void setDummyPrice(int dummyPrice) {
        this.dummyPrice = dummyPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(String storeNum) {
        this.storeNum = storeNum;
    }
}
