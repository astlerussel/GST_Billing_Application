package app.hks.billy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolBar;

    private CardView countCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = findViewById(R.id.main_app_name_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Billy");

        countCardView = (CardView) findViewById(R.id.count_cardview);





    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}


