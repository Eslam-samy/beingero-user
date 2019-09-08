package com.corptia.bringero.Adapter;

import androidx.annotation.LayoutRes;

import com.corptia.bringero.Utils.stickyheader.stickyData.HeaderData;

public class HeaderDataImpl implements HeaderData {


    public static final int HEADER_TYPE_1 = 1;

    private int headerType;
    @LayoutRes
    private final int layoutResource;

    public HeaderDataImpl(int headerType, @LayoutRes int layoutResource) {
        this.layoutResource = layoutResource;
        this.headerType = headerType;
    }

    @LayoutRes
    @Override
    public int getHeaderLayout() {
        return layoutResource;
    }

    @Override
    public int getHeaderType() {
        return headerType;
    }
}
