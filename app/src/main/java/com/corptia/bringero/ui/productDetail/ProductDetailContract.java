package com.corptia.bringero.ui.productDetail;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.SingleProductQuery;

public class ProductDetailContract {


    public  interface  ProductDetailView extends BaseView {

        void setDataProduct(SingleProductQuery.Product data);

    }


}
