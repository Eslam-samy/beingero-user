package com.corptia.bringero.view.productDetail;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.SingleProductQuery;

import org.jetbrains.annotations.Nullable;

public class ProductDetailContract {


    public  interface  ProductDetailView extends BaseView {

        void setDataProduct(SingleProductQuery.Product data);

    }


}
