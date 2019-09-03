package com.corptia.bringero.view.Main.otp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class VerifyPhoneNumberActivity extends AppCompatActivity {

    @BindView(R.id.pinView)
    PinView pinView ;
    @BindView(R.id.btn_verify)
    Button btn_verify;
    @BindView(R.id.txt_lab_message)
    TextView txt_lab_message;

    AlertDialog dialog;

    FirebaseAuth auth;
    String verification_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        ButterKnife.bind(this);
        txt_lab_message.setText(txt_lab_message.getText() +"\n+2" + Common.CURRENT_NUMBER);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("ar");

        sent();

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();

                verified();
            }
        });
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d("onVerificationCompleted", phoneAuthCredential.getProvider());

        }
        @Override
        public void onVerificationFailed(FirebaseException e) {

            Log.d("onVerificationFailed", e.getMessage());
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verification_code = s;
            Log.d("onCodeSent", "Code sent to the number");
        }
    };


    public void sent() {
        String number = Common.CURRENT_NUMBER;
       /* PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,60, TimeUnit.SECONDS,this,mCallback
        );*/

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2"+number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallback);        // OnVerificationStateChangedCallbacks

    }


    public void signInWithPhone(PhoneAuthCredential credential)
    {
        auth.signInWithCredential(credential)
                .addOnCompleteListener((OnCompleteListener) task -> {

                    dialog.dismiss();

                    if(task.isSuccessful())
                    {Toast.makeText(VerifyPhoneNumberActivity.this,"User signed in successfully",Toast.LENGTH_SHORT).show();
                    }
                    else if(task.getException() instanceof FirebaseAuthUserCollisionException)
                        Toast.makeText(VerifyPhoneNumberActivity.this,"User already exist",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(VerifyPhoneNumberActivity.this,"Wrong code. please try again",Toast.LENGTH_SHORT).show();
                });
    }

    public void verified() {
        if (!pinView.getText().toString().equalsIgnoreCase("") || !pinView.getText().toString().isEmpty()) {
            String input_code = pinView.getText().toString();
            if (verification_code != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_code, input_code);
                signInWithPhone(credential);
            }
        }
        else
            dialog.dismiss();
    }
}
