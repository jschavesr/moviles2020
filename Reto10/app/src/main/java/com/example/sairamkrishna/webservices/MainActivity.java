package com.example.sairamkrishna.webservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private RequestQueue queue;
    TextView first_response;
    Spinner filterMonth;
    private ListView obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

    }

    public void getData(View view){
        String url ="https://www.datos.gov.co/resource/etr2-mkeu.json" ;
        first_response = (TextView) findViewById(R.id.first_response);
        filterMonth = (Spinner) findViewById(R.id.month_filter);
        String month = (String) filterMonth.getSelectedItem().toString();
        if (month.equals("TODOS") == false){
            url = url +  "?depto=" + month;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        String[] all_responses = response.split("\\}");
                        first_response.setText("Satisfactorio");
                        int cant = all_responses.length;

                        for ( int i = 0 ; i < 50; i ++){
                            String tmp = all_responses[i];
                            tmp = tmp.replace('"', ' ');
                            tmp = tmp.replace(',', '\n');
                            tmp = tmp.substring(3,tmp.length()-1);
                            all_responses[i] = tmp;
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, all_responses);
                        obj = (ListView)findViewById(R.id.listView1);
                        obj.setAdapter(arrayAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                first_response.setText("ERROR RESPONSE: " + error.toString());
            }
        });
        queue.add(stringRequest);
    }


    public void getAllData(View view){
        String url ="https://www.datos.gov.co/resource/etr2-mkeu.json" ;
        first_response = (TextView) findViewById(R.id.first_response);
        filterMonth = (Spinner) findViewById(R.id.month_filter);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        String[] all_responses = response.split("\\}");
                        first_response.setText("Satisfactorio");
                        int cant = all_responses.length;

                        for ( int i = 0 ; i < 50; i ++){
                            String tmp = all_responses[i];
                            tmp = tmp.replace('"', ' ');
                            tmp = tmp.replace(',', '\n');
                            tmp = tmp.substring(3,tmp.length()-1);
                            all_responses[i] = tmp;
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, all_responses);
                        obj = (ListView)findViewById(R.id.listView1);
                        obj.setAdapter(arrayAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                first_response.setText("ERROR RESPONSE: " + error.toString());
            }
        });
        queue.add(stringRequest);
    }
}