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
import com.corptia.bringero.utils.Utils;

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
        this.productsList = new ArrayList<>(productsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GetStoreProductsQuery.Product product = productsList.get(position);
        holder.txt_price.setText(new StringBuilder().append(product.storePrice()).append(" ").append(context.getString(R.string.currency)));
        holder.txt_name_product.setText(Utils.cutName(product.Product().name()));

        if (product.Product().ImageResponse().data() != null)
            PicassoUtils.setImage(Common.BASE_URL_IMAGE + product.Product().ImageResponse().data().name(), holder.image_product);
        else
            PicassoUtils.setImage(holder.image_product);

        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(holder.itemView , position);

                    holder.txt_amount.setVisibility(View.VISIBLE);
                    holder.btn_delete.setVisibility(View.VISIBLE);

                    int count ;
                    count = Integer.parseInt(holder.txt_amount.getText().toString())+1;
                    holder.txt_amount.setText(""+count);


                    if (count != 1)
                        holder.txt_amount.animate().scaleX(1).scaleY(1).setDuration(100).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                holder.txt_amount.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100);
                            }
                        });
                }
            });
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

        @BindView(R.id.txt_amount)
        TextView txt_amount;
        @BindView(R.id.btn_delete)
        TextView btn_delete;
        @BindView(R.id.txt_discount)
        TextView txt_discount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }

    public GetStoreProductsQuery.Product getSelectProduct(int position) {
        return productsList.get(position);
    }

    public void removeSelectProduct(int position) {
        productsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, productsList.size());

    }


}
