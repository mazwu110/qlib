package com.qlib.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qlib.R;
import com.qlib.XListView.XListView;
import com.qlib.barutils.SystemBarUtils;
import com.qlib.components.LoadingDialog;
import com.qlib.fileutils.Utils;
import com.qlib.qinterface.QInterface;
import com.qlib.qremote.QApp;

public abstract class BaseActivity extends Activity implements QInterface, OnClickListener {//AppCompatActivity
    protected Context act_instance;
    protected LinearLayout ll_show_normal;
    protected LinearLayout ll_error;
    protected ImageView iv_error;
    protected TextView tv_error;
    protected int lvSelectRow = 0; // ListView 滚动到的行数
    protected boolean hasShowMsg = false; // true 已经显示提示没数据了
    protected View mViewError;
    protected MySharedPreferences msp;
    private Toast mToast;
    protected LoadingDialog loadingDialog; // 网络加载对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act_instance = this;
        msp = new MySharedPreferences(act_instance);
        loadingDialog = new LoadingDialog(act_instance);
        SystemBarUtils.setSystemBarStyle(this, QApp.systemBarStyle); // 统一设置沉寂式状态栏
    }

    protected void showNetErrInfo(String msg) {
        ll_show_normal.setVisibility(View.GONE);
        ll_error.setVisibility(View.VISIBLE);
        tv_error.setText(msg);
    }

    protected void hideNetErrInfo() {
        ll_show_normal.setVisibility(View.VISIBLE);
        ll_error.setVisibility(View.GONE);
    }

    protected void hideAllLayout() {
        ll_show_normal.setVisibility(View.GONE);
        ll_error.setVisibility(View.GONE);
    }

    // 刷新数据后一定要加onLoad()方法，否则刷新会一直进行，根本停不下来
    protected void onLoad(XListView mListView) {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(Utils.getCurrentTime());
    }

    // 显示网络加载对话框
    protected void showLoadingDialog() {
        if (loadingDialog == null)
            loadingDialog = new LoadingDialog(act_instance);

        if (!loadingDialog.isShowing())
            loadingDialog.show();
    }

    // 显示网络加载对话框
    protected void showLoadingDialog(String msg) {
        if (loadingDialog != null)
            loadingDialog = null;
        loadingDialog = new LoadingDialog(act_instance, msg);
        loadingDialog.show();
    }

    // 隐藏网络加载对话框
    protected void hideLoadingDialog() {
        if (loadingDialog != null)
            loadingDialog.cancel();
    }

    /**
     * 设置隐藏输入法
     */
    protected void hideKeyboard(final View myview) {
        InputMethodManager imm = (InputMethodManager) getSystemService(BaseActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myview.getWindowToken(), 0);
    }

    protected void showKeyboard(final View myview) {
        InputMethodManager imm = (InputMethodManager) getSystemService(BaseActivity.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(myview.getWindowToken(), InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = (TextView) findViewById(R.id.title);
        if (tvTitle == null) {
            super.setTitle(title);
        } else {
            tvTitle.setText(title);
        }
    }

    // 设置顶部条栏左返回按钮
    public void showLeftBack() {
        ImageView ivLeft = (ImageView) findViewById(R.id.iv_back);
        if (ivLeft == null) {
            return;
        }

        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setImageResource(R.drawable.ic_back);
        ivLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();// 调用返回按钮
            }
        });
    }

    public static void logD(String tag, String msg) {
        Log.d(tag, msg);
    }

    protected void showToast(String content) {
        if (mToast == null) {
            mToast = Toast.makeText(act_instance, content, Toast.LENGTH_LONG);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }

    public void showNetError(OnClickListener listener, int resId) {
        mViewError = findViewById(resId);
        mViewError.setVisibility(View.VISIBLE);
        mViewError.setOnClickListener(listener);
    }

    public void hideNetError() {
        if (mViewError != null && mViewError.getVisibility() == View.VISIBLE) {
            mViewError.setVisibility(View.GONE);
        }
    }

    // 母版ID:baseResId， 显示内容ID:resId
    public void showNothing(String text, int baseResId, int resId) {
        mViewError = findViewById(baseResId);
        mViewError.setVisibility(View.VISIBLE);
        TextView mTVError = (TextView) findViewById(resId);
        mTVError.setText(text);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setTitle(int resId) {
        setTitle(this.getResources().getString(resId));
    }

    public void setTitle(int resId, CharSequence title) {
        TextView tvTitle = (TextView) findViewById(resId);
        if (tvTitle == null) {
            super.setTitle(title);
        } else {
            tvTitle.setText(title);
        }
    }

    /**
     * 设置顶部条栏左返回按钮
     */
    public void showLeftBack(int resId) {
        ImageView ivLeft = (ImageView) findViewById(resId);
        if (ivLeft == null) {
            return;
        }

        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setImageResource(R.drawable.ic_back);
        ivLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();// 调用返回按钮
            }
        });
    }

    // 强制关闭键盘
    protected void forceHideKeyboard(final View myview) {
        InputMethodManager imm = (InputMethodManager) getSystemService(BaseActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myview.getWindowToken(), 0);
    }

    // 强制打开键盘
    protected void forceShowKeyboard(final View myview) {
        InputMethodManager imm = (InputMethodManager) getSystemService(BaseActivity.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(myview.getWindowToken(), 0);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }
}
