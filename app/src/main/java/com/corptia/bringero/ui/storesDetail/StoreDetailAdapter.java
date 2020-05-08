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
import com.corptia.bringero.Interface.CallbackListener;
import com.corptia.bringero.Interface.IOnProductClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseViewHolder;
import com.corptia.bringero.graphql.RemoveCartItemMutation;
import com.corptia.bringero.graphql.StoreSearchQuery;
import com.corptia.bringero.graphql.UpdateCartItemMutation;
import com.corptia.bringero.model.CartItemsModel;
import com.corptia.bringero.model.EventBus.CalculateCartEvent;
import com.corptia.bringero.type.UpdateCartItem;
import com.corptia.bringero.ui.search.SearchProductsActivity;
import com.corptia.bringero.ui.stores.StoresActivity;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.corptia.bringero.Common.Constants.VIEW_TYPE_ITEM;
import static com.corptia.bringero.Common.Constants.VIEW_TYPE_LOADING;

public class StoreDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private boolean isLoaderVisible = false;

    Context context;
    List<GetStoreProductsQuery.Product> productsList = new ArrayList<>();
    List<StoreSearchQuery.ProductQuery> productsListSearch = new ArrayList<>();

    IOnProductClickListener listener;


    boolean isSearch;

    //Var
    StoreSearchQuery.ProductQuery productSearch;
    GetStoreProductsQuery.Product product;

    String cartProductId = "";
    double price = 0;
    double oldPrice = 0;
    double discountRatio = 0;
    boolean discountActive;

    String productName = "";
    String productImage = "";
    String productId = "";

    //for cart
    double amount = 0;

    //isPackaged
    boolean isPackaged = false;
    double unitStep, minSellingUnits;

    //For Count product
    boolean isCount;

    public StoreDetailAdapter(SearchProductsActivity context, List<StoreSearchQuery.ProductQuery> products, boolean isSearch) {
        this.context = context;
        if (products != null)
            this.productsListSearch = new ArrayList<>(products);

        this.isSearch = true;
    }

    public void setListener(IOnProductClickListener listener) {
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
        @BindView(R.id.bg_delete)
        TextView bg_delete;

        @BindView(R.id.txt_discount)
        TextView txt_discount;
        @BindView(R.id.txt_old_price)
        TextView txt_old_price;

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

            cartProductId = "";

            //Populating item data
            if (!isSearch) {

                product = productsList.get(position);

                price = product.storePrice();
                oldPrice = product.storePrice();

                //Product Have Discount
                if (product.discountActive() != null && product.discountActive()) {

                    txt_discount.setVisibility(View.VISIBLE);
                    txt_old_price.setVisibility(View.VISIBLE);

                    discountActive = product.discountActive();
                    discountRatio = product.discountRatio();

                    price = (1 - discountRatio) * oldPrice;

                } else {

                    txt_discount.setVisibility(View.GONE);
                    txt_old_price.setVisibility(View.GONE);
                }

                productName = product.Product().name();
                productId = product._id();
                isPackaged = product.Product().isPackaged();

                if (product.Product().ImageResponse().status() == 200)
                    productImage = product.Product().ImageResponse().data().name();

                if (!isPackaged) {
                    unitStep = product.Product().unitStep();
                    minSellingUnits = product.Product().minSellingUnits();

                }

            } else {

                productSearch = productsListSearch.get(position);

                if (productSearch != null) {

                    oldPrice = productSearch.storePrice();

                    //Product Have Discount Search
                    if (productSearch.discountActive() != null && productSearch.discountActive()) {

                        txt_discount.setVisibility(View.VISIBLE);
                        txt_old_price.setVisibility(View.VISIBLE);

                        discountActive = productSearch.discountActive();
                        discountRatio = productSearch.discountRatio();

                        price = (1 - discountRatio) * oldPrice;

                    } else {

                        txt_discount.setVisibility(View.GONE);
                        txt_old_price.setVisibility(View.GONE);

                        price = productSearch.storePrice();

                    }

                    productId = productSearch._id();

                    productName = productSearch.Product().name();
                    isPackaged = productSearch.Product().isPackaged();

                    if (productSearch.Product().ImageResponse().status() == 200)
                        productImage = productSearch.Product().ImageResponse().data().name();

                }
            }

            //Check Item in cart
            checkItemInCart(txt_amount, btn_delete, bg_delete, false);

            //Continue populating

            //Here Set Data On Cart
            txt_price.setText(new StringBuilder().append(Common.getDecimalNumber(price)).append(" ").append(context.getString(R.string.currency)));
            txt_old_price.setText(new StringBuilder().append(Common.getDecimalNumber(oldPrice)).append(" ").append(context.getString(R.string.currency)));
            txt_discount.setText(new StringBuilder().append((int) (discountRatio * 100)).append(" %"));
            txt_name_product.setText(Utils.cutName(productName));

            PicassoUtils.setImage(Common.BASE_URL_IMAGE + productImage, image_product);

            //Click handling
            if (listener != null && Common.IS_AVAILABLE_STORE) {

                double finalPrice1 = price;
                boolean finalIsPackaged = isPackaged;

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //rePopulateData
                        rePopulateData(position);
                        //Here Condition Limit Max Price
                        if (Common.TOTAL_CART_PRICE <= 0) {
                            Common.TOTAL_CART_PRICE = 0;
                        }
                        if (!isPackaged) {
                            double step, actualAmount = 0;
                            if (Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) == 0f) {
                                step = minSellingUnits;
                                actualAmount = minSellingUnits;
                            } else {
                                step = unitStep;
                                actualAmount = Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) + unitStep;
                            }

                            if (Common.TOTAL_CART_PRICE + (finalPrice1 * step) > Common.BASE_MAX_PRICE) {
                                Toasty.warning(context, context.getString(R.string.limit_max_cart)).show();
                                return;
                            }

                            if (actualAmount < 100) {

                                if (cartProductId.isEmpty()) {
                                    //I am product new
                                    Common.TOTAL_CART_AMOUNT += 1;
                                    cartProductId = "any";
                                }

                                Common.TOTAL_CART_PRICE += (finalPrice1 * step);

                                Log.i("TAG Moha price", "onClick: " + Common.TOTAL_CART_PRICE);

                                txt_amount.setVisibility(View.VISIBLE);
                                btn_delete.setVisibility(View.VISIBLE);
                                bg_delete.setVisibility(View.VISIBLE);

                                if (!finalIsPackaged) {
                                    txt_amount.setText(new StringBuilder().append(actualAmount).append(" ").append(context.getString(R.string.kg)));
                                }
                                    //listener.onClick(productId, cartProductId, finalPrice1 * step, actualAmount, inCart,false, txt_amount, btn_delete, bg_delete);

                            }

                            if (step != 1)
                                txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {

                                        txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);

                                    }
                                });

                        } else {

                            Common.LOG("Hello I am package <3 " + productName + " position : " + position);

                            if (Common.TOTAL_CART_PRICE + finalPrice1 > Common.BASE_MAX_PRICE) {
                                Toasty.warning(context, context.getString(R.string.limit_max_cart)).show();
                                return;
                            }

                            int count;
                            count = Integer.parseInt(txt_amount.getText().toString().split(" ")[0]) + 1;


                            if (count < 100) {

                                if (cartProductId.isEmpty()) {
                                    //I am product new
                                    Common.TOTAL_CART_AMOUNT += 1;
                                    cartProductId = "any";
                                }

                                Common.TOTAL_CART_PRICE += finalPrice1;

                                Common.LOG("Hi I am count : " + count);
                                txt_amount.setVisibility(View.VISIBLE);
                                btn_delete.setVisibility(View.VISIBLE);
                                bg_delete.setVisibility(View.VISIBLE);

                                    //listener.onClick(productId, cartProductId, finalPrice1 , count, inCart,false, txt_amount, btn_delete, bg_delete);


                                if (finalIsPackaged) {
                                    txt_amount.setText(new StringBuilder().append(count).append(" ").append("X"));

                                }
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
                });
            }

            //TODO Will move this  from here
            double finalPrice = price;
            String finalProductId = productId;
            btn_delete.setOnClickListener(view -> {
                if (!cartProductId.isEmpty() && Common.CART_ITEMS_ID.contains(cartProductId)) {


                    if (isPackaged) {

                        double amountNow = Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) - 1;

                        if (amountNow > 0) {

                            txt_amount.setText(new StringBuilder().append(((int) amountNow)).append(" ").append("X"));

                            txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                }
                            });


                            Common.TOTAL_CART_AMOUNT -= 1;
                            Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;

                            updateCartItems(cartProductId, amountNow, finalPrice);
                            EventBus.getDefault().postSticky(new CalculateCartEvent(true, -price, amount));

                        } else if (amountNow <= 0) {
                            txt_amount.setVisibility(View.INVISIBLE);
                            btn_delete.setVisibility(View.INVISIBLE);
                            bg_delete.setVisibility(View.INVISIBLE);

                            txt_amount.setText("0 ");

                            deleteCartItems(cartProductId, finalPrice);

                        }

                    } else {

                        double amountNow = Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) - unitStep;

                        if (amountNow >= minSellingUnits) {

                            txt_amount.setText(new StringBuilder().append(amountNow).append(" ").append(context.getString(R.string.kg)));

                            txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                }
                            });

                            Common.TOTAL_CART_AMOUNT -= 1;
                            Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;

                            updateCartItems(cartProductId, amountNow, finalPrice * unitStep);
                            EventBus.getDefault().postSticky(new CalculateCartEvent(true, -price, amount));

                        } else {
                            txt_amount.setVisibility(View.INVISIBLE);
                            btn_delete.setVisibility(View.INVISIBLE);
                            bg_delete.setVisibility(View.INVISIBLE);

                            txt_amount.setText("0 ");

                            deleteCartItems(cartProductId, finalPrice * minSellingUnits);
                        }
                    }
                } else {

                    //This My Cart
                    for (CartItemsModel item : Common.CART_ITEMS_MODELS) {
                        if (item.getPricingProductId().equalsIgnoreCase(finalProductId)) {
                            if (isPackaged) {

                                cartProductId = item.getCartProductId();

                                double amountNow = Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) - 1;

                                if (amountNow > 0) {

                                    txt_amount.setText(new StringBuilder().append(((int) amountNow)).append(" ").append("X"));

                                    txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                        }
                                    });


                                    Common.TOTAL_CART_AMOUNT -= 1;
                                    Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;

                                    updateCartItems(item.getCartProductId(), amountNow, finalPrice);
                                    EventBus.getDefault().postSticky(new CalculateCartEvent(true, -price, amount));

                                } else if (amountNow <= 0) {
                                    txt_amount.setVisibility(View.INVISIBLE);
                                    btn_delete.setVisibility(View.INVISIBLE);
                                    bg_delete.setVisibility(View.INVISIBLE);

                                    txt_amount.setText("0 ");

                                    deleteCartItems(item.getCartProductId(), finalPrice);

                                }


                                break;

                            } else {
                                cartProductId = item.getCartProductId();
                                Log.d("HAZEM", "cartProductId 2 : " + cartProductId + " -- " + item.getCartProductId());

                                double amountNow = Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) - unitStep;

                                if (amountNow >= minSellingUnits) {

                                    txt_amount.setText(new StringBuilder().append(amountNow).append(" ").append(context.getString(R.string.kg)));

                                    txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                        }
                                    });


                                    Common.TOTAL_CART_AMOUNT -= 1;
                                    Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;

                                    updateCartItems(item.getCartProductId(), amountNow, finalPrice * unitStep);
                                    EventBus.getDefault().postSticky(new CalculateCartEvent(true, -price, amount));

                                } else {

                                    txt_amount.setVisibility(View.INVISIBLE);
                                    btn_delete.setVisibility(View.INVISIBLE);
                                    bg_delete.setVisibility(View.INVISIBLE);

                                    txt_amount.setText("0 ");

                                    deleteCartItems(item.getCartProductId(), finalPrice * minSellingUnits);
                                }


                                break;

                            }

                        }
                    }

                }


            });


        }

        private void rePopulateData(int position) {
            if (!isSearch) {
                product = productsList.get(position);
                price = product.storePrice();
                oldPrice = product.storePrice();
                //Check Discount
                if (product.discountActive() != null && product.discountActive()) {

                    txt_discount.setVisibility(View.VISIBLE);
                    txt_old_price.setVisibility(View.VISIBLE);

                    discountActive = product.discountActive();
                    discountRatio = product.discountRatio();

                    price = (1 - discountRatio) * oldPrice;

                } else {

                    txt_discount.setVisibility(View.GONE);
                    txt_old_price.setVisibility(View.GONE);
                }
                productName = product.Product().name();
                productId = product._id();
                isPackaged = product.Product().isPackaged();
                if (product.Product().ImageResponse().status() == 200)
                    productImage = product.Product().ImageResponse().data().name();
                if (!isPackaged) {
                    unitStep = product.Product().unitStep();
                    minSellingUnits = product.Product().minSellingUnits();

                }
            } else {
                productSearch = productsListSearch.get(position);
                if (productSearch != null) {
                    oldPrice = productSearch.storePrice();
                    //Check Discount
                    if (productSearch.discountActive() != null && productSearch.discountActive()) {

                        txt_discount.setVisibility(View.VISIBLE);
                        txt_old_price.setVisibility(View.VISIBLE);

                        discountActive = productSearch.discountActive();
                        discountRatio = productSearch.discountRatio();

                        price = (1 - discountRatio) * oldPrice;

                    } else {

                        txt_discount.setVisibility(View.GONE);
                        txt_old_price.setVisibility(View.GONE);

                        price = productSearch.storePrice();

                    }
                    productName = productSearch.Product().name();
                    productId = productSearch._id();
                    isPackaged = productSearch.Product().isPackaged();
                    if (productSearch.Product().ImageResponse().status() == 200)
                        productImage = productSearch.Product().ImageResponse().data().name();
                    if (!isPackaged) {
                        unitStep = product.Product().unitStep();
                        minSellingUnits = product.Product().minSellingUnits();

                    }
                }
            }
        }


    }

    Boolean inCart = false;

    public void checkItemInCart(TextView txt_amount, View btn_delete, View bg_delete, boolean update) {
        for (CartItemsModel item : Common.CART_ITEMS_MODELS) {
            Log.i("TAG Moha", "onBind: " + item.getPricingProductId());
            Log.i("TAG Moha", "onBind: " + item.getAmount());
            if (item.getPricingProductId().equalsIgnoreCase(productId)) {
                inCart = true;
                txt_amount.setVisibility(View.VISIBLE);
                btn_delete.setVisibility(View.VISIBLE);
                bg_delete.setVisibility(View.VISIBLE);

                if (!update) {
                    amount = item.getAmount();
                    if (item.isPackaged())
                        txt_amount.setText(new StringBuilder().append(((int) (amount))).append(" ").append("X"));
                    else
                        txt_amount.setText(new StringBuilder().append(amount).append(" ").append(context.getString(R.string.kg)));
                }
                cartProductId = item.getCartProductId();

                break;

            } else {
                txt_amount.setVisibility(View.GONE);
                btn_delete.setVisibility(View.GONE);
                bg_delete.setVisibility(View.GONE);
                inCart = false;
            }
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


    public void addItemsSearch(List<StoreSearchQuery.ProductQuery> productsListSearch) {
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
        StoreSearchQuery.ProductQuery item = getItemSearch(position);
        if (item == null) {
            productsListSearch.remove(position);
            notifyItemRemoved(position);
        }

    }

    public StoreSearchQuery.ProductQuery getItemSearch(int position) {
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


    public void updateCartItems(String itemsId, double amount, double price) {
        Log.d("HAZEM", "Here See " + Common.TOTAL_CART_PRICE);
        Log.i("TAG Moha price", "updateCartItems: " + Common.TOTAL_CART_PRICE);
        UpdateCartItem updateAmount = UpdateCartItem.builder().amount(amount).build();
        MyApolloClient.getApollowClientAuthorization().mutate(UpdateCartItemMutation.builder().id(itemsId).data(updateAmount).build())
                .enqueue(new ApolloCall.Callback<UpdateCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateCartItemMutation.Data> response) {

                        if (response.data().CartItemMutation().update().status() == 200) {
                            Common.GetCartItemsCount(new CallbackListener() {
                                @Override
                                public void OnSuccessCallback() {
                                    Log.i("TAG Moha price", "OnSuccessCallback: " + context);
                                    if (context instanceof StoreDetailActivity) {
                                        Log.i("TAG Moha price", "OnSuccessCallback: " + "true");
                                        StoreDetailActivity activity = (StoreDetailActivity) context;
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //checkItemInCart(txt_amount, btn_delete, bg_delete, true);
                                            }
                                        });
                                    }
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


    public void deleteCartItems(String ProductId, double price) {

        Common.TOTAL_CART_AMOUNT -= 1;
        Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;
        Log.i("TAG Moha price", "deleteCartItems: " + Common.TOTAL_CART_PRICE);
        MyApolloClient.getApollowClientAuthorization().mutate(RemoveCartItemMutation.builder()._id(ProductId).build())
                .enqueue(new ApolloCall.Callback<RemoveCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<RemoveCartItemMutation.Data> response) {


                        if (response.data().CartItemMutation().remove().status() == 200) {
                            EventBus.getDefault().postSticky(new CalculateCartEvent(true, -price, -1));
                            Common.GetCartItemsCount(new CallbackListener() {
                                @Override
                                public void OnSuccessCallback() {
                                    if (context instanceof StoreDetailActivity) {
                                        Log.i("TAG Moha price", "OnSuccessCallback: " + "true");
                                        StoreDetailActivity activity = (StoreDetailActivity) context;
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //checkItemInCart(txt_amount, btn_delete, bg_delete, true);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void OnFailedCallback() {

                                }
                            });
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });


    }

}
