package com.corptia.bringero.ui.Main.comingSoon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.Main.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.iwgang.countdownview.CountdownView;

public class ComingSoonActivity extends AppCompatActivity {

    CountdownView mCvCountdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.FullWindow);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        setContentView(R.layout.activity_coming_soon);

        mCvCountdownView = findViewById(R.id.countdownView);



//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("timestamp");
//        Map<String, Object> value = new HashMap<>();
//        value.put("timestamp", ServerValue.TIMESTAMP);
//        ref.setValue(value);
//
//
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                Map map = (Map) dataSnapshot.getValue();
//                Common.LOG("timestamp : " + map.get("timestamp"));
//
//                long currentTimeFromFirebase =(long) map.get("timestamp");
//
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String dateString = formatter.format(new Date(currentTimeFromFirebase));
//                Common.LOG("HAZEM" + dateString);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        if (getIntent()!=null)
        {

            mCvCountdownView.start(getIntent().getLongExtra("millisecondsFromNow" ,0)); // Millisecond

        }

//For LinearGradient
//        TextView txt_lab_soon =  findViewById(R.id.txt_lab_soon);
//        txt_lab_soon.setText(txt_lab_soon.getText().toString());
//
//        TextPaint paint = txt_lab_soon.getPaint();
//        float width = paint.measureText(txt_lab_soon.getText().toString());
//
//
//        Shader textShader = new LinearGradient(0, 0, width, txt_lab_soon.getTextSize(),
//                new int[]{
//                        getResources().getColor(R.color.colorAccent),
//                        getResources().getColor(R.color.colorPrimary)
//                }, null, Shader.TileMode.CLAMP);
//        txt_lab_soon.getPaint().setShader(textShader);



//// or
//        for (int time=0; time<1000; time++) {
//            mCvCountdownView.updateShow(time);
//        }


        mCvCountdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {

                Intent intent = new Intent(ComingSoonActivity.this , MainActivity.class);
                startActivity(intent);
                overridePendingTransition( R.anim.fade_in, R.anim.fade_out );
                finish();

            }
        });


    }

    public java.util.Map<String, String> getCreationDate() {
        return ServerValue.TIMESTAMP;
    }

}
