package ca.nait.dmit2504.courseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddDeleteStocks extends AppCompatActivity {

    private DbConnection addDbConnection;
    private ListView addStockNamesList;
    private EditText addEneterStockName;
    private String metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delete_stocks);

        addDbConnection = new DbConnection(this);

        addStockNamesList = findViewById(R.id.add_delete_activity_listview);
        addEneterStockName = findViewById(R.id.add_delete_activity_edittext);

    }

    @Override
    protected void onResume() {
        super.onResume();

        rebindListView();
    }

    private void rebindListView() {
        Cursor dbCursor = addDbConnection.getAllStockNames();
        
        // Define an array of columns names used by the cursor
        String[] fromFields = {"stock_name"};
        // Define an array of resource ids in the listview item layout
        int[] toViews = new int[] {
                R.id.add_delete_custom_stock_name
        };
        // Create a SimpleCursorAdapter for the ListView
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.add_delete_custom_layout,
                dbCursor,
                fromFields,
                toViews);
        addStockNamesList.setAdapter(cursorAdapter);
    }

    public void addStockToDb (View v) {
        String stockName = addEneterStockName.getText().toString().toUpperCase();
        if (stockName.isEmpty()) {
            Toast.makeText(this, "The stock name can not be empty", Toast.LENGTH_LONG).show();
            return;
        }
//        Cursor cursor = addDbConnection.getStockByName(stockName);
//        String id = null;
//        if (cursor.moveToFirst()){
//            id = cursor.getString(cursor.getColumnIndex("_id"));
//        }
//        if (id.toString().isEmpty() || id == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://finnhub.io")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            Connector connector = retrofit.create(Connector.class);

            Call<String> getCall = connector.listOfArchivedItems("/api/v1/stock/metric?symbol=" + stockName + "&metric=all&token=brvbfevrh5r9k3fgus3g");
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
                        metrics = jsonObjet.getString("metric");
                        String stop = "stop";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (metrics == null || metrics.isEmpty() || metrics.equals("{}")) {
                        Toast.makeText(AddDeleteStocks.this, "This stock name does not exist", Toast.LENGTH_LONG).show();
                    } else {
                        addDbConnection.addStock(stockName);
                        rebindListView();
                        addEneterStockName.setText("");
                    }

                }

                @Override
                public void onFailure(final Call<String> call, final Throwable t) {
                    Toast.makeText(getApplicationContext(), "Fetch reviews was not successful.", Toast.LENGTH_SHORT).show();
                }
            });
//        }
    }
}