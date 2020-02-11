package app.hks.billy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.BatchUpdateException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private Button newBillButton;

    private CardView countCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = findViewById(R.id.main_app_name_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Billy");

        countCardView = (CardView) findViewById(R.id.count_cardview);
        newBillButton = (Button) findViewById(R.id.create_new_bill_button);







        newBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this, BillingActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}


