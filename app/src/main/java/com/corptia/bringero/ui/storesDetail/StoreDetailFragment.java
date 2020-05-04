package com.corptia.bringero.ui.storesDetail;


import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.AdapterOnProductClickListener;
import com.corptia.bringero.Interface.CallbackListener;
import com.corptia.bringero.Interface.IOnProductClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.CreateCartItemMutation;
import com.corptia.bringero.graphql.StoreSearchQuery;
import com.corptia.bringero.model.EventBus.CalculateCartEvent;
import com.corptia.bringero.model.MyCart;
import com.corptia.bringero.type.CreateCartItem;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.recyclerview.PaginationListener;
import com.corptia.bringero.utils.recyclerview.decoration.GridSpacingItemDecoration;
import com.corptia.bringero.graphql.GetStoreProductsQuery;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.corptia.bringero.Common.Constants.VIEW_TYPE_ITEM;
import static com.corptia.bringero.Common.Constants.VIEW_TYPE_LOADING;
import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_START;

public class StoreDetailFragment extends Fragment implements StoreDetailContract.StoreDetailView {

    private static final String TAG = "TAG Moha price";
    //For Pagination
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    int totalPages = 1;

    @BindView(R.id.recycler_brands_detail)
    RecyclerView recycler_brands_detail;
    @BindView(R.id.root)
    ConstraintLayout root;

    NewStoreDetailAdapter storeDetailAdapter;

    Handler handler = new Handler();

    StoreDetailPresenter storeDetailPresenter;

    GridLayoutManager gridLayoutManager;

    String typeId, storeId;

    //For Placeholder
    @BindView(R.id.layout_placeholder)
    ConstraintLayout layout_placeholder;
    @BindView(R.id.img_placeholder)
    ImageView img_placeholder;
    @BindView(R.id.txt_placeholder_title)
    TextView txt_placeholder_title;
    @BindView(R.id.txt_placeholder_dec)
    TextView txt_placeholder_dec;
    @BindView(R.id.btn_1)
    Button btn_1;
    @BindView(R.id.btn_2)
    Button btn_2;

    @BindView(R.id.loading)
    LottieAnimationView loading;


