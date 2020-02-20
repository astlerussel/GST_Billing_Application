package app.hks.billy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

import static maes.tech.intentanim.CustomIntent.customType;

public class CheckoutActivity extends AppCompatActivity {

    private Toolbar mToolBar;

    private CardView checkOutCardView;

    private TextView invoiceNumber, gstinNumber, totalItemsCount, totalItemsMrp,stateGSTAmount, centralGSTAmount, totalItemsSubTotalAmount;

    private Button nextToPaymentActivtyButton;

    private String strInvoiceNumber,strInvioceChar, intInvoNumber;

    private String strTotalItemsMrp;
    private double intTotalItemsMrp, intTotalItemsSubTotalAmount;
    private double intStateGSTAmount,  intTotalCentralGstAmount;
    private String strTotalItemsSubTotalAmount;

    private FirebaseFirestore firebaseFirestore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        strInvoiceNumber = getIntent().getStringExtra("invoice_complete_number");
        strInvioceChar = getIntent().getStringExtra("invoice_character");
        intInvoNumber = getIntent().getStringExtra("invoice_number");

        //Toast.makeText(CheckoutActivity.this, "Value"+intInvoNumber,Toast.LENGTH_LONG).show();


        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        mToolBar = findViewById(R.id.checkout_activity_tool_bar);
        setSupportActionBar(mToolBar);

        checkOutCardView = (CardView) findViewById(R.id.checkout_card_view);

        nextToPaymentActivtyButton = (Button) findViewById(R.id.next_button_to_payment_activity);
        invoiceNumber = (TextView) checkOutCardView.findViewById(R.id.checkout_activity_invoice_number);
        gstinNumber = (TextView) checkOutCardView.findViewById(R.id.checkout_activity_gstin_number);
        totalItemsCount = (TextView) checkOutCardView.findViewById(R.id.checkout_activity_total_number_of_items);
        totalItemsMrp = (TextView) checkOutCardView.findViewById(R.id.checkout_activity_total_mrp);
        stateGSTAmount = (TextView) checkOutCardView.findViewById(R.id.checkout_activity_state_gst_amount);
        centralGSTAmount = (TextView) checkOutCardView.findViewById(R.id.checkout_activity_central_gst_amount);
        totalItemsSubTotalAmount = (TextView) checkOutCardView.findViewById(R.id.checkout_activity_sub_total_amount);


        invoiceNumber.setText(strInvoiceNumber);

        //updateTheInvoNumberAndCharacter();

        fetchAndDisplayCheckoutCardViewDetails();


        nextToPaymentActivtyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
                intent.putExtra("invoice_complete_number", strInvoiceNumber);
                intent.putExtra("invoice_character", strInvioceChar);
                intent.putExtra("invoice_number", intInvoNumber);
                startActivity(intent);
                customType(CheckoutActivity.this,"bottom-to-up");
            }
        });


    }

    private void fetchAndDisplayCheckoutCardViewDetails()
    {

        firebaseFirestore.collection("users")
                .document(userId)
                .collection("bills")
                .document(strInvoiceNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if(documentSnapshot.exists())
                            {
                                totalItemsCount.setText(documentSnapshot.getString("total_items_count"));
                                totalItemsMrp.setText(documentSnapshot.getString("total_items_mrp"));

                                strTotalItemsMrp = totalItemsMrp.getText().toString();
                                intTotalItemsMrp = Integer.parseInt(strTotalItemsMrp);

                                intStateGSTAmount = (0.025*intTotalItemsMrp);
                                intStateGSTAmount = Double.parseDouble(new DecimalFormat("##.####").format(intStateGSTAmount));
                                stateGSTAmount.setText(String.valueOf(intStateGSTAmount));

                                intTotalCentralGstAmount = (0.025*intTotalItemsMrp);
                                intTotalCentralGstAmount = Double.parseDouble(new DecimalFormat("##.####").format(intTotalCentralGstAmount));
                                centralGSTAmount.setText(String.valueOf(intTotalCentralGstAmount));

                                intTotalItemsMrp = intTotalItemsMrp + intStateGSTAmount + intTotalCentralGstAmount;
                                intTotalItemsMrp =Double.parseDouble(new DecimalFormat("##.####").format(intTotalItemsMrp));

                                strTotalItemsSubTotalAmount = String.valueOf(intTotalItemsMrp);

                                totalItemsSubTotalAmount.setText(strTotalItemsSubTotalAmount);

                                firebaseFirestore.collection("users")
                                        .document(userId)
                                        .collection("bills")
                                        .document(strInvoiceNumber)
                                        .update("sub_total_amount", strTotalItemsSubTotalAmount);



                            }
                            else
                            {

                            }


                        }
                        else
                        {

                        }

                    }
                });
    }

    /*private void  updateTheInvoNumberAndCharacter()
    {
         firebaseFirestore.collection("users")
                .document(userId)
                .update("invoice_char_count", strInvioceChar
                        ,"invoice_number", intInvoNumber)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

         firebaseFirestore.collection("users")
                 .document(userId)
                 .collection("self_info")
                 .document(userId)
                 .update("invoice_char_count", strInvioceChar
                         ,"invoice_number", intInvoNumber)
                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {

                     }
                 }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {

             }
         });

    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        customType(CheckoutActivity.this,"up-to-bottom");
    }
}
