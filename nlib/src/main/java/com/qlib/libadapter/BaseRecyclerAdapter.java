package com.qlib.libadapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.qlib.qutils.QLogger;
import com.qlib.qutils.QUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzw on 2018/3/26.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements BaseViewHolder.OnNotifyChangeListener {
    private List<T> mRecordList = new ArrayList();
    private boolean enableHead = false;
    private ViewGroup rootView;
    private BaseViewHolder headHolder;
    public final static int TYPE_HEAD = 0;
    public static final int TYPE_CONTENT = 1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        rootView = parent;
        //设置ViewHolder
        int type = getItemViewType(position);
        if (type == TYPE_HEAD) {
            return headHolder;
        } else {
            return setViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (enableHead) {
            if (position == 0) {
                ((BaseViewHolder) holder).bindHeadData();
            } else {
                ((BaseViewHolder) holder).bindData(mRecordList.get(position - 1), position);
            }
        } else {
            ((BaseViewHolder) holder).bindData(mRecordList.get(position), position);
        }

        ((BaseViewHolder) holder).setOnNotifyChangeListener(this);
    }

    public ViewGroup getRootView() {
        return rootView;
    }

    public List<T> getRecordList() {
        return mRecordList;
    }

    @Override
    public int getItemCount() {
        if (enableHead) {
            return mRecordList.size() + 1;
        }
        return mRecordList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (enableHead) {
            if (position == 0) {
                return TYPE_HEAD;
            } else {
                return TYPE_CONTENT;
            }
        } else {
            return TYPE_CONTENT;
        }
    }

    private int lastAnimatedPosition = -1;
    protected boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;
        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(QUtils.dp2px(view.getContext(), 100));//(position+1)*50f
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    }).start();
        }
    }

    @Override
    public void onNotify() {
        //提供给BaseViewHolder方便刷新视图
        notifyDataSetChanged();
    }

    //添加单条数据
    public void addOneRecordList(T data) {
        if (null == data) return;
        mRecordList.add(data);
        notifyDataSetChanged();
    }

    //添加第一页数据
    public void addRecordList(List<T> datas) {
        mRecordList.clear();
        if (null != datas) {
            mRecordList.addAll(datas);
        }
        notifyDataSetChanged();
    }

    // 添加更多数据
    public void addMoreRecordList(List<T> datas) {
        if (null != datas) {
            mRecordList.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public void clearDatas() {
        mRecordList.clear();
        notifyDataSetChanged();
    }


    public T getItem(int position) {
        if (mRecordList != null && position < mRecordList.size()) {
            return mRecordList.get(position);
        } else {
            return null;
        }
    }

    //删除单条数据
    public void deletItem(T data) {
        mRecordList.remove(data);
        QLogger.d("deletItem: ", mRecordList.remove(data) + "");
        notifyDataSetChanged();
    }

    //设置是否显示head
    public void setEnableHead(boolean isEnable) {
        enableHead = isEnable;
    }

    public void setHeadHolder(BaseViewHolder headHolder) {
        enableHead = true;
        this.headHolder = headHolder;
    }

    public void setHeadHolder(View itemView) {
        enableHead = true;
        headHolder = new HeadHolder(itemView);
        notifyItemInserted(0);
    }

    public BaseViewHolder getHeadHolder() {
        return headHolder;
    }

    /**
     * 子类重写实现自定义ViewHolder
     */
    public abstract BaseViewHolder<T> setViewHolder(ViewGroup parent);

    public class HeadHolder extends BaseViewHolder<Void> {
        public HeadHolder(View itemView) {
            super(itemView);
        }

        public HeadHolder(Context context, ViewGroup root, int layoutRes) {
            super(context, root, layoutRes);
        }

        @Override
        public void bindData(Void aVoid, int position) {//不用实现
        }
    }
}
