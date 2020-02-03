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
import com.corptia.bringero.Interface.CallbackListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.CreateCartItemMutation;
import com.corptia.bringero.model.EventBus.CalculateCartEvent;
import com.corptia.bringero.type.CreateCartItem;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.recyclerview.PaginationListener;
import com.corptia.bringero.utils.recyclerview.decoration.GridSpacingItemDecoration;
import com.corptia.bringero.graphql.GetStoreProductsQuery;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.corptia.bringero.Common.Constants.VIEW_TYPE_ITEM;
import static com.corptia.bringero.Common.Constants.VIEW_TYPE_LOADING;
import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_START;

public class StoreDetailFragment extends Fragment implements StoreDetailContract.StoreDetailView {

    //For Pagination
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    int totalPages = 1;

    @BindView(R.id.recycler_brands_detail)
    RecyclerView recycler_brands_detail;
    @BindView(R.id.root)
    ConstraintLayout root;

    StoreDetailAdapter storeDetailAdapter;

    Handler handler = new Handler();

    StoreDetailPresenter storeDetailPresenter;

    GridLayoutManager gridLayoutManager ;

    String typeId , storeId;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brand_detail, container, false);
        ButterKnife.bind(this, view);

        currentPage = 1;
        isLoading = false;
        isLastPage = false;

        if (Common.CURRENT_USER != null)
            if (Common.CURRENT_USER.getLanguage().equalsIgnoreCase("ar")) {
                root.setRotationY(180);
            }

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
            storeDetailPresenter.getProductStore(Common.CURRENT_STORE._id(), typeId , currentPage);
            //Here Get Products For This Type

        }


        recycler_brands_detail.addOnScrollListener(new PaginationListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {

                isLoading = true;
                currentPage++;
                if (currentPage <= totalPages) {
                    storeDetailAdapter.addLoading();
                    storeDetailPresenter.getProductStore(Common.CURRENT_STORE._id(), typeId ,currentPage);
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


        storeDetailAdapter = new StoreDetailAdapter(getActivity(), null);
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

            storeDetailAdapter.setListener((view, position,price , amount , isPackaged) -> {

                double finalAmount = 0;
                if (isPackaged)
                    finalAmount = 1;
                else
                    finalAmount = amount;

//                    Intent intent = new Intent(getActivity() , ProductDetailActivity.class);
//                    GetStoreProductsQuery.Product mProduct =  storeDetailAdapter.getSelectProduct(position);
//                    intent.putExtra(Constants.EXTRA_PRODUCT_ID , mProduct._id());
//                    if (mProduct.Product().ImageResponse().data()!=null)
//                    intent.putExtra(Constants.EXTRA_PRODUCT_IMAGE , mProduct.Product().ImageResponse().data().name());
//                    startActivity(intent);

                //Here Add To Cart
                //Get Data Store

                    GetStoreProductsQuery.Product items = storeDetailAdapter.productsList.get(position);

                    double priceProduct ;
                    if (items.discountActive()!=null &&items.discountActive())
                        priceProduct =(1- items.discountRatio()) * items.storePrice();
                    else
                        priceProduct = items.storePrice();

                    EventBus.getDefault().postSticky(new CalculateCartEvent(true, price , amount));
                    addToCart(storeDetailAdapter.getSelectProduct(position)._id() , position , finalAmount);


            });



        });

    }

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


    //TODO Here Make Refresh
    //notifyItemChanged(position); (have two adapter product and search)
    public void addToCart(String pricingProductId , int position , double amount) {

        CreateCartItem item = CreateCartItem.builder().amount(amount).pricingProductId(pricingProductId).build();
        MyApolloClient.getApollowClientAuthorization().mutate(CreateCartItemMutation.builder().data(item).build())
                .enqueue(new ApolloCall.Callback<CreateCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CreateCartItemMutation.Data> response) {

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
