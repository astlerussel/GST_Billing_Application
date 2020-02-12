package app.hks.billy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            String error = e.getMessage();
                            Toast.makeText(DetailsActivity.this, "Error"+error , Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });


    }
}
