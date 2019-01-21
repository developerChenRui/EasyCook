package com.example.chenrui.easycook;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/***
 * EndLessOnScrollListener
 *
 * Created by Zhiyi Yang on 11/16/2018
 *
 * Allows for endless scrolling in the Discovery fragment
 ***/

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
        firstVisibleItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(null)[0];

        if (loading) {

            Log.d(TAG, "firstVisibleItem: " + firstVisibleItem);
            Log.d(TAG, "totalPageCount:" + totalItemCount);
            Log.d(TAG, "visibleItemCount:" + visibleItemCount);

            // We have more items than can be displayed
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        // Need to get more items
        if (!loading && totalItemCount-visibleItemCount <= firstVisibleItem){
            currentPage ++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    public abstract void onLoadMore(int currentPage);

}
