package qlibtest.com.libtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qlib.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qlibtest.com.libtest.photo.MyInfoActivity;

/**
 * Created by 12865 on 2018/4/24.
 */

public class HomeActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tv_right_bar)
    TextView tvRightBar;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_right_sec)
    ImageView ivRightSec;
    @BindView(R.id.title_main)
    RelativeLayout titleMain;
    @BindView(R.id.include_head)
    LinearLayout includeHead;
    @BindView(R.id.btn_list)
    Button btnList;
    @BindView(R.id.btn_photo)
    Button btnPhoto;
    @BindView(R.id.btn_share)
    Button btnShare;
    @BindView(R.id.btn_umeng)
    Button btnUmeng;
    @BindView(R.id.btn_banner)
    Button btnBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setTitle("qlibdemo首页");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void InitComponents() {

    }

    @OnClick({R.id.btn_list, R.id.btn_photo, R.id.btn_share,
            R.id.btn_umeng, R.id.btn_banner, R.id.btn_sever})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_list:
                intent = new Intent(act_instance, MainListActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_photo:
                intent = new Intent(act_instance, MyInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_share:
                break;
            case R.id.btn_umeng:
                break;
            case R.id.btn_banner:
                intent = new Intent(act_instance, BannerActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_sever:
                intent = new Intent(act_instance, SeverDataActivity.class);
                startActivity(intent);
                break;
        }
    }
}
