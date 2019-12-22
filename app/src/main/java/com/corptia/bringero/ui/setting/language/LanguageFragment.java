package com.corptia.bringero.ui.setting.language;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.CallbackListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.UpdateUserInfoMutation;
import com.corptia.bringero.model.Language;
import com.corptia.bringero.type.UserInfo;
import com.corptia.bringero.utils.language.LocaleHelper;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

import static android.content.Context.MODE_PRIVATE;

public class LanguageFragment extends Fragment {

    @BindView(R.id.spinner_language)
    Spinner spinner_language;
    @BindView(R.id.btn_save)
    Button btn_save;
    ArrayList<Language> languageList;

    Language language ; // when Select Language store here

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
        fillSpinnerLanguage();
        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();

        if (Common.CURRENT_USER.getLanguage().equalsIgnoreCase("ar"))
            spinner_language.setSelection(0);
        else spinner_language.setSelection(1);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateLanguage(language);

            }
        });

        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                language = new Language();
                language = (Language) parent.getSelectedItem();
                // Log.d("onItemSelected", "" + language.getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    private void updateLanguage(Language mlanguage) {


        if (mlanguage!=null)
        {
            dialog.show();
            UserInfo info = UserInfo.builder().language(mlanguage.getId()).build();
            MyApolloClient.getApollowClientAuthorization().mutate(UpdateUserInfoMutation.builder().data(info).build())
                    .enqueue(new ApolloCall.Callback<UpdateUserInfoMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<UpdateUserInfoMutation.Data> response) {

                            if (response.data().UserMutation().updateInfo().status() == 200)
                            {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        updateViews(mlanguage.getId());
                                        language = null;
                                        dialog.dismiss();

                                        Common.CURRENT_USER.setLanguage(response.data().UserMutation().updateInfo().data().language());

                                    }
                                });

                            }
                            else
                            {

                                handler.post(() -> dialog.dismiss());
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {

                        }
                    });
        }

    }

    // For Lang

    private void fillSpinnerLanguage() {

        languageList = new ArrayList<>();
        languageList.add(new Language("ar", "العربيه"));
        languageList.add(new Language("en", "English"));

        //fill data in spinner
        ArrayAdapter<Language> adapter = new ArrayAdapter<Language>(getActivity(), android.R.layout.simple_spinner_dropdown_item, languageList);
        spinner_language.setAdapter(adapter);

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

}
