package com.example.cookedup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import static android.graphics.Color.BLACK;

public class FoodData extends AppCompatActivity {

    String name;
    ImageView image;
    TextView head;
    TextView category;
    TextView area;
    TextView ingredient;
    TextView recipe;
    TextView link;

    String eimage, ecat, earea, eingred, erecipe, elink, emeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_data);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        head = findViewById(R.id.foodName);
        head.setText(name);

        loadData();
    }

    void loadData() {
        Request req = new Request.Builder().url("https://www.themealdb.com/api/json/v1/1/search.php?s=" + name).get().build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(FoodData.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(!response.isSuccessful()) {
                    Toast.makeText(FoodData.this, "Error in request", Toast.LENGTH_SHORT).show();
                }
                String respFromApi = response.body().string();
                Log.d("Data", respFromApi);
                try {
                    extractData(respFromApi);
                } catch (Exception e) {
                    Log.d("Error", "Not in json format");
                }
            }
        });
    }

    void extractData(String respFromApi) throws Exception{
        Log.d("in", "success");
        JSONObject initial = new JSONObject(respFromApi);
        JSONArray arr = initial.getJSONArray("meals");
        JSONObject eFood = arr.getJSONObject(0);
        if(eFood.has("strCategory"))
            ecat = eFood.getString("strCategory");
        else
            ecat = "---";
        if(eFood.has("strArea"))
            earea = eFood.getString("strArea");
        else
            earea = "---";
        if(eFood.has("strMealThumb"))
            eimage = eFood.getString("strMealThumb");
        if(eFood.has("strYoutube"))
            elink = eFood.getString("strYoutube");
        else
            elink = "---";
        if(eFood.has("strInstructions"))
            erecipe = eFood.getString("strInstructions");
        else
            erecipe = "---";
        if(eFood.has("strIngredient1")) {
            String in = "";
            String item, value;
            for(int i = 0; i < 20; i++) {
                item = eFood.getString("strIngredient" + String.valueOf(i+1));
                value = eFood.getString("strMeasure" + String.valueOf(i+1));
                if(item == null || item.length()==0)
                    break;
                in = in.concat(item + " " + value + "\n");

            }
            eingred = in;
        } else
            eingred = "---";

        Asynchronous asynk  = new Asynchronous();
        asynk.execute();
    }

    private class Asynchronous extends AsyncTask<String ,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            return "";
        }
        @Override
        protected void onPostExecute(String None) {
            Log.d("Inside","in");
            image = findViewById(R.id.image);
            Picasso.get().load(eimage).into(image);

            category = findViewById(R.id.category);
            category.setText(ecat);

            area = findViewById(R.id.area);
            area.setText(earea);

            link = findViewById(R.id.link);
            link.setText(elink);

            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(elink == "---" || elink.length() == 0) {
                        link.setTextColor(BLACK);
                        link.setText("Video tutorial not available");
                    } else {
                        Uri uri = Uri.parse(elink);
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                }
            });

            ingredient = findViewById(R.id.ingredients);
            ingredient.setText(eingred);

            recipe = findViewById(R.id.recipe);
            recipe.setText(erecipe);

        }
    }
}