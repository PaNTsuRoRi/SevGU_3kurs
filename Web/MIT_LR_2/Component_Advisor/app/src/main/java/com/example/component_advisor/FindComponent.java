package com.example.component_advisor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class FindComponent extends AppCompatActivity {
    private ComponentExpert expert = new ComponentExpert();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_component);
    }

    public void onClickFindComponent(View view){
        Spinner components = findViewById(R.id.components);
        TextView type = findViewById(R.id.type);
        String com_type = String.valueOf(components.getSelectedItem());
        List<String> componentList = expert.getComponents(com_type);
        StringBuilder comForm = new StringBuilder();
        for(String component:componentList){
            comForm.append(component).append('\n');
        }
        type.setText(comForm);
    }
}