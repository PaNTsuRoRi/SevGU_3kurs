package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class InviteActivity extends AppCompatActivity {

    public static final String COUNTRY = "country";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView countryView = (TextView) findViewById(R.id.country);
        TextView priceView = (TextView) findViewById(R.id.price);
        if (data == null) {
            return;
        }
        String ansCountry = data.getStringExtra("ansCountry");
        String ansPrice = data.getStringExtra("ansPrice");
        countryView.setText(ansCountry);
        priceView.setText(ansPrice);
    }

    public void onInquiry(View view){
        Intent intent = new Intent(this, CountryActivity.class);
        startActivityForResult(intent, 1);
    }
}