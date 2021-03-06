package com.jude.easyrecyclerview;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

public class EasyNoRefreshRecyclerView extends FrameLayout {
    public static final String TAG = "EasyNoRefreshRecyclerView";
    public static final boolean DEBUG = false;
    protected RecyclerView mRecycler;
    protected ViewGroup mProgressView;
    protected ViewGroup mEmptyView;
    protected ViewGroup mErrorView;
    private int mProgressId;
    private int mEmptyId;
    private int mErrorId;

    protected boolean mClipToPadding;
    protected int mPadding;
    protected int mPaddingTop;
    protected int mPaddingBottom;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected int mScrollbarStyle;

    protected RecyclerView.OnScrollListener mInternalOnScrollListener;
    protected RecyclerView.OnScrollListener mExternalOnScrollListener;

    private boolean headerPrior = false;

    public void setHeaderPrior(boolean headerPrior) {
        this.headerPrior = headerPrior;
    }

    public RecyclerView getRecyclerView() {
        return mRecycler;
    }

    public EasyNoRefreshRecyclerView(Context context) {
        super(context);
        initView();
    }

    public EasyNoRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public EasyNoRefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        initView();
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.superrecyclerview);
        try {
            mClipToPadding = a.getBoolean(R.styleable.superrecyclerview_recyclerClipToPadding, false);
            mPadding = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPadding, -1.0f);
            mPaddingTop = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingTop, 0.0f);
            mPaddingBottom = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingBottom, 0.0f);
            mPaddingLeft = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingLeft, 0.0f);
            mPaddingRight = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingRight, 0.0f);
            mScrollbarStyle = a.getInteger(R.styleable.superrecyclerview_scrollbarStyle, -1);

            mEmptyId = a.getResourceId(R.styleable.superrecyclerview_layout_empty, 0);
            mProgressId = a.getResourceId(R.styleable.superrecyclerview_layout_progress, 0);
            mErrorId = a.getResourceId(R.styleable.superrecyclerview_layout_error, 0);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        // 生成主View
        View v = LayoutInflater.from(getContext()).inflate(R.layout.no_recresh_recyclerview, this);

        mProgressView = (ViewGroup) v.findViewById(R.id.progress);
        if (mProgressId != 0)
            LayoutInflater.from(getContext()).inflate(mProgressId, mProgressView);
        mEmptyView = (ViewGroup) v.findViewById(R.id.empty);
        if (mEmptyId != 0)
            LayoutInflater.from(getContext()).inflate(mEmptyId, mEmptyView);
        mErrorView = (ViewGroup) v.findViewById(R.id.error);
        if (mErrorId != 0)
            LayoutInflater.from(getContext()).inflate(mErrorId, mErrorView);
        initRecyclerView(v);
    }


    public void setRecyclerPadding(int left, int top, int right, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingTop = top;
        this.mPaddingRight = right;
        this.mPaddingBottom = bottom;
        mRecycler.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
    }

    public void setEmptyView(View emptyView) {
        mEmptyView.removeAllViews();
        mEmptyView.addView(emptyView);
    }

    public void setProgressView(View progressView) {
        mProgressView.removeAllViews();
        mProgressView.addView(progressView);
    }

    public void setErrorView(View errorView) {
        mErrorView.removeAllViews();
        mErrorView.addView(errorView);
    }

    public void setEmptyView(int emptyView) {
        mEmptyView.removeAllViews();
        LayoutInflater.from(getContext()).inflate(emptyView, mEmptyView);
    }

    public void setProgressView(int progressView) {
        mProgressView.removeAllViews();
        LayoutInflater.from(getContext()).inflate(progressView, mProgressView);
    }

    public void setErrorView(int errorView) {
        mErrorView.removeAllViews();
        LayoutInflater.from(getContext()).inflate(errorView, mErrorView);
    }

    public void scrollToPosition(int position) {
        getRecyclerView().scrollToPosition(position);
    }

    /**
     * Implement this method to customize the AbsListView
     */
    protected void initRecyclerView(View view) {
        mRecycler = (RecyclerView) view.findViewById(android.R.id.list);

        if (mRecycler != null) {
            mRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecycler.setHasFixedSize(true);
            mRecycler.setClipToPadding(mClipToPadding);
            mInternalOnScrollListener = new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (mExternalOnScrollListener != null)
                        mExternalOnScrollListener.onScrolled(recyclerView, dx, dy);

                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (mExternalOnScrollListener != null)
                        mExternalOnScrollListener.onScrollStateChanged(recyclerView, newState);

                }
            };
            mRecycler.addOnScrollListener(mInternalOnScrollListener);

            if (mPadding != -1.0f) {
                mRecycler.setPadding(mPadding, mPadding, mPadding, mPadding);
            } else {
                mRecycler.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
            }
            if (mScrollbarStyle != -1) {
                mRecycler.setScrollBarStyle(mScrollbarStyle);
            }
        }
        showRecycler();
    }

    /**
     * Set the layout manager to the recycler
     *
     * @param manager
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecycler.setLayoutManager(manager);
    }

    private static class EasyDataObserver extends RecyclerView.AdapterDataObserver {
        private EasyNoRefreshRecyclerView recyclerView;
        private boolean isInit = false;
        private boolean hasProgress = false;
        private boolean headerPrior;

        public EasyDataObserver(EasyNoRefreshRecyclerView recyclerView, boolean hasProgress, boolean headerPrior) {
            this.recyclerView = recyclerView;
            this.hasProgress = hasProgress;
            this.headerPrior = headerPrior;
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            update();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            update();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            update();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            update();
        }

        @Override
        public void onChanged() {
            super.onChanged();
            update();
        }

        // 自动更改Container的样式
        private void update() {
            log("update");
            if (recyclerView.getAdapter() instanceof RecyclerArrayAdapter) {
                if (((RecyclerArrayAdapter) recyclerView.getAdapter()).getCount() == 0) {
                    log("no data:" + ((hasProgress && !isInit) ? "show progress" : "show empty"));
                    if (hasProgress && !isInit) {
                        recyclerView.showProgress();
                    } else if (((RecyclerArrayAdapter) recyclerView.getAdapter()).getHeaderCount() > 0 && headerPrior) {
                        recyclerView.showRecycler();
                    } else {
                        recyclerView.showEmpty();
                    }
                } else {
                    log("has data");
                    recyclerView.showRecycler();
                }
            } else {
                if (recyclerView.getAdapter().getItemCount() == 0) {
                    log("no data:" + ((hasProgress && !isInit) ? "show progress" : "show empty"));
                    if (hasProgress && !isInit)
                        recyclerView.showProgress();
                    else
                        recyclerView.showEmpty();
                } else {
                    log("has data");
                    recyclerView.showRecycler();
                }
            }
            isInit = true;// 设置Adapter时会有一次onChange。忽略此次。
        }
    }

    /**
     * 设置适配器，关闭所有副view。展示recyclerView 适配器有更新，自动关闭所有副view。根据条数判断是否展示EmptyView
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecycler.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new EasyNoRefreshRecyclerView.EasyDataObserver(this, false, headerPrior));
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置适配器，关闭所有副view。展示进度条View 适配器有更新，自动关闭所有副view。根据条数判断是否展示EmptyView
     *
     * @param adapter
     */
    public void setAdapterWithProgress(RecyclerView.Adapter adapter) {
        mRecycler.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new EasyNoRefreshRecyclerView.EasyDataObserver(this, true, headerPrior));
        adapter.notifyDataSetChanged();
    }

    /**
     * Remove the adapter from the recycler
     */
    public void clear() {
        mRecycler.setAdapter(null);
    }

    private void hideAll() {
        mEmptyView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
        mErrorView.setVisibility(GONE);
        mRecycler.setVisibility(View.INVISIBLE);
    }

    public void showError() {
        log("showError");
        hideAll();
        mErrorView.setVisibility(View.VISIBLE);
    }

    public void showEmpty() {
        log("showEmpty");
        hideAll();
        mEmptyView.setVisibility(View.VISIBLE);
    }

    public void showProgress() {
        log("showProgress");
        hideAll();
        mProgressView.setVisibility(View.VISIBLE);
    }

    public void showRecycler() {
        log("showRecycler");
        hideAll();
        mRecycler.setVisibility(View.VISIBLE);
    }


    /**
     * Set the scroll listener for the recycler
     *
     * @param listener
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        mExternalOnScrollListener = listener;
    }

    /**
     * Add the onItemTouchListener for the recycler
     *
     * @param listener
     */
    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.addOnItemTouchListener(listener);
    }

    /**
     * Remove the onItemTouchListener for the recycler
     *
     * @param listener
     */
    public void removeOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.removeOnItemTouchListener(listener);
    }

    /**
     * @return the recycler adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return mRecycler.getAdapter();
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mRecycler.setOnTouchListener(listener);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecycler.setItemAnimator(animator);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecycler.addItemDecoration(itemDecoration, index);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.removeItemDecoration(itemDecoration);
    }

    /**
     * @return inflated error view or null
     */
    public View getErrorView() {
        if (mErrorView.getChildCount() > 0)
            return mErrorView.getChildAt(0);
        return null;
    }

    /**
     * @return inflated progress view or null
     */
    public View getProgressView() {
        if (mProgressView.getChildCount() > 0)
            return mProgressView.getChildAt(0);
        return null;
    }

    /**
     * @return inflated empty view or null
     */
    public View getEmptyView() {
        if (mEmptyView.getChildCount() > 0)
            return mEmptyView.getChildAt(0);
        return null;
    }

    public void setProgressBackground(int color) {
        mProgressView.setBackgroundColor(color);
    }

    public void setEmptyBackground(int color) {
        mEmptyView.setBackgroundColor(color);
    }

    public void setErrorBackground(int color) {
        mErrorView.setBackgroundColor(color);
    }

    public void setRecyclerBackground(int color) {
        mRecycler.setBackgroundColor(color);
    }

    private static void log(String content) {
        if (DEBUG) {
            Log.i(TAG, content);
        }
    }

}
