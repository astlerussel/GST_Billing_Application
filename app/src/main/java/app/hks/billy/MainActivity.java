package app.hks.billy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.BatchUpdateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private Button newBillButton;

    private CardView selfInfoCardView;
    private CardView countCardView;
    private FirebaseFirestore firebaseFirestore;


    private TextView tCompanyName, tOwnerName, tCompanyAddress, tOwnerPhoneNUmber;
    private Button tEditButoon;

    private FirebaseAuth mAuth;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = findViewById(R.id.main_app_name_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Billy");

        firebaseFirestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        countCardView = (CardView) findViewById(R.id.count_cardview);
        newBillButton = (Button) findViewById(R.id.create_new_bill_button);


        newBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createTotalMrpFieldInTheBill();

                Intent intent = new Intent(MainActivity.this, BillingActivity.class);
                startActivity(intent);
            }
        });

    }



    private void createTotalMrpFieldInTheBill()
    {
        DocumentReference doRef = firebaseFirestore.collection("users").document(userId);

        doRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                String strCompanyName = documentSnapshot.getString("company_name");
                String strOwnerName = documentSnapshot.getString("owners_name");
                String strInvoCharCount = documentSnapshot.getString("invoice_char_count");
                String strInvoiceNumber =documentSnapshot.getString("invoice_number");

                Integer intInvoiceNumber = Integer.parseInt(strInvoiceNumber);
                Character charCompanyName = strCompanyName.toUpperCase().charAt(0);
                Character charOwnerName = strOwnerName.toUpperCase().charAt(0);
                String finalInvoiceNumber;


                intInvoiceNumber = intInvoiceNumber+1;

                if(intInvoiceNumber > 999999999)
                {
                    char charInvoiceCharCount = strInvoCharCount.toUpperCase().charAt(0);
                    int intCount = charInvoiceCharCount+1;
                    charInvoiceCharCount = (char) intCount;

                    intInvoiceNumber = 1;
                    strInvoiceNumber = String.valueOf(100000000+intInvoiceNumber);

                    finalInvoiceNumber = charInvoiceCharCount+charCompanyName+charOwnerName+(strInvoiceNumber);


                }
                else
                {
                    char charInvoiceCharCount = strInvoCharCount.toUpperCase().charAt(0);
                    strInvoiceNumber = String.valueOf(100000000+intInvoiceNumber);

                    finalInvoiceNumber = strInvoCharCount+charCompanyName+charOwnerName+strInvoiceNumber;

                }


                Map<String, String> totalMrpMao  =new HashMap<>();
                totalMrpMao.put("total_items_mrp", "0");
                totalMrpMao.put("total_items_count","0");


                DocumentReference documentReference = firebaseFirestore.collection("users/"+userId+"/bills").document(finalInvoiceNumber);

                documentReference.set(totalMrpMao)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //Toast.makeText(MainActivity.this, "DataADDED",Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String message = e.getMessage();

                        Toast.makeText(MainActivity.this, "Error: "+message,Toast.LENGTH_LONG).show();

                    }
                });


            }
        });


    }



    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }else {
            String uid = mAuth.getCurrentUser().getUid();
            final DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {
                        String company_name = documentSnapshot.getString("company_name");
                        String owners_name = documentSnapshot.getString("owners_name");
                        String company_address = documentSnapshot.getString("company_address");
                        String company_mob = documentSnapshot.getString("company_phone_number");
                        //String nationality2 = documentSnapshot.getString("Nationality");
                        //||age1=="" || gender2=="" || nationality2==""|| dob2==""

                        if (company_name == "" || owners_name =="" || company_address == "" || company_mob == "") {
                            Intent intent1 = new Intent(MainActivity.this, DetailsActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent1);
                        }
                    } else {
                        mAuth.signOut();
                        Intent intent1 = new Intent(MainActivity.this, AuthActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                    }

                }
            });

            //ADDING VALUES TO SELF INFORMATION CARD VIEW
            selfInfoCardView = (CardView) findViewById(R.id.main_company_details_card_view);
            tCompanyName = (TextView) selfInfoCardView.findViewById(R.id.cardview_company_name);
            tOwnerName = (TextView) selfInfoCardView.findViewById(R.id.cardview_owner_name);
            tCompanyAddress = (TextView) selfInfoCardView.findViewById(R.id.cardview_company_address);
            tOwnerPhoneNUmber = (TextView) selfInfoCardView.findViewById(R.id.cardview_company_phone_number);
            tEditButoon = (Button) selfInfoCardView.findViewById(R.id.cardview_edit_card_button);

            DocumentReference docRef = firebaseFirestore.collection("users").document(userId);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    tCompanyName.setText(documentSnapshot.getString("company_name"));
                    tOwnerName.setText(documentSnapshot.getString("owners_name"));
                    tCompanyAddress.setText(documentSnapshot.getString("company_address"));
                    tOwnerPhoneNUmber.setText(documentSnapshot.getString("company_phone_number"));

                }
            });

            tEditButoon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
                    startActivity(intent);
                }
            });

        }





    }
}


