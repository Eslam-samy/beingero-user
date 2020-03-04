package com.corptia.bringero.ui.setting.contactUs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.GeneralOptionAllQuery;
import com.corptia.bringero.type.GeneralOptionNameEnum;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.CustomLoading;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ContactUsActivity extends BaseActivity implements ContactusView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.input_title)
    TextInputLayout input_title;
    @BindView(R.id.input_message)
    TextInputLayout input_message;
    @BindView(R.id.btn_send)
    Button btn_send;
    @BindView(R.id.txt_phone_number)
    TextView txt_phone_number;
    @BindView(R.id.circle_bg)
    CircleImageView circle_bg;

    //btn Social
    @BindView(R.id.btn_call)
    ImageView btn_call;
    @BindView(R.id.btn_facebook)
    ImageView btn_facebook;
    @BindView(R.id.btn_twitter)
    ImageView btn_twitter;
    @BindView(R.id.btn_instagram)
    ImageView btn_instagram;
    @BindView(R.id.btn_whatsapp)
    ImageView btn_whatsapp;
    @BindView(R.id.btn_youtube)
    ImageView btn_youtube;


    ContactusPresenter presenter = new ContactusPresenter(this);

    CustomLoading loading;

    //From Option
    String facebook = "";
    String instagram = "";
    String twitter = "";
    String phone = "";
    String whatsapp = "";
    String youtube = "";
    String googleForm = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        loading = new CustomLoading(this, true);

        ButterKnife.bind(this);

        initActionBar();

        presenter.getAllOption();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String title = input_title.getEditText().getText().toString();
                String message = input_message.getEditText().getText().toString();

                if (title.trim().isEmpty()) {
                    Toasty.info(ContactUsActivity.this, getString(R.string.title_field_is_required))
                            .show();
                    return;
                } else if (message.trim().isEmpty()) {
                    Toasty.info(ContactUsActivity.this, getString(R.string.message_field_is_required))
                            .show();
                    return;
                }


                presenter.sendMessage(title, message);


            }
        });


        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)));
                } catch (Exception e) {

                    //Toasty.error(MessagesSupportActivity.this , e.getMessage());
                }
            }
        });

        btn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url;
                if (whatsapp.startsWith("+2")) {
                    url = "https://api.whatsapp.com/send?phone=" + whatsapp;
                } else {
                    url = "https://api.whatsapp.com/send?phone=+2" + whatsapp;

                }
                try {
                    PackageManager pm = getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")));
                }

            }
        });

        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(ContactUsActivity.this, facebook);

                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);

            }
        });

        btn_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagram));
                startActivity(intent);
            }
        });

        btn_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtube));
                startActivity(intent);
            }
        });

        btn_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter));
                startActivity(intent);
            }
        });

    }


    private void initActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setTitle("");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void showProgressBar() {

        loading.showProgressBar(ContactUsActivity.this, false);
    }

    @Override
    public void hideProgressBar() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                loading.hideProgressBar();

            }
        });


    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toasty.success(ContactUsActivity.this, getString(R.string.the_message_sent_successfully)).show();
                Intent intent = new Intent(ContactUsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


    }

    @Override
    public void setSocialMedia(List<GeneralOptionAllQuery.Data1> socialList) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                for (GeneralOptionAllQuery.Data1 social : socialList) {

                    if (social.name().rawValue().equalsIgnoreCase(GeneralOptionNameEnum.FACEBOOK.rawValue())) {

                        facebook = social.value();
                        if (!facebook.isEmpty())
                            btn_facebook.setVisibility(View.VISIBLE);

                    } else if (social.name().rawValue().equalsIgnoreCase(GeneralOptionNameEnum.TWITTER.rawValue())) {

                        twitter = social.value();
                        if (!twitter.isEmpty())
                        btn_twitter.setVisibility(View.VISIBLE);

                    }
                    else if (social.name().rawValue().equalsIgnoreCase(GeneralOptionNameEnum.INSTAGRAM.rawValue())) {

                        instagram = social.value();
                        if (!instagram.isEmpty()) {
                            btn_instagram.setVisibility(View.VISIBLE);
                        }

                    }

                    else if (social.name().rawValue().equalsIgnoreCase(GeneralOptionNameEnum.YOUTUBE.rawValue())) {

                        youtube = social.value();
                        if (!youtube.isEmpty())
                        {
                        btn_youtube.setVisibility(View.VISIBLE);}

                    }
                    else if (social.name().rawValue().equalsIgnoreCase(GeneralOptionNameEnum.PHONE.rawValue())) {

                        phone = social.value();
                        if (!phone.isEmpty())
                        {
                            btn_call.setVisibility(View.VISIBLE);
                            circle_bg.setVisibility(View.VISIBLE);
                            txt_phone_number.setVisibility(View.VISIBLE);

                            txt_phone_number.setText("" + phone);
                        }


                    }
                    else if (social.name().rawValue().equalsIgnoreCase(GeneralOptionNameEnum.WHATSAPP.rawValue())) {

                        whatsapp = social.value();
                        if (!whatsapp.isEmpty())
                        btn_whatsapp.setVisibility(View.VISIBLE);

                    }

                }


            }
        });


    }


    public static String getFacebookPageURL(Context context, String uri) {
        String originalUri = uri;

        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app

                return "fb://facewebmodal/f?href=" + originalUri;
            } else { //older versions of fb app
                return "fb://page/" + uri;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return originalUri; //normal web url
        }
    }
}
