package com.qlib.webcontents;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qlib.R;
import com.qlib.base.BaseActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ShowWebContents extends BaseActivity {
	private String id = "";
	private String idname = "";
	private WebView webViewContent;
	private String dataURL = "";
	private String userid = "";
	private WebViewClient tt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showwebcontents);
		userid = getIntent().getStringExtra("userid");
		id = getIntent().getStringExtra("id");
		idname = getIntent().getStringExtra("idname");
		dataURL = getIntent().getStringExtra("dataURL");
		setTitle(getIntent().getStringExtra("title"));
		showLeftBack();
		InitComponents();
	}

	@Override
	public void InitComponents() {
		webViewContent = (WebView) findViewById(R.id.webViewContent);
		webViewContent.setVerticalScrollbarOverlay(true); // 指定的垂直滚动条有叠加样式
		// webViewContent.loadUrl("http://v.qq.com/iframe/player.html?vid=s0024o4a7c9&tiny=0&auto=0");

		WebSettings setting = webViewContent.getSettings();
		// setting.setSupportZoom(true);
		// setting.setTextSize(TextSize.NORMAL);

		// 启用支持javascript
		setting.setJavaScriptEnabled(true);
		setting.setDomStorageEnabled(true);
		setting.setCacheMode(WebSettings.LOAD_DEFAULT);
		// setting.setUseWideViewPort(true);
		setting.setLoadWithOverviewMode(true);
		setting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		getHttpData();
	}

	private void getHttpData() {
		Map params = new HashMap();
		params.put("userid", userid);
		params.put("userid", userid);
		params.put("aid", id);
		if (!TextUtils.isEmpty(idname))
			params.put(idname, id);
		
//		showLoadingDialog();
//		qHttpGet(dataURL, params, new QHttpRequestBack() {
//			@Override
//			public void onQhttpFailure() {
//				hideLoadingDialog();
//			}
//
//			@Override
//			public void onQhttpSuccess(JSONObject response) {
//				hideLoadingDialog();
//				try {
//					if (response.getString("success").equals("true")) {
//						JSONArray arr = response.getJSONArray("data");
//						if (arr.length() <= 0) {
//							showToast("未查询到详情信息");
//							finish();
//						}
//						JSONObject js = arr.getJSONObject(0);
//						String contents = js.getString("contents");
//						contents = getNewContent(contents);
//						contents = contents.replaceAll("https://", "http://");
//						int int640 = 640;
//						int int498 = 498;
//						float scrWd = QUtils.getScreenWitdh(act_instance) / 2;
//						float mdvalue = int640 / scrWd;
//						scrWd -= 15; // 屏幕宽度
//						int newht = (int)(int498 / mdvalue);
//						newht -= 30;
//						contents = contents.replaceAll(int640 + "", scrWd + "");
//						contents = contents.replaceAll(int498 + "", newht + "");
//
//						webViewContent.loadDataWithBaseURL("www.webdata.html",
//								contents, "text/html", "utf-8", null);
//					} else {
//						showToast(response.getString("message"));
//						finish();
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}

	private String getNewContent(String htmltext) {
		Document doc;
		try {
			doc = Jsoup.parse(htmltext);
			Elements elements = doc.getElementsByTag("img");
			for (Element element : elements) {
				element.attr("style", "width:100%;height:auto;");
			}
			return doc.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onPause() {
		try { // 停止播放
			webViewContent.getClass().getMethod("onPause")
					.invoke(webViewContent, (Object[]) null);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onPause();
	}
}
