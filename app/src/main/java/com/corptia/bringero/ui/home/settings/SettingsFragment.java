package com.corptia.bringero.ui.home.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.databinding.FragmentSettingsBinding;
import com.corptia.bringero.model.UserModel;
import com.corptia.bringero.ui.Main.login.LoginActivity;
import com.corptia.bringero.ui.home.setting.changePassword.ChangePasswordActivity;
import com.corptia.bringero.ui.home.setting.contactUs.ContactUsActivity;
import com.corptia.bringero.ui.home.setting.editProfile.EditProfileActivity;
import com.corptia.bringero.ui.home.setting.language.LanguageActivity;
import com.corptia.bringero.ui.home.setting.main.SettingPresenter;
import com.corptia.bringero.ui.home.setting.main.SettingView;
import com.corptia.bringero.ui.location.AllLocation.LocationsDeliveryActivity;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.language.LocaleHelper;
import com.corptia.bringero.utils.sharedPref.PrefKeys;
import com.corptia.bringero.utils.sharedPref.PrefUtils;
import com.squareup.picasso.Picasso;


public class SettingsFragment extends Fragment implements View.OnClickListener, SettingView {
    UserModel userData = Common.CURRENT_USER;
    FragmentSettingsBinding binding;
    Handler handler = new Handler();


    SettingPresenter presenter = new SettingPresenter(this);
    CustomLoading loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        loading = new CustomLoading(getActivity(), true);
        setUserData();
        initMainClicks();
        return binding.getRoot();
    }

    private void setUserData() {
        binding.name.setText(userData.getFirstName() + " " + userData.getLastName());
        binding.phoneNumber.setText(userData.getPhone());
        binding.phoneNumber.setText(userData.getPhone());
        if (userData.getAvatarName() != null)
            Picasso.get().load(Common.BASE_URL_IMAGE + userData.getAvatarName())
                    .placeholder(R.drawable.ic_placeholder_profile)
                    .into(binding.imgAvatar);
    }

    private void initMainClicks() {
        binding.myProfile.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), EditProfileActivity.class));

        });
        binding.password.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ChangePasswordActivity.class));
        });
        binding.addresses.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), LocationsDeliveryActivity.class));
        });

        binding.language.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), LanguageActivity.class));
        });
        binding.contactUs.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ContactUsActivity.class));

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myProfile:
                startActivity(new Intent(requireActivity(), EditProfileActivity.class));
                break;
            case R.id.addresses:
                startActivity(new Intent(requireActivity(), LocationsDeliveryActivity.class));
                break;
            case R.id.password:
                startActivity(new Intent(requireActivity(), ChangePasswordActivity.class));
                break;
            case R.id.contactUs:
                startActivity(new Intent(requireActivity(), ContactUsActivity.class));
                break;
            case R.id.language:
                startActivity(new Intent(getActivity(), LanguageActivity.class));
                break;
            case R.id.logout:
                String token = (String) PrefUtils.getFromPrefs(getActivity(), PrefKeys.USER_TOKEN_FIREBASE, "");
                presenter.logOut(token);
                break;

        }
    }

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

    }

    @Override
    public void onSuccessMessage(String message) {

    }
}