package qlibtest.com.libtest.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qlib.libadapter.BaseRecyclerAdapter;
import com.qlib.libadapter.BaseViewHolder;

import butterknife.BindView;
import qlibtest.com.libtest.R;
import qlibtest.com.libtest.bean.UserInfo;

public class UserInfoAdapter extends BaseRecyclerAdapter<UserInfo> {
    private onItermClickListener listener;
    private Context context;

    public UserInfoAdapter(Context context) {
        this.context = context;
    }

    public interface itemClickListener {
        void onItemClick(View view, int position, String ClientId, int CustomerId, int AreaId, String name, String productCode);
    }

    public void setOnItemClickListener(onItermClickListener listener) {
        this.listener = listener;
    }


    public interface  onItermClickListener {
        public void onItemClick(String id, String name, int position);
    }

    @Override
    public BaseViewHolder<UserInfo> setViewHolder(ViewGroup parent) {
        return new UserInfoHolder(parent.getContext(), parent);
    }

    class UserInfoHolder extends BaseViewHolder<UserInfo> {
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_age)
        TextView tv_age;
        @BindView(R.id.tv_desc)
        TextView tv_desc;

        public UserInfoHolder(Context context, ViewGroup root) {
            super(context, root, R.layout.item_userinfo);
        }

        @Override
        public void bindData(UserInfo bean, int positon) {
            tv_name.setText(bean.getRealName());
            tv_age.setText(bean.getAge() + "");
            tv_desc.setText(bean.getIntro());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(bean.getId(), bean.getRealName(), positon);
                    }
                }
            });
        }
    }

    public class FooterViewHolder extends BaseViewHolder<Void> {
        @BindView(R.id.xlistview_footer_progressbar)
        ProgressBar mPbLoad;
        @BindView(R.id.xlistview_footer_hint_textview)
        TextView mTvLoadText;
        @BindView(R.id.xlistview_footer_content)
        RelativeLayout mLoadLayout;
        public FooterViewHolder(Context context, ViewGroup root) {
            super(context, root, R.layout.xlistview_footer);
        }

        @Override
        public void bindData(Void aVoid, int position) {
        }
    }
}
