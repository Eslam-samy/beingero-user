package com.corptia.bringero.ui.order.main.lastOrder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.R;
import com.corptia.bringero.model.CartItems;

import java.util.ArrayList;
import java.util.List;

public class LastOrderAdapter extends RecyclerView.Adapter<LastOrderAdapter.ViewHolder>{

    Context context;
    List<CartItems> storeTypesList  = new ArrayList<>();

    public LastOrderAdapter(Context context, List<CartItems> storeTypesList) {
        this.context = context;
        this.storeTypesList = storeTypesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_order_last , parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull LastOrderAdapter.ViewHolder holder, int position) {

        //holder.itemView.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        //holder.itemView.getLayoutParams().height = Common.dpToPx(160, context);

    }

    @Override
    public int getItemCount() {
        return storeTypesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
