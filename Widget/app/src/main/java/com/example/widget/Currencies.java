package com.example.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

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

/**
 * Implementation of App Widget functionality.
 */
public class Currencies extends AppWidgetProvider {

    private static final String XML_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        fetchCurrencyRates(context, appWidgetManager, appWidgetIds);
    }

    private void fetchCurrencyRates(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, XML_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the XML response and display the currency rates
                        Map<String, Double> rates = parseExchangeRates(response);
                        displayCurrencyRates(context, appWidgetManager, appWidgetIds, rates);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", String.valueOf(error));
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

    private void displayCurrencyRates(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Map<String, Double> rates) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

            double usdAmount = rates.get("USD");
            double gbpAmount = rates.get("GBP");
            double sekAmount = rates.get("SEK");

            String result = String.format("USD: %.2f\nGBP: %.2f\nSEK: %.2f", usdAmount, gbpAmount, sekAmount);

            views.setTextViewText(R.id.currency_rates_textview, result);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}