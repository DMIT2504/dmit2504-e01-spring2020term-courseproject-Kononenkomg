package ca.nait.dmit2504.courseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private DbConnection mDbConnection;
    private ListView mStocksList;
    private Float currentPrice;
    private Float closingPrice;
    private Drawable arrowUp;
    private Drawable arrowDown;
    private String stock;
    TextView head;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbConnection = new DbConnection(this);
        mStocksList = findViewById(R.id.activity_main_listview);
        arrowUp = ContextCompat.getDrawable(this, R.drawable.ic_baseline_keyboard_arrow_up_24);
        arrowUp.setTint(getResources().getColor(R.color.colorGreen));
        arrowDown = ContextCompat.getDrawable(this, R.drawable.ic_baseline_keyboard_arrow_down_24);
        arrowDown.setTint(getResources().getColor(R.color.colorRed));
        head = findViewById(R.id.head_text);

        mStocksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long longId) {
                TextView stockName = (TextView) view.findViewById(R.id.display_stock_name);
                stock = stockName.getText().toString();
                Intent stockDetails = new Intent(MainActivity.this, StockDetails.class);
                stockDetails.putExtra("stock_name", stock);
                startActivity(stockDetails);
            }
        });

        setGame2048TitleStateListAnimator();
        //initNewButtonOnTouchAnim();
        head.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofInt(head,"textColor",
                        Color.parseColor("#776e65"),
                        Color.parseColor("#2196f3"),
                        Color.parseColor("#ffea00"),
                        Color.parseColor("#776e65"));
                objectAnimator.setDuration(5000);
                objectAnimator.setEvaluator(new ArgbEvaluator());
                objectAnimator.start();
                return false;
            }
        });

        mediaPlayer = MediaPlayer.create(this,R.raw.background_music);
        mediaPlayer.start();
    }


    @Override
    protected void onResume() {
        super.onResume();


        rebindStockPrices();

    }


    public void addStock(View v){
        Intent addDeleteStocks = new Intent(this, AddDeleteStocks.class);
        startActivity(addDeleteStocks);
    }

    public void rebindStockPrices() {
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
                            for (int i = 0; i < mStocksList.getCount(); i++) {
                                View v = getViewByPosition(i, mStocksList);
                                TextView stock = v.findViewById(R.id.display_stock_name);
                                TextView price = v.findViewById(R.id.display_stock_price);
                                TextView arrow = v.findViewById(R.id.display_arrow);

                                String name = stock.getText().toString();
                                Cursor cursorWithPrices = mDbConnection.getStockWithPrices(name);
                                if (cursorWithPrices.moveToFirst()){
                                    Float current = cursorWithPrices.getFloat(2);
                                    Float closing = cursorWithPrices.getFloat(3);
                                    if (closing > current){
                                        arrow.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                                        price.setTextColor(getResources().getColor(R.color.colorRed));
                                    }else{
                                        arrow.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                                        price.setTextColor(getResources().getColor(R.color.colorGreen));
                                    }
                                }
                            }

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

        Cursor cursor = mDbConnection.getAllStockNames();
        String[] fromFields = {"stock_name", "current_price"};
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



    private void setGame2048TitleStateListAnimator(){
        StateListAnimator stateListAnimator = AnimatorInflater.loadStateListAnimator(this,R.animator.game2048_game_title_state_change);
        head.setStateListAnimator(stateListAnimator);
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

}