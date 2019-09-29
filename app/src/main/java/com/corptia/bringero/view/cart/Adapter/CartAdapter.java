package com.corptia.bringero.view.cart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.MyCartQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<MyCartQuery.StoreDatum> myCartList = new ArrayList<>();
    public CartItemsAdapter itemsAdapter;
    boolean isCart;

    CallBackUpdateCartItemsListener callBackUpdateCartItemsListener;

    public void setCallBackUpdateCartItemsListener(CallBackUpdateCartItemsListener callBackUpdateCartItemsListener) {
        this.callBackUpdateCartItemsListener = callBackUpdateCartItemsListener;
    }

    public CartAdapter(Context context, List<MyCartQuery.StoreDatum> cartModels , boolean isCart) {
        this.context = context;
        this.myCartList = cartModels;
        this.isCart = isCart;
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
        itemsAdapter = new CartItemsAdapter(context, cartModel.Items() ,isCart);

        holder.recycler_items.setNestedScrollingEnabled(false);
        holder.recycler_items.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        holder.recycler_items.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, context)));
        holder.recycler_items.setAdapter(itemsAdapter);

        holder.txt_name_store.setText(cartModel.Store().name());

        if (callBackUpdateCartItemsListener != null) {


            itemsAdapter.setUpdateCartItemsListener(new CartItemsAdapter.UpdateCartItemsListener() {
                @Override
                public void onUpdateCart(String itemId, int amount) {
                    callBackUpdateCartItemsListener.callBack(itemId, amount);
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
        @BindView(R.id.txt_name_store)
        TextView txt_name_store;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }
    }

    public interface CallBackUpdateCartItemsListener {
        void callBack(String itemId, int amount);
    }
}
