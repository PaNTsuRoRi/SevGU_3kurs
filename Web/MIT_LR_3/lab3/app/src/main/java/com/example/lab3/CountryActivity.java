package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class CountryActivity extends AppCompatActivity {

    public static final String COUNTRY = "country";
    public static final String PRICE = "price";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);
    }

    public void onReady(View view){
        Intent intent = new Intent();
        intent.putExtra("ansCountry", countryList(true));
        intent.putExtra("ansPrice", countryList(false));
        setResult(RESULT_OK, intent);
        finish();
    }

    public String countryList(boolean choice){
        CheckBox france = findViewById(R.id.france);
        CheckBox italy = findViewById(R.id.italy);
        CheckBox china = findViewById(R.id.china);
        CheckBox germany = findViewById(R.id.germany);
        CheckBox japan = findViewById(R.id.japan);
        String list = "Заказ: ";
        String strPrice = "На сумму: ";
        int price = 0;
        if(france.isChecked()){
            list += "Франция ";
            price += 50;
        }
        if(italy.isChecked()){
            list += "Италия ";
            price += 60;
        }
        if(china.isChecked()){
            list += "Китай ";
            price += 70;
        }
        if(germany.isChecked()){
            list += "Германия ";
            price += 80;
        }
        if(japan.isChecked()){
            list += "Япония";
            price += 90;
        }
        strPrice += String.valueOf(price);
        if(choice) return list;
        else return strPrice;
    }
}