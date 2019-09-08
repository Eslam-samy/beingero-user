package com.corptia.bringero.Utils.stickyheader.stickyData;


import androidx.annotation.LayoutRes;

public interface HeaderData extends StickyMainData {
    @LayoutRes
    int getHeaderLayout();

    int getHeaderType();
}
