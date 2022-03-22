package com.corptia.bringero.ui.home.setting.language;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.UpdateUserInfoMutation;
import com.corptia.bringero.model.Language;
import com.corptia.bringero.type.UserInfo;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.language.LocaleHelper;


import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

import static android.content.Context.MODE_PRIVATE;

public class LanguageFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.btn_arabic)
    Button btn_arabic;
    @BindView(R.id.btn_english)
    Button btn_english;
    @BindView(R.id.btn_save)
    Button btn_save;

    Language language = new Language(); // when Select Language store here

    AlertDialog dialog ;
    Handler handler = new Handler();

    public LanguageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_language, container, false);

        ButterKnife.bind(this, view);

        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();

        if (Common.CURRENT_USER.getLanguage().equalsIgnoreCase("ar"))
            setCheckOnArabic();
        else setCheckOnEnglish();


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(language);
            }
        });


        btn_arabic.setOnClickListener(this);
        btn_english.setOnClickListener(this);

        return view;
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

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (response.data().UserMutation().updateInfo().status() == 200) {

                                            Common.CURRENT_USER.setToken(response.data().UserMutation().updateInfo().token());
                                            Common.CURRENT_USER.setLanguage(response.data().UserMutation().updateInfo().data().language());
//                                    updateViews(mlanguage.getId());
                                            language = null;
                                            dialog.dismiss();

                                            LocaleHelper.setLocale(getActivity(),  Common.CURRENT_USER.getLanguage().toLowerCase());

                                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                        } else {
                                            dialog.dismiss();
                                        }
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



    private void updateViews(String languageCode) {

        Context context = LocaleHelper.setLocale(getActivity(), languageCode);

        //Save
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", languageCode);
        editor.apply();
        Resources resources = context.getResources();

        getActivity().recreate();

//        edt_email.setHint(resources.getString(R.string.email));
//        edt_first.setHint(resources.getString(R.string.first_name));
//        edt_last.setHint(resources.getString(R.string.last_name));
//        edt_phone.setHint(resources.getString(R.string.phone));
//        txt_hint_spinner_language.setText(resources.getString(R.string.language));
        /*

        mTitleTextView.setText(resources.getString(R.string.main_activity_title));
        mDescTextView.setText(resources.getString(R.string.main_activity_desc));
        mAboutTextView.setText(resources.getString(R.string.main_activity_about));
        mToTRButton.setText(resources.getString(R.string.main_activity_to_tr_button));
        mToENButton.setText(resources.getString(R.string.main_activity_to_en_button));

        setTitle(resources.getString(R.string.main_activity_toolbar_title));*/
    }

    private  void setCheckOnArabic(){
        btn_arabic.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_check_primary, 0);
        btn_english.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
        language.setId("ar");
        language.setName("Arabic");

    }


    private  void setCheckOnEnglish(){
        btn_english.setCompoundDrawablesWithIntrinsicBounds(0, 0,  R.drawable.ic_check_primary, 0);
        btn_arabic.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        language.setId("en");
        language.setName("English");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.btn_arabic:
                setCheckOnArabic();
                break;

            case R.id.btn_english:
                setCheckOnEnglish();
                break;
        }

    }
}
