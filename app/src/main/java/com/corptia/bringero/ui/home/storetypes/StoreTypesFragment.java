package com.corptia.bringero.ui.home.storetypes;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.databinding.FragmentStoreTypesBinding;
import com.corptia.bringero.graphql.NotificationCountUnreadQuery;
import com.corptia.bringero.graphql.StoreTypesQuery;
import com.corptia.bringero.type.NotificationFilterInput;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.ui.home.HomeModefiedActivity;
import com.corptia.bringero.ui.home.storetypes.Adapter.HomeCategoriesAdapter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationPresenter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationView;
import com.corptia.bringero.ui.storesDetail.single_store.adapters.CategoryAdapter;
import com.corptia.bringero.utils.recyclerview.decoration.GridSpacingItemDecoration;
import com.corptia.bringero.ui.home.storetypes.Adapter.StoreTypesAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


public class StoreTypesFragment extends Fragment implements StoreTypesContract.StoreTypesView, SelectDeliveryLocationView {
    FragmentStoreTypesBinding binding;
    Boolean isOffer;

    //For Select Location
    BottomSheetDialog bottomSheetDialog;
    SelectDeliveryLocationPresenter presenter = new SelectDeliveryLocationPresenter(this);


    //private StoreTypesViewModel storeTypesViewModel;
    //Data
    HomeCategoriesAdapter adapter;
    StoreTypesPresenter storeTypesPresenter;

    LayoutAnimationController layoutAnimationController;

    Handler handler = new Handler();

    public StoreTypesFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // storeTypesViewModel = ViewModelProviders.of(this).get(StoreTypesViewModel.class);
        binding = FragmentStoreTypesBinding.inflate(inflater, container, false);
        binding.homeHeader.menuButton.setOnClickListener(v -> {
            HomeModefiedActivity.showDrawer();
        });
        View view = binding.getRoot();
        setCurrentLocation();

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        if (getArguments() != null) {
            isOffer = getArguments().getBoolean(Constants.EXTRA_STORE_OFFER, false);
        }
        storeTypesPresenter = new StoreTypesPresenter(this);
        storeTypesPresenter.getStoreTypes(getString(R.string.special_offers));
        binding.homeHeader.addressLinear.setOnClickListener(view1 -> {
            bottomSheetDialog = Common.showDialogSelectLocation(requireActivity(), bottomSheetDialog, presenter);
        });
        binding.seeAllCategories.setOnClickListener(view1 -> {
            NavDirections action = StoreTypesFragmentDirections.actionStoreTypesFragmentToCategoriesFragment();
            Navigation.findNavController(view1).navigate(action);
        });
        return view;
    }

    @Override
    public void setStoreTypes(List<StoreTypesQuery.StoreCategory> repositoryList) {
        binding.categoriesRecycler.setLayoutAnimation(layoutAnimationController);
        binding.categoriesRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false));
//        recycler_store.addItemDecoration(new GridSpacingItemDecoration(
//                2,
//                Common.dpToPx(15, getActivity()),
//                true,
//                0,
//                Common.dpToPx(10, getActivity()),
//                Common.dpToPx(2, getActivity()),
//                Common.dpToPx(2, getActivity())));
        adapter = new HomeCategoriesAdapter(getActivity(), repositoryList);
        binding.categoriesRecycler.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayError(String errorMessage) {

        Toasty.error(getActivity(), errorMessage).show();
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

        handler.post(() -> {

            binding.loading.resumeAnimation();
            binding.loading.setVisibility(View.GONE);
        });

    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void setCurrentLocation() {

//        Log.d("HAZEM" , "Hello Again " + Common.CURRENT_USER.getCurrentDeliveryAddress().getName());

        if (Common.CURRENT_USER != null) {
            if (Common.CURRENT_USER.getCurrentDeliveryAddress() != null) {

                String region = Common.CURRENT_USER.getCurrentDeliveryAddress().getRegion();
                String name = Common.CURRENT_USER.getCurrentDeliveryAddress().getName();

                if (name != null && region != null)
                    binding.homeHeader.addressTitle.setText(new StringBuilder().append(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())
                            .append(" (")
                            .append(region.substring(0, 1).toUpperCase() + region.substring(1).toLowerCase())
                            .append(")"));

            } else
                binding.homeHeader.addressTitle.setText(getString(R.string.select_location));
        }
    }

    private void countNotificationUnread() {

        NotificationFilterInput filter = NotificationFilterInput.builder().status("Unread").build();
        MyApolloClient.getApollowClientAuthorization().query(NotificationCountUnreadQuery.builder().filter(filter).build())
                .enqueue(new ApolloCall.Callback<NotificationCountUnreadQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<NotificationCountUnreadQuery.Data> response) {

                        NotificationCountUnreadQuery.@Nullable GetAll data = response.data().NotificationCountUnreadQuery().getAll();
                        if (data.status() == 200) {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Update Number

                                    int count = data.pagination().totalDocs();
                                    if (count == 0) {
//                                        binding.homeHeader.setVisibility(GONE);
                                    } else {
//                                        notificationBadge.setVisibility(VISIBLE);
//                                        txt_notificationsBadge.setText("" + (count > 99 ? "99+" : count));
                                    }

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }

    @Override
    public void onSuccessUpdateCurrentLocation() {
        requireActivity().runOnUiThread(() -> {
            bottomSheetDialog.dismiss();
            setCurrentLocation();
        });
    }

    @Override
    public void onSuccessUpdateNestedLocation() {

    }

    @Override
    public void onSuccessRemovedLocation() {

    }
}