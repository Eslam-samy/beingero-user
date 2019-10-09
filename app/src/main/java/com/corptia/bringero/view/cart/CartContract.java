package com.corptia.bringero.view.cart;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.MyCartQuery;

import java.util.List;

import butterknife.BindView;

public class CartContract {

    public interface CartView extends BaseView {

        void setMyCart(MyCartQuery.MyCart myCartData);

    }
}
