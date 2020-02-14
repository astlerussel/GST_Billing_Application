package app.hks.billy;

public class ModelBillItems {

    public String item_name , item_barcode_number, item_cost,item_weight;


    public ModelBillItems()
    {
        //Empty Constructor needed
    }

    public ModelBillItems(String item_name, String item_barcode_number, String item_cost,String item_weight) {
        this.item_name = item_name;
        this.item_barcode_number = item_barcode_number;
        this.item_cost = item_cost;
        this.item_weight = item_weight;
        //this.counter = counter;
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
   /*public int getItemCounter() {
        return counter;
    }

    public void setItemCounter(int counter) {
        this.counter = counter;
    }*/
}
