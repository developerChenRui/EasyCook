package com.example.chenrui.easycook;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by Zhiyi Yang on 11/16/2018
 */

public abstract class EndLessOnScrollListener extends  RecyclerView.OnScrollListener{
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private int currentPage = 0;
    private int totalItemCount;
    private int previousTotal = 0;
    private int visibleItemCount;
    private int firstVisibleItem;
    private boolean loading = true;
    private String TAG = "YANG";

    public EndLessOnScrollListener(StaggeredGridLayoutManager staggeredGridLayoutManager) {
        this.staggeredGridLayoutManager = staggeredGridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = staggeredGridLayoutManager.getItemCount();
//        firstVisibleItem = staggeredGridLayoutManager.findFirstVisibleItemPosition();
        firstVisibleItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(null)[0];

        if (loading) {

            Log.d(TAG, "firstVisibleItem: " + firstVisibleItem);
            Log.d(TAG, "totalPageCount:" + totalItemCount);
            Log.d(TAG, "visibleItemCount:" + visibleItemCount);

            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && totalItemCount-visibleItemCount <= firstVisibleItem){
            currentPage ++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    public abstract void onLoadMore(int currentPage);

}
