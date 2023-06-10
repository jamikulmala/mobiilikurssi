package com.example.saatila;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "http://opendata.fmi.fi/wfs/fin?service=WFS&version=2.0.0&request=GetFeature&storedquery_id=fmi::observations::weather::timevaluepair&place=";

    private TableLayout temperatureTableLayout;
    private EditText cityEditText;
    private Button fetchWeatherButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureTableLayout = findViewById(R.id.temperature_table_layout);
        cityEditText = findViewById(R.id.city_edit_text);
        fetchWeatherButton = findViewById(R.id.fetch_weather_button);

        fetchWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString().trim();
                if (!city.isEmpty()) {
                    String url = API_URL + city;
                    fetchWeatherData(url);
                }
            }
        });
    }

    private void fetchWeatherData(String url) {

        temperatureTableLayout.removeAllViews();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the XML response
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            InputSource inputSource = new InputSource(new StringReader(response));
                            Document document = builder.parse(inputSource);

                            // Get the list of MeasurementTVP elements
                            NodeList measurementTVPList = document.getElementsByTagName("wml2:MeasurementTVP");

                            for (int i = 0; i < 5; i++) {
                                Element measurementTVP = (Element) measurementTVPList.item(i);

                                // Get the time and value elements
                                Element timeElement = (Element) measurementTVP.getElementsByTagName("wml2:time").item(0);
                                Element valueElement = (Element) measurementTVP.getElementsByTagName("wml2:value").item(0);

                                String time = timeElement.getTextContent();
                                String temperature = valueElement.getTextContent();

                                // Add the temperature to the table
                                addTemperatureView(time, temperature);
                            }
                        } catch (ParserConfigurationException | SAXException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", String.valueOf(error));
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void addTemperatureView(String time, String temperature) {
        TableRow tableRow = new TableRow(this);

        TextView timeTextView = new TextView(this);
        timeTextView.setText(time);
        timeTextView.setPadding(0, 2, 16, 2);
        tableRow.addView(timeTextView);

        TextView temperatureTextView = new TextView(this);
        temperatureTextView.setText(temperature);
        temperatureTextView.setPadding(16, 2, 0, 2);
        tableRow.addView(temperatureTextView);

        temperatureTableLayout.addView(tableRow);
    }
}