package com.corptia.bringero.ui.storesDetail.single_store.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Interface.IClickRecyclerAdapter;
import com.corptia.bringero.R;
import com.corptia.bringero.databinding.ItemCustomCategoryBinding;
import com.corptia.bringero.graphql.ProductsTypeQuery;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.HomeViewHolder> {

    Context context;
    public int selectedPosition;
    List<ProductsTypeQuery.Data1> categories;
    IClickRecyclerAdapter clickListener;

    public void setClickListener(IClickRecyclerAdapter clickListener) {
        this.clickListener = clickListener;
    }

    public CategoryAdapter(Context context, List<ProductsTypeQuery.Data1> categories) {
        this.categories = categories;
        this.context = context;
        selectedPosition = 0;

    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomCategoryBinding binding = ItemCustomCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        //Conventional writing
        //View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.one_layout,parent,false);
        return new HomeViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (categories.get(position)._id().isEmpty()) {
            holder.binding.subCategoryText.setText(R.string.offers);
        } else {
            holder.binding.subCategoryText.setText(String.valueOf(categories.get(position).name()));
        }
        if (selectedPosition == position) {
            holder.binding.parentConstraint.setBackgroundColor(context.getColor(R.color.main_color_with_opacity));
            holder.binding.subCategoryText.setTextColor(context.getColor(R.color.colorPrimary));

        } else {

            holder.binding.parentConstraint.setBackgroundColor(context.getColor(R.color.main_gray_with_opacity));
            holder.binding.subCategoryText.setTextColor(context.getColor(R.color.black));

        }

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onClickAdapter(position);
            }
            selectedPosition = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        {
            return categories != null ? categories.size() : 0;
        }
    }

    public void setMeals(List<ProductsTypeQuery.Data1> mealsList) {
        this.categories = mealsList;
        notifyDataSetChanged();
    }

    public ProductsTypeQuery.Data1 getItem(int position) {
        return categories.get(position);
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder {
        ItemCustomCategoryBinding binding;

        public HomeViewHolder(@NonNull ItemCustomCategoryBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}

