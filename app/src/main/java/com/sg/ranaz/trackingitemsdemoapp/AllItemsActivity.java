/*
 * Author: Rana
 * email id: ranakrishnapaul@gmail.com
 * skype id: rana.krishna.paul
 * file name: AllItemsActivity.java
 * This class is used for displaying all stored items from DB into the RecyclerView
 *
 *  */


package com.sg.ranaz.trackingitemsdemoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AllItemsActivity extends AppCompatActivity {

    //Variables, Views, Objects Declaration
    private List<ItemModel> itemsList;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;
    private ProgressBar progressBar;

    private DatabasehelperForItems dbhelperForItems;
    private DBManipulationForItems dbManipulationObjForItems;
    private Cursor cur;
    private int total;

    private AlertDialog.Builder ald;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);

        //Setting up the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Creating an Image Directory for storing Items' Pictures
        createImageDirectory();

        //DataBase creation & Manipulation Objects
        dbhelperForItems = new DatabasehelperForItems(this);
        dbManipulationObjForItems = new DBManipulationForItems();

        //AlertDialog.Builder Object
        ald = new AlertDialog.Builder(AllItemsActivity.this);

        //Initialize Recycler View
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_viewID);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //ProgressBar Object
        progressBar = (ProgressBar) findViewById(R.id.progress_barID);
        progressBar.setVisibility(View.VISIBLE);

        //Manipulation of Database via an AsyncTask
        new DataBaseAsyncTask().execute();


        //Add a 'new Item' Floating Button logic
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AllItemsActivity.this,DataEntryActivity.class);
                startActivity(i);

            }
        });

    }

    //Manipulation of Database via the AsyncTask
    public class DataBaseAsyncTask extends AsyncTask<Void,Void,String>{

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected String doInBackground(Void... v){

            String status = "Failed";

            try {

                  cur = dbManipulationObjForItems.getRecordsForItems(dbhelperForItems);
                  cur.moveToFirst();
                  total = cur.getCount();
                  if(total!=0){
                      processCursor(cur);
                      //Here comes the code for "Retrieving the data into the Cloud"
                      status = "Success";
                  }
             }
            catch (Exception e){
                e.printStackTrace();
            }

            return status;
        }

        @Override
        protected void onPostExecute(String status) {
            //Update UI
            progressBar.setVisibility(View.GONE);

            if (status.equalsIgnoreCase("Success")) {
                adapter = new MyRecyclerAdapter(AllItemsActivity.this, itemsList);
                mRecyclerView.setAdapter(adapter);

            } else {
                Toast.makeText(AllItemsActivity.this, "No Record Found!", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void processCursor(Cursor cur) {

        itemsList = new ArrayList<ItemModel>();

        do {

            System.out.println("-------------------------------------------");
            String name = cur.getString(cur.getColumnIndex(DatabasehelperForItems.ITEM_NAME));
            String image = cur.getString(cur.getColumnIndex(DatabasehelperForItems.ITEM_IMAGE));
            long id = cur.getLong(cur.getColumnIndex(BaseColumns._ID));
            String desc = cur.getString(cur.getColumnIndex(DatabasehelperForItems.ITEM_DESCRIPTION));
            String loc = cur.getString(cur.getColumnIndex(DatabasehelperForItems.ITEM_LOCATION));
            String cost = cur.getString(cur.getColumnIndex(DatabasehelperForItems.ITEM_COST));
            System.out.println("name: " + name);
            System.out.println("image: " + image);
            System.out.println("id: " + id);
            System.out.println("desc: " + desc);
            System.out.println("loc: " + loc);
            System.out.println("cost: " + cost);
            System.out.println("-------------------------------------------");

            ItemModel itemModel = new ItemModel();
            itemModel.setTitle(name);
            itemModel.setThumbnail(image);
            itemModel.setID(id);
            itemModel.setDesc(desc);
            itemModel.setLoc(loc);
            itemModel.setCost(cost);

            itemsList.add(itemModel);

        } while (cur.moveToNext());


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Add item
        if (id == R.id.action_AddItemID) {

            //Toast.makeText(AllItemsActivity.this,"Add an item",Toast.LENGTH_LONG).show();
            Intent i = new Intent(AllItemsActivity.this,DataEntryActivity.class);
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Creating an Image Directory for storing Items' Pictures
    public void createImageDirectory()
    {

        File file = new File(Environment.getExternalStorageDirectory()+"/Item_Pictures");
        if(!file.exists())
        {
            file.mkdir();
        }

    }


    public void onBackPressed(){
        showExitDialog();
    }

    private void showExitDialog(){

        ald.setMessage("Exit the app?");
        ald.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                AllItemsActivity.this.finish();
            }
        });

        ald.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        ald.show();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(dbManipulationObjForItems!=null){
            dbManipulationObjForItems.close(dbhelperForItems);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onPause(){
        super.onPause();
    }


}
