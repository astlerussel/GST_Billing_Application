package app.hks.billy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static maes.tech.intentanim.CustomIntent.customType;

import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    private EditText myCompanyName;
    private Button nxtButton;



    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        myCompanyName = (EditText) findViewById(R.id.detail_company_name_edit_text);
        nxtButton = (Button) findViewById(R.id.details_next_button);

        firebaseFirestore = FirebaseFirestore.getInstance();


        nxtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String companyName = myCompanyName.getText().toString();
                final String userId = FirebaseAuth.getInstance().getUid();


                if(companyName.isEmpty())
                {
                    myCompanyName.setError("Mandatory Field...");
                    myCompanyName.requestFocus();
                }
                if(!companyName.isEmpty())
                {

                    DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(userId);

                    docRef.update("company_name", companyName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(DetailsActivity.this,"Data Added succesfully", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(DetailsActivity.this, NameActivity.class);
                                    startActivity(intent);
                                    //customType(DetailsActivity.this,"bottom-to-up");
                                    overridePendingTransition(R.anim.bottom_to_up, R.anim.up_to_bottom);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            String error = e.getMessage();
                            Toast.makeText(DetailsActivity.this, "Error"+error , Toast.LENGTH_LONG).show();

                        }
                    });

                    DocumentReference docNewColRef = FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(userId)
                            .collection("self_info")
                            .document(userId);

                            docNewColRef.update("company_name", companyName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //overridePendingTransition(R.anim.up_to_bottom, R.anim.bottom_to_up);

        customType(DetailsActivity.this,"up-to-bottom");


        /*
         *left-to-right
         *right-to-left
         *bottom-to-up
         *up-to-bottom
         *fadein-to-fadeout
         *rotateout-to-rotatein
         */

    }

    @Override
    public void finish() {
        super.finish();

        //overridePendingTransition(R.anim.up_to_bottom, R.anim.bottom_to_up);

        customType(DetailsActivity.this,"up-to-bottom");
    }

    @Override
    protected void onStart() {
        super.onStart();

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                myCompanyName.setText(documentSnapshot.getString("company_name"));
            }
        });
    }
}
