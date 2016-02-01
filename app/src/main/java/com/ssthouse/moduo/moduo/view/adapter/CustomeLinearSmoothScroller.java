package com.ssthouse.moduo.moduo.view.adapter;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ssthouse on 2016/1/25.
 */
class CustomeLinearSmoothScroller extends LinearSmoothScroller {

    public CustomeLinearSmoothScroller(Context context) {
        super(context);
    }

    @Override
    public int calculateDyToMakeVisible(View view, int snapPreference) {
        final RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (!layoutManager.canScrollVertically()) {
            return 0;
        }
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        final int top = layoutManager.getDecoratedTop(view) - params.topMargin;
        final int bottom = layoutManager.getDecoratedBottom(view) + params.bottomMargin;
        final int viewHeight = bottom - top;
        final int start = layoutManager.getPaddingTop();
        final int end = start + viewHeight;
        return calculateDtToFit(top, bottom, start, end, snapPreference);
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return null;
    }
}