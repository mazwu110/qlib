package qlibtest.com.libtest;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qlib.base.BaseActivity;
import com.qlib.libadapter.LoadMoreAdapterWrapper;
import com.qlib.libadapter.SwipeToLoadHelper;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import qlibtest.com.libtest.adapter.UserInfoAdapter;
import qlibtest.com.libtest.bean.UserInfo;

public class MainListActivity extends BaseActivity implements SwipeToLoadHelper.LoadQMoreListener,
        SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recyclerview)
    SwipeMenuRecyclerView recyclerview;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tv_right_bar)
    TextView tv_right_bar;
    private UserInfoAdapter mAdapter;
    private LoadMoreAdapterWrapper ldmAdapter; //加载更多类
    private SwipeToLoadHelper toLoadHelper; // 加载更多辅助类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("列表");
        showLeftBack();
        InitComponents();
    }

    @Override
    public void InitComponents() {
        tv_right_bar.setText("明细");
        tv_right_bar.setVisibility(View.VISIBLE);
        tv_right_bar.setOnClickListener(this);

        recyclerview.setSwipeMenuCreator(creator);
        recyclerview.setSwipeMenuItemClickListener(menuItemListener);
        mAdapter = new UserInfoAdapter(act_instance); // 1创建主适配器
        ldmAdapter = new LoadMoreAdapterWrapper(mAdapter); // 2 创建加载更多适配器
        recyclerview.setLayoutManager(new LinearLayoutManager(act_instance)); // 3设置recyclerview布局方式
        toLoadHelper = new SwipeToLoadHelper(recyclerview, ldmAdapter); //4 上滑加载更多操作辅助类
        toLoadHelper.setLoadMoreListener(this); // 5加载更多监听
        recyclerview.setAdapter(ldmAdapter); //6 给recyclerview 添加适配器

        mSwipeRefreshLayout.setOnRefreshListener(this); // 7 下拉刷新监听

        setData("李明天", true);

        mAdapter.setOnItemClickListener(new UserInfoAdapter.onItermClickListener() {
            @Override
            public void onItemClick(String id, String name, int position) {
                showToast("点击了" + name);
            }
        });
    }

    @Override // 下拉刷新调用
    public void onRefresh() {
        mAdapter.clearDatas(); // 下拉刷新要先删除数据
        // 刷新时禁用上拉加载更多
        toLoadHelper.setSwipeToLoadEnabled(false);
        toLoadHelper.setLoadMoreViewVisibility(false);// 限制加载更多数据功能不显示
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setData("朱常来", true);
                mSwipeRefreshLayout.setRefreshing(false);
                // 刷新完成时解禁上拉加载更多
                toLoadHelper.setSwipeToLoadEnabled(true);
            }
        }, 500);
    }

    @Override // 加载更多监听，从这里请求更多的网络数据
    public void onLoadMore() {
        mSwipeRefreshLayout.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setData("刘一刀", false);
               // Toast.makeText(act_instance, "没有更多数据", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setEnabled(true);
                toLoadHelper.setLoadMoreFinish();
                ldmAdapter.notifyDataSetChanged();
            }
        }, 500);
    }

    // true 第一个数据
    private void setData(String name, boolean flag) {
        List<UserInfo> data = new ArrayList();
        for (int i = 0; i < 10; i++) {
            UserInfo bean = new UserInfo();
            bean.setRealName(name + (i + 1));
            bean.setAge(32 + i);
            bean.setIntro("开心就好！");
            data.add(bean);
        }

        if (flag)
            mAdapter.addRecordList(data);
        else
            mAdapter.addMoreRecordList(data);
    }

    //菜单创建器
    private SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.user_photo_width);
            int heigh = ViewGroup.LayoutParams.MATCH_PARENT;
            //添加删除
            SwipeMenuItem delete = new SwipeMenuItem(act_instance)
                    .setBackground(R.color.red)
                    .setText("删除")
                    .setTextColor(getResources().getColor(R.color.white))
                    .setWidth(width)
                    .setHeight(heigh);

            SwipeMenuItem share = new SwipeMenuItem(act_instance)
                    .setBackground(R.color.blue)
                    .setText("分享")
                    .setTextColor(getResources().getColor(R.color.white))
                    .setWidth(width)
                    .setHeight(heigh);

            swipeRightMenu.addMenuItem(delete);
            swipeRightMenu.addMenuItem(share);
        }
    };

    //菜单item监听
    private SwipeMenuItemClickListener menuItemListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                showToast("点击了第" + menuPosition + "个菜单项");
            }
        }
    };

    @Override
    public void onClick(View view) {

    }
}
