package com.example.valuuttalaskin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private static final String XML_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private Button btnFetchRates;
    private TextView tvRates;
    private EditText editAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFetchRates = findViewById(R.id.btnFetchRates);
        tvRates = findViewById(R.id.tvRates);

        btnFetchRates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCurrencyRates();
            }
        });
    }

    private void fetchCurrencyRates() {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, XML_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the XML response and display the currency rates
                        Map<String, Double> rates = parseExchangeRates(response);
                        displayCurrencyRates(rates);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error case
                        tvRates.setText("Failed to fetch currency rates.");
                    }
                });

        queue.add(request);
    }

    private Map<String, Double> parseExchangeRates(String xmlResponse) {
        Map<String, Double> rates = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputSource inputSource = new InputSource(new StringReader(xmlResponse));
            Document doc = builder.parse(inputSource);

            Element rootElement = doc.getDocumentElement();

            NodeList cubeList = rootElement.getElementsByTagName("Cube");

            for (int i = 0; i < cubeList.getLength(); i++) {
                Node cubeNode = cubeList.item(i);
                if (cubeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cubeElement = (Element) cubeNode;
                    String currency = cubeElement.getAttribute("currency");
                    String rateString = cubeElement.getAttribute("rate");

                    if (!currency.isEmpty() && !rateString.isEmpty()) {
                        double rate = Double.parseDouble(rateString);
                        rates.put(currency, rate);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return rates;
    }

    private void displayCurrencyRates(Map<String, Double> rates) {

        tvRates = findViewById(R.id.tvRates);
        editAmount = findViewById(R.id.editAmount);

        String amountString = editAmount.getText().toString().trim();
        if (amountString.isEmpty()) {
            tvRates.setText("Please enter an amount");
            return;
        }

        double amount = Double.parseDouble(amountString);

        double usdAmount = amount * rates.get("USD");
        double gbpAmount = amount * rates.get("GBP");
        double sekAmount = amount * rates.get("SEK");

        String result = String.format("USD: %.2f\nGBP: %.2f\nSEK: %.2f", usdAmount, gbpAmount, sekAmount);
        tvRates.setText(result);
    }
}