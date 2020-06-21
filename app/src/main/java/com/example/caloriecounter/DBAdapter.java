package com.example.caloriecounter;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

public class DBAdapter {
    /* 01 Variables --------------------------- */
    private static final String databaseName = "caloriecounter";
    private static final int databaseVersion =14;

    /* 02 Database variables ------------------ */
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;


    /* Class DbAdapter ------------------------ */
    public DBAdapter(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    /* 04 DatabaseHelper ---------------------- */
    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){

            super(context, databaseName,null,databaseVersion);
        }



        @Override
        public void onCreate(SQLiteDatabase db){
            try{
                //Create tables
                db.execSQL("CREATE TABLE IF NOT EXISTS food (" +
                        "food_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "food_name VARCHAR," +
                        "food_calories VARCHAR) ;");
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


            // ! All tables that are going to be dropped need to be listed here
            db.execSQL("DROP TABLE IF EXISTS food " );
            onCreate(db);

            String TAG = "Tag";
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

        }
    }



    /* 05 open database -------------------------- */
    public DBAdapter open () throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    /* 06 close database -------------------------- */
    public void close() {
        DBHelper.close();
    };

    /* 07 Insert data ----------------------------- */
    public void insert(String table, String fields, String values){
        db.execSQL("INSERT INTO " + table + " ("+ fields +") VALUES ("+ values +")");
    }

}
