package com.example.nitin.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView txtClickme;
    private String strWebService ="https://api.dailyforex.com/2.16/Mobile/Content?AuthGUID=91670246-5cd0-49b0-b7ec-f28433c457ac&LanguageID=1&ItemType=12&PageNumber=1&CategoryID=0&Package=PageNumber";
    private DBHandler mDbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate: ");

        mDbHandler = new DBHandler(MainActivity.this);

        txtClickme=(TextView)findViewById(R.id.txtClickme);
        txtClickme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


             /*   Intent mIntent= new Intent(MainActivity.this,SecondActivity.class);
                startActivity(mIntent);
                finish();
*/
                new LongOperation().execute(strWebService);


            }
        });


    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart: ");
        super.onStart();

    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: " );
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop: " );
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.e(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: " );
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    private class LongOperation extends AsyncTask<String, Void, Void>{
        private final HttpClient Client = new DefaultHttpClient();
        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
        private String Content;
        private String Error = null;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Dialog.setMessage("Please wait..");
            Dialog.show();
            Log.e(TAG, "onPreExecute: ");
        }

        @Override
        protected Void doInBackground(String... params) {

            Log.e(TAG, "doInBackground: ");

            BufferedReader reader=null;

            //call post data
           /* try
            {
                // Defined URL  where to send data
                URL url = new URL(params[0]);

                // Send POST data request
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( strWebService );
                wr.flush();

                // Get the server response
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line + " ");
                }

                // Append Server Response To Content String
                Content = sb.toString();
            }
            catch(Exception ex)
            {
                Error = ex.getMessage();
            }
            finally
            {
                try
                {

                    reader.close();
                }

                catch(Exception ex) {}
            }
*/

            /*****************************************************/

            //-------(Call Get web service)--------//

            try
            {
                // Defined URL  where to send data
                URL url = new URL(params[0]);

                // Send POST data request
                HttpGet request = new HttpGet();
                request.setURI(URI.create(strWebService));
                HttpResponse response = Client.execute(request);
                response.getStatusLine().getStatusCode();

                // Get the server response
                reader =  new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                String nl = System.getProperty("line.separator");
                while ((line = reader.readLine()) != null) {
                    sb.append(line + nl);
                }

                // Append Server Response To Content String
                Content = sb.toString();
            }
            catch(Exception ex)
            {
                Error = ex.getMessage();
            }
            finally
            {
                try
                {

                    reader.close();
                }

                catch(Exception ex) {}
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Dialog.dismiss();


            String OutputData = "";
            JSONObject jsonResponse;
            if (Error != null) {

                Toast.makeText(MainActivity.this, "Error occurd", Toast.LENGTH_SHORT).show();
            } else {

                try {

                    /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                    jsonResponse = new JSONObject(Content);

                    /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                    /*******  Returns null otherwise.  *******/
                    JSONArray jsonMainNode = jsonResponse.optJSONArray("Content");

                    /*********** Process each JSON Node ************/

                    int lengthJsonArr = jsonMainNode.length();

                    for (int i = 0; i < lengthJsonArr; i++) {
                        /****** Get Object for each JSON node.***********/
                        JSONObject jsonChildNode = null;
                        try {
                            jsonChildNode = jsonMainNode.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        /******* Fetch node values **********/
                        String name = jsonChildNode.optString("Title").toString();
                        String number = jsonChildNode.optString("IsPremium").toString();
                        String date_added = jsonChildNode.optString("ExpireDate").toString();

                        Log.e(TAG, "onPostExecute: "+name +"  number  " +number +"  date_added  "+date_added );


                        mDbHandler.addShop(name ,number);




                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

                List<Shop> shops = mDbHandler.getAllShops();

                for (Shop shop : shops) {
                    String log = "Id: " + shop.getId() + " ,Name: " + shop.getName() + " ,Address: " + shop.getAddress();
                    // Writing shops  to log
                    Log.d("Shop Detailssss: : ", log);
                }

            }
        }
    }
}
