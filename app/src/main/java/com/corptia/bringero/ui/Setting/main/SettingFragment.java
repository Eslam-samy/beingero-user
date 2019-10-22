package com.corptia.bringero.ui.Setting.main;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.corptia.bringero.R;
import com.corptia.bringero.ui.Main.MainActivity;
import com.corptia.bringero.ui.location.AllLocation.LocationsDeliveryActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {


    NavController navController;

    @BindView(R.id.btn_edit_profile)
    Button btn_edit_profile;
    @BindView(R.id.btn_address)
    Button btn_address;
    @BindView(R.id.btn_change_password)
    Button btn_change_password;
    @BindView(R.id.btn_contact_us)
    Button btn_contact_us;
    @BindView(R.id.btn_language)
    Button btn_language;
    @BindView(R.id.btn_log_out)
    Button btn_log_out;


    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        btn_edit_profile.setOnClickListener(this);
        btn_address.setOnClickListener(this);
        btn_change_password.setOnClickListener(this);
        btn_contact_us.setOnClickListener(this);
        btn_language.setOnClickListener(this);
        btn_log_out.setOnClickListener(this);

        return view;


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_edit_profile:
                navController.navigate(R.id.action_settingFragment2_to_editProfileFragment);
                //HomeActivity.navController.navigate(R.id.action_nav_settings_to_editProfileFragment2);
                break;
            case R.id.btn_address:
                 startActivity(new Intent(getActivity() , LocationsDeliveryActivity.class));
                //Toasty.warning(getActivity(), "Change Address").show();
                break;
            case R.id.btn_change_password:
                navController.navigate(R.id.action_settingFragment2_to_changePasswordFragment);
                //HomeActivity.navController.navigate(R.id.action_nav_settings_to_changePasswordFragment2);

                break;
            case R.id.btn_contact_us:
                navController.navigate(R.id.action_settingFragment2_to_contactUsFragment);
                //HomeActivity.navController.navigate(R.id.action_nav_settings_to_contactUsFragment2);
                break;
            case R.id.btn_language:
                navController.navigate(R.id.action_settingFragment2_to_languageFragment);
                //HomeActivity.navController.navigate(R.id.action_nav_settings_to_languageFragment2);
                break;
            case R.id.btn_log_out:
                //Toasty.warning(getActivity() , "LogOut").show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //getActivity().finishAffinity();
                startActivity(intent);
                break;

            default:
                //Toast.makeText(getContext(), "Null", Toast.LENGTH_SHORT).show();
                break;

        }


    }
}
