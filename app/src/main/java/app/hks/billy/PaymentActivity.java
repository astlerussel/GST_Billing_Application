package app.hks.billy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentActivity extends AppCompatActivity {

    String strInvoiceNumber, strInvioceChar, intInvoNumber;

    private  CardView paymentDitailsCard;

    private TextView tInvoiceNumber, tSubTotal, tChange;
    private EditText eAmountReceived;

    private FirebaseFirestore firebaseFirestore;
    String userId, strSubTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        strInvoiceNumber = getIntent().getStringExtra("invoice_complete_number");
        strInvioceChar = getIntent().getStringExtra("invoice_character");
        intInvoNumber = getIntent().getStringExtra("invoice_number");

        userId = FirebaseAuth.getInstance().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        paymentDitailsCard = (CardView) findViewById(R.id.payment_card_view);
        tInvoiceNumber = (TextView) paymentDitailsCard.findViewById(R.id.payment_activity_invoice_number);
        tSubTotal = (TextView) paymentDitailsCard.findViewById(R.id.payment_activity_sub_total_amount);
        eAmountReceived = (EditText) paymentDitailsCard.findViewById(R.id.payment_activity_amount_received);
        tChange = (TextView) paymentDitailsCard.findViewById(R.id.payment_activity_change_amount);

        tInvoiceNumber.setText(strInvoiceNumber);

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
                            DocumentSnapshot document = task.getResult();
                            if(document.exists())
                            {
                                tSubTotal.setText(document.getString("sub_total_amount"));
                                strSubTotal = document.getString("sub_total_amount");
                            }
                        }
                        else
                        {

                        }
                    }
                });

        eAmountReceived.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //eAmountReceived.setText("- -");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length()>=11)
                {
                    int maxLength = 11;
                    eAmountReceived.setError("Max Length Reached!!");
                   eAmountReceived.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                    eAmountReceived.requestFocus();
                }




            }

            @Override
            public void afterTextChanged(Editable s) {

                double doubleSubTotalAmount = Double.parseDouble(strSubTotal);




                if(s.length()!=0) {
                    String strAmountReceived = s.toString();
                    long intAmountReceived = Long.parseLong(strAmountReceived);

                    if (intAmountReceived < doubleSubTotalAmount) {
                        tChange.setText("- -");
                    } else if (intAmountReceived == doubleSubTotalAmount) {
                        tChange.setText("0");
                    } else if (intAmountReceived > doubleSubTotalAmount) {
                        double doubleChange = intAmountReceived - doubleSubTotalAmount;
                        String strChange = String.valueOf(doubleChange);

                        tChange.setText(strChange);

                    }
                }
                else {

                    eAmountReceived.getText().clear();
                    tChange.setText("- -");
                }



            }
        });

    }




}
