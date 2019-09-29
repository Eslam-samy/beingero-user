package com.corptia.bringero.view.cart.Adapter;

import android.content.Context;
import android.media.Image;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Interface.IOnImageViewAdapterClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.EditText.InputFilterMinMax;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.view.cart.CartPresenter;

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

    public void setUpdateCartItemsListener(UpdateCartItemsListener updateCartItemsListener) {
        this.updateCartItemsListener = updateCartItemsListener;
    }

    public CartItemsAdapter(Context context, @Nullable List<MyCartQuery.Item> cartItems, boolean isCart) {
        this.context = context;
        this.cartItems = cartItems;
        this.isCart = isCart;
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
        holder.txt_name_product.setText(item.PricingProduct().Product().name());

        if (isCart) {
            holder.txt_quantity.setText("" + item.amount());

            //Event
            holder.setListener((view, position1, isDecrease, isDelete) -> {

                if (!isDelete) {
                    //If not Button delete food From Cart Click

                    int amount = Integer.parseInt(holder.txt_quantity.getText().toString());
                    if (isDecrease) //if Decrease quantity
                    {
                        if (amount > 1) {
                            holder.txt_quantity.setText("" + (amount - 1));
                        }
                    } else {
                        if (amount < item.PricingProduct().amount()) {
                            holder.txt_quantity.setText("" + (amount + 1));
                        }
                    }

                    if (updateCartItemsListener != null) {
                        updateCartItemsListener.onUpdateCart(item._id(), Integer.parseInt(holder.txt_quantity.getText().toString()));
                    }

                } else {

                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();

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
                chb_select_item = itemView.findViewById(R.id.chb_select_item);

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
}
