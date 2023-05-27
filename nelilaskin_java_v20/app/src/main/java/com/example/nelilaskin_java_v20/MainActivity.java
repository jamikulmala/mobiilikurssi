package com.example.nelilaskin_java_v20;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private void writeToFile(String result) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("calculations.txt", Context.MODE_APPEND);

            fileOutputStream.write(result.getBytes());
            fileOutputStream.write("\n".getBytes());

            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private EditText inputPlus1, inputPlus2, inputSub, inputSub2, inputMult1, inputMult2, inputDiv1, inputDiv2;
    private TextView resultPlus, resultSub, resultMult, resultDiv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputPlus1 = findViewById(R.id.inputPlus1);
        inputPlus2 = findViewById(R.id.inputPlus2);
        resultPlus = findViewById(R.id.resultPlus);
        Button plusButton = findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateSum();
            }
        });

        inputSub = findViewById(R.id.inputSub);
        inputSub2 = findViewById(R.id.inputSub2);
        resultSub = findViewById(R.id.resultSub);
        Button subButton = findViewById(R.id.subButton);
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateSubtraction();
            }
        });

        inputMult1 = findViewById(R.id.inputMult1);
        inputMult2 = findViewById(R.id.inputMult2);
        resultMult = findViewById(R.id.resultMult);
        Button multButton = findViewById(R.id.multButton);
        multButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateMultiplication();
            }
        });

        inputDiv1 = findViewById(R.id.inputDiv1);
        inputDiv2 = findViewById(R.id.inputDiv2);
        resultDiv = findViewById(R.id.resultDiv);
        Button divButton = findViewById(R.id.divButton);
        divButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateDivision();
            }
        });
    }

    private void calculateSum() {
        double num1 = Double.parseDouble(inputPlus1.getText().toString());
        double num2 = Double.parseDouble(inputPlus2.getText().toString());
        double result = num1 + num2;
        String res = num1 + " + " + num2 + " = " + result;
        resultPlus.setText(String.valueOf(result));
        writeToFile(res);
        Intent intent = new Intent(MainActivity.this, log.class);
        intent.putExtra("result", res);
        startActivity(intent);
    }

    private void calculateSubtraction() {
        double num1 = Double.parseDouble(inputSub.getText().toString());
        double num2 = Double.parseDouble(inputSub2.getText().toString());
        double result = num1 - num2;
        String res = num1 + " - " + num2 + " = " + result;
        resultSub.setText(String.valueOf(result));
        writeToFile(res);
        Intent intent = new Intent(MainActivity.this, log.class);
        intent.putExtra("result", res);
        startActivity(intent);
    }

    private void calculateMultiplication() {
        double num1 = Double.parseDouble(inputMult1.getText().toString());
        double num2 = Double.parseDouble(inputMult2.getText().toString());
        double result = num1 * num2;
        String res = num1 + " * " + num2 + " = " + result;
        resultMult.setText(String.valueOf(result));
        writeToFile(res);
        Intent intent = new Intent(MainActivity.this, log.class);
        intent.putExtra("result", res);
        startActivity(intent);
    }

    private void calculateDivision() {
        double num1 = Double.parseDouble(inputDiv1.getText().toString());
        double num2 = Double.parseDouble(inputDiv2.getText().toString());
        if (num2 != 0) {
            double result = num1 / num2;
            String res = num1 + " / " + num2 + " = " + result;
            resultDiv.setText(String.valueOf(result));
            writeToFile(res);
            Intent intent = new Intent(MainActivity.this, log.class);
            intent.putExtra("result", res);
            startActivity(intent);
        } else {
            resultDiv.setText("Cannot divide by 0");
        }
    }
}