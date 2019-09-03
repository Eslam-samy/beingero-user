package com.corptia.bringero.Utils.decoration;

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

        final int itemCount = state.getItemCount();
        if (itemCount > 0 && position == itemCount - 1) {
            outRect.bottom = verticalSpaceHeight+100;
        }


        outRect.bottom = -30;
        outRect.left = verticalSpaceHeight/2;
        outRect.right = verticalSpaceHeight/2;
    }

    //  /** first position */
//        if (itemPosition == 0) {
//            outRect.set(padding, padding, 0, padding);
//        }
//        /** last position */
//        else if (itemCount > 0 && position == itemCount - 1) {
//            outRect.set(0, padding, padding, padding);
//        }
//        /** positions between first and last */
//        else {
//            outRect.set(0, padding, 0, padding);
//        }
}