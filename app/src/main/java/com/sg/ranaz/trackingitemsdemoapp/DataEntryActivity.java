/*
 * Author: Rana
 * email id: ranakrishnapaul@gmail.com
 * skype id: rana.krishna.paul
 * file name: DataEntryActivity.java
 * This class is used for creating a new record into the table & also updating the table
 *
 *  */


package com.sg.ranaz.trackingitemsdemoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

public class DataEntryActivity extends AppCompatActivity {

    //Variables, Views, Objects Declaration
    private EditText homepage_ItemName_EditTextID;
    private EditText homepage_ItemDesc_EditTextID;
    private EditText homepage_ItemLocation_EditTextID;
    private EditText homepage_ItemCost_EditTextID;

    private ImageView homepage_ItemImage_ImageViewID;
    private ImageView homepage_CameraItemImage_ImageViewID;
    private ImageView homepage_BrowseItemImage_ImageViewID;

    private static Uri capturedImageUri = null;
    private static String realpath = null;
    private int CAMERA_REQUEST  = 1111;

    private int GALLERY_REQUEST  = 2222;
    private static String realpath1 = null;
    private static String imageSelectionType = null;

    private DatabasehelperForItems dbhelperForItems;
    private DBManipulationForItems dbManipulationObjForItems;

    private Intent i;
    private boolean updateRequest = false;
    private long iID;
    private String iName = null;
    private String iDescription = null;
    private String iLocation = null;
    private String iCost = null;
    private String iImagePath = null;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);

        //Setting up the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Receiving the Intent from the calling Activity
        i = new Intent();
        i = getIntent();

        if(i.hasExtra("UPDATEREQUEST")) {

            updateRequest = i.getBooleanExtra("UPDATEREQUEST",false);
            System.out.println("updateRequest: "+updateRequest);
            iID = i.getLongExtra("ID", 0);
            iName = i.getStringExtra("NAME");
            iDescription = i.getStringExtra("DESC");
            iLocation = i.getStringExtra("LOC");
            iCost = i.getStringExtra("COST");
            iImagePath = i.getStringExtra("IMAGE");
        }

        //DataBase creation & Manipulation Objects
        dbhelperForItems = new DatabasehelperForItems(this);
        dbManipulationObjForItems = new DBManipulationForItems();

        //Initialize all the views with respect to ID References from XML
        initAllViewID();

        if(updateRequest){
            System.out.println("updateValuesIntoAllViews(): Called: "+updateRequest);
            updateValuesIntoAllViews();

        }

        //Camera Click Logic
        homepage_CameraItemImage_ImageViewID.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();
                File file = new File(Environment.getExternalStorageDirectory()+"/Item_Pictures", (cal.getTimeInMillis() + ".jpg"));
                if(!file.exists()){
                    try{
                        file.createNewFile();
                    }catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }else{
                    file.delete();
                    try{
                        file.createNewFile();
                    }catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                capturedImageUri = Uri.fromFile(file);
                System.out.println("capturedImageUri: "+capturedImageUri);
                realpath = file.getAbsolutePath().toString();
                System.out.println("realpath: "+realpath);
                Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                startActivityForResult(i, CAMERA_REQUEST);

            }

        });

        //Browse for Image from Gallery Click Logic
        homepage_BrowseItemImage_ImageViewID.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i, GALLERY_REQUEST);

            }

        });


    }

    //Initialize all the views with respect to ID References from XML
    private void initAllViewID(){

        homepage_ItemName_EditTextID = (EditText)findViewById(R.id.homepage_ItemName_EditTextID);
        homepage_ItemDesc_EditTextID = (EditText)findViewById(R.id.homepage_ItemDesc_EditTextID);
        homepage_ItemLocation_EditTextID = (EditText)findViewById(R.id.homepage_ItemLocation_EditTextID);
        homepage_ItemCost_EditTextID = (EditText)findViewById(R.id.homepage_ItemCost_EditTextID);

        homepage_ItemImage_ImageViewID = (ImageView)findViewById(R.id.homepage_ItemImage_ImageViewID);
        homepage_CameraItemImage_ImageViewID = (ImageView)findViewById(R.id.homepage_CameraItemImage_ImageViewID);
        homepage_BrowseItemImage_ImageViewID = (ImageView)findViewById(R.id.homepage_BrowseItemImage_ImageViewID);

        //Progress Dialog
        progressDialog = new ProgressDialog(DataEntryActivity.this);

    }

    private void updateValuesIntoAllViews(){

        homepage_ItemName_EditTextID.setText(iName);
        homepage_ItemDesc_EditTextID.setText(iDescription);
        homepage_ItemLocation_EditTextID.setText(iLocation);
        homepage_ItemCost_EditTextID.setText(iCost);
        Bitmap bitmap = BitmapFactory.decodeFile(iImagePath);
        if (bitmap != null) {
            homepage_ItemImage_ImageViewID.setImageBitmap(bitmap);
            imageSelectionType = "UPDATE";
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){

            if (requestCode == CAMERA_REQUEST) {
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), capturedImageUri);
                    homepage_ItemImage_ImageViewID.setImageBitmap(bitmap);
                    imageSelectionType = "CAMERA";

                }catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (requestCode == GALLERY_REQUEST) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                realpath1 = cursor.getString(columnIndex);
                cursor.close();
                System.out.println("selectedImage Uri: " + selectedImage);
                System.out.println("realpath1: " + realpath1);

                Bitmap bitmap = BitmapFactory.decodeFile(realpath1);
                if (bitmap != null) {
                    homepage_ItemImage_ImageViewID.setImageBitmap(bitmap);
                    imageSelectionType = "GALLERY";
                }

            }

        }
        if(resultCode == RESULT_CANCELED){

            if (requestCode == CAMERA_REQUEST) {
                capturedImageUri = null;
                realpath = null;
                imageSelectionType = null;
                System.out.println("capturedImageUri: "+capturedImageUri);
                System.out.println("realpath: "+realpath);
            }
            if (requestCode == GALLERY_REQUEST) {
                realpath1 = null;
                imageSelectionType = null;
                System.out.println("realpath1: " + realpath1);
            }

        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Save Logic
        if (id == R.id.action_SaveItemID) {
            //Validating the user's input
            validateData();
            return true;
        }

        //Update Logic
        if (id == R.id.action_updteItemID) {
            //Validating the user's input
            validateData();
            return true;
        }

        //Reset Logic
        if (id == R.id.action_ClearItemID) {
            //Resetting the user's input
            clearAll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Enabling & Disabling Menu Items
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem save = menu.findItem(R.id.action_SaveItemID);
        MenuItem update = menu.findItem(R.id.action_updteItemID);

        if(updateRequest){
            save.setVisible(false);
            update.setVisible(true);
        }
        else{
            save.setVisible(true);
            update.setVisible(false);
        }

        return true;
    }

    //Validating the user's input
    private void validateData(){

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(homepage_ItemName_EditTextID.getText().toString()))
        {
            homepage_ItemName_EditTextID.setError("Required Field!");
            focusView = homepage_ItemName_EditTextID;
            cancel = true;
        }
        if(TextUtils.isEmpty(homepage_ItemDesc_EditTextID.getText().toString()))
        {
            homepage_ItemDesc_EditTextID.setError("Required Field!");
            focusView = homepage_ItemDesc_EditTextID;
            cancel = true;
        }
        if(TextUtils.isEmpty(homepage_ItemLocation_EditTextID.getText().toString()))
        {
            homepage_ItemLocation_EditTextID.setError("Required Field!");
            focusView = homepage_ItemLocation_EditTextID;
            cancel = true;
        }
        if(TextUtils.isEmpty(homepage_ItemCost_EditTextID.getText().toString()))
        {
            homepage_ItemCost_EditTextID.setError("Required Field!");
            focusView = homepage_ItemCost_EditTextID;
            cancel = true;
        }
        if(cancel){

            focusView.requestFocus();
        }
        else
        {
            if(imageSelectionType!=null){
                if(imageSelectionType.equalsIgnoreCase("CAMERA")){
                    if(realpath!=null){
                        System.out.println("getValues(realpath): "+"CAMERA");
                        getValues(realpath);
                    }

                }
                if(imageSelectionType.equalsIgnoreCase("GALLERY")){
                    if(realpath1!=null){
                        System.out.println("getValues(realpath1): "+"GALLERY");
                        getValues(realpath1);
                    }
                }
                if(imageSelectionType.equalsIgnoreCase("UPDATE")){
                    if(iImagePath!=null){
                        System.out.println("getValues(iImagePath): "+"UPDATE");
                        getValues(iImagePath);
                    }
                }
                if(realpath==null&&realpath1==null&&!updateRequest){
                    Toast.makeText(DataEntryActivity.this,"Image not selected",Toast.LENGTH_SHORT).show();
                }

            }
            else{
                 Toast.makeText(DataEntryActivity.this,"Image not selected",Toast.LENGTH_SHORT).show();
            }

        }

    }

    //Retrieving all the Input values from EditText & ImageView
    private void getValues(String itemImagePath){

        iName = homepage_ItemName_EditTextID.getText().toString();
        iDescription = homepage_ItemDesc_EditTextID.getText().toString();
        iLocation = homepage_ItemLocation_EditTextID.getText().toString();
        iCost = homepage_ItemCost_EditTextID.getText().toString();
        iImagePath = itemImagePath;

        if(validateCost(iCost)){

            if(updateRequest){
                updateRecordIntoTable();
            }
            else{
                saveRecordIntoTable();
            }

        }

    }

    //Validate the Item's Cost
    private boolean validateCost(String q){

        boolean status = false;
        View focusView = null;
        if(!q.equalsIgnoreCase("")){

            if(!CommonUtils.QUANTITIES_PATTERN.matcher(q).matches()){

                status = false;
                homepage_ItemCost_EditTextID.setError("Invalid Input");
                focusView = homepage_ItemCost_EditTextID;
                focusView.requestFocus();
            }
            else{

                status = true;
            }

        }//if

        return status;
    }

    //Save Record into Table
    private void saveRecordIntoTable(){

        progressDialog.setMessage("Saving Record... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub

                try {
                     dbManipulationObjForItems.insertRecordForItems(dbhelperForItems,iName,iDescription,iImagePath,iLocation,iCost);
                     //Here comes the code for "Saving the data into the Cloud"
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
            if(DBManipulationForItems.ROWID!=-1){
                Toast.makeText(DataEntryActivity.this, "Item Insertion Success", Toast.LENGTH_SHORT).show();
                clearAll();
            }
            if(DBManipulationForItems.ROWID==-1){
                Toast.makeText(DataEntryActivity.this, "Item Insertion Failed", Toast.LENGTH_SHORT).show();
            }

        }

    };

    //Update Record into Table
    private void updateRecordIntoTable(){

        progressDialog.setMessage("Updating Record... Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub

                try {
                     dbManipulationObjForItems.updateRecord(dbhelperForItems,iID,iName,iDescription,iImagePath,iLocation,iCost);
                    //Here comes the code for "Updating the data into the Cloud"
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                handler1.sendEmptyMessage(0);

            }

        }).start();

    }

    private Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg){

            progressDialog.dismiss();
            if(DBManipulationForItems.TOTALUPDATEDROW==1){
                Toast.makeText(DataEntryActivity.this, "Item Updated Successfully", Toast.LENGTH_SHORT).show();
                DataEntryActivity.this.finish();
            }
            if(DBManipulationForItems.TOTALUPDATEDROW==0){
                Toast.makeText(DataEntryActivity.this, "Item Update Failed", Toast.LENGTH_SHORT).show();
            }

        }

    };

    //Resetting the user's input
    private void clearAll(){
        homepage_ItemName_EditTextID.setText("");
        homepage_ItemDesc_EditTextID.setText("");
        homepage_ItemLocation_EditTextID.setText("");
        homepage_ItemCost_EditTextID.setText("");
        homepage_ItemImage_ImageViewID.setImageResource(R.drawable.noimage);
        capturedImageUri = null;
        realpath = null;
        realpath1 = null;
        imageSelectionType = null;
        if(updateRequest){
            iImagePath = null;
        }
        homepage_ItemName_EditTextID.requestFocus();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        DataEntryActivity.this.finish();

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
