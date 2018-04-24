package qlibtest.com.libtest;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qlib.base.BaseActivity;
import com.qlib.qhttputils.QHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;
import qlibtest.com.libtest.bean.IndexBean;
import qlibtest.com.libtest.bean.IndexDataBean;

public class SeverDataActivity extends BaseActivity implements View.OnClickListener, QHttpUtils.onHttpListener {
    public final int REQUEST_CODE_INDEX = 1;
    @BindView(R.id.tv_data)
    TextView tv_data;
    private String url = "http://mf.xbsms.com/miaofeng/webapp/index?pageIndex=1&pageSize=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_severdata);
        ButterKnife.bind(this);
        setTitle("从服务器获取数据");
        showLeftBack();
        getData();
    }

    private void getData() {
        showLoadingDialog();
        Map<String, Object> params = new HashMap();
        params.put("pageIndex", 1);
        params.put("pageSize", 2);
        QHttpUtils.get(url, params, IndexDataBean.class, REQUEST_CODE_INDEX, this); // 发送请求
    }

    // IndexDataBean 和IndexDataBean2的定义，大家可以结合底部的testStr 和testStr2的结构看看就明白了，要不会取不到数据的哦
    @Override
    public void httpSuccess(int what, Object result, Response response) {
        switch (what) {
            case REQUEST_CODE_INDEX: // 结果返回处理
                hideLoadingDialog();
                IndexDataBean bean = (IndexDataBean) result;
                ArrayList<IndexBean> list = bean.getList();
                if (list.size() <= 0)
                    return;

                IndexBean data = list.get(0); // 列表中的每条数据
                String tpname = data.getName();
                showToast("返回数据了:" + tpname);
                tv_data.setText(tpname);
                break;
            default:
                break;
        }
    }

    @Override
    public void httpFailure(int what, Object result, Response response) {
        showToast(result.toString() + "");
    }

    @Override
    public void onClick(View v) {

    }

    // 此QHttpUtils库也支持解析如下的数据格式，如下数据格式testStr2,GSON是解析不了的哦,大家可以试试，GSON解析不出来list中的数据，所以我采用大神Mark Reinhold的方式
    private static String testStr2 = "{'data': {'list':[{'name': '苗风追风贴','sellPrice': '58','storeNum': '1000'}]},'message': '查询成功','success': true}";
    private static String testStr = "{'data': [{'name': '苗风追风贴','sellPrice': '58','storeNum': '1000'}],'message': '查询成功','success': true}";


    @Override
    public void InitComponents() {

    }

}
