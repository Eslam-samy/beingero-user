package com.corptia.bringero.Utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class LinearSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public LinearSpacingItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view) ;

        if (position ==0)
        {
            outRect.top = verticalSpaceHeight;

        }

        outRect.bottom = -30;
        outRect.left = verticalSpaceHeight/2;
        outRect.right = verticalSpaceHeight/2;
    }
}