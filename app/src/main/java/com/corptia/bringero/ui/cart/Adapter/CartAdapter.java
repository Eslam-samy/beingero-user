package com.corptia.bringero.ui.cart.Adapter;

import android.content.Context;
import android.util.Log;
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
import com.corptia.bringero.Utils.PicassoUtils;
import com.corptia.bringero.Utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.MyCartQuery;

import org.jetbrains.annotations.Nullable;

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

    public CartAdapter(Context context, List<MyCartQuery.StoreDatum> cartModels, boolean isCart) {
        this.context = context;
        this.myCartList  = new ArrayList<>(cartModels);
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
        @Nullable List<MyCartQuery.Item> itemList  =new ArrayList<>(cartModel.Items()) ;
        itemsAdapter = new CartItemsAdapter(context, itemList, isCart, new IClickRecyclerAdapter() {
            @Override
            public void onClickAdapter(int positionItems) {
                itemList.remove(positionItems);

                Log.d("HAZEM" , "DELETE FROM List " + position + " DATA " + cartModel.Store().name());

                if (itemList.size() == 0)
                {

                    myCartList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, myCartList.size());
                }
            }
        });

        holder.recycler_items.setNestedScrollingEnabled(false);
        holder.recycler_items.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        holder.recycler_items.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, context)));
        holder.recycler_items.setAdapter(itemsAdapter);

        holder.txt_name_store.setText(cartModel.Store().name());
        //holder.txt_total_price.setText(""+cartModel.TotalPrice());

        if (cartModel.Store().ImageResponse().data()!=null)
        PicassoUtils.setImage(Common.BASE_URL_IMAGE + cartModel.Store().ImageResponse().data().name(), holder.image_store);
        else
            PicassoUtils.setImage( holder.image_store);

        //Log.d("HAZEM" , "FULL : " +Common.BASE_URL_IMAGE + cartModel.Store().imageId());


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
//        @BindView(R.id.txt_total_price)
//        TextView txt_total_price;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }
    }

    public interface CallBackUpdateCartItemsListener {
        void callBack(String itemId, int amount);
    }


}
