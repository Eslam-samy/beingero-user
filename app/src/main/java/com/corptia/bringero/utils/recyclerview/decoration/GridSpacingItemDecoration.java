package com.corptia.bringero.utils.recyclerview.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;
    private int headerNum;
    private int spacingTop;
    private int left;
    private int right;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge, int headerNum, int spacingTop , int left , int right) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        this.headerNum = headerNum;
        this.spacingTop = spacingTop;
        this.left = left;
        this.right = right;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view) - headerNum; // item position

        if (position >= 0) {
            int column = position % spanCount; // item column

            if (Common.CURRENT_USER!=null)
            if (Common.CURRENT_USER.getLanguage().equalsIgnoreCase("ar"))

            {

                if (includeEdge) {

                    if (position == 0 || position == 1) {
                        outRect.top = spacing;
                    } else {
                        outRect.top = -spacingTop;
                    }

                    if (position % 2 == 0) {
                        outRect.right = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                        outRect.left = -left;
                    } else {
                        outRect.left = (column + 1) * spacing / spanCount;
                        outRect.right = -right;
                    } // (column + 1) * ((1f / spanCount) * spacing)

//                if (position < spanCount) { // top edge
//                    outRect.top = spacing;
//                }
                    //outRect.bottom = spacing; // item bottom
                } else {
                    outRect.right = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                    outRect.left = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (position >= spanCount) {
                        outRect.top = spacing; // item top
                    }
                }
            }

            else
            {

                if (includeEdge) {

                    if (position == 0 || position == 1) {
                        outRect.top = spacing;
                    } else {
                        outRect.top = -spacingTop;
                    }

                    if (position % 2 == 0) {
                        outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                        outRect.right = -right;
                    } else {
                        outRect.right = (column + 1) * spacing / spanCount;
                        outRect.left = -left;
                    } // (column + 1) * ((1f / spanCount) * spacing)

//                if (position < spanCount) { // top edge
//                    outRect.top = spacing;
//                }
                    //outRect.bottom = spacing; // item bottom
                } else {
                    outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                    outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (position >= spanCount) {
                        outRect.top = spacing; // item top
                    }
                }

            }


        } else {
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }
}
