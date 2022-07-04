package com.zofa.mastermind;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HardwordsActivity extends AppCompatActivity {

    MaterialButton send,quit;
    RequestQueue requestQueue;
    TextView tv,tv2;
    EditText word;
    ProgressBar pb;


    private static final String url=Api.apiurl +"mastermind/api/game/testscores.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardwords);
        word = findViewById(R.id.testword);
        pb = findViewById(R.id.pbar1);
        pb.setProgress(0);

        SharedPreferences sp=getSharedPreferences("credentials",MODE_PRIVATE);
        if(sp.contains("uname"))
        {

        }else{
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }




        GetData();
        GetScore();

    }




    private void GetScore() {
        SharedPreferences sp = getSharedPreferences("credentials",MODE_PRIVATE);
        String email= sp.getString("uname","");
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                Api.apiurl +"mastermind/api/game/testscores.php?email2="+email, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                tv2 = findViewById(R.id.myscore);
                send =(MaterialButton) findViewById(R.id.send);


                try {
                    String a = response.getString("test");
                    tv2.setText("Your Score = "+a+" ");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error"+e, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void GetData() {

        CountDownTimer countDownTimer = new CountDownTimer(30000,300) {
            @Override
            public void onTick(long millisUntilFinished) {
                int current = pb.getProgress() + 1;
                if (current > pb.getMax()) current = 0;
                pb.setProgress(current);

            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Time Over", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),LevelsActivity.class));
            }
        }.start();


        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                Api.apiurl +"mastermind/api/game/hard.php", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                tv = findViewById(R.id.corword);
                send =(MaterialButton) findViewById(R.id.send);
                quit = (MaterialButton) findViewById(R.id.quit);


                try {
                    quit.setOnClickListener(view -> {
                        countDownTimer.cancel();
                        startActivity(new Intent(getApplicationContext(),LevelsActivity.class));
                        finish();
                    });

                    String a = response.getString("test");
                    String w = response.getString("word");
                    tv.setText(a);
                    send.setOnClickListener(view -> {
                        countDownTimer.cancel();
                        String dword = word.getText().toString();
                        if(dword.isEmpty()){
                            word.setError("Field Is Empty");
                            word.requestFocus();
                            return;
                        }
                        if(dword.toString().equals(w)){


                            class dbprocess extends AsyncTask<String,Void,String>
                            {
                                @Override
                                protected  void onPostExecute(String data)
                                {
                                    if(data.equals("found"))
                                    {
                                        Toast.makeText(getApplicationContext(), "15 points added", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                    }
                                }
                                @Override
                                protected String doInBackground(String... params)
                                {
                                    String furl=params[0];

                                    try
                                    {
                                        URL url=new URL(furl);
                                        HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));

                                        return br.readLine();

                                    }catch (Exception ex)
                                    {
                                        return ex.getMessage();
                                    }
                                }
                            }
                            SharedPreferences sp = getSharedPreferences("credentials",MODE_PRIVATE);
                            String email= sp.getString("uname","");
                            String qry="?level=3&type=1&email="+email;
                            dbprocess obj=new dbprocess();
                            obj.execute(url+qry);

                            startActivity(new Intent(getApplicationContext(),HardwordsActivity.class));
                        }else{
                            class dbprocess extends AsyncTask<String,Void,String>
                            {
                                @Override
                                protected  void onPostExecute(String data)
                                {
                                    if(data.equals("found"))
                                    {
                                        Toast.makeText(getApplicationContext(), "-15 points ", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                    }
                                }
                                @Override
                                protected String doInBackground(String... params)
                                {
                                    String furl=params[0];

                                    try
                                    {
                                        URL url=new URL(furl);
                                        HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));

                                        return br.readLine();

                                    }catch (Exception ex)
                                    {
                                        return ex.getMessage();
                                    }
                                }
                            }
                            SharedPreferences sp = getSharedPreferences("credentials",MODE_PRIVATE);
                            String email= sp.getString("uname","");
                            String qry="?level=3&type=0&email="+email;
                            dbprocess obj=new dbprocess();
                            obj.execute(url+qry);

                            startActivity(new Intent(getApplicationContext(),HardwordsActivity.class));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {

    }
}