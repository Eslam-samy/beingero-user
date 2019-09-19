package com.corptia.bringero.view.storesDetail;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.Utils.decoration.GridSpacingItemDecoration;
import com.corptia.bringero.graphql.GetProductQuery;
import com.corptia.bringero.graphql.PricingProductMutation;
import com.corptia.bringero.graphql.SingleStoreHeaderQuery;
import com.corptia.bringero.model.StoreTypes;
import com.corptia.bringero.type.CreatePricingProduct;
import com.corptia.bringero.type.ProductFilterInput;
import com.corptia.bringero.view.Main.login.User;
import com.corptia.bringero.view.home.HomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreDetailFragment extends Fragment implements StoreDetailContract.StoreDetailView {

    @BindView(R.id.recycler_brands_detail)
    RecyclerView recycler_brands_detail;
    StoreDetailAdapter adapter ;

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

        recycler_brands_detail.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_brands_detail.addItemDecoration(new GridSpacingItemDecoration(2, Common.dpToPx(15, getActivity()), true, 0, Common.dpToPx(10, getActivity())));

        if (getArguments() != null) {

            // 13. set Value from argument data to view
            String typeId = getArguments().getString(Constants.EXTRA_PRODUCT_TYPE_ID);
            storeDetailPresenter.getProductStore(typeId ,isPrice);
            //Here Get Products For This Type

        }


        return view;
    }

    @Override
    public void setStoresDetailHeader(SingleStoreHeaderQuery.StoreDetail detail) {

    }

    @Override
    public void displayError(String errorMessage) {

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void setProduct(List<GetProductQuery.Product> product) {

        handler.post(() -> {
            adapter = new StoreDetailAdapter(getActivity(), product);
            recycler_brands_detail.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (isPrice)
            {
                adapter.setListener(new IOnRecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Toast.makeText(getActivity(), "This is Gallery"+ position, Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else
            {
                adapter.setListener((view, position) -> {
                   // Toast.makeText(getActivity(), "This is Pricing"+ position, Toast.LENGTH_SHORT).show();
                    showCreatePricingDialog(adapter.getSelectProduct(position)._id() , Common.CURRENT_STORE._id() , position);
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

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (response.data().CreatePricingProduct().create().status() == 200)
                                    {
                                        bottomSheetDialog.dismiss();
                                        adapter.removeSelectProduct(position);
                                    }
                                    else
                                    {
                                        bottomSheetDialog.dismiss();
                                    }

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
