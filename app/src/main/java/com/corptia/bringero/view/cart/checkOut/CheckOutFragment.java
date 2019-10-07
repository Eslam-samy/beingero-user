package com.corptia.bringero.view.cart.checkOut;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.view.cart.Adapter.CartAdapter;
import com.corptia.bringero.view.home.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckOutFragment extends Fragment implements CheckOutView{

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    private CartAdapter cartAdapter;

    @BindView(R.id.btn_confirm)
    Button btn_confirm;

    CheckOutPresenter checkOutPresenter;
    Handler handler = new Handler();

    AlertDialog alertDialog ;

   // OnBackPressedCallback

    public CheckOutFragment() {
        // Required empty public constructor
        checkOutPresenter = new CheckOutPresenter(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_out, container, false);

        ButterKnife.bind(this, view);

        alertDialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();

        cartAdapter = new CartAdapter(getActivity(), Common.CURRENT_CART , false);
        recycler_cart.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_cart.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, getActivity())));
        recycler_cart.setAdapter(cartAdapter);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HomeActivity.navController.popBackStack();
                HomeActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                HomeActivity.fab.show();



                //checkOutPresenter.sendOrder();

               // HomeActivity.navController.navigate(R.id.action_checkOutFragment_to_nav_cart);
                //requireActivity().finish();


                //دول ذي بعض في الفعل
                //getActivity().onBackPressed();
                //NavHostFragment.findNavController(CheckOutFragment.this).popBackStack();





            }
        });

        return view;
    }

    @Override
    public void showProgressBar() {
        alertDialog.show();
    }

    @Override
    public void hideProgressBar() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog.hide();
            }
        });

    }

    @Override
    public void showErrorMessage(String Message) {


        handler.post(new Runnable() {
            @Override
            public void run() {
                Toasty.success(getActivity() , Message).show();
            }
        });



    }

    @Override
    public void onSuccessMessage(String message) {

    }


}
