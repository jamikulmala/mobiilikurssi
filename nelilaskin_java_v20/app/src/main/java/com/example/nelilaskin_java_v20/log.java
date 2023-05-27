package com.example.nelilaskin_java_v20;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class log extends AppCompatActivity {

    private TextView previousCalculationTextView;

    private String readFromFile() {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            FileInputStream fileInputStream = openFileInput("calculations.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }

            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);

        previousCalculationTextView = findViewById(R.id.previousCalculationTextView);
        String previousCalculations = readFromFile();
        previousCalculationTextView.setText(previousCalculations);
    }
}