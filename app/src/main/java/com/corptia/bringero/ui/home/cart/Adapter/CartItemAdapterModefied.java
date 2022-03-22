package com.corptia.bringero.ui.home.cart.Adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.IClickRecyclerAdapter;
import com.corptia.bringero.Interface.IOnImageViewAdapterClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.graphql.RemoveCartItemMutation;
import com.corptia.bringero.model.EventBus.CalculatePriceEvent;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.PicassoUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class CartItemAdapterModefied extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<MyCartQuery.Item> cartItems = new ArrayList<>();
    boolean isCart;
    IClickRecyclerAdapter iClickRecyclerAdapter;
    Handler handler;
    CustomLoading loading;
    public static final int VIEW_TYPE_CART_NORMAL = 0;
    public static final int VIEW_TYPE_CHECK_OUT = 2;
    public CartItemAdapterModefied.UpdateCartItemsListener updateCartItemsListener;


    public CartItemAdapterModefied(Context context, @Nullable List<MyCartQuery.Item> cartItems, boolean isCart, IClickRecyclerAdapter iClickRecyclerAdapter) {
        this.context = context;
        this.cartItems = new ArrayList<>(cartItems);
        this.isCart = isCart;
        this.iClickRecyclerAdapter = iClickRecyclerAdapter;
        handler = new Handler();

        loading = new CustomLoading(context, true);

    }

    public void setUpdateCartItemsListener(CartItemAdapterModefied.UpdateCartItemsListener updateCartItemsListener) {
        this.updateCartItemsListener = updateCartItemsListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_CART_NORMAL:
                return new CartItemAdapterModefied.ViewHolderCart(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_modefied, parent, false));
            case VIEW_TYPE_CHECK_OUT:
                return new CartItemAdapterModefied.ViewHolderCartDiscount(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_items_check_out, parent, false));
            default:
                return null;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        AtomicReference<MyCartQuery.Item> itemInCart = new AtomicReference<>(cartItems.get(position));

        String productName = itemInCart.get().PricingProduct().Product().name();
        double priceAfterDiscount;

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_CART_NORMAL:
                CartItemAdapterModefied.ViewHolderCart cartViewHolder = (CartItemAdapterModefied.ViewHolderCart) holder;

                if (cartItems.get(position).PricingProduct().discountActive() != null && cartItems.get(position).PricingProduct().discountActive()) {
                    priceAfterDiscount = (1 - itemInCart.get().PricingProduct().discountRatio()) * itemInCart.get().PricingProduct().storePrice();
                    cartViewHolder.txt_price.setText(new StringBuilder().append(Common.getDecimalNumber(priceAfterDiscount)).append(" ").append(context.getString(R.string.currency)));
                } else {
                    priceAfterDiscount = 0;
                    cartViewHolder.txt_price.setText(new StringBuilder().append(Common.getDecimalNumber(itemInCart.get().PricingProduct().storePrice())).append(" ").append(context.getString(R.string.currency)));
                }
                cartViewHolder.txt_name_product.setText(productName.length() >= 30 ? productName.substring(0, 20) + "..." : productName);

                if (itemInCart.get().PricingProduct().Product().ImageResponse().data() != null)
                    PicassoUtils.setImage(Common.BASE_URL_IMAGE + itemInCart.get().PricingProduct().Product().ImageResponse().data().name(), cartViewHolder.image_product);

                if (isCart) {
                    if (itemInCart.get().PricingProduct().Product().isPackaged())
                        cartViewHolder.txt_quantity.setText("" + ((int) itemInCart.get().amount()));
                    else
                        cartViewHolder.txt_quantity.setText("" + itemInCart.get().amount());
                    if (cartItems.get(position).PricingProduct().discountActive() != null && cartItems.get(position).PricingProduct().discountActive()) {
                        cartViewHolder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(itemInCart.get().totalPrice())).append(" ").append(context.getString(R.string.currency)));
                        cartViewHolder.txt_old_price.setText(new StringBuilder().append(itemInCart.get().PricingProduct().storePrice()).append(" ").append(context.getString(R.string.currency)));
                    } else {
                        cartViewHolder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(itemInCart.get().totalPrice())).append(" ").append(context.getString(R.string.currency)));

                    }
                    //Event
                    cartViewHolder.setListener((view, position1, isDecrease, isDelete) -> {

                        double amount = itemInCart.get().amount(); //View UI
                        double step = 0;

                        if (!isDelete) {
                            if (isDecrease) //if Decrease quantity
                            {
                                if (itemInCart.get().PricingProduct().Product().isPackaged()) {

                                    if (amount > 1) {
                                        amount -= 1;
                                        cartViewHolder.txt_quantity.setText("" + ((int) amount));

                                        if (cartItems.get(position).PricingProduct().discountActive() != null && cartItems.get(position).PricingProduct().discountActive()) {
                                            cartViewHolder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(amount * (itemInCart.get().PricingProduct().storePrice() - itemInCart.get().PricingProduct().storePrice() * itemInCart.get().PricingProduct().discountRatio()))).append(" ").append(context.getString(R.string.currency)));
                                            EventBus.getDefault().postSticky(new CalculatePriceEvent(true, itemInCart.get()._id(), amount, -(itemInCart.get().PricingProduct().storePrice() - itemInCart.get().PricingProduct().storePrice() * itemInCart.get().PricingProduct().discountRatio())));
                                        } else {
                                            cartViewHolder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(amount * itemInCart.get().PricingProduct().storePrice())).append(" ").append(context.getString(R.string.currency)));
                                            EventBus.getDefault().postSticky(new CalculatePriceEvent(true, itemInCart.get()._id(), amount, -itemInCart.get().PricingProduct().storePrice()));
                                        }
                                    }

                                } else {

                                    amount -= itemInCart.get().PricingProduct().Product().unitStep();
                                    step = itemInCart.get().PricingProduct().Product().unitStep();

                                    if (amount + step <= itemInCart.get().PricingProduct().Product().minSellingUnits()) {

                                        return;
                                    }

                                    cartViewHolder.txt_quantity.setText("" + (amount));
                                    if (cartItems.get(position).PricingProduct().discountActive() != null && cartItems.get(position).PricingProduct().discountActive()) {
                                        cartViewHolder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(amount * (itemInCart.get().PricingProduct().storePrice() - itemInCart.get().PricingProduct().storePrice() * itemInCart.get().PricingProduct().discountRatio()))).append(" ").append(context.getString(R.string.currency)));
                                        EventBus.getDefault().postSticky(new CalculatePriceEvent(true, itemInCart.get()._id(), amount, -(itemInCart.get().PricingProduct().storePrice() - itemInCart.get().PricingProduct().storePrice() * itemInCart.get().PricingProduct().discountRatio()) * step));

                                    } else {
                                        cartViewHolder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(amount * itemInCart.get().PricingProduct().storePrice())).append(" ").append(context.getString(R.string.currency)));
                                        EventBus.getDefault().postSticky(new CalculatePriceEvent(true, itemInCart.get()._id(), amount, -(itemInCart.get().PricingProduct().storePrice() * step)));

                                    }

                                }

                            } else if (amount < 99) {

                                if (itemInCart.get().PricingProduct().Product().isPackaged()) {

                                    if (itemInCart.get().PricingProduct().storePrice() + Common.TOTAL_CART_PRICE > Common.BASE_MAX_PRICE) {
                                        Toasty.warning(context, context.getString(R.string.limit_max_cart)).show();
                                        return;
                                    }
                                    amount += 1;
                                    cartViewHolder.txt_quantity.setText("" + ((int) amount));

                                    if (cartItems.get(position).PricingProduct().discountActive() != null && cartItems.get(position).PricingProduct().discountActive()) {
                                        EventBus.getDefault().postSticky(new CalculatePriceEvent(true, itemInCart.get()._id(), amount, (itemInCart.get().PricingProduct().storePrice() - itemInCart.get().PricingProduct().storePrice() * itemInCart.get().PricingProduct().discountRatio())));
                                    } else {
                                        EventBus.getDefault().postSticky(new CalculatePriceEvent(true, itemInCart.get()._id(), amount, itemInCart.get().PricingProduct().storePrice()));
                                    }

                                } else {
                                    amount += itemInCart.get().PricingProduct().Product().unitStep();
                                    step = itemInCart.get().PricingProduct().Product().unitStep();

                                    if ((itemInCart.get().PricingProduct().storePrice() * step) + Common.TOTAL_CART_PRICE > Common.BASE_MAX_PRICE) {
                                        Toasty.warning(context, context.getString(R.string.limit_max_cart)).show();
                                        return;
                                    }
                                    cartViewHolder.txt_quantity.setText("" + (amount));
                                }
                                if (cartItems.get(position).PricingProduct().discountActive() != null && cartItems.get(position).PricingProduct().discountActive()) {
                                    cartViewHolder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(amount * (itemInCart.get().PricingProduct().storePrice() - itemInCart.get().PricingProduct().storePrice() * itemInCart.get().PricingProduct().discountRatio()))).append(" ").append(context.getString(R.string.currency)));
                                    EventBus.getDefault().postSticky(new CalculatePriceEvent(true, itemInCart.get()._id(), amount, (itemInCart.get().PricingProduct().storePrice() - itemInCart.get().PricingProduct().storePrice() * itemInCart.get().PricingProduct().discountRatio()) * step));
                                } else {
                                    cartViewHolder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(amount * itemInCart.get().PricingProduct().storePrice())).append(" ").append(context.getString(R.string.currency)));
                                    EventBus.getDefault().postSticky(new CalculatePriceEvent(true, itemInCart.get()._id(), amount, itemInCart.get().PricingProduct().storePrice() * step));

                                }

                            }

                            cartViewHolder.txt_quantity.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    cartViewHolder.txt_quantity.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                }
                            });


                            //Update Amount local list
                            if (cartItems.get(position).PricingProduct().discountActive() != null && cartItems.get(position).PricingProduct().discountActive()) {
                                updateCartItemsLocal(position, amount, amount * priceAfterDiscount);
                            } else {

                                updateCartItemsLocal(position, amount, amount * itemInCart.get().PricingProduct().storePrice());
                            }
                            itemInCart.set(updateCurrentItems(amount, itemInCart.get()));


                        } else {

                            loading.showProgressBar(context, false);

                            double finalAmount = amount;
                            MyApolloClient.getApollowClientAuthorization().mutate(RemoveCartItemMutation.builder()._id(itemInCart.get()._id()).build())
                                    .enqueue(new ApolloCall.Callback<RemoveCartItemMutation.Data>() {
                                        @Override
                                        public void onResponse(@NotNull Response<RemoveCartItemMutation.Data> response) {

                                            handler.post(() -> {
                                                loading.hideProgressBar();

                                                if (response.data().CartItemMutation().remove().status() == 200) {

                                                    Toast.makeText(context, "Items Deleted !", Toast.LENGTH_SHORT).show();

                                                    cartItems.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeChanged(position, cartItems.size());

                                                    double totalProductPrice = finalAmount * itemInCart.get().PricingProduct().storePrice();


                                                    //TODO I stop this code

                                                    EventBus.getDefault().postSticky(new CalculatePriceEvent(true, itemInCart.get()._id(), -finalAmount, -(finalAmount) * priceAfterDiscount));

                                                    iClickRecyclerAdapter.onClickAdapter(position);

                                                }

                                            });

                                        }

                                        @Override
                                        public void onFailure(@NotNull ApolloException e) {

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {

                                                    Toasty.error(context, "Error in connecting to the Internet").show();

                                                    loading.hideProgressBar();

                                                }
                                            });

                                        }
                                    });
                        }
                    });
                }

                break;
            case VIEW_TYPE_CHECK_OUT:

                CartItemAdapterModefied.ViewHolderCart cartViewHolder2 = (CartItemAdapterModefied.ViewHolderCart) holder;

                cartViewHolder2.txt_price.setText("" + itemInCart.get().PricingProduct().storePrice() + " " + context.getString(R.string.currency));
                cartViewHolder2.txt_name_product.setText(productName.length() >= 30 ? productName.substring(0, 20) + "..." : productName);


                if (itemInCart.get().PricingProduct().Product().ImageResponse().data() != null)
                    PicassoUtils.setImage(Common.BASE_URL_IMAGE + itemInCart.get().PricingProduct().Product().ImageResponse().data().name(), cartViewHolder2.image_product);


                break;
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolderCart extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_name_product)
        TextView txt_name_product;
        @BindView(R.id.txt_price)
        TextView txt_price;
        @BindView(R.id.image_product)
        ImageView image_product;

        //@BindView(R.id.txt_quantity)
        TextView txt_quantity;
        //@BindView(R.id.img_decrease)
        ImageView img_decrease;
        //@BindView(R.id.img_increase)
        ImageView img_increase;
        //@BindView(R.id.img_delete_product)
        ImageView img_delete_product;

        TextView txt_total_price;
        ProgressBar progress_circular;
        @BindView(R.id.txt_old_price)
        TextView txt_old_price;

        IOnImageViewAdapterClickListener listener;

        public void setListener(IOnImageViewAdapterClickListener listener) {
            this.listener = listener;
        }


        public ViewHolderCart(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            if (isCart) {

                txt_quantity = itemView.findViewById(R.id.txt_quantity);
                img_decrease = itemView.findViewById(R.id.img_decrease);
                img_increase = itemView.findViewById(R.id.img_increase);
                img_delete_product = itemView.findViewById(R.id.img_delete_product);
                txt_total_price = itemView.findViewById(R.id.txt_total_price);
                progress_circular = itemView.findViewById(R.id.progress_circular);

                img_decrease.setOnClickListener(this);
                img_increase.setOnClickListener(this);
                img_delete_product.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View view) {

            if (img_decrease == view) {
                listener.onCalculatePriceListener(view, getAdapterPosition(), true, false);
            } else if (img_increase == view) {
                listener.onCalculatePriceListener(view, getAdapterPosition(), false, false);
            } else if (img_delete_product == view) {

                listener.onCalculatePriceListener(view, getAdapterPosition(), false, true);

            }
        }
    }

    public class ViewHolderCartDiscount extends CartItemAdapterModefied.ViewHolderCart {

        @BindView(R.id.txt_discount)
        TextView txt_discount;
        @BindView(R.id.txt_old_price)
        TextView txt_old_price;

        public ViewHolderCartDiscount(@NonNull View itemView) {
            super(itemView);
        }
    }


    public interface UpdateCartItemsListener {
        void onUpdateCart(String itemId, int amount);
    }

    public interface IDeleteCartItemsListener {
        void onDeleteCart(int position, int amount);
    }


    @Override
    public int getItemViewType(int position) {

        if (isCart) {
//            (cartItems.get(position).PricingProduct().discountActive() != null && cartItems.get(position).PricingProduct().discountActive())
            return VIEW_TYPE_CART_NORMAL;
        } else
            return VIEW_TYPE_CHECK_OUT;
    }

    private void updateCartItemsLocal(int position, double amount, double totalPrice) {

        MyCartQuery.Item tempItem = cartItems.get(position);

        MyCartQuery.Item newItem = new MyCartQuery.Item(tempItem.__typename(), tempItem._id(), tempItem.PricingProduct(), amount, totalPrice);

        cartItems.set(position, newItem);

//        notifyItemChanged(position);


    }

    private MyCartQuery.Item updateCurrentItems(double amount, MyCartQuery.Item tempItem) {

        return new MyCartQuery.Item(tempItem.__typename(), tempItem._id(), tempItem.PricingProduct(), amount, tempItem.PricingProduct().storePrice() * amount);
    }

}
