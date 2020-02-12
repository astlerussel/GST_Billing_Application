package app.hks.billy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {

    private String verificationId;
    private FirebaseAuth mAuth;
    private EditText editText;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        editText = findViewById(R.id.entr_verif_code_field);

        String phonenumber = getIntent().getStringExtra("phonenumber");
        sendVerificationCode(phonenumber);


        findViewById(R.id.verif_code_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editText.getText().toString().trim();
                if((code.length() < 6) || code.isEmpty()){

                    editText.setError("Enter Code...");
                    editText.requestFocus();
                    return;
                }
                else {
                    verifyCode(code);
                }
            }
        });

        findViewById(R.id.change_number_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void verifyCode(String code) {
        if(code == null){
            String input_code = editText.getText().toString().trim();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, input_code);
            signInWithCredential(credential);
        }
        else{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String phonenumber = getIntent().getStringExtra("phonenumber");
                            //int num = Integer.parseInt(phonenumber);

                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

                            Map<String, String> userMap = new HashMap<>();

                            userMap.put("user_phone_number", phone);
                            userMap.put("uid", uid);

                            firebaseFirestore.collection("users")
                                    .document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Intent intent = new Intent(VerifyActivity.this, DetailsActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                            firebaseFirestore.collection("users")
                                    .document(uid)
                                    .collection("self_info")
                                    .document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        } else {
                            Toast.makeText(VerifyActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editText.setText(code);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }
    };
}
