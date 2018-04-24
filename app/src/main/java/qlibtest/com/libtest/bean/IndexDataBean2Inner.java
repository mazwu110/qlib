package qlibtest.com.libtest.bean;

import com.qjsonlib.annotation.JsonName;
import com.qlib.baseBean.BaseBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mzw on 2018/3/24.
 */

public class IndexDataBean2Inner extends BaseBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonName("list") // 多层的话还可以在内部继续
    private ArrayList<IndexBean> list;


    public ArrayList<IndexBean> getList() {
        return list;
    }

    public void setList(ArrayList<IndexBean> list) {
        this.list = list;
    }
}
