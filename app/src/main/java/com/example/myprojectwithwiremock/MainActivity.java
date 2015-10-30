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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity
{
    private static String TAG = "MainActivity";
    private TextView returnTxtView;

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

    }

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
}



