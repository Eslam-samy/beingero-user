package com.corptia.bringero.ui.home.setting.language;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.databinding.ActivityLanguageBinding;
import com.corptia.bringero.graphql.UpdateUserInfoMutation;
import com.corptia.bringero.model.Language;
import com.corptia.bringero.type.UserInfo;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.language.LocaleHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class LanguageActivity extends BaseActivity {

    ActivityLanguageBinding binding;
    List<String> languages;
    ArrayAdapter<Object> adapterStatus;
    AlertDialog dialog;
    Handler handler = new Handler();
    Language language = new Language(); // when Select Language store here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        spinnerAdapter();
        languages = new ArrayList<>();
        binding.languageSelect.setAdapter(adapterStatus);
        languages.add(getString(R.string.arabic));
        languages.add(getString(R.string.english));
        adapterStatus.clear();
        adapterStatus.add(getString(R.string.language));
        adapterStatus.addAll(languages);
        adapterStatus.notifyDataSetChanged();
        binding.languageSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Payment.DataBeanX.DataBean payment = (Payment.DataBeanX.DataBean) parent.getSelectedItem();
                TextView text = (TextView) parent.getChildAt(0);
                if (position > 0)
                    text.setTextColor(Color.BLACK);
                if (parent.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.arabic)))
                    setCheckOnArabic();
                else setCheckOnEnglish();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.btnSave.setOnClickListener(view -> {
            updateLanguage(language);
        });
    }

    private void setCheckOnArabic() {
        Log.d("ESLAM", "setCheckOnArabic:AR checked ");
        language.setId("ar");
        language.setName("Arabic");
    }

    private void setCheckOnEnglish() {
        Log.d("ESLAM", "setCheckOnArabic:EN checked ");
        language.setId("en");
        language.setName("English");
    }

    private void spinnerAdapter() {
        adapterStatus = new ArrayAdapter<Object>(this, R.layout.spinner_text, new ArrayList<>()) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public View getDropDownView(int position, @androidx.annotation.Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(getColor(R.color.colorGray));
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapterStatus.setDropDownViewResource(R.layout.spinner_list);

    }

    private void updateLanguage(Language mlanguage) {
        if (mlanguage != null) {
            if (!mlanguage.getName().equalsIgnoreCase(Common.CURRENT_USER.getLanguage())) {
                dialog.show();
                UserInfo info = UserInfo.builder().language(mlanguage.getId()).build();
                MyApolloClient.getApollowClientAuthorization().mutate(UpdateUserInfoMutation.builder().data(info).build())
                        .enqueue(new ApolloCall.Callback<UpdateUserInfoMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<UpdateUserInfoMutation.Data> response) {

                                handler.post(() -> {

                                    if (response.data().UserMutation().updateInfo().status() == 200) {

                                        Common.CURRENT_USER.setToken(response.data().UserMutation().updateInfo().token());
                                        Common.CURRENT_USER.setLanguage(response.data().UserMutation().updateInfo().data().language());
//                                    updateViews(mlanguage.getId());
                                        language = null;
                                        dialog.dismiss();


                                        LocaleHelper.setLocale(LanguageActivity.this, Common.CURRENT_USER.getLanguage().toLowerCase());
                                        Intent intent = new Intent(LanguageActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    } else {
                                        dialog.dismiss();
                                    }
                                });

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {

                            }
                        });
            }

        }
    }
}
