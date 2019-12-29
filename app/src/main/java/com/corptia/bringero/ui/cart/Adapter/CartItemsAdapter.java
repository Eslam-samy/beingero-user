package com.corptia.bringero.ui.cart.Adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.ViewHolder> {

    Context context;
    List<MyCartQuery.Item> cartItems = new ArrayList<>();
    public UpdateCartItemsListener updateCartItemsListener;
    boolean isCart;
    IClickRecyclerAdapter iClickRecyclerAdapter;
    Handler handler ;
    IDeleteCartItemsListener iDeleteCartItemsListener;
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
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isCart)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_shop, parent, false));
        else
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_items_check_out, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyCartQuery.Item item = cartItems.get(position);

        holder.txt_price.setText("" + item.PricingProduct().storePrice() + " " + context.getString(R.string.currency));
        String productName = item.PricingProduct().Product().name();
        holder.txt_name_product.setText(productName.length() > 30 ? productName.substring(0,20) + "..." : productName);


        if (item.PricingProduct().Product().ImageResponse().data() != null)
            PicassoUtils.setImage(Common.BASE_URL_IMAGE + item.PricingProduct().Product().ImageResponse().data().name(), holder.image_product);

        //Log.d("HAZEM" , "FULL : " +Common.BASE_URL_IMAGE +item.PricingProduct().Product().imageId() );


        if (isCart) {
            holder.txt_quantity.setText("" + item.amount());
            holder.txt_total_price.setText(new StringBuilder().append(item.totalPrice()).append(" ").append(context.getString(R.string.currency)));

            //Event
            holder.setListener((view, position1, isDecrease, isDelete) -> {

                int amount = Integer.parseInt(holder.txt_quantity.getText().toString());


                if (!isDelete) {
                    //If not Button delete food From Cart Click
                    int totalAmountStore = 9999;
                    if (item.PricingProduct().amount() != null)
                        totalAmountStore = item.PricingProduct().amount();

                    if (isDecrease) //if Decrease quantity
                    {
                        if (amount > 1) {
                            holder.txt_quantity.setText("" + (amount - 1));
                            EventBus.getDefault().postSticky(new CalculatePriceEvent(item._id(), amount - 1, -item.PricingProduct().storePrice()));
                            holder.txt_total_price.setText(new StringBuilder().append(((amount - 1) * item.PricingProduct().storePrice())).append(" ").append(context.getString(R.string.currency)));


                        }
                    } else {
//                        if (amount < totalAmountStore) {
                        holder.txt_quantity.setText("" + (amount + 1));
                        EventBus.getDefault().postSticky(new CalculatePriceEvent(item._id(), amount + 1, item.PricingProduct().storePrice()));
                        holder.txt_total_price.setText(new StringBuilder().append(((amount + 1) * item.PricingProduct().storePrice())).append(" ").append(context.getString(R.string.currency)));

//                        }


                    }


                    if (updateCartItemsListener != null) {
                        //updateCartItemsListener.onUpdateCart(item._id(), Integer.parseInt(holder.txt_quantity.getText().toString()));
                    }


                    holder.txt_quantity.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            holder.txt_quantity.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                        }
                    });



                } else {
                    Log.d("HAZEM" , "SEE Postion << " + position);
                    iDeleteCartItemsListener.onDeleteCart(position , amount);

                    cartItems.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, cartItems.size());



//                    holder.progress_circular.setVisibility(View.VISIBLE);
//                    holder.img_delete_product.setVisibility(View.INVISIBLE);







                }


            });
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

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        //@BindView(R.id.chb_select_item)
        CheckBox chb_select_item;

        TextView txt_total_price;
        ProgressBar progress_circular;


        IOnImageViewAdapterClickListener listener;

        public void setListener(IOnImageViewAdapterClickListener listener) {
            this.listener = listener;
        }


        public ViewHolder(@NonNull View itemView) {
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

    public interface UpdateCartItemsListener {
        void onUpdateCart(String itemId, int amount);
    }

    public interface IDeleteCartItemsListener {
        void onDeleteCart(int position , int amount);
    }
}
