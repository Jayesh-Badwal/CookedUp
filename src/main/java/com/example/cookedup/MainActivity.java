package com.example.cookedup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SearchView search;
    ListView listAllFood;
    ArrayList<String> onlyNames=new ArrayList<>();
    int maincount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.searchView);
        listAllFood = findViewById(R.id.allFood);

        char c = 'a';
        for(int i = 0; i <= 25; i++) {
            if(c == 'q' || c == 'u' || c == 'x' || c == 'z') {
                c++;
                continue;
            }
            loadData(String.valueOf(c));
            c++;
        }
    }

    void loadData(String c) {
        maincount++;
        Request req = new Request.Builder().url("https://www.themealdb.com/api/json/v1/1/search.php?f=" + c).get().build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Error in request", Toast.LENGTH_SHORT).show();
                }
                String respFromAPI = response.body().string();
                Log.d("RawData", respFromAPI);
                try {
                    extractData(respFromAPI);
                } catch (Exception e) {
                    Log.d("Error", "Not in json format");
                }
            }
        });
    }

    void extractData(String respFromAPI) throws Exception{
        Log.d("Mydata", respFromAPI);
        JSONObject obj = new JSONObject(respFromAPI);
        JSONArray array = obj.getJSONArray("meals");
        for(int  i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            String name = jsonObject.getString("strMeal");
            Log.d("AllFood", name);
            onlyNames.add(name);
        }
        if(maincount == 22) {
            Asynchronous asynk  = new Asynchronous();
            asynk.execute();
        }
    }


    private class Asynchronous extends AsyncTask<String ,Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            return onlyNames;
        }
        @Override
        protected void onPostExecute(ArrayList<String> foodClass) {
            final FoodArrayAdapter foodAdapter = new FoodArrayAdapter(MainActivity.this, foodClass);
            listAllFood.setAdapter(foodAdapter);
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    foodAdapter.getFilter().filter(s);
                    return false;
                }
            });
            listAllFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String food = (String) foodAdapter.getItem(i);
                    Log.d("SelectedFood", food);
                    Intent intent = new Intent(MainActivity.this , FoodData.class);
                    intent.putExtra("name", food);
                    startActivity(intent);
                }
            });

        }
    }
}