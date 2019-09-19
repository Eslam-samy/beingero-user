package com.corptia.bringero.view.storesDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.graphql.GetProductQuery;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreDetailAdapter extends RecyclerView.Adapter<StoreDetailAdapter.ViewHolder> {

    Context context;
    List<GetProductQuery.Product> productsList = new ArrayList<>();

    IOnRecyclerViewClickListener listener;

    public void setListener(IOnRecyclerViewClickListener listener) {
        this.listener = listener;
    }

    public StoreDetailAdapter(Context context, List<GetProductQuery.Product> productsList) {
        this.context = context;
        this.productsList =   new ArrayList<>(productsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_product , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GetProductQuery.Product product = productsList.get(position);
        holder.txt_price.setText(""+product.customerPrice());
        holder.txt_name_product.setText(product.name());

        if (listener !=null)
        {
            holder.itemView.setOnClickListener(view -> listener.onClick(holder.itemView , position));
        }
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_product)
        ImageView image_product;
        @BindView(R.id.txt_name_product)
        TextView txt_name_product;
        @BindView(R.id.txt_price)
        TextView txt_price;
        @BindView(R.id.ratingBar)
        RatingBar ratingBar;
        @BindView(R.id.txt_count_available)
        TextView txt_count_available;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);

        }

    }

    public GetProductQuery.Product getSelectProduct(int position)
    {
        return productsList.get(position);
    }

    public void removeSelectProduct(int position)
    {
        productsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, productsList.size());

    }


}
