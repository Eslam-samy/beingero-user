package com.corptia.bringero.ui.setting.main;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.Main.login.LoginActivity;
import com.corptia.bringero.ui.home.setting.contactUs.ContactUsActivity;
import com.corptia.bringero.ui.home.setting.main.SettingView;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.language.LocaleHelper;
import com.corptia.bringero.utils.sharedPref.PrefKeys;
import com.corptia.bringero.utils.sharedPref.PrefUtils;
import com.corptia.bringero.ui.location.AllLocation.LocationsDeliveryActivity;
import com.corptia.bringero.ui.home.setting.main.SettingPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment implements View.OnClickListener, SettingView {


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

    //For Logout
    SettingPresenter presenter = new SettingPresenter(this);
    CustomLoading loading;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);

        loading = new CustomLoading(getActivity(), true);

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        btn_edit_profile.setOnClickListener(this);
        btn_address.setOnClickListener(this);
        btn_change_password.setOnClickListener(this);
        btn_contact_us.setOnClickListener(this);
        btn_language.setOnClickListener(this);
        btn_log_out.setOnClickListener(this);

        if (Common.CURRENT_USER != null) {


            if (Common.CURRENT_USER.getLanguage().equalsIgnoreCase("ar")) {

                Drawable img = getContext().getResources().getDrawable(R.drawable.ic_arrow_rtl);
                img.setBounds(0, 0, Common.dpToPx(8, getActivity()), Common.dpToPx(13, getActivity()));
                btn_edit_profile.setCompoundDrawables(img, null, null, null);
                btn_address.setCompoundDrawables(img, null, null, null);
                btn_change_password.setCompoundDrawables(img, null, null, null);
                btn_contact_us.setCompoundDrawables(img, null, null, null);
                btn_language.setCompoundDrawables(img, null, null, null);
                btn_log_out.setCompoundDrawables(img, null, null, null);

            }

        }

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
                startActivity(new Intent(getActivity(), LocationsDeliveryActivity.class));
                //Toasty.warning(getActivity(), "Change Address").show();
                break;
            case R.id.btn_change_password:
                navController.navigate(R.id.action_settingFragment2_to_changePasswordFragment);
                //HomeActivity.navController.navigate(R.id.action_nav_settings_to_changePasswordFragment2);

                break;
            case R.id.btn_contact_us:
                startActivity(new Intent(getActivity(), ContactUsActivity.class));
//                navController.navigate(R.id.action_settingFragment2_to_contactUsFragment);
                //HomeActivity.navController.navigate(R.id.action_nav_settings_to_contactUsFragment2);
                break;
            case R.id.btn_language:
                navController.navigate(R.id.action_settingFragment2_to_languageFragment);
                //HomeActivity.navController.navigate(R.id.action_nav_settings_to_languageFragment2);
                break;
            case R.id.btn_log_out:

                String token = (String) PrefUtils.getFromPrefs(getActivity(), PrefKeys.USER_TOKEN_FIREBASE, "");

                presenter.logOut(token);

                break;

            default:
                //Toast.makeText(getContext(), "Null", Toast.LENGTH_SHORT).show();
                break;

        }


    }

    Handler handler = new Handler();

    @Override
    public void OnSuccessLogOut() {

        handler.post(() -> {

            //Toasty.warning(getActivity() , "LogOut").show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PrefUtils.saveToPrefs(getActivity(), PrefKeys.USER_LOGIN, false);
            //getActivity().finishAffinity();
            LocaleHelper.setLocale(getActivity(), "en");
            startActivity(intent);

        });

    }

    @Override
    public void OnFailedLogOut() {

    }

    @Override
    public void showProgressBar() {

        loading.showProgressBar(getActivity(), false);
    }

    @Override
    public void hideProgressBar() {

        handler.post(() -> loading.hideProgressBar());
    }

    @Override
    public void showErrorMessage(String Message) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                loading.hideProgressBar();

            }
        });
    }

    @Override
    public void onSuccessMessage(String message) {

    }
}
