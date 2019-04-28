package com.fyp.emasjid;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class AdzanActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private TextView data;
    private Button next_page;
    private Button mp3_page;

    private RequestQueue mRequestQueue;
    private String url = "https://api.azanpro.com/times/today.json?zone=sgr01&format=12-hour";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adzan);

        data = findViewById(R.id.result);

        Spinner mySpinner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(AdzanActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.locations));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    url = "https://api.azanpro.com/times/today.json?zone=jhr02&format=12-hour";
                    jsonParse();
                } else if (i == 1) {
                    url = "https://api.azanpro.com/times/today.json?zone=kdh01&format=12-hour";
                    jsonParse();
                }else if (i == 2) {
                    url = "https://api.azanpro.com/times/today.json?zone=ktn01&format=12-hour";
                    jsonParse();
                }else if (i == 3) {
                    url = "https://api.azanpro.com/times/today.json?zone=wly01&format=12-hour";
                    jsonParse();
                }else if (i == 4) {
                    url = "https://api.azanpro.com/times/today.json?zone=mlk01&format=12-hour";
                    jsonParse();
                }else if (i == 5) {
                    url = "https://api.azanpro.com/times/today.json?zone=ngs02&format=12-hour";
                    jsonParse();
                }else if (i == 6) {
                    url = "https://api.azanpro.com/times/today.json?zone=phg02&format=12-hour";
                    jsonParse();
                }else if (i == 7) {
                    url = "https://api.azanpro.com/times/today.json?zone=prk02&format=12-hour";
                    jsonParse();
                }else if (i == 8) {
                    url = "https://api.azanpro.com/times/today.json?zone=pls01&format=12-hour";
                    jsonParse();
                }else if (i == 9) {
                    url = "https://api.azanpro.com/times/today.json?zone=png01&format=12-hour";
                    jsonParse();
                }else if (i == 10) {
                    url = "https://api.azanpro.com/times/today.json?zone=sbh07&format=12-hour";
                    jsonParse();
                }else if (i == 11) {
                    url = "https://api.azanpro.com/times/today.json?zone=swk08&format=12-hour";
                    jsonParse();
                }else if (i == 12) {
                    url = "https://api.azanpro.com/times/today.json?zone=sgr01&format=12-hour";
                    jsonParse();
                }else if (i == 13) {
                    url = "https://api.azanpro.com/times/today.json?zone=trg01&format=12-hour";
                    jsonParse();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void jsonParse() {

        mRequestQueue = Volley.newRequestQueue(this);
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jobj = response.getJSONObject("prayer_times");


                            String subuh = jobj.getString("subuh");
                            String zohor = jobj.getString("zohor");
                            String asar = jobj.getString("asar");
                            String maghrib = jobj.getString("maghrib");
                            String isyak = jobj.getString("isyak");

                            data.setText("Subuh: " + subuh + "\n\n" +
                                    "Zohor: " + zohor + "\n\n" +
                                    "Asar: " + asar + "\n\n" +
                                    "Maghrib: " + maghrib + "\n\n" +
                                    "Isyak: " + isyak + "\n\n" );

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

}
