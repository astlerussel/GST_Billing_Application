package app.hks.billy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;




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
        //holder.itemQuantity.setText(model.getItemCounter());
        holder.itemCost.setText(model.getItemCost());
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
        TextView itemName,itemWeight,itemCost,itemQuantity;


        public BillsHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.billing_item_name);
            itemCost = itemView.findViewById(R.id.billing_item_price);
            itemQuantity = itemView.findViewById(R.id.billing_item_counter);
            itemNumber = itemView.findViewById(R.id.billing_item_ean_number);
            itemWeight = itemView.findViewById(R.id.billing_item_weight);

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
