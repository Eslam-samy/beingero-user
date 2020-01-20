package com.corptia.bringero.ui.cart.Adapter;

import android.content.Context;
import android.os.Handler;
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
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.graphql.RemoveCartItemMutation;
import com.corptia.bringero.model.EventBus.CalculatePriceEvent;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class CartItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<MyCartQuery.Item> cartItems = new ArrayList<>();
    public UpdateCartItemsListener updateCartItemsListener;
    boolean isCart;
    IClickRecyclerAdapter iClickRecyclerAdapter;
    Handler handler;
    IDeleteCartItemsListener iDeleteCartItemsListener;

    CustomLoading loading;

    public static final int VIEW_TYPE_CART_NORMAL = 0;
    public static final int VIEW_TYPE_CART_DISCOUNT = 1;
    public static final int VIEW_TYPE_CHECK_OUT = 2;

    public void setUpdateCartItemsListener(UpdateCartItemsListener updateCartItemsListener) {
        this.updateCartItemsListener = updateCartItemsListener;
    }

    public IDeleteCartItemsListener getiDeleteCartItemsListener() {
        return iDeleteCartItemsListener;
    }

    public void setiDeleteCartItemsListener(IDeleteCartItemsListener iDeleteCartItemsListener) {
        this.iDeleteCartItemsListener = iDeleteCartItemsListener;
    }

    public CartItemsAdapter(Context context, @Nullable List<MyCartQuery.Item> cartItems, boolean isCart, IClickRecyclerAdapter iClickRecyclerAdapter) {
        this.context = context;
        this.cartItems = new ArrayList<>(cartItems);
        this.isCart = isCart;
        this.iClickRecyclerAdapter = iClickRecyclerAdapter;
        handler = new Handler();

        loading = new CustomLoading(context, true);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        switch (viewType) {
            case VIEW_TYPE_CART_NORMAL:
                return new ViewHolderCart(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_shop, parent, false));
            case VIEW_TYPE_CART_DISCOUNT:
                return new ViewHolderCartDiscount(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_shop_discount, parent, false));

            case VIEW_TYPE_CHECK_OUT:
                return new ViewHolderCartDiscount(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_items_check_out, parent, false));
            default:
                return null;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyCartQuery.Item item = cartItems.get(position);

        String productName = item.PricingProduct().Product().name();

        switch (holder.getItemViewType()) {


            case VIEW_TYPE_CART_NORMAL:
                ViewHolderCart cartViewHolder = (ViewHolderCart) holder;


                cartViewHolder.txt_price.setText(new StringBuilder().append(Common.getDecimalNumber(item.PricingProduct().storePrice())).append(" ").append(context.getString(R.string.currency)));
                cartViewHolder.txt_name_product.setText(productName.length() >= 30 ? productName.substring(0, 20) + "..." : productName);


                if (item.PricingProduct().Product().ImageResponse().data() != null)
                    PicassoUtils.setImage(Common.BASE_URL_IMAGE + item.PricingProduct().Product().ImageResponse().data().name(), cartViewHolder.image_product);

                //Log.d("HAZEM" , "FULL : " +Common.BASE_URL_IMAGE +item.PricingProduct().Product().imageId() );


                if (isCart) {
                    cartViewHolder.txt_quantity.setText("" + item.amount());
                    cartViewHolder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(item.totalPrice())).append(" ").append(context.getString(R.string.currency)));

                    //Event
                    cartViewHolder.setListener((view, position1, isDecrease, isDelete) -> {

                        int amount = Integer.parseInt(cartViewHolder.txt_quantity.getText().toString());

                        if (!isDelete) {
                            //If not Button delete food From Cart Click
                            int totalAmountStore = 9999;
                            if (item.PricingProduct().amount() != null)
                                totalAmountStore = item.PricingProduct().amount();

                            if (isDecrease) //if Decrease quantity
                            {
                                if (amount > 1) {

                                    amount -= 1;

                                    cartViewHolder.txt_quantity.setText("" + amount);
                                    EventBus.getDefault().postSticky(new CalculatePriceEvent(true, item._id(), amount, -item.PricingProduct().storePrice()));
                                    cartViewHolder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(amount * item.PricingProduct().storePrice())).append(" ").append(context.getString(R.string.currency)));
                                }

                            } else if (amount < 99) {

                                if (item.PricingProduct().storePrice() + Common.TOTAL_CART_PRICE > Common.BASE_MAX_PRICE) {

                                    Toasty.warning(context, context.getString(R.string.limit_max_cart)).show();
                                    return;
                                } else {

                                    amount += 1;

                                    //                        if (amount < totalAmountStore) {
                                    cartViewHolder.txt_quantity.setText("" + amount);
                                    EventBus.getDefault().postSticky(new CalculatePriceEvent(true, item._id(), amount, item.PricingProduct().storePrice()));
                                    cartViewHolder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber((amount) * item.PricingProduct().storePrice())).append(" ").append(context.getString(R.string.currency)));

//                        }
                                }


                            }


                            if (updateCartItemsListener != null) {
                                //updateCartItemsListener.onUpdateCart(item._id(), Integer.parseInt(holder.txt_quantity.getText().toString()));
                            }


                            cartViewHolder.txt_quantity.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    cartViewHolder.txt_quantity.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                }
                            });


                            //Update Amount local list
                            updateCartItemsLocal(position, amount, amount * item.PricingProduct().storePrice());


                        } else {


//                    Common.TOTAL_CART_PRICE += -item.PricingProduct().storePrice();
//                    Common.TOTAL_CART_AMOUNT += -amount;
//
//                    iDeleteCartItemsListener.onDeleteCart(position, amount);
//
//                    cartItems.remove(position);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position, cartItems.size());
//
//
////                    holder.progress_circular.setVisibility(View.VISIBLE);
////                    holder.img_delete_product.setVisibility(View.INVISIBLE);


                            loading.showProgressBar(context, false);

                            int finalAmount = amount;
                            MyApolloClient.getApollowClientAuthorization().mutate(RemoveCartItemMutation.builder()._id(item._id()).build())
                                    .enqueue(new ApolloCall.Callback<RemoveCartItemMutation.Data>() {
                                        @Override
                                        public void onResponse(@NotNull Response<RemoveCartItemMutation.Data> response) {

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {

//                                            holder.progress_circular.setVisibility(View.VISIBLE);
//                                            holder.img_delete_product.setVisibility(View.INVISIBLE);

                                                    if (response.data().CartItemMutation().remove().status() == 200) {
                                                        loading.hideProgressBar();

//                                                holder.progress_circular.setVisibility(View.GONE);

//                                                holder.progress_circular.setVisibility(View.VISIBLE);
//                                                holder.img_delete_product.setVisibility(View.INVISIBLE);

                                                        Toast.makeText(context, "Items Deleted !", Toast.LENGTH_SHORT).show();

                                                        cartItems.remove(position);
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeChanged(position, cartItems.size());

                                                        double totalProductPrice = finalAmount * item.PricingProduct().storePrice();


                                                        //TODO I stop this code

                                                        EventBus.getDefault().postSticky(new CalculatePriceEvent(true, item._id(), -finalAmount, -(finalAmount) * item.PricingProduct().storePrice()));

                                                        iClickRecyclerAdapter.onClickAdapter(position);

                                                    } else {
//                                                holder.progress_circular.setVisibility(View.GONE);
//                                                holder.img_delete_product.setVisibility(View.VISIBLE);
                                                    }

                                                }
                                            });

                                        }

                                        @Override
                                        public void onFailure(@NotNull ApolloException e) {

                                        }
                                    });


                        }


                    });


                }

                break;

            case VIEW_TYPE_CART_DISCOUNT:

                ViewHolderCartDiscount cartDiscount = (ViewHolderCartDiscount) holder;

                double priceAfterDiscount = (1 - item.PricingProduct().discountRatio()) * item.PricingProduct().storePrice();

                cartDiscount.txt_price.setText(new StringBuilder().append(Common.getDecimalNumber(priceAfterDiscount)).append(" ").append(context.getString(R.string.currency)));

                cartDiscount.txt_name_product.setText(productName.length() >= 30 ? productName.substring(0, 20) + "..." : productName);


                if (item.PricingProduct().Product().ImageResponse().data() != null)
                    PicassoUtils.setImage(Common.BASE_URL_IMAGE + item.PricingProduct().Product().ImageResponse().data().name(), cartDiscount.image_product);

                //Log.d("HAZEM" , "FULL : " +Common.BASE_URL_IMAGE +item.PricingProduct().Product().imageId() );

                if (isCart) {
                    cartDiscount.txt_quantity.setText("" + item.amount());
                    cartDiscount.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(item.totalPrice())).append(" ").append(context.getString(R.string.currency)));
                    cartDiscount.txt_discount.setText(new StringBuilder().append((int) (item.PricingProduct().discountRatio() * 100)).append(" %"));
                    cartDiscount.txt_old_price.setText(new StringBuilder().append(item.PricingProduct().storePrice()).append(" ").append(context.getString(R.string.currency)));

                    //Event
                    cartDiscount.setListener((view, position1, isDecrease, isDelete) -> {

                        int amount = Integer.parseInt(cartDiscount.txt_quantity.getText().toString());


                        if (!isDelete) {
                            //If not Button delete food From Cart Click
                            int totalAmountStore = 9999;
                            if (item.PricingProduct().amount() != null)
                                totalAmountStore = item.PricingProduct().amount();

                            if (isDecrease) //if Decrease quantity
                            {
                                if (amount > 1) {
                                    amount -= 1;
                                    cartDiscount.txt_quantity.setText("" + amount);
                                    EventBus.getDefault().postSticky(new CalculatePriceEvent(true, item._id(), amount, -priceAfterDiscount));
                                    cartDiscount.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(amount * priceAfterDiscount)).append(" ").append(context.getString(R.string.currency)));
                                }

                            } else if (amount < 99) {

                                if (priceAfterDiscount + Common.TOTAL_CART_PRICE > Common.BASE_MAX_PRICE) {

                                    Toasty.warning(context, context.getString(R.string.limit_max_cart)).show();
                                } else {
                                    amount += 1;
                                    //                        if (amount < totalAmountStore) {
                                    cartDiscount.txt_quantity.setText("" + amount);
                                    EventBus.getDefault().postSticky(new CalculatePriceEvent(true, item._id(), amount, priceAfterDiscount));
                                    cartDiscount.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(amount * priceAfterDiscount)).append(" ").append(context.getString(R.string.currency)));

//                        }
                                }


                            }


                            if (updateCartItemsListener != null) {
                                //updateCartItemsListener.onUpdateCart(item._id(), Integer.parseInt(holder.txt_quantity.getText().toString()));
                            }


                            cartDiscount.txt_quantity.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    cartDiscount.txt_quantity.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                                }
                            });

                            //Update Amount local list
                            updateCartItemsLocal(position, amount, amount * priceAfterDiscount);

                        } else {


//                    Common.TOTAL_CART_PRICE += -item.PricingProduct().storePrice();
//                    Common.TOTAL_CART_AMOUNT += -amount;
//
//                    iDeleteCartItemsListener.onDeleteCart(position, amount);
//
//                    cartItems.remove(position);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position, cartItems.size());
//
//
////                    holder.progress_circular.setVisibility(View.VISIBLE);
////                    holder.img_delete_product.setVisibility(View.INVISIBLE);


                            loading.showProgressBar(context, false);

                            int finalAmount = amount;
                            MyApolloClient.getApollowClientAuthorization().mutate(RemoveCartItemMutation.builder()._id(item._id()).build())
                                    .enqueue(new ApolloCall.Callback<RemoveCartItemMutation.Data>() {
                                        @Override
                                        public void onResponse(@NotNull Response<RemoveCartItemMutation.Data> response) {

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {

//                                            holder.progress_circular.setVisibility(View.VISIBLE);
//                                            holder.img_delete_product.setVisibility(View.INVISIBLE);

                                                    if (response.data().CartItemMutation().remove().status() == 200) {
                                                        loading.hideProgressBar();

//                                                holder.progress_circular.setVisibility(View.GONE);

//                                                holder.progress_circular.setVisibility(View.VISIBLE);
//                                                holder.img_delete_product.setVisibility(View.INVISIBLE);

                                                        Toast.makeText(context, "Items Deleted !", Toast.LENGTH_SHORT).show();

                                                        cartItems.remove(position);
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeChanged(position, cartItems.size());

                                                        double totalProductPrice = finalAmount * priceAfterDiscount;

                                                        //I stop this code
                                                        EventBus.getDefault().postSticky(new CalculatePriceEvent(true, item._id(), -finalAmount, -(finalAmount) * priceAfterDiscount));


                                                        iClickRecyclerAdapter.onClickAdapter(position);

                                                    } else {
//                                                holder.progress_circular.setVisibility(View.GONE);
//                                                holder.img_delete_product.setVisibility(View.VISIBLE);
                                                    }

                                                }
                                            });

                                        }

                                        @Override
                                        public void onFailure(@NotNull ApolloException e) {

                                        }
                                    });


                        }


                    });


                }

                break;

            case VIEW_TYPE_CHECK_OUT:

                ViewHolderCart cartViewHolder2 = (ViewHolderCart) holder;

                cartViewHolder2.txt_price.setText("" + item.PricingProduct().storePrice() + " " + context.getString(R.string.currency));
                cartViewHolder2.txt_name_product.setText(productName.length() >= 30 ? productName.substring(0, 20) + "..." : productName);


                if (item.PricingProduct().Product().ImageResponse().data() != null)
                    PicassoUtils.setImage(Common.BASE_URL_IMAGE + item.PricingProduct().Product().ImageResponse().data().name(), cartViewHolder2.image_product);


                break;
        }
    }


