package com.corptia.bringero.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.R;
import com.corptia.bringero.utils.stickyheader.stickyView.StickHeaderRecyclerView;

public class NewCartAdapter extends StickHeaderRecyclerView<MyData, HeaderDataImpl> {


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HeaderDataImpl.HEADER_TYPE_1:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_header, parent, false));
            /*case 2:
                return new Header2ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_header, parent, false));*/
            default:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_shop, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bindData(position);
        } else if (holder instanceof HeaderViewHolder){
            ((HeaderViewHolder) holder).bindData(position);
        } /*else if (holder instanceof Header2ViewHolder){
            ((Header2ViewHolder) holder).bindData(position);
        }*/
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {

        TextView tv = header.findViewById(R.id.txt_name_brands);
        tv.setText("Data " + headerPosition);

    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        HeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.txt_name_brands);
        }

        void bindData(int position) {
            tvHeader.setText("Header " + position);
        }
    }

    class Header2ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        Header2ViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.txt_name_brands);
        }

        void bindData(int position) {
            tvHeader.setText(String.valueOf(position / 5));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRows;

        ViewHolder(View itemView) {
            super(itemView);
            tvRows = itemView.findViewById(R.id.txt_name_product);
        }

        void bindData(int position) {
            //getDataInPosition(position).getFelan();
            tvRows.setText("Hazem" + position);
            //((ViewGroup) tvRows.getParent()).setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }
}
