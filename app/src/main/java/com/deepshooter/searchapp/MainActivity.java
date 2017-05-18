package com.deepshooter.searchapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    RecyclerView mListRecyclerView;

    RecyclerViewListAdapter recyclerViewListAdapter ;
    EditText edit_query ;
    TextView textView ;

    ArrayList<CountryBean> countryBeanArrayList = new ArrayList<>();
    ArrayList<StateBean> stateBeanArrayList = new ArrayList<>();
    ArrayList<MainBean> mainBeanArrayList = new ArrayList<>();
    ProgressDialog  progressDialog ;
    enum ApiType { country , state } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeView();

    }



    private void initializeView()
    {

        mListRecyclerView = (RecyclerView) findViewById(R.id.vR_recyclerViewList);
        edit_query = (EditText) findViewById(R.id.edit_query);
        textView = (TextView) findViewById(R.id.text);

        setValues();
    }


    private void setValues()
    {

         progressDialog = new ProgressDialog(this);
         progressDialog.setMessage("Loading");
         progressDialog.setCancelable(false);
         progressDialog.setCanceledOnTouchOutside(false);
         progressDialog.show();

         Thread thread = new Thread(countryRun);
         thread.start();

        recyclerViewListAdapter = new RecyclerViewListAdapter(MainActivity.this, mainBeanArrayList , textView);
        mListRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mListRecyclerView.setHasFixedSize(false);

        mListRecyclerView.setAdapter(recyclerViewListAdapter);


        edit_query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                recyclerViewListAdapter.filter(editable.toString());
            }
        });
    }



     private  void onFinished(ApiType  type)
     {
         if(type == ApiType.country)
         {
             Thread thread = new Thread(stateRun);
             thread.start();
         }else if(type == ApiType.state)
         {

             for(int i= 0 ; i< countryBeanArrayList.size() ; i++)
             {

                 MainBean mainBean = new MainBean();
                 mainBean.setId(countryBeanArrayList.get(i).getId());
                 mainBean.setName(countryBeanArrayList.get(i).getCountryName());
                 mainBeanArrayList.add(mainBean);
             }


             for(int i= 0 ; i< stateBeanArrayList.size() ; i++)
             {

                 MainBean mainBean = new MainBean();
                 mainBean.setId(stateBeanArrayList.get(i).getStateId());
                 mainBean.setName(stateBeanArrayList.get(i).getStateName());
                 mainBeanArrayList.add(mainBean);
             }



             recyclerViewListAdapter.notifyDataSetChanged();


              if(progressDialog.isShowing())
              {
                  progressDialog.cancel();
              }
         }

     }
    private Runnable countryRun = new Runnable() {

        String countryURL = "http://52.172.204.117/WebFactory/Geolocation/GetAllCountry";

        @Override
        public void run() {


            String result =  queryRESTurlConnection(countryURL);
            countryBeanArrayList =  parseCountryList(result);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    onFinished(ApiType.country);
                }
            });


        }
    };

    private Runnable stateRun = new Runnable() {

        String stateURL = "http://52.172.204.117/WebFactory/State/GetAllStates";

        @Override
        public void run() {

            String result =  queryRESTurlConnection(stateURL);
            stateBeanArrayList =  parseStateList(result);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    onFinished(ApiType.state);
                }
            });

        }
    };


    public String queryRESTurlConnection(String url) {

        String finalResult = null;
        URL urlMain = null;
        try {
            urlMain=new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection connection= null;
            if (urlMain != null) {
                connection = (HttpURLConnection) urlMain.openConnection();
            }
            connection.connect();
            int lenghtOfFile = connection.getContentLength();
            Log.e("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
            InputStream i= null;
            if (connection != null) {
                i = connection.getInputStream();
            }
            InputStreamReader ir= null;
            if (i != null) {
                ir = new InputStreamReader(i);
            }
            BufferedReader bufferedReader= null;
            if (ir != null) {
                bufferedReader = new BufferedReader(ir);
            }
            String string = null;
            if (bufferedReader != null) {
                string = bufferedReader.readLine();
            }
            StringBuilder stringBuilder=new StringBuilder();
            while (string!=null){
                stringBuilder.append(string);
                string=bufferedReader.readLine();
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalResult;
    }



    //get state
    public ArrayList<StateBean> parseStateList(String JsonResult) {

        ArrayList<StateBean> list = new ArrayList<StateBean>();

        try {

            JSONArray jsonArray = new JSONArray(JsonResult);


            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                StateBean stateList = new StateBean();
                stateList.setStateId(jsonObject.getInt("Id"));
                stateList.setStateName(jsonObject.getString("StateName"));
                list.add(stateList);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }


        return list;


    }



    //get  Country
    public ArrayList<CountryBean> parseCountryList(String JsonResult) {

        ArrayList<CountryBean> list = new ArrayList<CountryBean>();

        try {

            JSONArray jsonArray = new JSONArray(JsonResult);


            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                CountryBean countryList = new CountryBean();
                countryList.setId(jsonObject.getInt("Id"));
                countryList.setCountryName(jsonObject.getString("Name"));

                list.add(countryList);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        return list;

    }
}
