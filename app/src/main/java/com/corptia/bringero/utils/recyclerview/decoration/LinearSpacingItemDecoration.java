package com.corptia.bringero.utils.recyclerview.decoration;

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
            //outRect.top = verticalSpaceHeight;

        }

        outRect.bottom = -30; //-30
        //outRect.bottom = -50;

        outRect.left = verticalSpaceHeight/2;
        outRect.right = verticalSpaceHeight/2;

        final int itemCount = state.getItemCount();
        if (itemCount > 0 && position == itemCount - 1) {
            //outRect.bottom = verticalSpaceHeight+14;
        }



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