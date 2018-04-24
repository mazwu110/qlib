package com.qlib.webcontents;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.qlib.R;
import com.qlib.base.BaseActivity;

public class LibWebActivity extends BaseActivity implements OnClickListener {
	private LinearLayout include_head;
	private WebView mWebView;
	private ProgressBar mProgressBar;
	private View mViewError;
	private static String inTitle = "";
	private String userid = "";
	private String sys_org_code = "";
	private String token = "";

	public static void startWebActivity(Context context, String title,
			String url) {
		inTitle = title;
		Intent intent = new Intent(context, LibWebActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		showLeftBack();

		include_head = (LinearLayout) findViewById(R.id.include_head);
		// if (inTitle.equals(""))
		include_head.setVisibility(View.GONE);

		mWebView = (WebView) findViewById(R.id.webview);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		mViewError = findViewById(R.id.ll_error);
		mViewError.setOnClickListener(this);

		InitComponents();
		initWbView();
	}

	@Override
	public void InitComponents() {
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String url = intent.getStringExtra("url");
		userid = intent.getStringExtra("userid");
		sys_org_code = intent.getStringExtra("sys_org_code");
		token = intent.getStringExtra("token");
		if (!title.equals("")) {
			include_head.setVisibility(View.VISIBLE);
			setTitle(title);
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("User-Agent", "Android");
		map.put("userid", userid);
		map.put("sys_org_code", sys_org_code);
		map.put("AuthToken", token);
		mWebView.loadUrl(url, map);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWbView() {
		WebSettings setting = mWebView.getSettings();
		// 启用支持javascript
		setting.setJavaScriptEnabled(true);
		setting.setDomStorageEnabled(true);
		setting.setCacheMode(WebSettings.LOAD_DEFAULT);
		setting.setUseWideViewPort(true);
		setting.setLoadWithOverviewMode(true);
		setting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					// 网页加载完成
					mProgressBar.setVisibility(View.GONE);
				} else {
					// 加载中
					mProgressBar.setProgress(newProgress);
				}
			}
		});

		// WebView加载web资源
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				mViewError.setVisibility(View.VISIBLE);
				mWebView.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.GONE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				mViewError.setVisibility(View.GONE);
				mWebView.setVisibility(View.INVISIBLE);
				mProgressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (mWebView.getVisibility() == View.INVISIBLE)
					mWebView.setVisibility(View.VISIBLE);

			}

		});
	}

	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void onClick(View arg0) {
		mWebView.loadUrl(mWebView.getUrl());
	}

}
