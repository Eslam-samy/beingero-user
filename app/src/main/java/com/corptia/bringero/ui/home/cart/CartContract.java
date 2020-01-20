package com.corptia.bringero.ui.home.cart;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.MyCartQuery;

public class CartContract {

    public interface CartView extends BaseView {

        void setMyCart(MyCartQuery.MyCart myCartData);

        void setPlaceholder();

    }
}
