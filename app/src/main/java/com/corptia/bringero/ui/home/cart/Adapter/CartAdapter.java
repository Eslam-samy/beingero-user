package com.corptia.bringero.ui.home.cart.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IClickRecyclerAdapter;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.storesDetail.StoreDetailActivity;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.MyCartQuery;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<MyCartQuery.StoreDatum> myCartList = new ArrayList<>();
    public CartItemAdapterModefied itemsAdapter;
    boolean isCart;
    Handler handler;
    CallBackUpdateCartItemsListener callBackUpdateCartItemsListener;

    public void setCallBackUpdateCartItemsListener(CallBackUpdateCartItemsListener callBackUpdateCartItemsListener) {
        this.callBackUpdateCartItemsListener = callBackUpdateCartItemsListener;
    }

    public CartAdapter(Context context, List<MyCartQuery.StoreDatum> cartModels, boolean isCart) {
        this.context = context;
        this.myCartList = new ArrayList<>(cartModels);
        this.isCart = isCart;
        handler = new Handler();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isCart)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_header, parent, false));
        else
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_header_check_out, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyCartQuery.StoreDatum cartModel = myCartList.get(position);

        @Nullable List<MyCartQuery.Item> itemList = new ArrayList<>(cartModel.Items());

        itemsAdapter = new CartItemAdapterModefied(context, itemList, isCart, positionItems -> {
            itemList.remove(positionItems);

            if (itemList.size() == 0) {

                myCartList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, myCartList.size());
            }
        });


        if (!isCart) {
            double price = 0;
            holder.checkoutLinear.setVisibility(View.VISIBLE);
            for (int x = 0; x < itemList.size(); x++) {
                price += itemList.get(x).totalPrice();
            }
            holder.totalPrice.setText(String.valueOf(price) + " " + context.getString(R.string.currency));
        }

        holder.recycler_items.setNestedScrollingEnabled(false);
        holder.recycler_items.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
//        holder.recycler_items.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(10, context)));
        holder.recycler_items.setAdapter(itemsAdapter);

        holder.txt_name_store.setText(cartModel.Store().name());
        //holder.txt_total_price.setText(""+cartModel.TotalPrice());

        if (cartModel.Store().ImageResponse().data() != null)
            PicassoUtils.setImage(Common.BASE_URL_IMAGE + cartModel.Store().ImageResponse().data().name(), holder.image_store);

        //Log.d("HAZEM" , "FULL : " +Common.BASE_URL_IMAGE + cartModel.Store().imageId());


        if (callBackUpdateCartItemsListener != null) {
            itemsAdapter.setUpdateCartItemsListener((itemId, amount) -> callBackUpdateCartItemsListener.callBack(itemId, amount));
        }

        if (isCart) holder.img_delete_store_products.setOnClickListener(v -> {
            if (removeAllStoreItemsListener != null) {
                removeAllStoreItemsListener.removeAllStoreProducts(myCartList.get(position).Items(), position, myCartList.get(position).TotalPrice(), myCartList);
            }
        });
        if (isCart) {

            if (cartModel.Store().isAvailable()) {
                holder.img_lock.setVisibility(View.GONE);
            } else holder.img_lock.setVisibility(View.VISIBLE);


            holder.layout_store_cart.setOnClickListener(view -> {

                MyCartQuery.Store store = cartModel.Store();
                Common.IS_AVAILABLE_STORE = store.isAvailable();


                if (store.isAvailable()) {
                    Intent intentStore = new Intent(context, StoreDetailActivity.class);
                    intentStore.putExtra(Constants.EXTRA_STORE_ID, store._id());
                    intentStore.putExtra(Constants.EXTRA_ADMIN_USER_ID, store.adminUserId());
                    intentStore.putExtra(Constants.EXTRA_STORE_NAME, store.name());
                    if (store.ImageResponse().data() != null)
                        intentStore.putExtra(Constants.EXTRA_STORE_IMAGE, store.ImageResponse().data().name());
                    else
                        intentStore.putExtra(Constants.EXTRA_STORE_IMAGE, "null");


                    context.startActivity(intentStore);
                } else {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    View layout_alert = LayoutInflater.from(context).inflate(R.layout.layout_dialog_alert, null);

                    Button btn_ok = layout_alert.findViewById(R.id.btn_ok);
                    Button btn_continue = layout_alert.findViewById(R.id.btn_continue);


                    alertDialog.setView(layout_alert);
                    AlertDialog dialog = alertDialog.create();

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btn_continue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intentStore = new Intent(context, StoreDetailActivity.class);
                            intentStore.putExtra(Constants.EXTRA_STORE_ID, store._id());
                            intentStore.putExtra(Constants.EXTRA_ADMIN_USER_ID, store.adminUserId());
                            intentStore.putExtra(Constants.EXTRA_STORE_NAME, store.name());
                            if (store.ImageResponse().data() != null)
                                intentStore.putExtra(Constants.EXTRA_STORE_IMAGE, store.ImageResponse().data().name());
                            else
                                intentStore.putExtra(Constants.EXTRA_STORE_IMAGE, "null");

                            dialog.dismiss();

                            context.startActivity(intentStore);

                        }
                    });

                    dialog.show();
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return myCartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recycler_items)
        RecyclerView recycler_items;
        @BindView(R.id.image_store)
        ImageView image_store;
        ImageView img_lock;
        @BindView(R.id.txt_name_store)
        TextView txt_name_store;

//        @BindView(R.id.txt_total_price)
//        TextView txt_total_price;

        ConstraintLayout layout_store_cart;
        ImageView img_delete_store_products;

        LinearLayoutCompat checkoutLinear;
        TextView totalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            if (isCart) {
                {
                    layout_store_cart = itemView.findViewById(R.id.layout_store_cart);
                    img_delete_store_products = itemView.findViewById(R.id.img_delete_store_products);
                    img_lock = itemView.findViewById(R.id.img_lock);
                }
            } else {
                checkoutLinear = itemView.findViewById(R.id.checkout_linear);
                totalPrice = itemView.findViewById(R.id.total_checkout);
            }
        }
    }

    public interface CallBackUpdateCartItemsListener {
        void callBack(String itemId, int amount);
    }

    public interface RemoveAllStoreItemsListener {
        void removeAllStoreProducts(List<MyCartQuery.Item> storeItems, int position, Double totalPrice, List<MyCartQuery.StoreDatum> cartList);
    }

    RemoveAllStoreItemsListener removeAllStoreItemsListener;

    public void setRemoveAllStoreItemsListener(RemoveAllStoreItemsListener removeAllStoreItemsListener) {
        this.removeAllStoreItemsListener = removeAllStoreItemsListener;
    }
    //------------------- OnClickListener ---------------

//    View.OnClickListener onClickListenerOk = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//    };
//
//    View.OnClickListener onClickListenerContinue = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//    };

}
