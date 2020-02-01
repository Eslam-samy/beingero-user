package com.corptia.bringero.utils.button;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.corptia.bringero.R;


public class ProgressButton {

    private CardView cardView;
    private ConstraintLayout layout;
    private ProgressBar progressBar;
    private TextView textView;

    Animation fade_in,fade_out;
    Context context;


    public ProgressButton(Context context, View view) {

        fade_in = AnimationUtils.loadAnimation(context , R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(context , R.anim.fade_out);

        cardView = view.findViewById(R.id.cardView);
        layout = view.findViewById(R.id.root_layout);
        progressBar = view.findViewById(R.id.progress_Loading);
        textView = view.findViewById(R.id.txt);

        this.context = context;
    }

    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setAnimation(fade_in);
        textView.setText(context.getString(R.string.please_wait));
        textView.setAnimation(fade_in);

        disable();
    }

    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
        progressBar.setAnimation(fade_out);
        textView.setText(context.getString(R.string.login));
        textView.setAnimation(fade_out);

        enable();
    }

    private void disable(){
        layout.setEnabled(false);
    }

    private void enable(){
        layout.setEnabled(true);
    }

//    public void buttonFinished(){
//        layout.setBackgroundColor(cardView.getResources().getColor(R.color.green));
//        progressBar.setVisibility(View.GONE);
//        textView.setText("Done");
//    }

}
