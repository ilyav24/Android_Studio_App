package com.example.CalorieCounter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;


import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;



import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {


    class Model{
        private static final String databaseName = "CalorieCounter";
        private static final int databaseVersion = 8;



        private Model.DatabaseHelper DBHelper;
        private SQLiteDatabase db;


        public Model(){

            DBHelper = new Model.DatabaseHelper(MainActivity.this);

        }

        private class DatabaseHelper extends SQLiteOpenHelper {
            DatabaseHelper(Context context){
                super(context,databaseName,null,databaseVersion);
            }

            @Override
            public void onCreate(SQLiteDatabase db){


                try{
                    db.execSQL("CREATE TABLE IF NOT EXISTS food (" +
                            " food_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            " foodId VARCHAR, " +
                            " food_name VARCHAR, " +
                            " food_calories VARCHAR);");

                }
                catch (SQLException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){
                db.execSQL("DROP TABLE IF EXISTS food ");



                onCreate(db);

                String TAG = "Tag";
                Log.w(TAG, "Upgrading database from version " + oldVersion + "to " + newVersion + ", which will destroy all data");
            }


        }
        public Model open() throws SQLException{
            db = DBHelper.getWritableDatabase();
            return this;
        }

        public void close(){
            DBHelper.close();
        }

        public void insert(String table, String fields, String values){
            db.execSQL("INSERT INTO " + table + "(" + fields + ") VALUES (" + values + ");");
        }
        public int get(String foodId ,String table, String fields){
            Cursor cursor = db.rawQuery("SELECT SUM(" + fields +") "
                    + "FROM " +table +
                    " GROUP BY foodId " +
                    " HAVING foodId = '"+ foodId+"';",null);
            if(cursor.moveToFirst()){
                return cursor.getInt(0);
            }
            return 0;
        }




    }

    class ViewModel{

        WebView webView;
        Model model;


        public ViewModel(WebView webView, Model model) {
            this.webView = webView;
            this.model = model;

        }

        @android.webkit.JavascriptInterface
        public void inputData(String foodId ,String foodNameInput,String calAmountInput){

            Log.d("myMsg","foodId: "+foodId + "food name: " + foodNameInput + ", amount in calories: " + calAmountInput);

            model.open();

            //String foodNameInput = foodName.getText().toString();
            //String calAmountInput = calAmount.getText().toString();


            model.insert("food", "food_id, foodId, food_name, food_calories", "NULL, "+"'"+ foodId +"', '" + foodNameInput + "', '" + calAmountInput + "'");

            //Toast.makeText(MainActivity.this, "Food added to the list", Toast.LENGTH_LONG).show();

            //Toast.makeText(MainActivity.this, "There are " + numberRos + "rows in table", Toast.LENGTH_LONG).show();
            model.close();
            Log.d("food table","food added to the list");

        }
        @android.webkit.JavascriptInterface
        public int getAmount(String foodId){
            model.open();
            int sum = model.get(foodId,"food","food_calories");
            model.close();
            return sum;

        }


    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initializeWithDefaults(this);

        new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();
        //ViewModel vm = new ViewModel();
        final WebView webView = new WebView(this);

        final Model model = new Model();
        final ViewModel vm = new ViewModel(webView,model);


        webView.getSettings().setJavaScriptEnabled(true);



        //webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(vm,"vm");
        webView.loadUrl("file:///android_asset/home.html");

        setContentView(webView);









        /*final DBAdapter db = new DBAdapter(this);

        final EditText foodName = (EditText)findViewById(R.id.foodNameET);
        final EditText calAmount = (EditText)findViewById(R.id.amountCalET);
        //final int numberRos = db.count("food");

        Toast.makeText(MainActivity.this,"Database works, food created",Toast.LENGTH_LONG).show();

        Button addButton = (Button)findViewById(R.id.addBtn);
        addButton.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                db.open();

                String foodNameInput = foodName.getText().toString();
                String calAmountInput = calAmount.getText().toString();


                db.insert("food", "food_id, food_name, food_calories", "NULL, '" + foodNameInput + "', '" + calAmountInput + "'");

                Toast.makeText(MainActivity.this, "Food added to the list", Toast.LENGTH_LONG).show();

                //Toast.makeText(MainActivity.this, "There are " + numberRos + "rows in table", Toast.LENGTH_LONG).show();
                db.close();



            }
        });






        //


        //                MainActivity.this.startActivity(intent);


        //            }


        //        });*/


    }

}

