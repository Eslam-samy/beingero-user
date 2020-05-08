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
import com.corptia.bringero.Interface.AdapterIOnProductClickListener;
import com.corptia.bringero.Interface.CallbackListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseViewHolder;
import com.corptia.bringero.graphql.CreateCartItemMutation;
import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.graphql.RemoveCartItemMutation;
import com.corptia.bringero.graphql.StoreSearchQuery;
import com.corptia.bringero.graphql.UpdateCartItemMutation;
import com.corptia.bringero.model.CartItemsModel;
import com.corptia.bringero.model.EventBus.CalculateCartEvent;
import com.corptia.bringero.model.MyCart;
import com.corptia.bringero.type.CreateCartItem;
import com.corptia.bringero.type.UpdateCartItem;
import com.corptia.bringero.ui.search.SearchProductsActivity;
import com.corptia.bringero.utils.PicassoUtils;
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

public class NewStoreDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private boolean isLoaderVisible = false;
    private String TAG = "TAG Moha price adapter ";

    Context context;
    List<GetStoreProductsQuery.Product> productsList = new ArrayList<>();
    List<StoreSearchQuery.ProductQuery> productsListSearch = new ArrayList<>();

    AdapterIOnProductClickListener listener;


    boolean isSearch;

    //Var

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

    public NewStoreDetailAdapter(SearchProductsActivity context, List<StoreSearchQuery.ProductQuery> products, boolean isSearch) {
        this.context = context;
        if (products != null)
            this.productsListSearch = new ArrayList<>(products);
        Common.adapterIsLoading = false;
        this.isSearch = true;
    }

    public void setListener(AdapterIOnProductClickListener listener) {
        this.listener = listener;
    }

    public NewStoreDetailAdapter(Context context, List<GetStoreProductsQuery.Product> productsList) {
        this.context = context;
        if (productsList != null)
            this.productsList = new ArrayList<>(productsList);
        isSearch = false;
        Common.adapterIsLoading = false;
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
                final GetStoreProductsQuery.Product product = productsList.get(position);
                continueBinding(position, product, null, productId = product._id());
            } else {
                final StoreSearchQuery.ProductQuery productSearch = productsListSearch.get(position);
                continueBinding(position, null, productSearch, productSearch._id());
            }
        }

        private void continueBinding(int position, GetStoreProductsQuery.Product product, StoreSearchQuery.ProductQuery productSearch, final String productId) {
            if (!isSearch) {
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
                isPackaged = product.Product().isPackaged();

                if (product.Product().ImageResponse().status() == 200)
                    productImage = product.Product().ImageResponse().data().name();

                if (!isPackaged) {
                    unitStep = product.Product().unitStep();
                    minSellingUnits = product.Product().minSellingUnits();

                }

            } else {

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
                    productName = productSearch.Product().name();
                    isPackaged = productSearch.Product().isPackaged();

                    if (productSearch.Product().ImageResponse().status() == 200)
                        productImage = productSearch.Product().ImageResponse().data().name();

                }
            }
            //Check Item in cart
            checkItemInCart(txt_amount, btn_delete, bg_delete, false);

            //Here Set Data On Cart
            txt_price.setText(new StringBuilder().append(Common.getDecimalNumber(price)).append(" ").append(context.getString(R.string.currency)));
            txt_old_price.setText(new StringBuilder().append(Common.getDecimalNumber(oldPrice)).append(" ").append(context.getString(R.string.currency)));
            txt_discount.setText(new StringBuilder().append((int) (discountRatio * 100)).append(" %"));
            txt_name_product.setText(Utils.cutName(productName));
            PicassoUtils.setImage(Common.BASE_URL_IMAGE + productImage, image_product);
            //Click handling

            if (!Common.adapterIsLoading) {
                itemView.setClickable(true);
                btn_delete.setClickable(true);
                if (listener != null && Common.IS_AVAILABLE_STORE) {
                    double finalPrice1 = price;
                    boolean finalIsPackaged = isPackaged;
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //rePopulateData
                            rePopulateData(position, product, productSearch);
                            //Here Condition Limit Max Price
                            if (Common.TOTAL_CART_PRICE <= 0) {
                                Common.TOTAL_CART_PRICE = 0;
                            }
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
                                    if (cartProductId.isEmpty()) {
                                        //I am product new
                                        Common.TOTAL_CART_AMOUNT += 1;
                                        cartProductId = "any";
                                    }
                                    Common.TOTAL_CART_PRICE += (finalPrice1 * step);
                                    if (!finalIsPackaged) {
                                        txt_amount.setText(new StringBuilder().append(actualAmount).append(" ").append(context.getString(R.string.kg)));
                                    }
                                    continueClickFromOutSide(actualAmount, finalPrice1 * step, false, product, productSearch);
                                }

                                animateTheAmount(step);

                            } else {
                                if (Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) == 0f) {
                                    inCart = false;
                                } else {
                                    inCart = true;
                                }
                                Log.i(TAG, "onClick: here");
                                if (checkMaxPrice(finalPrice1)) return;
                                Log.i(TAG, "onClick: here after");
                                int count;
                                count = Integer.parseInt(txt_amount.getText().toString().split(" ")[0]) + 1;
                                if (count < 100) {
                                    if (cartProductId.isEmpty()) {
                                        //I am product new
                                        Common.TOTAL_CART_AMOUNT += 1;
                                        cartProductId = "any";
                                    }
                                    Common.TOTAL_CART_PRICE += finalPrice1;
                                    continueClickFromOutSide(count, finalPrice1, false, product, productSearch);
                                    if (finalIsPackaged) {
                                        txt_amount.setText(new StringBuilder().append(count).append(" ").append("X"));
                                    }
                                }
                                animateTheAmount(count);
                            }
                        }
                    });
                    double finalPrice = price;
                    btn_delete.setOnClickListener(view -> {
                        if (Common.myLocalCartIds.contains(productId)) {
                            MyCart myCartItem = new MyCart();
                            for (MyCart myCart : Common.myLocalCart) {
                                if (myCart.getProductId().equals(productId)) {
                                    myCartItem = myCart;
                                    break;
                                }
                            }
                            isPackaged = myCartItem.getPackaged();
                            if (isPackaged) {
                                double amountNow = myCartItem.getAmount() - 1;
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
                                    continueClickFromOutSide(amountNow, finalPrice, true, product, productSearch);
                                    updateBottomSheet(-finalPrice, amountNow);
                                } else if (amountNow <= 0) {
                                    txt_amount.setText("0 ");
                                    Common.TOTAL_CART_AMOUNT -= 1;
                                    Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;

                                    continueClickFromOutSide(0, finalPrice, true, product, productSearch);
                                    updateBottomSheet(-finalPrice, 1);
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
                                    continueClickFromOutSide(amountNow, finalPrice * unitStep, true, product, productSearch);
                                    updateBottomSheet(-finalPrice * unitStep, amountNow);
                                } else {
                                    txt_amount.setText("0 ");
                                    Common.TOTAL_CART_AMOUNT -= 1;
                                    Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;

                                    continueClickFromOutSide(0, finalPrice * minSellingUnits, true, product, productSearch);
                                    updateBottomSheet(-finalPrice * minSellingUnits, 1);
                                }
                            }
                        } else {
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
                                        Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - finalPrice;
                                        inCart = true;

                                        continueClickFromOutSide(amountNow, finalPrice, true, product, productSearch);

                                        updateBottomSheet(-finalPrice, amountNow);

                                    } else if (amountNow <= 0) {
                                        inCart = false;

                                        txt_amount.setVisibility(View.INVISIBLE);
                                        btn_delete.setVisibility(View.INVISIBLE);
                                        bg_delete.setVisibility(View.INVISIBLE);

                                        txt_amount.setText("0 ");
                                        amountNow = 0;
                                        Common.TOTAL_CART_AMOUNT -= 1;
                                        Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - finalPrice;

                                        continueClickFromOutSide(0, finalPrice, true, product, productSearch);
                                        updateBottomSheet(-finalPrice, 1);

                                    }

                                } else {

                                    double amountNow = Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) - unitStep;

                                    if (amountNow >= minSellingUnits) {

                                        txt_amount.setText(new StringBuilder().append(amountNow).append(" ").append(context.getString(R.string.kg)));

                                        animateTheAmount(unitStep);
                                        inCart = true;
                                        Common.TOTAL_CART_AMOUNT -= 1;
                                        Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;
                                        continueClickFromOutSide(amountNow, finalPrice * unitStep, true, product, productSearch);

                                        updateBottomSheet(-finalPrice * unitStep, amountNow);

                                    } else {
                                        inCart = false;
                                        txt_amount.setVisibility(View.INVISIBLE);
                                        btn_delete.setVisibility(View.INVISIBLE);
                                        bg_delete.setVisibility(View.INVISIBLE);

                                        txt_amount.setText("0 ");
                                        amountNow = 0;
                                        Common.TOTAL_CART_AMOUNT -= 1;
                                        Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;
                                        continueClickFromOutSide(0, finalPrice * minSellingUnits, true, product, productSearch);
                                        updateBottomSheet(-finalPrice * minSellingUnits, 1);
                                    }
                                }
                            } else {
                                //This My Cart
                                for (CartItemsModel item : Common.CART_ITEMS_MODELS) {
                                    if (item.getPricingProductId().equalsIgnoreCase(productId)) {
                                        if (isPackaged) {

                                            cartProductId = item.getCartProductId();

                                            double amountNow = Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) - 1;

                                            if (amountNow > 0) {
                                                inCart = true;
                                                txt_amount.setText(new StringBuilder().append(((int) amountNow)).append(" ").append("X"));

                                                txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                                    }
                                                });

                                                inCart = true;

                                                Common.TOTAL_CART_AMOUNT -= 1;
                                                Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;
                                                continueClickFromOutSide(amountNow, finalPrice, true, product, productSearch);

                                                updateBottomSheet(-finalPrice, amountNow);

                                            } else if (amountNow <= 0) {
                                                inCart = false;

                                                txt_amount.setVisibility(View.INVISIBLE);
                                                btn_delete.setVisibility(View.INVISIBLE);
                                                bg_delete.setVisibility(View.INVISIBLE);

                                                txt_amount.setText("0 ");
                                                amountNow = 0;
                                                Common.TOTAL_CART_AMOUNT -= 1;
                                                Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;
                                                continueClickFromOutSide(0, finalPrice, true, product, productSearch);
                                                updateBottomSheet(-finalPrice, 1);
                                            }


                                            break;

                                        } else {
                                            cartProductId = item.getCartProductId();

                                            double amountNow = Double.parseDouble(txt_amount.getText().toString().split(" ")[0]) - unitStep;

                                            if (amountNow >= minSellingUnits) {
                                                inCart = true;
                                                txt_amount.setText(new StringBuilder().append(amountNow).append(" ").append(context.getString(R.string.kg)));

                                                txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                                    }
                                                });
                                                Common.TOTAL_CART_AMOUNT -= 1;
                                                Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;
                                                continueClickFromOutSide(amountNow, finalPrice * unitStep, true, product, productSearch);
                                                updateBottomSheet(-finalPrice * unitStep, amountNow);
                                            } else {
                                                inCart = false;
                                                txt_amount.setVisibility(View.INVISIBLE);
                                                btn_delete.setVisibility(View.INVISIBLE);
                                                bg_delete.setVisibility(View.INVISIBLE);
                                                txt_amount.setText("0 ");
                                                amountNow = 0;
                                                Common.TOTAL_CART_AMOUNT -= 1;
                                                Common.TOTAL_CART_PRICE = Common.TOTAL_CART_PRICE - price;
                                                continueClickFromOutSide(0, finalPrice * minSellingUnits, true, product, productSearch);
                                                updateBottomSheet(-finalPrice * minSellingUnits, 1);
                                            }

                                            break;

                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            } else {
                itemView.setClickable(false);
                btn_delete.setClickable(false);
            }
        }

        private void animateTheAmount(double step) {
            if (step != 1)
                txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                    }
                });
        }

        private void continueClickFromOutSide(double actualAmount, double price, boolean isDecrease, GetStoreProductsQuery.Product product, StoreSearchQuery.ProductQuery productSearch) {
            Boolean inCart = false;
            String cartId = "";
            for (CartItemsModel item : Common.CART_ITEMS_MODELS) {
                if (item != null) if (item.getPricingProductId().equalsIgnoreCase(product._id())) {
                    inCart = true;
                    cartId = item.getCartProductId();

                    if (!isSearch)
                        listener.onClickNoSearch(product, cartId, actualAmount, price, inCart, isDecrease, txt_amount, btn_delete, bg_delete);
                    else
                        listener.onClickSearch(productSearch, cartId, actualAmount, price, inCart, isDecrease, txt_amount, btn_delete, bg_delete);
                    break;
                }
            }
            if (!inCart) {
                if (!isSearch)
                    listener.onClickNoSearch(product, cartId, actualAmount, price, inCart, isDecrease, txt_amount, btn_delete, bg_delete);
                else
                    listener.onClickSearch(productSearch, cartId, actualAmount, price, inCart, isDecrease, txt_amount, btn_delete, bg_delete);

            }
        }

        private boolean checkMaxPrice(double v) {
            if (Common.TOTAL_CART_PRICE + (v) > Common.BASE_MAX_PRICE) {
                Toasty.warning(context, context.getString(R.string.limit_max_cart)).show();
                return true;
            }
            return false;
        }

        private void rePopulateData(int position, GetStoreProductsQuery.Product product, StoreSearchQuery.ProductQuery productSearch) {
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

    private void updateBottomSheet(double price, double amount) {
        if (amount > 0)
            EventBus.getDefault().postSticky(new CalculateCartEvent(true, price, amount));
    }

    Boolean inCart = false;

    public void checkItemInCart(TextView txt_amount, View btn_delete, View bg_delete, boolean update) {
        for (CartItemsModel item : Common.CART_ITEMS_MODELS) {
            if (item != null) if (item.getPricingProductId().equalsIgnoreCase(productId)) {
                inCart = true;

                if (!update) {

                    txt_amount.setVisibility(View.VISIBLE);
                    btn_delete.setVisibility(View.VISIBLE);
                    bg_delete.setVisibility(View.VISIBLE);
                    amount = item.getAmount();
                    if (item.isPackaged())
                        txt_amount.setText(new StringBuilder().append(((int) (amount))).append(" ").append("X"));
                    else
                        txt_amount.setText(new StringBuilder().append(amount).append(" ").append(context.getString(R.string.kg)));
                }
                cartProductId = item.getCartProductId();

                break;

            } else {
                if (!update) {
                    txt_amount.setVisibility(View.GONE);
                    btn_delete.setVisibility(View.GONE);
                    bg_delete.setVisibility(View.GONE);
                }
                inCart = false;
            }
        }
        if (!Common.myLocalCartIds.isEmpty()) {
            if (Common.myLocalCartIds.contains(productId)) {
                for (MyCart myCartItem : Common.myLocalCart) {
                    if (myCartItem.getProductId().equals(productId)) {
                        txt_amount.setVisibility(View.VISIBLE);
                        btn_delete.setVisibility(View.VISIBLE);
                        bg_delete.setVisibility(View.VISIBLE);

                        amount = myCartItem.getAmount();
                        if (isPackaged)
                            txt_amount.setText(new StringBuilder().append(((int) (amount))).append(" ").append("X"));
                        else
                            txt_amount.setText(new StringBuilder().append(amount).append(" ").append(context.getString(R.string.kg)));
                    }
                }
            }
        }
    }

    //------------------------------------
    public void addItems(List<GetStoreProductsQuery.Product> productsList) {
        this.productsList.addAll(productsList);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        Common.adapterIsLoading = true;
        productsList.add(null);
        notifyItemInserted(productsList.size() - 1);
    }


    public void removeLoading() {
        isLoaderVisible = false;
        Common.adapterIsLoading = false;
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
        Common.adapterIsLoading = true;
        productsListSearch.add(null);
        notifyItemInserted(productsListSearch.size() - 1);
    }

    public void removeLoadingSearch() {
        isLoaderVisible = false;
        Common.adapterIsLoading = false;
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


    public void updateCartItems(MyCart myCartItem) {
        String id = myCartItem.getCartId();
        double amount = myCartItem.getAmount();

        UpdateCartItem updateAmount = UpdateCartItem.builder().amount(amount).build();
        UpdateCartItemMutation updateCartItemMutation = UpdateCartItemMutation.builder().id(id).data(updateAmount).build();

        MyApolloClient.getApollowClientAuthorization()
                .mutate(updateCartItemMutation)
                .enqueue(new ApolloCall.Callback<UpdateCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateCartItemMutation.Data> response) {
                        if (response.data().CartItemMutation().update().status() == 200) {

                            myCartItem.setInCartAmount(response.data().CartItemMutation().update().data().amount());
                            myCartItem.setAmount(response.data().CartItemMutation().update().data().amount());
                            myCartItem.setCartId(response.data().CartItemMutation().update().data()._id());
                            myCartItem.setInCart(true);

                            Common.GetCartItemsCount(new CallbackListener() {
                                @Override
                                public void OnSuccessCallback() {
                                    if (context instanceof StoreDetailActivity) {
                                        StoreDetailActivity activity = (StoreDetailActivity) context;
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //checkItemInCart(null, null, null, true);
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

    public void deleteCartItems(MyCart myCartItem) {
        String id = myCartItem.getCartId();
        Double amount = myCartItem.getAmount();
        RemoveCartItemMutation removeCartItemMutation = RemoveCartItemMutation.builder()._id(id).build();

        MyApolloClient.getApollowClientAuthorization()
                .mutate(removeCartItemMutation)
                .enqueue(new ApolloCall.Callback<RemoveCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<RemoveCartItemMutation.Data> response) {

                        if (response.data().CartItemMutation().remove().status() == 200) {
                            if (Common.myLocalCartIds.contains(myCartItem.getProductId())) {
                                Common.myLocalCartIds.remove(myCartItem.getProductId());
                                for (int i = Common.myLocalCart.size() - 1; i >= 0; i--) {
                                    MyCart myCart = Common.myLocalCart.get(i);
                                    if (myCart.getProductId().equals(myCartItem.getProductId())) {
                                        Common.myLocalCart.remove(myCart);
                                        break;
                                    }
                                }

                            }
                            Common.GetCartItemsCount(new CallbackListener() {
                                @Override
                                public void OnSuccessCallback() {
                                    myCartItem.setInCart(false);
                                    myCartItem.setCartId(null);
                                    myCartItem.setAmount(0.0);
                                    myCartItem.setInCartAmount(0.0);
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
        updateBottomSheet(-price * amount, amount);
    }

}
