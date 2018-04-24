package qlibtest.com.libtest.bean;

import com.qjsonlib.annotation.JsonName;
import com.qlib.baseBean.BaseBean;

import java.io.Serializable;

/**
 * Created by mzw on 2018/3/24.
 */

public class IndexDataBean2 extends BaseBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonName("data") // 多层的话还可以在内部继续
    private IndexDataBean2Inner list;

    public IndexDataBean2Inner getList() {
        return list;
    }

    public void setList(IndexDataBean2Inner list) {
        this.list = list;
    }
}
