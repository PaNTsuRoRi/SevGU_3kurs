package com.example.lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickCheck(View view){
        CheckBox check1 = findViewById(R.id.check1);
        CheckBox check2 = findViewById(R.id.check2);
        TextView textView = findViewById(R.id.textView);
        if(check1.isChecked() && !check2.isChecked()) {
            textView.setText("Выбран левый");
        }else{
        if(!check1.isChecked() && check2.isChecked()) {
            textView.setText("Выбран правый");
        }else {
            if (check1.isChecked() && check2.isChecked()){
                textView.setText("Выбраны оба");
            }else textView.setText("Ни один");
        }
        }
    }

    public void onClickDrop(View view){
        CheckBox check1 = findViewById(R.id.check1);
        CheckBox check2 = findViewById(R.id.check2);
        check1.setChecked(false);
        check2.setChecked(false);
        onClickCheck(view);
    }
}