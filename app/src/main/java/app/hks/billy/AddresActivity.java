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

public class AddresActivity extends AppCompatActivity {

    private EditText eAddress;
    private Button bNextButton;
    private Button bBcackButton;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addres);

        eAddress = (EditText) findViewById(R.id.addres_company_address_edit_text);
        bNextButton = (Button) findViewById(R.id.addres_activity_next_button);
        bBcackButton = (Button) findViewById(R.id.addres_activity_back_button);

        firebaseFirestore = FirebaseFirestore.getInstance();

        bNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String companyAddress = eAddress.getText().toString();
                final String userId = FirebaseAuth.getInstance().getUid();

                if(companyAddress.isEmpty())
                {
                    eAddress.setError("Mandatory Field...");
                    eAddress.requestFocus();
                }
                if(!companyAddress.isEmpty())
                {

                    DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(userId);

                    docRef.update("company_address", companyAddress)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(AddresActivity.this,"Data Added succesfully", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(AddresActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    customType(AddresActivity.this,"bottom-to-up");


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            String error = e.getMessage();
                            Toast.makeText(AddresActivity.this, "Error"+error , Toast.LENGTH_LONG).show();

                        }
                    });
                }


            }
        });

        bBcackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddresActivity.this, NameActivity.class);
                startActivity(intent);
                customType(AddresActivity.this,"up-to-bottom");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                eAddress.setText(documentSnapshot.getString("company_address"));
            }
        });
    }
}
