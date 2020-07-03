package ca.nait.dmit2504.courseproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;



import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }


    @Override
    protected void onResume() {
        super.onResume();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://finnhub.io")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Connector connector = retrofit.create(Connector.class);

        Call<String> getCall = connector.listOfArchivedItems("/api/v1/stock/metric?symbol=AMZN&metric=all&token=brvbfevrh5r9k3fgus3g");
        getCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(final Call<String> call, final Response<String> response) {
                String responseBody = response.body();
                JSONObject jsonObjet = null;
                try {
                    jsonObjet = new JSONObject(responseBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String name = jsonObjet.getString("symbol");
                    String stop = "stop";
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(final Call<String> call, final Throwable t) {
                Toast.makeText(getApplicationContext(), "Fetch reviews was not successful.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addStock(View v){
        Intent addDeleteStocks = new Intent(this, AddDeleteStocks.class);
        startActivity(addDeleteStocks);
    }
}