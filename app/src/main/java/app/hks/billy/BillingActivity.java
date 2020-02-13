package app.hks.billy;

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

public class BillingActivity extends AppCompatActivity {

    private Button addNewItemButton;
    private Button cancelItenDialogButton, addItemDialogButton;
    private EditText barcodeRadio1, barcodeRadio2, itemNameRadio2, itemWeightRadio2, itemCostRadio2;

    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2;

    private Dialog addNewItemDialog;

    private LinearLayout layoutForRadio1, layoutForRadio2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        addNewItemButton = (Button) findViewById(R.id.billing_add_item_button);
        radioButton1 = (RadioButton) findViewById(R.id.enter_barcode_dialog_radio_button);
        radioButton2 = (RadioButton) findViewById(R.id.enter_details_dialog_radio_button);
        layoutForRadio1 = (LinearLayout) findViewById(R.id.fields_layout_for_radio1);
        layoutForRadio2 = (LinearLayout) findViewById(R.id.fields_layour_for_radio2);




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
                        }



                    }
                });






            }
        });


    }

}
