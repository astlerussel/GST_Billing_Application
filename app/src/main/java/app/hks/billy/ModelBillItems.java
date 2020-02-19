package app.hks.billy;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ModelBillItems {

    public String item_name , item_barcode_number, item_cost, item_weight, item_quantity, total_item_cost;
    public String invoice_number;


    public ModelBillItems()
    {
        //Empty Constructor needed
    }

    public ModelBillItems(String item_name, String item_barcode_number, String item_cost,String item_weight,String item_quantity, String total_item_cost, String invoice_number) {
        this.item_name = item_name;
        this.item_barcode_number = item_barcode_number;
        this.item_cost = item_cost;
        this.item_weight = item_weight;
        this.item_quantity = item_quantity;
        this.total_item_cost = total_item_cost;
        this.invoice_number = invoice_number;
        //this.counter = counter;
    }

    public String getInvoiceNumber() { return invoice_number; }

    public String getItemQuantity() { return  item_quantity;}

    public String getTotalItemCost() {
        double itemCost  = Double.parseDouble(item_cost);
        int itemQuantity = Integer.parseInt(item_quantity);

        double totalItemCost = itemCost*itemQuantity;

        return total_item_cost = String.valueOf(totalItemCost);
    }

    public String getItemName() {
        return item_name;
    }

    public void setItemName(String item_name) {
        this.item_name = item_name;
    }

    public String getItemNumber() {
        return item_barcode_number;
    }

    public void setItemNumber(String item_barcode_number) {
        this.item_barcode_number = item_barcode_number;
    }

    public String getItemCost() {
        return item_cost;
    }

    public void setItemCost(String item_cost) {
        this.item_cost = item_cost;
    }
    public String getItemWeight() {
        return item_weight;
    }

    public void setItemWeight(String item_weight) {
        this.item_weight = item_weight;
    }

    public void  setTotalItemCost()
    {


    }

    public void setItemQuantity(String item_quantity) {
        this.item_quantity = item_quantity;



    }

    /*public int getItemCounter() {
        return counter;
    }

    public void setItemCounter(int counter) {
        this.counter = counter;
    }*/
}
