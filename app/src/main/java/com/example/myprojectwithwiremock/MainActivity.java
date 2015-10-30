package com.example.myprojectwithwiremock;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tomakehurst.wiremock.client.WireMock;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class MainActivity extends Activity
{

    private static String TAG = "MainActivity";
    TextView returnTxtView;

    // Values for email and password at the time of the login attempt.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        returnTxtView = (TextView) findViewById(R.id.returnStringLbl);
        Button sendRequestBtn = (Button) findViewById(R.id.sendRequest);
        sendRequestBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                Log.d(TAG, "Inside sendRequst Button");
                SendRequestTask sendRequestTask = new SendRequestTask();
                sendRequestTask.execute((Void) null);


            } // end of onClick
        });

    } // end of OnCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    public class SendRequestTask extends AsyncTask<Void, Void, Boolean>
    {

        String returnText = "Nothing returned";

        @Override
        protected Boolean doInBackground(Void... params)
        {
            // TODO: attempt authentication against a network service.

            startServer();

            boolean status = false;
            try
            {


                Log.d(TAG, "Inside DoBackground");
                String host = "192.168.0.100";
                String path = "/seam/resource/rest/recipe/list";
                int port = 8080;
                URL url = new URL("http", host, port, path);
                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                conn.setAllowUserInteraction(true);
                conn.connect();

                StringBuilder sb = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;

                while ((line = in.readLine()) != null)
                {
                    sb.append(line);
                }
                Log.d(TAG, "After request");
                returnText = sb.toString();

                Log.d(TAG, "Returned String " + sb.toString());
                status = true;

                return status;

            }
            catch (Exception e)
            {

                Log.e(TAG, "Exception occurred " + e);
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success)
        {


            if (success)
            {
                //finish();

                returnTxtView.setText(returnText);
                Toast.makeText(getApplicationContext(), "Request Successful", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Request Unsuccessful. ", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled()
        {

        }
    }


    private static void startServer()
    {
        //final WireMockRule wireMockRule = new WireMockRule(8888);
        //WireMock wire = new WireMock("10.0.2.2", 8888) ;
        try
        {
            //	WireMockConfiguration wireMockConfig = new WireMockConfiguration() ;
            //	WireMockServer wireMockServer = new WireMockServer(wireMockConfig.port(8888)); //No-args constructor will start on port 8080, no HTTPS
            //	wireMockServer.start();
            WireMock.configureFor("192.168.0.100", 8080);
            stubFor(get(urlEqualTo("/seam/resource/rest/recipe/list"))
                    //.withHeader("Accept", equalTo("text/xml"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/xml")
                            .withBody("<response>This is mock response</response>")));


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }


}



