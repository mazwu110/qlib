package qlibtest.com.libtest;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.qlib.XListView.XListView;
import com.qlib.XListView.XListView.IXListViewListener;
import com.qlib.base.BaseBannerActivity;
import com.qlib.components.ViewPagerIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BannerActivity extends BaseBannerActivity implements IXListViewListener {
	private XListView listview;
	private int page = 1;
	private int errType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_banner);
		setTitle("横幅");
		InitComponents();
		showLeftBack();
	}

	@Override
	public void InitComponents() {
		id_banner = (LinearLayout) findViewById(R.id.id_banner);
		listview = (XListView) findViewById(R.id.listview);
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this);

		findViewById(R.id.llabort).setOnClickListener(this);

		mViewPager = (ViewPager) findViewById(R.id.vp_banner);
		indicator = (ViewPagerIndicator) findViewById(R.id.indicator);
		super.InitComponents();
		setBannerData();
	}


	private void setBannerData() {
		bannerData = new JSONArray();
		for (int i = 0; i < 4; i++) {
			JSONObject js = new JSONObject();
			try {
				js.put("title", i + "");
				js.put("img_url", imgUlr[i]);
				js.put("linkurl", "");
				bannerData.put(js);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		setBanner();
	}

	private String[] imgUlr = {
			"https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=989341999,236255160&fm=27&gp=0.jpg",
			"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=77186311,257125688&fm=27&gp=0.jpg",
			"https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2715417528,1907297180&fm=27&gp=0.jpg",
			"https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3893629247,3126033403&fm=27&gp=0.jpg"
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_error:
			break;
		case R.id.llabort:
			break;
		}
	}

	@Override
	public void onRefresh() {
		lvSelectRow = 0;
		page = 1;
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(true);
		onLoad(listview);
	}

	@Override
	public void onLoadMore() {
		lvSelectRow = listview.getCount() - 1;
		page += 1;
		onLoad(listview);
	}
}