    public StoreDetailFragment() {
        storeDetailPresenter = new StoreDetailPresenter(this);
        Common.LOG("Hi StoreDetailFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brand_detail, container, false);
        ButterKnife.bind(this, view);

        currentPage = 1;
        isLoading = false;
        isLastPage = false;

//        if (Common.CURRENT_USER != null)
//            if (Common.CURRENT_USER.getLanguage().equalsIgnoreCase("ar")) {
//                root.setRotationY(180);
//            }

        gridLayoutManager = new GridLayoutManager(getActivity(), 2);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (storeDetailAdapter.getItemViewType(position)) {
                    case VIEW_TYPE_ITEM:
                        return 1;
                    case VIEW_TYPE_LOADING:
                        return 2; //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });

        recycler_brands_detail.setLayoutManager(gridLayoutManager);
        recycler_brands_detail.addItemDecoration(new GridSpacingItemDecoration(
                2,
                Common.dpToPx(10, getActivity()),
                true,
                0,
                Common.dpToPx(17, getActivity()),
                Common.dpToPx(2, getActivity()),
                Common.dpToPx(2, getActivity())));

        if (getArguments() != null) {

            // set argument data to view
            typeId = getArguments().getString(Constants.EXTRA_PRODUCT_TYPE_ID);
            storeId = getArguments().getString(Constants.EXTRA_STORE_ID);
            storeDetailPresenter.getProductStore(Common.CURRENT_STORE._id(), typeId, currentPage);
            //Here Get Products For This Type

        }


        recycler_brands_detail.addOnScrollListener(new PaginationListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                for (MyCart myCartItem : Common.myLocalCart) {
                    updateCartItem(myCartItem, false, null);
                }

                isLoading = true;
                currentPage++;
                if (currentPage <= totalPages) {
                    storeDetailAdapter.addLoading();
                    storeDetailPresenter.getProductStore(Common.CURRENT_STORE._id(), typeId, currentPage);
                } else {
                    isLastPage = true;
                }
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        storeDetailAdapter = new NewStoreDetailAdapter(getActivity(), null);
        recycler_brands_detail.setAdapter(storeDetailAdapter);
        return view;
    }


    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }

    @Override
    public void setProduct(GetStoreProductsQuery.GetStoreProducts product) {

        handler.post(() -> {

            if (isLoading) {
                storeDetailAdapter.removeLoading();
                isLoading = false;
            }

            totalPages = product.pagination().totalPages();
            storeDetailAdapter.addItems(product.Products());
            storeDetailAdapter.setListener((id, cartID, price, amount, inCart, isDecrease, txt_amount, btn_delete, bg_delete) -> {
                if (!Common.myLocalCart.isEmpty())
                    for (int i = Common.myLocalCart.size() - 1; i >= 0; i--) {
                        MyCart myCartItem = Common.myLocalCart.get(i);
                        if (!myCartItem.getProductId().equals(id)) {
                            updateCartItem(myCartItem, i == Common.myLocalCart.size() - 1, loading);
                            Common.myLocalCart.remove(myCartItem);
                            Common.myLocalCartIds.remove(myCartItem.getProductId());
                        }
                    }

                if (!Common.myLocalCartIds.contains(id)) {
                    Common.myLocalCartIds.add(id);
                    MyCart myCartItem = new MyCart();
                    myCartItem.setAmount(amount);
                    myCartItem.setCartId(cartID);
                    myCartItem.setInCart(inCart);
                    myCartItem.setDecrease(isDecrease);
                    myCartItem.setPrice(price);
                    myCartItem.setProductId(id);
                    Common.myLocalCart.add(myCartItem);
                } else {
                    for (MyCart myCartItem : Common.myLocalCart) {
                        if (myCartItem.getProductId().equals(id)) {
                            int index = Common.myLocalCart.indexOf(myCartItem);
                            myCartItem.setAmount(amount);
                            myCartItem.setDecrease(isDecrease);
                            Common.myLocalCart.set(index, myCartItem);
                        }
                    }
                }

                if (amount > 0) {
                    txt_amount.setVisibility(View.VISIBLE);
                    btn_delete.setVisibility(View.VISIBLE);
                    bg_delete.setVisibility(View.VISIBLE);
                } else {
                    txt_amount.setVisibility(View.INVISIBLE);
                    btn_delete.setVisibility(View.INVISIBLE);
                    bg_delete.setVisibility(View.INVISIBLE);
                }

                if (!isDecrease) {
                    EventBus.getDefault().postSticky(new CalculateCartEvent(true, price, amount));
                }
            });
            /*storeDetailAdapter.setListener(new AdapterOnProductClickListener() {
                @Override
                public void onClickSearch(StoreSearchQuery.ProductQuery searchProduct, String cartID, double price, double amount, boolean inCart, boolean isDecrease, TextView txt_amount, View btn_delete, View bg_delete) {

                }

                @Override
                public void onClickNoSearch(GetStoreProductsQuery.Product noSearchProduct, String cartID, double finalPrice1, double amount, boolean inCart, boolean isDecrease, TextView txt_amount, View btn_delete, View bg_delete) {
                    if (Common.TOTAL_CART_PRICE <= 0) {
                        Common.TOTAL_CART_PRICE = 0;
                    }
                    Boolean isPackaged = noSearchProduct.Product().isPackaged();
                    double minSellingUnits = noSearchProduct.Product().minSellingUnits();
                    double unitStep = noSearchProduct.Product().unitStep();
                    if (!isPackaged) {
                        double step, actualAmount = 0;
                        if (Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) == 0f) {
                            step = minSellingUnits;
                            actualAmount = minSellingUnits;
                            inCart = false;
                        } else {
                            inCart = true;
                            step = unitStep;
                            actualAmount = Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) + unitStep;
                        }

                        if (checkMaxPrice(finalPrice1 * step)) return;
                        if (actualAmount < 100) {
                            if (cartID.isEmpty()) {
                                //I am product new
                                Common.TOTAL_CART_AMOUNT += 1;
                                cartID = "any";
                            }
                            Common.TOTAL_CART_PRICE += (finalPrice1 * step);
                            txt_amount.setText(new StringBuilder().append(actualAmount).append(" ").append(getContext().getString(R.string.kg)));
                            performClick(noSearchProduct._id(), cartID, finalPrice1 * step, actualAmount, inCart, isDecrease, txt_amount, btn_delete, bg_delete);

                        }

                        if (step != 1)
                            txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                }
                            });

                    } else {
                        if (Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) == 0f) {
                            inCart = false;
                        } else {
                            inCart = true;
                        }
                        if (checkMaxPrice(finalPrice1)) return;
                        int count;
                        count = Integer.parseInt(txt_amount.getText().toString().split(" ")[0]) + 1;
                        if (count < 100) {
                            if (cartID.isEmpty()) {
                                //I am product new
                                Common.TOTAL_CART_AMOUNT += 1;
                                cartID = "any";
                            }
                            Common.TOTAL_CART_PRICE += finalPrice1;
                            if (isPackaged) {
                                Log.i(TAG, "onClick:2= " + count);
                                txt_amount.setText(new StringBuilder().append(count).append(" ").append("X"));
                            }
                            performClick(noSearchProduct._id(), cartID, finalPrice1, count, inCart, isDecrease, txt_amount, btn_delete, bg_delete);
                        }
                        if (count != 1)
                            txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                }
                            });

                    }
                }
            });*/

        });

    }
/*

    private boolean checkMaxPrice(double v) {
        if (Common.TOTAL_CART_PRICE + (v) > Common.BASE_MAX_PRICE) {
            Toasty.warning(getContext(), getActivity().getString(R.string.limit_max_cart)).show();
            return true;
        }
        return false;
    }

    private void performClick(String id, String cartId, double price, double amount, boolean inCart, boolean isDecrease, View txt_amount, View btn_delete, View bg_delete) {
        if (!Common.myLocalCart.isEmpty())
            for (int i = Common.myLocalCart.size() - 1; i >= 0; i--) {
                MyCart myCartItem = Common.myLocalCart.get(i);
                if (!myCartItem.getProductId().equals(id)) {
                    updateCartItem(myCartItem);
                    Common.myLocalCart.remove(myCartItem);
                    Common.myLocalCartIds.remove(myCartItem.getProductId());
                }
            }

        if (!Common.myLocalCartIds.contains(id)) {
            Common.myLocalCartIds.add(id);
            MyCart myCartItem = new MyCart();
            myCartItem.setAmount(amount);
            myCartItem.setCartId(cartId);
            myCartItem.setInCart(inCart);
            myCartItem.setDecrease(isDecrease);
            myCartItem.setPrice(price);
            myCartItem.setProductId(id);
            Common.myLocalCart.add(myCartItem);
        } else {
            for (MyCart myCartItem : Common.myLocalCart) {
                if (myCartItem.getProductId().equals(id)) {
                    int index = Common.myLocalCart.indexOf(myCartItem);
                    myCartItem.setAmount(amount);
                    myCartItem.setDecrease(isDecrease);
                    Common.myLocalCart.set(index, myCartItem);
                }
            }
        }

        if (amount > 0) {
            txt_amount.setVisibility(View.VISIBLE);
            btn_delete.setVisibility(View.VISIBLE);
            bg_delete.setVisibility(View.VISIBLE);
        } else {
            txt_amount.setVisibility(View.INVISIBLE);
            btn_delete.setVisibility(View.INVISIBLE);
            bg_delete.setVisibility(View.INVISIBLE);
        }

        if (!isDecrease) {
            EventBus.getDefault().postSticky(new CalculateCartEvent(true, price, amount));
        }
    }
*/

    @Override
    public void setPlaceholder() {

        handler.post(new Runnable() {
            @Override
            public void run() {

                recycler_brands_detail.setVisibility(View.GONE);


                layout_placeholder.setVisibility(View.VISIBLE);
                img_placeholder.setImageResource(R.drawable.ic_placeholder_product);
                btn_1.setText(getString(R.string.another_store));
                btn_2.setText(getString(R.string.menu_home));

                btn_1.setOnClickListener(view -> getActivity().finish());

                btn_2.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });

                txt_placeholder_title.setText(getString(R.string.placeholder_title_product));
                txt_placeholder_dec.setText("");


            }
        });

    }

