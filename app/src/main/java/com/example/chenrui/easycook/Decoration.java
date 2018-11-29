package com.example.chenrui.easycook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Zhiyi Yang on 11/17/2018
 */

public class Decoration extends RecyclerView.ItemDecoration {
    private int dividerHeight;
    private Paint dividerPaint;

    public Decoration(Context context){
        dividerPaint = new Paint();
        dividerPaint.setColor(context.getResources().getColor(R.color.colorLightGrey));
        dividerHeight = 3;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft()+ 20;
        int right = parent.getWidth() - parent.getPaddingRight() - 20;

        for(int i = 0; i < childCount - 1; i++){
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            c.drawRect(left,top,right,bottom,dividerPaint);
        }

    }
}
