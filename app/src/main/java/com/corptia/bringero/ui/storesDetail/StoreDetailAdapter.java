package com.corptia.bringero.ui.storesDetail;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseViewHolder;
import com.corptia.bringero.graphql.GetPricedByQuery;
import com.corptia.bringero.graphql.UpdateCartItemMutation;
import com.corptia.bringero.model.CartItemsModel;
import com.corptia.bringero.model.EventBus.CalculateCartEvent;
import com.corptia.bringero.type.UpdateCartItem;
import com.corptia.bringero.ui.search.SearchProductsActivity;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.corptia.bringero.Common.Constants.VIEW_TYPE_ITEM;
import static com.corptia.bringero.Common.Constants.VIEW_TYPE_LOADING;

public class StoreDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private boolean isLoaderVisible = false;

    Context context;
    List<GetStoreProductsQuery.Product> productsList = new ArrayList<>();
    List<GetPricedByQuery.Product> productsListSearch = new ArrayList<>();

    IOnRecyclerViewClickListener listener;

    boolean isSearch;

    public StoreDetailAdapter(SearchProductsActivity context, List<GetPricedByQuery.Product> products, boolean isSearch) {
        this.context = context;
        if (products != null)
            this.productsListSearch = new ArrayList<>(products);

        this.isSearch = true;
    }

    public void setListener(IOnRecyclerViewClickListener listener) {
        this.listener = listener;
    }

    public StoreDetailAdapter(Context context, List<GetStoreProductsQuery.Product> productsList) {
        this.context = context;
        if (productsList != null)
            this.productsList = new ArrayList<>(productsList);
        isSearch = false;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_ITEM:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_product, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public void updateCartItems(String itemsId, int amount, double price) {

        UpdateCartItem updateAmount = UpdateCartItem.builder().amount(amount).build();
        MyApolloClient.getApollowClientAuthorization().mutate(UpdateCartItemMutation.builder().id(itemsId).data(updateAmount).build())
                .enqueue(new ApolloCall.Callback<UpdateCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateCartItemMutation.Data> response) {

                        if (response.data().CartItemMutation().update().status() == 200) {

                        } else {

                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

        EventBus.getDefault().postSticky(new CalculateCartEvent(true, -price, -1));

        Common.GetCartItemsCount();

    }

    @Override
    public int getItemCount() {

        if (isSearch) {
            return productsListSearch != null ? productsListSearch.size() : 0;
        } else
            return productsList != null ? productsList.size() : 0;

    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.image_product)
        ImageView image_product;
        @BindView(R.id.txt_name_product)
        TextView txt_name_product;
        @BindView(R.id.txt_price)
        TextView txt_price;

        @BindView(R.id.txt_amount)
        TextView txt_amount;
        @BindView(R.id.btn_delete)
        ImageView btn_delete;
        @BindView(R.id.txt_discount)
        TextView txt_discount;
        @BindView(R.id.bg_delete)
        TextView bg_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            setIsRecyclable(false);

        }

        @Override
        protected void clear() {

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            GetPricedByQuery.Product productSearch;
            GetStoreProductsQuery.Product product;


            double price = 0;
            String productName = "";
            String productImage = "";
            String productId = "";

            //for cart
            int amount = 0;
            String cartProductId = "";


            if (!isSearch) {
                product = productsList.get(position);

                price = product.storePrice();
                productName = product.Product().name();
                productId = product._id();

                if (product.Product().ImageResponse().status() == 200)
                    productImage = product.Product().ImageResponse().data().name();


                //This My Cart
                for (CartItemsModel item : Common.CART_ITEMS_MODELS) {

                    if (item.getPricingProductId().equalsIgnoreCase(productId)) {

                        txt_amount.setVisibility(View.VISIBLE);
                        btn_delete.setVisibility(View.VISIBLE);
                        bg_delete.setVisibility(View.VISIBLE);

                        txt_amount.setText("" + item.getAmount());

                        amount = item.getAmount();
                        cartProductId = item.getCartProductId();

                    }
                }


            } else {
                productSearch = productsListSearch.get(position);

                if (productSearch != null) {
                    price = productSearch.Price();
                    productName = productSearch.name();
                    productId = productSearch._id();

                    if (productSearch.ImageResponse().status() == 200)
                        productImage = productSearch.ImageResponse().data().name();

                }
            }


            txt_price.setText(new StringBuilder().append(price).append(" ").append(context.getString(R.string.currency)));
            txt_name_product.setText(Utils.cutName(productName));

            PicassoUtils.setImage(Common.BASE_URL_IMAGE + productImage, image_product);

            if (listener != null && Common.IS_AVAILABLE_STORE) {

                double finalPrice1 = price;
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Here Condition Limit Max Price
                        if (Common.TOTAL_CART_PRICE + finalPrice1 > Common.BASE_MAX_PRICE) {
                            Toasty.warning(context , context.getString(R.string.limit_max_cart)).show();
                        } else {

                            Common.TOTAL_CART_AMOUNT +=1;
                            Common.TOTAL_CART_PRICE +=finalPrice1;

                            listener.onClick(itemView, position);

                            txt_amount.setVisibility(View.VISIBLE);
                            btn_delete.setVisibility(View.VISIBLE);
                            bg_delete.setVisibility(View.VISIBLE);

                            int count;
                            count = Integer.parseInt(txt_amount.getText().toString()) + 1;
                            txt_amount.setText("" + count);


                            if (count != 1)
                                txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                    }
                                });


                            //Update Local Item Cart
//                        if (!isSearch) {
//                            int x = 0;
//                            for (CartItemsModel item : Common.CART_ITEMS_MODELS) {
//
//                                if (item.getPricingProductId().equalsIgnoreCase(finalProductId1)) {
//                                    Common.CART_ITEMS_MODELS.get(x).setAmount(count);
//                                }
//                                x++;
//                            }
//                        }
                        }
                    }
                });
            }


            //TODO Will move this  from here
            String finalCartProductId = cartProductId;
            double finalPrice = price;
            btn_delete.setOnClickListener(view -> {

                int amountNow = Integer.parseInt(txt_amount.getText().toString()) - 1;

                if (amountNow == 0) {
                    txt_amount.setVisibility(View.INVISIBLE);
                    btn_delete.setVisibility(View.INVISIBLE);
                    bg_delete.setVisibility(View.INVISIBLE);
                }

                txt_amount.setText("" + amountNow);

                txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                    }
                });

                updateCartItems(finalCartProductId, amountNow, finalPrice);


            });


        }
    }

    public GetStoreProductsQuery.Product getSelectProduct(int position) {
        return productsList.get(position);
    }

    public void removeSelectProduct(int position) {
        productsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, productsList.size());

    }

    //------------------------------------
    public void addItems(List<GetStoreProductsQuery.Product> productsList) {
        this.productsList.addAll(productsList);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        productsList.add(null);
        notifyItemInserted(productsList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = productsList.size() - 1;
        GetStoreProductsQuery.Product item = getItem(position);
        if (item == null) {
            productsList.remove(position);
            notifyItemRemoved(position);
        }

    }

    GetStoreProductsQuery.Product getItem(int position) {
        return productsList.get(position);
    }

    //------------------------------------ This For Search -----------------------------------


    public void addItemsSearch(List<GetPricedByQuery.Product> productsListSearch) {
        this.productsListSearch.addAll(productsListSearch);
        notifyDataSetChanged();
    }

    public void addLoadingSearch() {
        isLoaderVisible = true;
        productsListSearch.add(null);
        notifyItemInserted(productsListSearch.size() - 1);
    }

    public void removeLoadingSearch() {
        isLoaderVisible = false;
        int position = productsListSearch.size() - 1;
        GetPricedByQuery.Product item = getItemSearch(position);
        if (item == null) {
            productsListSearch.remove(position);
            notifyItemRemoved(position);
        }

    }

    GetPricedByQuery.Product getItemSearch(int position) {
        return productsListSearch.get(position);
    }

    public void removeSearch() {
        if (productsListSearch != null)
            this.productsListSearch.clear();
    }


    //------------------------------------


    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            if (!isSearch)
                return position == productsList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
            else
                return position == productsListSearch.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }


}