    @Override
    public void onPause() {

        sendToServer();
        super.onPause();
    }

    private void sendToServer() {
        if (!Common.myLocalCart.isEmpty()) {
            loading.setVisibility(View.VISIBLE);
        }
        for (MyCart myCartItem : Common.myLocalCart) {
            updateCartItem(myCartItem, Common.myLocalCart.indexOf(myCartItem) == Common.myLocalCart.size() - 1, loading);
        }
        Common.myLocalCart.clear();
        Common.myLocalCartIds.clear();
    }

    private void updateCartItem(MyCart myCartItem, boolean isLast, View loading) {
        if (!myCartItem.getInCart()) {
            Log.i(TAG, "updateCartItem: 1");
            if (myCartItem.getAmount() > 0) {
                Log.i(TAG, "updateCartItem: 2");
                addToCart(myCartItem.getProductId(), myCartItem.getAmount(), isLast);
            }
        } else {
            Log.i(TAG, "updateCartItem: 3");
            if (myCartItem.getAmount() > 1) {
                Log.i(TAG, "updateCartItem: 4");

                storeDetailAdapter.updateCartItems(myCartItem.getCartId(), myCartItem.getAmount(), myCartItem.getPrice(), isLast, loading);
            } else {
                Log.i(TAG, "updateCartItem: 5");
                storeDetailAdapter.deleteCartItems(myCartItem.getCartId(), isLast, loading);
            }
        }
        if (myCartItem.getDecrease()) {
            if (myCartItem.getAmount() > 1) {
                Log.i(TAG, "updateCartItem: 6");
                storeDetailAdapter.updateCartItems(myCartItem.getCartId(), myCartItem.getAmount(), myCartItem.getPrice(), isLast, loading);
            } else {
                Log.i(TAG, "updateCartItem: 7");
                storeDetailAdapter.deleteCartItems(myCartItem.getCartId(), isLast, loading);
            }
        }
    }

    //TODO Here Make Refresh
    //notifyItemChanged(position); (have two adapter product and search)
    public void addToCart(String id, double amount, boolean isLast) {

        CreateCartItem item = CreateCartItem.builder().amount(amount).pricingProductId(id).build();
        MyApolloClient.getApolloClient().mutate(CreateCartItemMutation.builder().data(item).build())
                .enqueue(new ApolloCall.Callback<CreateCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CreateCartItemMutation.Data> response) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (isLast)
                                    loading.setVisibility(View.VISIBLE);
                            }
                        });


                        CreateCartItemMutation.Create createResponse = response.data().CartItemMutation().create();
                        if (createResponse.status() == 200) {

                            Common.GetCartItemsCount(new CallbackListener() {
                                @Override
                                public void OnSuccessCallback() {
//                                    Log.d("HAZEM" , "Welcome OnSuccessCallback " +position);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
//                                            storeDetailAdapter.notifyItemChanged(position);
                                        }
                                    });
                                }

                                @Override
                                public void OnFailedCallback() {

                                }
                            });


                        } else {

                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                    }
                });
    }

}
