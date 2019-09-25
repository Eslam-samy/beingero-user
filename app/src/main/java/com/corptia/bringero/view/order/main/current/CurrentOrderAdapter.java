package com.corptia.bringero.view.order.main.current;

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

public class CurrentOrderAdapter  extends RecyclerView.Adapter<CurrentOrderAdapter.ViewHolder>{

    Context context;
    List<CartItems> storeTypesList  = new ArrayList<>();

    public CurrentOrderAdapter(Context context, List<CartItems> storeTypesList) {
        this.context = context;
        this.storeTypesList = storeTypesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_order_current , parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentOrderAdapter.ViewHolder holder, int position) {

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
