package com.corptia.bringero.view.brandDetail;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.decoration.GridSpacingItemDecoration;
import com.corptia.bringero.model.StoreTypes;
import com.corptia.bringero.view.home.ui.storetypes.Adapter.StoreTypesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrandDetailFragment extends Fragment {

    @BindView(R.id.recycler_brands_detail)
    RecyclerView recycler_brands_detail;
    BrandDetailAdapter adapter ;
    List<StoreTypes> storeTypesList = new ArrayList<>();

    public BrandDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brand_detail, container, false);
        ButterKnife.bind(this , view);

        recycler_brands_detail.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_brands_detail.addItemDecoration(new GridSpacingItemDecoration(2, Common.dpToPx(15, getActivity()), true, 0, Common.dpToPx(10, getActivity())));


        storeTypesList.add(new StoreTypes(R.drawable.img1, "data1"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data2"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data3"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data4"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data5"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data6"));
        storeTypesList.add(new StoreTypes(R.drawable.img1, "data7"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data8"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data9"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data10"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data11"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data12"));
        storeTypesList.add(new StoreTypes(R.drawable.img1, "data13"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data14"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data15"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data16"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data17"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data18"));

        adapter = new BrandDetailAdapter(getActivity(), storeTypesList);
        recycler_brands_detail.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

}
