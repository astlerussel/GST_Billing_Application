package app.hks.billy;

import android.drm.DrmManagerClient;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


public class BillingAdapter extends FirestoreRecyclerAdapter<ModelBillItems, BillingAdapter.BillsHolder> {
    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BillingAdapter(@NonNull FirestoreRecyclerOptions<ModelBillItems> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BillsHolder holder, int position, @NonNull ModelBillItems model) {

        holder.itemNumber.setText(model.getItemNumber());
        holder.itemName.setText(model.getItemName());
        holder.itemWeight.setText(model.getItemWeight());
        holder.itemQuantity.setText(model.getItemQuantity());
        holder.itemCost.setText(model.getItemCost());
        holder.totalItemCost.setText(model.getTotalItemCost());
        holder.itemQuantitySetterTextView.setText(model.getItemQuantity());
        holder.invoiceNumber.setText(model.getInvoiceNumber());
        //holder.userProfileImage.setImageDrawable(model.getImage()););




    }


    @NonNull
    @Override
    public BillsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.billing_items_card_view, parent, false);
        return new BillsHolder(view);
    }

    class BillsHolder extends RecyclerView.ViewHolder{

        TextView itemNumber;
        TextView itemName,itemWeight,itemCost,itemQuantity, totalItemCost, itemQuantitySetterTextView, invoiceNumber;

        Button increaseQuantityButton, decreaseQuantityButton;

        ImageView moveItemToBinImageView;

        FirebaseFirestore firebaseFirestore;

        String userId, strTotalMrpSum;
        int intTotalMrpSum=0;


        public BillsHolder(@NonNull View itemView) {
            super(itemView);

            userId = FirebaseAuth.getInstance().getUid();
            firebaseFirestore = FirebaseFirestore.getInstance();



            itemName = itemView.findViewById(R.id.billing_item_name);
            itemCost = itemView.findViewById(R.id.billing_item_price);
            itemQuantity = itemView.findViewById(R.id.billing_item_quantity);
            itemNumber = itemView.findViewById(R.id.billing_item_ean_number);
            itemWeight = itemView.findViewById(R.id.billing_item_weight);
            totalItemCost = itemView.findViewById(R.id.billing_total_item_cost);
            itemQuantitySetterTextView = itemView.findViewById(R.id.billing_item_quantity_display_textview);
            increaseQuantityButton = itemView.findViewById(R.id.billing_increase_quantity_button);
            decreaseQuantityButton = itemView.findViewById(R.id.billing_decrease_item_quantity_button);
            invoiceNumber = itemView.findViewById(R.id.billing_item_invoice_number);
            moveItemToBinImageView = itemView.findViewById(R.id.billing_item_delete_icon);


            increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(itemQuantity.getText().toString());

                    final int cost = Integer.parseInt(itemCost.getText().toString());
                    int total_cost;

                    quantity++;

                    String strQuantity;
                    final String invNum = String.valueOf(invoiceNumber.getText());
                    String eanNUm = String.valueOf(itemNumber.getText());

                    itemQuantity.setText(String.valueOf(quantity));
                    itemQuantitySetterTextView.setText(String.valueOf(quantity));
                    total_cost = cost*quantity;
                    totalItemCost.setText(String.valueOf(total_cost));

                    strQuantity = String.valueOf(itemQuantity.getText());

                    //INCREASING THE QUANTITY IN FIRESTORE DATABASE
                    DocumentReference doRef = firebaseFirestore.collection("users/"+userId+"/bills/"+invNum+"/"+invNum)
                            .document(eanNUm);
                    doRef.update("item_quantity", strQuantity)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    firebaseFirestore.collection("users/"+userId+"/bills").document(invNum)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful())
                                            {

                                                DocumentSnapshot documentSnapshot = task.getResult();

                                                if(documentSnapshot.exists())
                                                {
                                                    String strTotalMrp = documentSnapshot.getString("total_items_mrp");
                                                    int intTotalMrp = Integer.parseInt(strTotalMrp);
                                                    intTotalMrp = intTotalMrp+cost;
                                                    strTotalMrp = String.valueOf(intTotalMrp);

                                                    firebaseFirestore.collection("users/"+userId+"/bills").document(invNum)
                                                            .update("total_items_mrp", strTotalMrp)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    //PROGRESS BAR TO BE ADDED HERE

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                                    String strTotalItems = documentSnapshot.getString("total_items_count");
                                                    int intTotalItems = Integer.parseInt(strTotalItems);
                                                    intTotalItems++;
                                                    strTotalItems=String.valueOf(intTotalItems);
                                                    firebaseFirestore.collection("users/"+userId+"/bills")
                                                            .document(invNum)
                                                            .update("total_items_count",strTotalItems)
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
                                            else
                                            {

                                            }

                                        }
                                    });



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            String message = e.getMessage();
                        }
                    });






                }
            });

            decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int quantity = Integer.parseInt(itemQuantity.getText().toString());
                    final int cost = Integer.parseInt(itemCost.getText().toString());
                    int total_cost;
                    quantity--;

                    if(quantity < 1)
                    {

                        String strQuantity;
                        String invNum = String.valueOf(invoiceNumber.getText());
                        String eanNUm = String.valueOf(itemNumber.getText());

                        quantity++;
                        itemQuantity.setText(String.valueOf(quantity));
                        itemQuantitySetterTextView.setText(String.valueOf(quantity));
                        total_cost = cost*quantity;
                        totalItemCost.setText(String.valueOf(total_cost));

                        strQuantity = String.valueOf(itemQuantity.getText());

                        //DECREASING THE QUANTITY IN FIRESTORE DATABASE
                        DocumentReference doRef = firebaseFirestore.collection("users/"+userId+"/bills/"+invNum+"/"+invNum)
                                .document(eanNUm);
                        doRef.update("item_quantity", strQuantity)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                String message = e.getMessage();
                            }
                        });


                    }
                    else {

                        String strQuantity;
                        final String invNum = String.valueOf(invoiceNumber.getText());
                        String eanNUm = String.valueOf(itemNumber.getText());


                        itemQuantity.setText(String.valueOf(quantity));
                        itemQuantitySetterTextView.setText(String.valueOf(quantity));
                        total_cost = cost*quantity;
                        totalItemCost.setText(String.valueOf(total_cost));

                        strQuantity = String.valueOf(itemQuantity.getText());

                        //DECREASING THE QUANTITY IN FIRESTORE DATABASE
                        DocumentReference doRef = firebaseFirestore.collection("users/"+userId+"/bills/"+invNum+"/"+invNum)
                                .document(eanNUm);
                        doRef.update("item_quantity", strQuantity)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        firebaseFirestore.collection("users/"+userId+"/bills").document(invNum)
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if (task.isSuccessful())
                                                {

                                                    DocumentSnapshot documentSnapshot = task.getResult();

                                                    if(documentSnapshot.exists())
                                                    {
                                                        String strTotalMrp = documentSnapshot.getString("total_items_mrp");
                                                        int intTotalMrp = Integer.parseInt(strTotalMrp);
                                                        intTotalMrp = intTotalMrp-cost;
                                                        strTotalMrp = String.valueOf(intTotalMrp);

                                                        firebaseFirestore.collection("users/"+userId+"/bills").document(invNum)
                                                                .update("total_items_mrp", strTotalMrp)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        //PROGRESS BAR TO BE ADDED HERE

                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });

                                                        String strTotalItems = documentSnapshot.getString("total_items_count");
                                                        int intTotalItems = Integer.parseInt(strTotalItems);
                                                        intTotalItems--;
                                                        strTotalItems=String.valueOf(intTotalItems);
                                                        firebaseFirestore.collection("users/"+userId+"/bills")
                                                                .document(invNum)
                                                                .update("total_items_count",strTotalItems)
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
                                                else
                                                {

                                                }

                                            }
                                        });




                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                String message = e.getMessage();
                            }
                        });

                    }


                }
            });


            moveItemToBinImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String invNum = String.valueOf(invoiceNumber.getText());
                    String eanNUm = String.valueOf(itemNumber.getText());
                    String strItemQuantity = String.valueOf(itemQuantity.getText());

                    final int intItemQuantity = Integer.parseInt(strItemQuantity);

                    final String strItemTotalCost = String.valueOf(totalItemCost.getText());

                    final int intTotalItemCost = Integer.parseInt(strItemTotalCost);

                    firebaseFirestore.collection("users")
                            .document(userId)
                            .collection("bills")
                            .document(invNum)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot documentSnapshot = task.getResult();

                                        String strTotalItemsMrp = documentSnapshot.getString("total_items_mrp");
                                        String strTotalItemsCount = documentSnapshot.getString("total_items_count");

                                        int intTotalItemsCount = Integer.parseInt(strTotalItemsCount);

                                        intTotalItemsCount = intTotalItemsCount-intItemQuantity;
                                        strTotalItemsCount = String.valueOf(intTotalItemsCount);

                                        int intTotalItemsMrp = Integer.parseInt(strTotalItemsMrp);
                                        intTotalItemsMrp = intTotalItemsMrp-intTotalItemCost;

                                        strTotalItemsMrp = String.valueOf(intTotalItemsMrp);

                                        firebaseFirestore.collection("users")
                                                .document(userId)
                                                .collection("bills")
                                                .document(invNum)
                                                .update("total_items_mrp", strTotalItemsMrp,
                                                        "total_items_count", strTotalItemsCount)
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
                                    else
                                    {

                                    }


                                }
                            });




                    DocumentReference doRef = firebaseFirestore.collection("users")
                            .document(userId)
                            .collection("bills")
                            .document(invNum)
                            .collection(invNum)
                            .document(eanNUm);

                    doRef.delete()
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
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int user_position = getAdapterPosition();
                    if (user_position!= RecyclerView.NO_POSITION && listener != null)
                    {
                        listener.OnItemClick(getSnapshots().getSnapshot(user_position),user_position);



                    }

                }
            });
        }
    }

    public interface OnItemClickListener{
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;

    }
}
