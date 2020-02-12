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

public class NameActivity extends AppCompatActivity {

    private EditText eOwnerName;
    private Button bNextButton;
    private Button bBackButton;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        eOwnerName = (EditText) findViewById(R.id.name_activity_owner_name_edit_text);
        bNextButton = (Button) findViewById(R.id.name_activity_next_button);
        bBackButton = (Button) findViewById(R.id.name_activity_back_button);


        firebaseFirestore = FirebaseFirestore.getInstance();


        bNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String ownerName = eOwnerName.getText().toString();
                final String userId = FirebaseAuth.getInstance().getUid();

                if(ownerName.isEmpty())
                {
                    eOwnerName.setError("Mandatory Field...");
                    eOwnerName.requestFocus();
                }
                if(!ownerName.isEmpty())
                {

                    DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(userId);

                    docRef.update("owners_name", ownerName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(NameActivity.this,"Data Added succesfully", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(NameActivity.this, AddresActivity.class);
                                    startActivity(intent);
                                    customType(NameActivity.this,"bottom-to-up");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            String error = e.getMessage();
                            Toast.makeText(NameActivity.this, "Error"+error , Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });


        bBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NameActivity.this, DetailsActivity.class);
                startActivity(intent);
                customType(NameActivity.this,"up-to-bottom");
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

                eOwnerName.setText(documentSnapshot.getString("owners_name"));
            }
        });
    }
}
