package com.corptia.bringero.view.Setting.main;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.corptia.bringero.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment  implements View.OnClickListener {


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
        ButterKnife.bind(this,view);

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

        switch (view.getId())
        {
            case R.id.btn_edit_profile:
                navController.navigate(R.id.action_settingFragment2_to_editProfileFragment);
                break;
            case R.id.btn_address:
                // navController.navigate(R.id.action_settingFragment2_to_editProfileFragment);
                Toasty.warning(getActivity() , "Change Address").show();
                break;
            case R.id.btn_change_password:
                navController.navigate(R.id.action_settingFragment2_to_changePasswordFragment);
                break;
            case R.id.btn_contact_us:
                navController.navigate(R.id.action_settingFragment2_to_contactUsFragment);
                break;
            case R.id.btn_language:
                navController.navigate(R.id.action_settingFragment2_to_languageFragment);
                break;
            case R.id.btn_log_out:
                Toasty.warning(getActivity() , "LogOut").show();
                break;

                default:
                    //Toast.makeText(getContext(), "Null", Toast.LENGTH_SHORT).show();
                    break;

        }

    }
}
