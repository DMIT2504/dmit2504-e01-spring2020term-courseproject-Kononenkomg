package ca.nait.dmit2504.courseproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DbConnection extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = ".db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_STOCKS = "stocks";
    private static final String COLUMN_STOCK_NAME = "stock_name";
    private static final String COLUMN_DATE = "date";

    public DbConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        // execute SQL statements to create required database tables
        db.execSQL("CREATE TABLE " + TABLE_STOCKS
                + "(_id INTEGER PRIMARY KEY, "
                + COLUMN_STOCK_NAME + " TEXT, "
                + COLUMN_DATE + " TEXT);");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // SQL code to execute when database schema changes (database version)
        db.execSQL("DROP TABLE " + TABLE_STOCKS);
        onCreate(db);
    }

}
