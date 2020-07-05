package ca.nait.dmit2504.courseproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

class DbConnection extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = ".db";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_STOCKS = "stocks";
    private static final String COLUMN_STOCK_NAME = "stock_name";
    private static final String COLUMN_STOCK_CURRENT_PRICE = "current_price";
    private static final String COLUMN_STOCK_CLOSING_PRICE = "closing_price";

    public DbConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private List<String> baseStocks = new ArrayList<String>();

    @Override
    public void onCreate(final SQLiteDatabase db) {
        // execute SQL statements to create required database tables
        db.execSQL("CREATE TABLE " + TABLE_STOCKS
                + "(_id INTEGER PRIMARY KEY, "
                + COLUMN_STOCK_NAME + " TEXT, "
                + COLUMN_STOCK_CURRENT_PRICE + " REAL, "
                + COLUMN_STOCK_CLOSING_PRICE + " REAL);");

        baseStocks.add("AAPL");
        baseStocks.add("AMZN");
        baseStocks.add("TSLA");

        for (String stockName : baseStocks){
            ContentValues values = new ContentValues();
            values.put(COLUMN_STOCK_NAME, stockName);
            db.insert(TABLE_STOCKS, null, values);
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // SQL code to execute when database schema changes (database version)
        db.execSQL("DROP TABLE " + TABLE_STOCKS);
        onCreate(db);
    }


    public long addStock(String stockName) {
        // Get a writeable database
        SQLiteDatabase db = getWritableDatabase();
        // Create a ContentValue with the values to insert
        ContentValues values = new ContentValues();
        values.put(COLUMN_STOCK_NAME, stockName);
        // Call the insert() method
        return db.insert(TABLE_STOCKS, null, values);
    }

    public Cursor getAllStockNames() {
        // Create a readable database
        SQLiteDatabase db = getReadableDatabase();
        // Construct a SQL query statement
        String queryStatement = "SELECT _id, "
                + COLUMN_STOCK_NAME + ","
                + COLUMN_STOCK_CURRENT_PRICE
                + " FROM " + TABLE_STOCKS;
        // Execute the raw query
        return db.rawQuery(queryStatement, null);
    }

    public Cursor getStockWithPrices(String stock_name) {
        // Create a readable database
        SQLiteDatabase db = getReadableDatabase();
        // Construct a SQL query statement
        String queryStatement = "SELECT _id, "
                + COLUMN_STOCK_NAME + ", "
                + COLUMN_STOCK_CURRENT_PRICE + ", "
                + COLUMN_STOCK_CLOSING_PRICE
                + " FROM " + TABLE_STOCKS
                + " WHERE " + COLUMN_STOCK_NAME + " = '" + stock_name + "'";
        // Execute the raw query
        return db.rawQuery(queryStatement, null);
    }

    public String getStockByName(String stock_name) {
        // Create a readable database
        SQLiteDatabase db = getReadableDatabase();
        // Construct a SQL query statement
        String queryStatement = "SELECT _id "
                + " FROM " + TABLE_STOCKS
                + " WHERE " + COLUMN_STOCK_NAME + " = '" + stock_name + "'";
        // Execute the raw query
        Cursor cursor = db.rawQuery(queryStatement, null);
        if (cursor.moveToFirst()){
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            return id;
        }else {
            return "";
        }
    }

    public void deleteStock(String stock_name) {
        SQLiteDatabase db = getReadableDatabase();

        String queryStatement = "DELETE FROM " + TABLE_STOCKS
                + " WHERE " + COLUMN_STOCK_NAME + " = '" + stock_name + "'";
        db.execSQL( queryStatement );
    }

    public void addPrices (Float current_price, Float closing_price, String stock_name) {
        SQLiteDatabase db = getReadableDatabase();

        String queryStatement = "UPDATE " + TABLE_STOCKS
                + " SET " + COLUMN_STOCK_CURRENT_PRICE + " = " + current_price + ","
                + COLUMN_STOCK_CLOSING_PRICE + " = " + closing_price
                + " WHERE " + COLUMN_STOCK_NAME + " = '" + stock_name + "'";
        db.execSQL( queryStatement );
    }
}
