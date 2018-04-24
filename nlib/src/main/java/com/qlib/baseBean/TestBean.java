package com.qlib.baseBean;

import java.io.Serializable;
import java.util.ArrayList;


public class TestBean extends BaseBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<TestItemBean> list;

    public ArrayList<TestItemBean> getList() {
        return list;
    }

    public void setList(ArrayList<TestItemBean> list) {
        this.list = list;
    }

}
