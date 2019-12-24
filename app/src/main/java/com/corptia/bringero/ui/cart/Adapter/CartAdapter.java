package com.corptia.bringero.ui.cart.Adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.IClickRecyclerAdapter;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.RemoveCartItemMutation;
import com.corptia.bringero.model.EventBus.CalculatePriceEvent;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.MyCartQuery;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
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
    Handler handler ;

    CallBackUpdateCartItemsListener callBackUpdateCartItemsListener;

    CustomLoading loading;

    public void setCallBackUpdateCartItemsListener(CallBackUpdateCartItemsListener callBackUpdateCartItemsListener) {
        this.callBackUpdateCartItemsListener = callBackUpdateCartItemsListener;
    }

    public CartAdapter(Context context, List<MyCartQuery.StoreDatum> cartModels, boolean isCart) {
        this.context = context;
        this.myCartList  = new ArrayList<>(cartModels);
        this.isCart = isCart;
        handler = new Handler();

        loading = new CustomLoading(context, true);
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

                if (itemList.size() == 0)
                {

                    myCartList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, myCartList.size());
                }
            }
        });

        itemsAdapter.setiDeleteCartItemsListener(new CartItemsAdapter.IDeleteCartItemsListener() {
            @Override
            public void onDeleteCart(int positionItems , int amount) {

                loading.showProgressBar(context, false);



                MyApolloClient.getApollowClientAuthorization().mutate(RemoveCartItemMutation.builder()._id(itemList.get(position)._id()).build())
                        .enqueue(new ApolloCall.Callback<RemoveCartItemMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<RemoveCartItemMutation.Data> response) {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {


                                        if (response.data().CartItemMutation().remove().status() ==200)
                                        {

                                            Toast.makeText(context, "Items Deleted !", Toast.LENGTH_SHORT).show();

                                            double totalProductPrice = amount * itemList.get(position).PricingProduct().storePrice();
                                            EventBus.getDefault().postSticky(new CalculatePriceEvent(totalProductPrice));

                                            itemList.remove(positionItems);

                                            if (itemList.size() == 0)
                                            {

                                                myCartList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, myCartList.size());
                                            }

                                        }
                                        else
                                        {

                                        }

                                        loading.hideProgressBar();



                                    }
                                });

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {

                            }
                        });

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
