package com.sychev.rss_reader;;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;

public class SetupSourceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_source);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        TextInputEditText editText = (TextInputEditText) findViewById(R.id.edit_url);
        editText.setText(getIntent().getStringExtra("url_for_edit"));

        spinner.setSelection(getIntent().getIntExtra("selected_category", 0));

        Button saveButton = findViewById(R.id.save_source_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextInputEditText editText = (TextInputEditText) findViewById(R.id.edit_url);
            String stringToPassBack = editText.getText().toString();
            Spinner catSpinner = (Spinner) findViewById(R.id.spinner_category);
            System.out.println("Selected " + catSpinner.getSelectedItemPosition());
            // Put the String to pass back into an Intent and close this activity
            Intent intent = new Intent();
            intent.putExtra("url", stringToPassBack);
            setResult(RESULT_OK, intent);
            finish();

        }
    });

    }
}