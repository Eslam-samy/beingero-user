package com.corptia.bringero.Utils;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * InfiniteScrollListener adds a scroll listener when the
 * recycler view is scrolled
 *
 * @author Gowsik K C
 * @version 1.0 ,10/27/2018
 * @since 1.0
 */
public abstract class InfiniteScrollListener extends RecyclerView.OnScrollListener {

    private static final String TAG = InfiniteScrollListener.class.getSimpleName();

    //to check the current loading state
    private boolean isLoading = false;

    //new page start loading when two items are left
    private final static int VISIBLE_THRESHOLD = 2;

    private int firstVisibleItem, totalItemCount;

    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();


        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        if (linearLayoutManager != null) {

            totalItemCount = linearLayoutManager.getItemCount();
            firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
        }


        //when isLoading is false and the there are only two items
        //left in the list load more is called
        if (!isLoading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + VISIBLE_THRESHOLD)) {

            isLoading = true;
            onLoadMore();

        }
    }

    /**
     * This function is called when the end of the list is reached
     * to load more items
     */
    public abstract void onLoadMore();


    /**
     * Setter of the isloading Flag
     *
     * @param isLoading flat to indicate load more
     */
    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }


}
