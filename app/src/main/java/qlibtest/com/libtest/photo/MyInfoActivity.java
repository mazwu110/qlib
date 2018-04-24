package qlibtest.com.libtest.photo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.qlib.base.BaseActivity;
import com.qlib.components.CircleImageView;
import com.qlib.components.MulcCameraPopWindow;
import com.qlib.components.MulcCameraPopWindow.OnMulcBitmapListener;
import com.qlib.components.PopListWindow;
import com.qlib.qremote.QApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import qlibtest.com.libtest.R;

public class MyInfoActivity extends BaseActivity implements OnMulcBitmapListener {
    @BindView(R.id.iv_head)
    CircleImageView iv_head;
    private MulcCameraPopWindow cameraPop; // 图片
    private PopListWindow pop_sex;
    private List<String> photoList = new ArrayList(); // 图片路径
    private String sex = "";
    private String myheadimg = "";
    private String headImg = ""; // 头像回传
    private String rzID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        ButterKnife.bind(this);
        rzID = getIntent().getStringExtra("rzID");
        sex = msp.getString("sex");
        myheadimg = msp.getString("headimg");
        setTitle("编辑我的资料");
        showLeftBack();
        InitComponents();
    }

    @Override
    public void InitComponents() {
        cameraPop = new MulcCameraPopWindow(this, this);
        iv_head = (CircleImageView) findViewById(R.id.iv_head);

        findViewById(R.id.ll_head).setOnClickListener(this);
        findViewById(R.id.tv_sex).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_head:
                openCameraPop();
                break;
        }
    }

    private void openCameraPop() {
        forceHideKeyboard(findViewById(R.id.layout));
        cameraPop.setEnableCrop(true, false); // 单选，裁剪, 此两参数是控制单选还是多选等，多选第一个参数false即可
        cameraPop.showAtLocation(findViewById(R.id.layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void mulcbitmapResult(String filePath, boolean mutiSelect) {
        if (TextUtils.isEmpty(filePath)) {
            showToast("选择的文件有误，请稍后再试");
            return;
        }

        File file = new File(filePath);
        if (file != null && file.exists()) {
            photoList = new ArrayList(); // 删除全部
            photoList.add(filePath);
            QApp.mImageLoader.displayLocalFileImage(iv_head, filePath);
        }

        if (cameraPop != null && cameraPop.isShowing()) {
            cameraPop.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cameraPop.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        cameraPop.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
