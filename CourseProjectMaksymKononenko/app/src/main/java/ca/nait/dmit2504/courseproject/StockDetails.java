package ca.nait.dmit2504.courseproject;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StockDetails extends AppCompatActivity {

    private String stockName;
    private String country;
    TextView tCountry;
    TextView tExchange;
    TextView tIndustry;
    TextView tName;
    TextView tCurrency;
    TextView tUrl;
    TextView tStockName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                stockName= null;
            } else {
                stockName= extras.getString("stock_name");
            }
        } else {
            stockName= (String) savedInstanceState.getSerializable("stock_name");
        }
        tStockName = findViewById(R.id.display_stock);
        tCountry = findViewById(R.id.details_country);
        tExchange = findViewById(R.id.display_exchange);
        tIndustry = findViewById(R.id.display_industry);
        tName = findViewById(R.id.display_name);
        tCurrency = findViewById(R.id.display_currency);
        tUrl = findViewById(R.id.display_url);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://finnhub.io")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Connector connector = retrofit.create(Connector.class);

        Call<String> getCall = connector.StockMetrics("api/v1/stock/profile2?symbol=" + stockName + "&token=brvbfevrh5r9k3fgus3g");
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
                    tStockName.setText(stockName);
                    tCountry.setText(jsonObjet.getString("country"));
                    tExchange.setText(jsonObjet.getString("exchange"));
                    tIndustry.setText(jsonObjet.getString("finnhubIndustry"));
                    tName.setText(jsonObjet.getString("name"));
                    tCurrency.setText(jsonObjet.getString("currency"));
                    tUrl.setText(jsonObjet.getString("weburl"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(final Call<String> call, final Throwable t) {
                Toast.makeText(getApplicationContext(), "Fetch stocks was not successful.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}