//        holder.edt_quantity.setFilters(new InputFilter[]{ new InputFilterMinMax(1, item.PricingProduct().amount())});
//        holder.edt_quantity.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                if(   holder.edt_quantity.getText().length() == 0){
//                    holder.edt_quantity.setText("1");
//                }
//
//                if ( holder.edt_quantity.getText().toString().trim().matches("^0") )
//                {
//                    // Not allowed
//                    holder.edt_quantity.setText("1");
//                }
//            }
//        });


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

    public class ViewHolderCartDiscount extends ViewHolderCart {

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
            if (cartItems.get(position).PricingProduct().discountActive() != null && cartItems.get(position).PricingProduct().discountActive()) {
                return VIEW_TYPE_CART_DISCOUNT;
            } else
                return VIEW_TYPE_CART_NORMAL;
        } else
            return VIEW_TYPE_CHECK_OUT;
    }


    private void updateCartItemsLocal(int position, int amount, double totalPrice) {


        MyCartQuery.Item tempItem = cartItems.get(position);

        MyCartQuery.Item newItem = new MyCartQuery.Item(tempItem.__typename(), tempItem._id(), tempItem.PricingProduct(), amount, totalPrice);

        cartItems.set(position, newItem);

//        notifyItemChanged(position);


    }


}
