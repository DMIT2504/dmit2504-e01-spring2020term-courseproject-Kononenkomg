package ca.nait.dmit2504.courseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

    private DbConnection mDbConnection;
    private ListView mStocksList;
    private Float currentPrice;
    private Float closingPrice;
    private String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbConnection = new DbConnection(this);
        mStocksList = findViewById(R.id.activity_main_listview);

    }


    @Override
    protected void onResume() {
        super.onResume();


        rebindListView();
    }

    public void addStock(View v){
        Intent addDeleteStocks = new Intent(this, AddDeleteStocks.class);
        startActivity(addDeleteStocks);
    }

    private void rebindListView() {
        Cursor dbCursor = mDbConnection.getAllStockNames();


            while (dbCursor.moveToNext()) {
                String stockName = dbCursor.getString(dbCursor.getColumnIndex("stock_name"));

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://finnhub.io")
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
                Connector connector = retrofit.create(Connector.class);

                Call<String> getCall = connector.StockMetrics("api/v1/quote?symbol=" + stockName + "&token=brvbfevrh5r9k3fgus3g");
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
                            currentPrice = Float.parseFloat(jsonObjet.getString("c"));
                            closingPrice = Float.parseFloat(jsonObjet.getString("pc"));
                            mDbConnection.addPrices(currentPrice, closingPrice, stockName);

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

        Cursor cursor = mDbConnection.getAllStockNames();
//                            Float test = cursor.getFloat(cursor.getColumnIndex("current_price"));
        // Define an array of columns names used by the cursor
        String[] fromFields = {"stock_name", "current_price"};
        // Define an array of resource ids in the listview item layout
        int[] toViews = new int[] {
                R.id.display_stock_name,
                R.id.display_stock_price
        };
        // Create a SimpleCursorAdapter for the ListView
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.display_data_custom_layout,
                cursor,
                fromFields,
                toViews);
        mStocksList.setAdapter(cursorAdapter);

    }

    public void onLoad(View v) {
        rebindListView();
    }

}