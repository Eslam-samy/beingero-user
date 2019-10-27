package com.corptia.bringero.ui.storesDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.graphql.GetStoreProductsQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreDetailAdapter extends RecyclerView.Adapter<StoreDetailAdapter.ViewHolder> {

    Context context;
    List<GetStoreProductsQuery.Product> productsList = new ArrayList<>();

    IOnRecyclerViewClickListener listener;

    public void setListener(IOnRecyclerViewClickListener listener) {
        this.listener = listener;
    }

    public StoreDetailAdapter(Context context, List<GetStoreProductsQuery.Product> productsList) {
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

        GetStoreProductsQuery.Product product = productsList.get(position);
        holder.txt_price.setText(""+product.storePrice());
        holder.txt_name_product.setText(product.Product().name());

        if (product.Product().ImageResponse().data()!=null)
            PicassoUtils.setImage(Common.BASE_URL_IMAGE + product.Product().ImageResponse().data().name() , holder.image_product);
        else
            PicassoUtils.setImage( holder.image_product);

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

    public GetStoreProductsQuery.Product getSelectProduct(int position)
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
