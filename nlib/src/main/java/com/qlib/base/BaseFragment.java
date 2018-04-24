package com.qlib.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qlib.R;
import com.qlib.XListView.XListView;
import com.qlib.components.LoadingDialog;
import com.qlib.fileutils.Utils;
import com.qlib.qinterface.QInterface;

public abstract class BaseFragment extends Fragment implements QInterface, OnClickListener {
    protected View layoutView;
    protected Context act_instance;
    protected LinearLayout ll_show_normal;
    protected LinearLayout ll_error;
    protected ImageView iv_error;
    protected TextView tv_error;
    protected int lvSelectRow = 0; // ListView 滚动到的行数
    protected boolean hasShowMsg = false; // true 已经显示提示没数据了
    protected View mViewError;
    protected MySharedPreferences msp;
    protected LoadingDialog loadingDialog; // 网络加载对话框
    private Toast mToast;
    protected LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        act_instance = getActivity();
        msp = new MySharedPreferences(act_instance);
        loadingDialog = new LoadingDialog(act_instance);
        return null;
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
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(BaseActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myview.getWindowToken(), 0);
    }

    protected void showKeyboard(final View myview) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(BaseActivity.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(myview.getWindowToken(), InputMethodManager.SHOW_FORCED);
    }

    public void setTitle(CharSequence title) {
        TextView tvTitle = (TextView) layoutView.findViewById(R.id.title);
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    // 设置顶部条栏左返回按钮
    public void showLeftBack() {
        ImageView ivLeft = (ImageView) layoutView.findViewById(R.id.iv_back);
        if (ivLeft == null) {
            return;
        }

        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setImageResource(R.drawable.ic_back);
        ivLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();// 调用返回按钮
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

    // 母版ID errId
    public void showNetError(OnClickListener listener, int errId) {
        mViewError = layoutView.findViewById(errId);
        mViewError.setVisibility(View.VISIBLE);
        mViewError.setOnClickListener(listener);
    }

    public void hideNetError() {
        if (mViewError != null && mViewError.getVisibility() == View.VISIBLE)
            mViewError.setVisibility(View.GONE);
    }

    // 母版ID:baseResId， 显示内容ID:resId
    public void showNothing(String text, int baseResId, int resId) {
        mViewError = layoutView.findViewById(baseResId);
        mViewError.setVisibility(View.VISIBLE);
        TextView mTVError = (TextView) layoutView.findViewById(resId);
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

    public void setTitle(int resId) {
        setTitle(this.getResources().getString(resId));
    }

    public void setTitle(CharSequence title, int resId) {
        TextView tvTitle = (TextView) layoutView.findViewById(resId);
        if (tvTitle != null)
            tvTitle.setText(title);
    }

    /**
     * 设置顶部条栏左返回按钮
     */
    public void showLeftBack(int resId) {
        ImageView ivLeft = (ImageView) layoutView.findViewById(resId);
        if (ivLeft == null) {
            return;
        }

        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setImageResource(R.drawable.ic_back);
        ivLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();// 调用返回按钮
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T $(int resId) {
        return (T) this.layoutView.findViewById(resId);
    }
}
