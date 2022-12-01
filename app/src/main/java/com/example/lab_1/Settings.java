package com.example.lab_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Objects;

public class Settings extends AppCompatActivity
{
    SharedPreferences sPref;
    RadioGroup radioGroup;
    RadioButton radioButtonDB;
    RadioButton radioButtonFile;
    Button buttonSave;
    MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        radioButtonDB = findViewById(R.id.radioButtonDB);
        radioButtonFile = findViewById(R.id.radioButtonFile);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v ->
        {
            saveSettings();
        });

        sPref = getSharedPreferences("settings", MODE_PRIVATE);
        if (sPref.contains("saveMode"))
        {
            String saveMode = sPref.getString("saveMode", "");
            if (Objects.equals(saveMode, "DB"))
            {
                radioButtonDB.setChecked(true);
            }
            if (Objects.equals(saveMode, "Files"))
            {
                radioButtonFile.setChecked(true);
            }
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        saveSettings();
    }
    protected void saveSettings()
    {
            if (radioButtonDB.isChecked())
            {
                Intent changeActivity = new Intent(Settings.this, MainActivity.class);
                changeActivity.putExtra("typeStorage", true);
                startActivity(changeActivity);
            }
            if (radioButtonFile.isChecked())
            {
                Intent changeActivity = new Intent(Settings.this, MainActivity.class);
                changeActivity.putExtra("typeStorage", false);
                startActivity(changeActivity);
            }
        Toast.makeText(this, "Настройки успешно сохранены", Toast.LENGTH_SHORT).show();
    }
}