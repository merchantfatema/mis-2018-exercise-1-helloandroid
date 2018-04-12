/*
* Name: Assignment No.1
* Created Date: 09 April, 2018
* Purpose: To create an Android HTTP Client App
* Student Name: Fatema Merchant
* Student Id: 119431
* */
package com.mis.ass1.assignment1;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * HTTP Client App
 * Contains the following:
 * 1. A editText for entering Target URL
 * 2. A Button on which action is called
 * 3. Web View to show the website
 * 4. TextView to show the raw data
*/
public class HttpClientApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_client_app);
    }

    /*
    @Purpose: Method called on click of submit button
    */
    public void submitRequest( View view) {

        EditText targetUrl;
        targetUrl = (EditText)findViewById(R.id.searchText);
        String urlStr = targetUrl.getText().toString();

        if( urlStr.equals("") || urlStr.equals(null)){
            showToastMessage("Plese enter a valid search URL");
        }else{
            ExecuteAsyncTask executeTask = new ExecuteAsyncTask();
            executeTask.execute(new String[]{urlStr});
        }
    }

    /*
    @Purpose: Method to show toas message
    @Param: Message to be displayed in toast
    @Return: -
    */
    public void showToastMessage(String message){
        Toast toast = Toast.makeText(HttpClientApp.this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0 ,0);
        toast.show();
    }

    /*
        Class for making a callout to fetch the data asynchronously and showing the result
     */
    public class ExecuteAsyncTask extends AsyncTask<String, String, String> {

        ProgressDialog showProgress;
        Boolean isSucess;
        String mimeType;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress = new ProgressDialog(HttpClientApp.this);
            showProgress.setMessage("Processing");
            showProgress.setCancelable(false);
            showProgress.show();
        }

        /*
            @Purpose: to make the server side request in bakground
            @Param: Target URL as String
            @Return: response from server
            @Other variabels updated:
                1. isSucess - indicates whether the response was sucessfully received
                2. mimeType - stores the mime type of response
        */
        @Override
        protected String doInBackground(String... params) {

                URL url;
                HttpURLConnection urlConnection = null;
                StringBuffer response = new StringBuffer();
                String inputLine,strResponse;
                try {
                    url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    BufferedReader bReader = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));

                    while ((inputLine = bReader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    bReader.close();
                    mimeType = urlConnection.getContentType();
                    strResponse = response.toString();
                    isSucess = true;

                    return strResponse;

                } catch (Exception e) {
                    e.printStackTrace();
                    isSucess = false;
                    return e.getMessage();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
        }
        /*
            @Purpose: Called after completion of execution of doInBackground Method.
            It is used to show result in the UI Thread.
            @Param: response in string format from doInBackground method
        */
        @Override
        protected void onPostExecute( String responseStr ){

            View view = getWindow().getDecorView();
            if (view != null) {
                InputMethodManager iMManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                iMManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            WebView resultView = (WebView) findViewById(R.id.resultView);
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setMovementMethod(new ScrollingMovementMethod());

            if( isSucess ){
                // if request to server ws sucessfully completed & we have received a response
                String data = responseStr;
                if(mimeType.contains("text/html")){

                    //If mimeType is text/html then loadData in WebView & set textView with the raw data
                    resultView.loadData(data, "text/html", null);
                    textView.setText(data);
                }
                else{
                    //If mimeType is not text/html then show a toast message
                    showToastMessage("Sorry!!! We are only showing response with text/html contentType");
                    resultView.loadData("", "text/html", null);
                    textView.setText("");
                }
            }
            else{
                //If there were some exception in sending request or reciving response from server
                // then show the exception in toast message
                showToastMessage(responseStr);
                resultView.loadData("", "text/html", null);
                textView.setText("");
            }
            showProgress.dismiss();
        }
    }    
}
