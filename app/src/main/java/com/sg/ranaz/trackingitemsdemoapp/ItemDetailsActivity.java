/* Author: Rana
 * email id: ranakrishnapaul@gmail.com
 * skype id: rana.krishna.paul
 * File Name: ItemDetailsActivity.java
 * This is an Activity for displaying each Item's Details
 *
 *  */

package com.sg.ranaz.trackingitemsdemoapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemDetailsActivity extends AppCompatActivity {

    //Variables, Views, Objects Declaration
    private Intent i;
    private long iID;
    private String iName = null;
    private String iDescription = null;
    private String iLocation = null;
    private String iCost = null;
    private String iImagePath = null;

    private TextView detailspage_ItemID_TextViewID;
    private TextView detailspage_ItemName_TextViewID;
    private TextView detailspage_ItemDesc_TextViewID;
    private TextView detailspage_ItemLoc_TextViewID;
    private TextView detailspage_ItemCost_TextViewID;
    private ImageView detailspage_ItemImage_ImageViewID;

    private AlertDialog.Builder ald;
    private ProgressDialog progressDialog;

    private DatabasehelperForItems dbhelperForItems;
    private DBManipulationForItems dbManipulationObjForItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting up the Toolbar
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Receiving the Intent from the calling Activity
        i = new Intent();
        i = getIntent();

        iID = i.getLongExtra("ID", 0);
        iName = i.getStringExtra("NAME");
        iDescription = i.getStringExtra("DESC");
        iLocation = i.getStringExtra("LOC");
        iCost = i.getStringExtra("COST");
        iImagePath = i.getStringExtra("IMAGE");

        //DataBase creation & Manipulation Objects
        dbhelperForItems = new DatabasehelperForItems(this);
        dbManipulationObjForItems = new DBManipulationForItems();

        //Initialize all the views with respect to ID References from XML
        initAllViewID();

        detailspage_ItemID_TextViewID.append("\t" + String.valueOf(iID));
        detailspage_ItemName_TextViewID.append("\t"+iName);
        detailspage_ItemDesc_TextViewID.append("\t"+iDescription);
        detailspage_ItemLoc_TextViewID.append("\t"+iLocation);
        detailspage_ItemCost_TextViewID.append("\t"+iCost);

        Bitmap bitmap = BitmapFactory.decodeFile(iImagePath);
        if (bitmap != null) {
            detailspage_ItemImage_ImageViewID.setImageBitmap(bitmap);
        }
        else{
            detailspage_ItemImage_ImageViewID.setImageResource(R.drawable.noimage);
        }


    }

    //Initialize all the views with respect to ID References from XML
    private void initAllViewID(){

        detailspage_ItemID_TextViewID = (TextView)findViewById(R.id.detailspage_ItemID_TextViewID);
        detailspage_ItemName_TextViewID = (TextView)findViewById(R.id.detailspage_ItemName_TextViewID);
        detailspage_ItemDesc_TextViewID = (TextView)findViewById(R.id.detailspage_ItemDesc_TextViewID);
        detailspage_ItemLoc_TextViewID = (TextView)findViewById(R.id.detailspage_ItemLoc_TextViewID);
        detailspage_ItemCost_TextViewID = (TextView)findViewById(R.id.detailspage_ItemCost_TextViewID);
        detailspage_ItemImage_ImageViewID = (ImageView)findViewById(R.id.detailspage_ItemImage_ImageViewID);

        ald = new AlertDialog.Builder(ItemDetailsActivity.this);
        //Progress Dialog
        progressDialog = new ProgressDialog(ItemDetailsActivity.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Delete Item Logic
        if (id == R.id.action_DelateItemID) {

            //Showing delete dialog
            showDeleteDialog();

            return true;
        }

        //Update Logic
        if (id == R.id.action_UpdateItemID) {

            Intent i = new Intent(ItemDetailsActivity.this,DataEntryActivity.class);
            i.putExtra("UPDATEREQUEST",true);
            i.putExtra("ID",iID);
            i.putExtra("NAME",iName);
            i.putExtra("DESC",iDescription);
            i.putExtra("IMAGE",iImagePath);
            i.putExtra("LOC",iLocation);
            i.putExtra("COST",iCost);
            ItemDetailsActivity.this.finish();
            ItemDetailsActivity.this.startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Showing delete dialog
    private void showDeleteDialog(){

        ald.setMessage("Would you like to delete this Item?");
        ald.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                deleteRecordFromTable();
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

    //Deleting the item from the Table
    private void deleteRecordFromTable(){

        progressDialog.setMessage("Deleting Record... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub

                try {
                     dbManipulationObjForItems.deleteRecordFromItemsById(dbhelperForItems, iID);
                    //Here comes the code for "Deleting the data from the Cloud"
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);

            }

        }).start();

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            progressDialog.dismiss();
            if(DBManipulationForItems.deleteSuccess){
                Toast.makeText(ItemDetailsActivity.this, "Item Deletion Success", Toast.LENGTH_SHORT).show();
                ItemDetailsActivity.this.finish();
            }
            if(!DBManipulationForItems.deleteSuccess){
                Toast.makeText(ItemDetailsActivity.this, "Item Deletion Failed", Toast.LENGTH_SHORT).show();
            }

        }

    };


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        ItemDetailsActivity.this.finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
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
