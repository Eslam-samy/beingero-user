package com.corptia.bringero.ui.storesDetail;


import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.CreateCartItemMutation;
import com.corptia.bringero.model.EventBus.CalculateCartEvent;
import com.corptia.bringero.type.CreateCartItem;
import com.corptia.bringero.utils.recyclerview.decoration.GridSpacingItemDecoration;
import com.corptia.bringero.graphql.GetNotPricedByQuery;
import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.graphql.PricingProductMutation;
import com.corptia.bringero.graphql.SingleStoreHeaderQuery;
import com.corptia.bringero.type.CreatePricingProduct;
import com.corptia.bringero.ui.pricing.PricingAdapter;
import com.corptia.bringero.ui.productDetail.ProductDetailActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreDetailFragment extends Fragment implements StoreDetailContract.StoreDetailView {

    @BindView(R.id.recycler_brands_detail)
    RecyclerView recycler_brands_detail;
    @BindView(R.id.root)
    ConstraintLayout root;

    StoreDetailAdapter storeDetailAdapter ;
    PricingAdapter pricingAdapter ;

    Handler handler = new Handler();

    StoreDetailPresenter storeDetailPresenter ;
    boolean isPrice;
    BottomSheetDialog bottomSheetDialog;


    public StoreDetailFragment(boolean isPrice) {
        // Required empty public constructor
        storeDetailPresenter = new StoreDetailPresenter(this);
        this.isPrice = isPrice;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brand_detail, container, false);
        ButterKnife.bind(this , view);

        if (Common.CURRENT_USER!=null)
            if (Common.CURRENT_USER.language().equalsIgnoreCase("ar"))
        {
            root.setRotationY(180);
        }
        recycler_brands_detail.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_brands_detail.addItemDecoration(new GridSpacingItemDecoration(2, Common.dpToPx(15, getActivity()), true, 0, Common.dpToPx(10, getActivity())));

        if (getArguments() != null) {

            // set argument data to view
            String typeId = getArguments().getString(Constants.EXTRA_PRODUCT_TYPE_ID);
            String storeId = getArguments().getString(Constants.EXTRA_STORE_ID);
            storeDetailPresenter.getProductStore(Common.CURRENT_STORE._id(),typeId ,isPrice);
            //Here Get Products For This Type

        }


        return view;
    }

    @Override
    public void setStoresDetailHeader(SingleStoreHeaderQuery.StoreDetail detail) {

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }

    @Override
    public void setProduct(List<GetStoreProductsQuery.Product> product) {

        handler.post(() -> {
            storeDetailAdapter = new StoreDetailAdapter(getActivity(), product);
            recycler_brands_detail.setAdapter(storeDetailAdapter);
            storeDetailAdapter.notifyDataSetChanged();

            if (isPrice)
            {
                storeDetailAdapter.setListener((view, position) -> {

//                    Intent intent = new Intent(getActivity() , ProductDetailActivity.class);
//                    GetStoreProductsQuery.Product mProduct =  storeDetailAdapter.getSelectProduct(position);
//                    intent.putExtra(Constants.EXTRA_PRODUCT_ID , mProduct._id());
//                    if (mProduct.Product().ImageResponse().data()!=null)
//                    intent.putExtra(Constants.EXTRA_PRODUCT_IMAGE , mProduct.Product().ImageResponse().data().name());
//                    startActivity(intent);

                    //Here Add To Cart
                    EventBus.getDefault().postSticky(new CalculateCartEvent(true , storeDetailAdapter.productsList.get(position).storePrice()));
                    addToCart(storeDetailAdapter.getSelectProduct(position)._id());

                });

            }

        });

    }


    public void addToCart (String pricingProductId){

        CreateCartItem item = CreateCartItem.builder().amount(1).pricingProductId(pricingProductId).build();
        MyApolloClient.getApollowClientAuthorization().mutate(CreateCartItemMutation.builder().data(item).build())
                .enqueue(new ApolloCall.Callback<CreateCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CreateCartItemMutation.Data> response) {

                        CreateCartItemMutation.Create createResponse = response.data().CartItemMutation().create();
                        if (createResponse.status() == 200)
                        {

                        }
                        else
                        {
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }

    @Override
    public void setProductNotPriced(List<GetNotPricedByQuery.Product> product) {

        handler.post(() -> {
            pricingAdapter= new PricingAdapter(getActivity(), product);
            recycler_brands_detail.setAdapter(pricingAdapter);
            pricingAdapter.notifyDataSetChanged();

            if (isPrice)
            {


            }
            else
            {
                pricingAdapter.setListener((view, position) -> {
                    // Toast.makeText(getActivity(), "This is Pricing"+ position, Toast.LENGTH_SHORT).show();
                    showCreatePricingDialog(pricingAdapter.getSelectProduct(position)._id() , Common.CURRENT_STORE._id() , position);
                });
            }

        });

    }


    private void showCreatePricingDialog(String productId , String storeId , int position) {

        // init loadingDialog
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setTitle(getString(R.string.price));
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_create_pricing, null);

        Button btn_add = sheetView.findViewById(R.id.btn_add);
        final TextInputEditText edt_price = sheetView.findViewById(R.id.edt_price);
        final TextInputEditText edt_amount = sheetView.findViewById(R.id.edt_amount);

        btn_add.setOnClickListener(view -> {


            CreatePricingProduct createPricingProduct = CreatePricingProduct.builder()
                    .productId(productId)
                    .storeId(storeId)
                    .storePrice(Double.parseDouble(edt_price.getText().toString()))
                    .amount(Integer.valueOf(edt_amount.getText().toString()))
                    .build();

            MyApolloClient.getApollowClientAuthorization().mutate(PricingProductMutation.builder().data(createPricingProduct).build())
                    .enqueue(new ApolloCall.Callback<PricingProductMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<PricingProductMutation.Data> response) {

                            handler.post(() -> {

                                if (response.data().CreatePricingProduct().create().status() == 200)
                                {
                                    bottomSheetDialog.dismiss();
                                    pricingAdapter.removeSelectProduct(position);
                                }
                                else
                                {
                                    bottomSheetDialog.dismiss();
                                }

                            });


                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {

                        }
                    });


        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }
}
