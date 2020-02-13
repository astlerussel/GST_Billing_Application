package app.hks.billy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BillingActivity extends AppCompatActivity {

    private Button addNewItemButton;
    private Button cancelItenDialogButton, addItemDialogButton;
    private EditText barcodeRadio1, barcodeRadio2, itemNameRadio2, itemWeightRadio2, itemCostRadio2;

    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2;

    private Dialog addNewItemDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        //userId = firebaseAuth.getCurrentUser().getUid();

        addNewItemButton = (Button) findViewById(R.id.billing_add_item_button);
        radioButton1 = (RadioButton) findViewById(R.id.enter_barcode_dialog_radio_button);
        radioButton2 = (RadioButton) findViewById(R.id.enter_details_dialog_radio_button);




        addNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNewItemDialog = new Dialog(BillingActivity.this);

                addNewItemDialog.setContentView(R.layout.custom_popup_add_item_to_bill_cardview);
                barcodeRadio1 = (EditText) addNewItemDialog.findViewById(R.id.enter_barcode_edit_text_radio1);
                barcodeRadio2 = (EditText) addNewItemDialog.findViewById(R.id.enter_barcode_edit_text_radio2);
                itemNameRadio2 = (EditText) addNewItemDialog.findViewById(R.id.enter_item_name_edit_text_radio2);
                itemWeightRadio2 = (EditText) addNewItemDialog.findViewById(R.id.enter_item_weight_edit_text_radio2);
                itemCostRadio2 = (EditText) addNewItemDialog.findViewById(R.id.enter_item_cost_edit_text_radio2);
                cancelItenDialogButton = (Button) addNewItemDialog.findViewById(R.id.cancel_item_card_button);
                addNewItemButton = (Button) addNewItemDialog.findViewById(R.id.add_item_card_button);

                radioGroup = (RadioGroup) addNewItemDialog.findViewById(R.id.radio_group);


                addNewItemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addNewItemDialog.show();

                cancelItenDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        addNewItemDialog.dismiss();

                    }
                });



                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        int checkRadioInput = radioGroup.getCheckedRadioButtonId();


                        if(checkRadioInput == R.id.enter_barcode_dialog_radio_button)
                        {
                            //SETTING FIELDS FOR RADIO BUTTON 1
                            barcodeRadio1.setVisibility(View.VISIBLE);

                            //SETTING FIELDS FOR RADIO BUTTON 2
                            barcodeRadio2.setVisibility(View.GONE);
                            itemNameRadio2.setVisibility(View.GONE);
                            itemWeightRadio2.setVisibility(View.GONE);
                            itemCostRadio2.setVisibility(View.GONE);

                            addNewItemButton.setVisibility(View.VISIBLE);

                            Toast.makeText(BillingActivity.this, "ID : "+ checkRadioInput, Toast.LENGTH_LONG);

                            addNewItemButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //THIS WILL FIRST FETCH THE ENTERED BARCODE DETAILS FROM DATABASE AND ADD IT TO A NEW COLLECTION->DOCUMENT OF ITEMS
                                    final String barcode1  = barcodeRadio1.getText().toString();

                                    if(barcode1.isEmpty())
                                    {
                                        barcodeRadio1.setError("Mandatory Field!!!");
                                        barcodeRadio1.requestFocus();
                                    }
                                    if(!barcode1.isEmpty())
                                    {
                                        //FETCHING THE BARCODE DETAILS FROM DATABASE


                                        //CREATING A COLLECTION->DOCUMENT AND ADDING BARCODE DETAILS TO THAT
                                    }

                                }
                            });

                        }
                        else if(checkRadioInput == R.id.enter_details_dialog_radio_button)
                        {
                            //SETTING FIELDS FOR RADIO BUTTON 1
                            barcodeRadio1.setVisibility(View.GONE);

                            //SETTING FIELDS FOR RADIO BUTTON 2
                            barcodeRadio2.setVisibility(View.VISIBLE);
                            itemNameRadio2.setVisibility(View.VISIBLE);
                            itemWeightRadio2.setVisibility(View.VISIBLE);
                            itemCostRadio2.setVisibility(View.VISIBLE);

                            addNewItemButton.setVisibility(View.VISIBLE);

                            Toast.makeText(BillingActivity.this, "ID : "+checkRadioInput, Toast.LENGTH_LONG);


                            addNewItemButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final String barcode = barcodeRadio2.getText().toString();
                                    final String itemName = itemNameRadio2.getText().toString();
                                    final String itemWeight = itemWeightRadio2.getText().toString();
                                    final String itemCost = itemCostRadio2.getText().toString();

                                    if(barcode.length() != 13)
                                    {
                                        barcodeRadio2.setError("Invalid Input!!");
                                        barcodeRadio2.requestFocus();
                                    }
                                    if(barcode.isEmpty())
                                    {
                                        barcodeRadio2.setError("Mandatory Field!!");
                                        barcodeRadio2.requestFocus();
                                    }
                                    if(itemName.isEmpty())
                                    {
                                        itemNameRadio2.setError("Mandatory Field!!");
                                        itemNameRadio2.requestFocus();
                                    }
                                    if(itemWeight.isEmpty())
                                    {
                                        itemWeightRadio2.setError("Mandatory Field!!");
                                        itemWeightRadio2.requestFocus();
                                    }
                                    if(itemCost.isEmpty())
                                    {
                                        itemCostRadio2.setError("Mandatory Field!!");
                                        itemCostRadio2.requestFocus();
                                    }
                                    if(!barcode.isEmpty() && !itemName.isEmpty() && !itemWeight.isEmpty() && !itemCost.isEmpty())
                                    {

                                        //FIRST CHECK WHETHER BARCODE EXISITS IN DATABASE OR NOT
                                        checkIfBarcodeAlreadyExistsInDatabase();

                                        //ADDING DETAILS TO A NEW BARCODE DATABASE
                                        //addDetailsToDataBase();
                                    }


                                }
                            });



                        }



                    }
                });

            }
        });


    }

    private void checkIfBarcodeAlreadyExistsInDatabase() {

        DocumentReference docRef = firebaseFirestore.collection("items_database").document(barcodeRadio2.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {
                        barcodeRadio2.setError("Item Already Exists!!!");
                        barcodeRadio2.requestFocus();
                        Toast.makeText(BillingActivity.this, "Please Select ENTER BARCODE Option.", Toast.LENGTH_LONG).show();

                    }
                    else if(!documentSnapshot.exists())
                    {

                        addDetailsToDataBase();
                    }
                }
                else
                {

                }

            }
        });


    }

    private void addDetailsToDataBase() {

        Map<String, String> userMap = new HashMap<>();

        userMap.put("item_barcode_number", barcodeRadio2.getText().toString());
        userMap.put("item_name", itemNameRadio2.getText().toString());
        userMap.put("item_cost", itemCostRadio2.getText().toString());
        userMap.put("item_weight", itemWeightRadio2.getText().toString());

        firebaseFirestore.collection("items_database")
                .document(barcodeRadio2.getText().toString())
                .set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(BillingActivity.this, "Data Added Successfully", Toast.LENGTH_LONG);

                        addNewItemDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

}
