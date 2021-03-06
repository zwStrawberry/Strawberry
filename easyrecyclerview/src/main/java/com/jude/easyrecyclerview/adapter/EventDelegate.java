package com.jude.easyrecyclerview.adapter;

import android.view.View;

public interface EventDelegate {
	void addData(int length);

	void clear();

	void stopLoadMore();

	void pauseLoadMore();

	void resumeLoadMore();

	void setMore(View view, RecyclerArrayAdapter.OnLoadMoreListener listener);

	void setNoMore(View view);

	void setErrorMore(View view);

	void reset();
}
