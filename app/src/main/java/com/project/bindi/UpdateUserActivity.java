package com.project.bindi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class UpdateUserActivity extends AppCompatActivity {
    Spinner genderSpinner;
    String gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        genderSpinner=(Spinner)findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    gender="Male";
                }
                else{
                    gender="Female";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender="Male";
            }
        });
// Apply the adapter to the spinner
        genderSpinner.setAdapter(adapter);

    }
}