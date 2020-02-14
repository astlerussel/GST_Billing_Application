package app.hks.billy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingActivity extends AppCompatActivity {
    private RecyclerView findFriendRecyclerList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static String invoNumber;





    private Button addNewItemButton;
    private Button cancelItenDialogButton, addItemDialogButton;
    private EditText barcodeRadio1, barcodeRadio2, itemNameRadio2, itemWeightRadio2, itemCostRadio2;

    private RadioGroup radioGroup;
    private BillingAdapter adapter;
    private RadioButton radioButton1, radioButton2;


    private Dialog addNewItemDialog;


    private Dialog discardOptionsDialog;


    private int counter = 0;


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userId;

    private CardView summaryCardView, discardBillCardView;
    private TextView invoice_number, total_cost_of_items, total_items_count;
    private Button discardButton, checkoutButton;

    private Integer intInvoNumber;
    private String strInvoNumber, strCompanyName, strOwnerName, strInvioceCharCount;
    private Character strCharCompanyName, strCharOwnerName;
    String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
    CollectionReference ItemsRef;



    private Button discardOPtionsYesButton, discardoptionsNoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        findFriendRecyclerList = (RecyclerView) findViewById(R.id.item_recycler_list);
        findFriendRecyclerList.setLayoutManager(new LinearLayoutManager(this));



        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        //userId = firebaseAuth.getCurrentUser().getUid();

        addNewItemButton = (Button) findViewById(R.id.billing_add_item_button);
        radioButton1 = (RadioButton) findViewById(R.id.enter_barcode_dialog_radio_button);
        radioButton2 = (RadioButton) findViewById(R.id.enter_details_dialog_radio_button);
        initializeSummaryCardViewFields();













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


                        //WHEN RADIO BUTTON 1 IS SELECTED
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
                                    if(barcode1.length() != 13)
                                    {
                                        barcodeRadio1.setError("Invalid Input!!!");
                                        barcodeRadio1.requestFocus();

                                    }
                                    if(!barcode1.isEmpty() && !(barcode1.length() != 13))
                                    {
                                        //FETCHING THE BARCODE DETAILS FROM DATABASE
                                        //CREATING A COLLECTION->DOCUMENT AND ADDING BARCODE DETAILS TO THAT

                                        DocumentReference docRef = firebaseFirestore.collection("items_database").document(barcodeRadio1.getText().toString());
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if(task.isSuccessful())
                                                {
                                                    DocumentSnapshot documentSnapshot = task.getResult();

                                                    if(documentSnapshot.exists())
                                                    {
                                                        //Toast.makeText(BillingActivity.this, "Item Exists", Toast.LENGTH_LONG).show();

                                                        String barcdeValue = barcodeRadio1.getText().toString();
                                                        addItemToTheBill(barcdeValue);
                                                        addNewItemDialog.dismiss();

                                                    }
                                                    else if(!documentSnapshot.exists())
                                                    {

                                                        barcodeRadio2.setError("Item doesn't Exists!!!");
                                                        barcodeRadio2.requestFocus();
                                                        Toast.makeText(BillingActivity.this, "Item Doesn't Exists!!!Please Select ENTER DETAILS Option.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                                else
                                                {

                                                }

                                            }
                                        });
                                    }

                                }
                            });

                        }

                        //WHEN RADIO BUTTON 2 IS SELECTED
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

                                    }

                                }
                            });

                        }

                    }
                });

            }
        });



        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BillingActivity.this, CheckoutActivity.class);
                intent.putExtra("invoice_complete_number", strInvoNumber);
                intent.putExtra("invoice_character", strInvioceCharCount);
                intent.putExtra("invoice_number", intInvoNumber);
                startActivity(intent);

            }
        });





    }

    private void setUpRecyclerView() {







        Toast.makeText(BillingActivity.this, invoNumber, Toast.LENGTH_LONG).show();



        Query query = ItemsRef;

        FirestoreRecyclerOptions<ModelBillItems> options = new FirestoreRecyclerOptions.Builder<ModelBillItems>()
                .setQuery(query, ModelBillItems.class)
                .build();

        adapter = new BillingAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.item_recycler_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void deleteTheBillFromTheDatabase() {



        CollectionReference coleref = firebaseFirestore.collection("users/"+userId+"/bills/"+strInvoNumber+"/"+strInvoNumber+"/");

        coleref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        DocumentReference docRef = firebaseFirestore.collection("users")
                                .document(userId)
                                .collection("bills")
                                .document(strInvoNumber).collection(strInvoNumber).document(document.getId());


                        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(BillingActivity.this,"Data Deleted", Toast.LENGTH_LONG).show();
                                //Toast.makeText(BillingActivity.this,strInvoNumber, Toast.LENGTH_LONG).show();
                                //discardOptionsDialog.dismiss();
                                Intent intent = new Intent(BillingActivity.this, MainActivity.class);
                                startActivity(intent);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                String message = e.getMessage();
                                Toast.makeText(BillingActivity.this, "Error: "+ message, Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                    //Log.d(TAG, list.toString());
                } else {
                    //Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });




    }


    private void initializeSummaryCardViewFields() {

        summaryCardView = (CardView) findViewById(R.id.summary_card_view);
        invoice_number = (TextView) summaryCardView.findViewById(R.id.summary_card_invoice_number);
        total_items_count = (TextView) summaryCardView.findViewById(R.id.summary_card_total_items);
        total_cost_of_items = (TextView) summaryCardView.findViewById(R.id.summary_card_total_cost);

        discardButton = (Button) summaryCardView.findViewById(R.id.summary_card_discard_button);
        checkoutButton = (Button) summaryCardView.findViewById(R.id.summary_card_checkout_button);

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                discardOptionsDialog = new Dialog(BillingActivity.this);

                discardOptionsDialog.setContentView(R.layout.custom_on_disacrd_options_card_view_dialog);
                discardOPtionsYesButton = (Button) discardOptionsDialog.findViewById(R.id.discard_option_yes_card_button);
                discardoptionsNoButton = (Button) discardOptionsDialog.findViewById(R.id.discard_option_no_card_button);

                discardOptionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                discardOptionsDialog.show();

                discardoptionsNoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        discardOptionsDialog.dismiss();
                    }
                });


                discardOPtionsYesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteTheBillFromTheDatabase();


                    }
                });


            }
        });




    }

    private void addItemToTheBill(final String barcodeValue) {

        DocumentReference docRef = firebaseFirestore.collection("items_database").document(barcodeValue);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {



                Map<String, String> itemsMap = new HashMap<>();

                itemsMap.put("item_barcode_number", documentSnapshot.getString("item_barcode_number"));
                itemsMap.put("item_cost", documentSnapshot.getString("item_cost"));
                itemsMap.put("item_name", documentSnapshot.getString("item_name"));
                itemsMap.put("item_weight", documentSnapshot.getString("item_weight"));

                firebaseFirestore.collection("users")
                        .document(userId)
                        .collection("bills")
                        .document(strInvoNumber)
                        .collection(strInvoNumber).document(barcodeValue)
                        .set(itemsMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String message = e.getMessage();
                        Toast.makeText(BillingActivity.this, "Error: "+message, Toast.LENGTH_LONG).show();

                    }
                });


                counter++;
                DocumentReference docRef = firebaseFirestore.collection("users/"+userId+"/bills/"+strInvoNumber+"/"+strInvoNumber).document(barcodeValue);
                docRef.update("counter", counter)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String message = e.getMessage();
                        Toast.makeText(BillingActivity.this, "Error: "+message, Toast.LENGTH_LONG).show();
                    }
                });

                CollectionReference colRef = firebaseFirestore.collection("users")
                        .document(userId).collection("bills").document(strInvoNumber).collection(strInvoNumber);
                colRef.orderBy("counter");


            }
        });


    }

    private void checkIfBarcodeAlreadyExistsInDatabase() {

        DocumentReference docRef = firebaseFirestore.collection("items_database").document(barcodeRadio2.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                String barcodeValue = barcodeRadio2.getText().toString();

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

                        //ADDING DETAILS TO A NEW BARCODE DATABASE
                        //addDetailsToDataBase();
                        addItemToTheBill(barcodeValue);
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
        userMap.put("counter", "");

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


    @Override
    protected void onStart() {
        super.onStart();
        if(invoNumber==""){
            Intent refresh = new Intent(this, BillingActivity.class);
            startActivity(refresh);//Start the same Activity
            finish();
        }



        userId = firebaseAuth.getUid();


        DocumentReference docRef = firebaseFirestore.collection("users").document(userId);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                //invoice_number.setText(documentSnapshot.getString("invoice_number"));

                strInvoNumber = documentSnapshot.getString("invoice_number");
                strCompanyName = documentSnapshot.getString("company_name");
                strOwnerName = documentSnapshot.getString("owners_name");
                strInvioceCharCount = documentSnapshot.getString("invoice_char_count");


                strCharCompanyName = strCompanyName.toUpperCase().charAt(0);
                strCharOwnerName = strOwnerName.toUpperCase().charAt(0);


                intInvoNumber = Integer.parseInt(strInvoNumber);
                intInvoNumber = intInvoNumber+1;

                if(intInvoNumber > 999999999)
                {
                    char charInvoiceCharCount = strInvioceCharCount.toUpperCase().charAt(0);
                    int intCount = charInvoiceCharCount+1;
                    charInvoiceCharCount = (char)intCount;
                    strInvioceCharCount = String.valueOf(charInvoiceCharCount);

                    intInvoNumber = 1;


                    strInvoNumber = strInvioceCharCount+strCharCompanyName+strCharOwnerName+(900000000+intInvoNumber);


                    //Toast.makeText(BillingActivity.this, "Data: "+strInvoNumber, Toast.LENGTH_LONG).show();
                    invoice_number.setText(strInvoNumber);


                }
                else {


                    strInvoNumber = strInvioceCharCount+strCharCompanyName+strCharOwnerName+(900000000+intInvoNumber);
                    // Toast.makeText(BillingActivity.this, "Data: "+strInvoNumber, Toast.LENGTH_LONG).show();
                    invoice_number.setText(strInvoNumber);
                    invoNumber = invoice_number.getText().toString();


                }



            }

        });


        ItemsRef = db.collection("/users/"+uid+"/bills/"+invoNumber+"/"+invoNumber);
        setUpRecyclerView();









        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}





