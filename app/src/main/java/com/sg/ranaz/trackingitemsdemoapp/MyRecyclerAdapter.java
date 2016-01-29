/* Author: Rana
 * email id: ranakrishnapaul@gmail.com
 * skype id: rana.krishna.paul
 * File Name: MyRecyclerAdapter.java
 * This is an Adapter Class for RecyclerView
 *
 *  */


package com.sg.ranaz.trackingitemsdemoapp;



import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {

    //Variables, Views, Objects Declaration
    private List<ItemModel> itemsList;
    private Context mContext;

    //Constructor
    public MyRecyclerAdapter(Context context, List<ItemModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        ItemModel itemModel = itemsList.get(i);

        //Setting Item's name
        customViewHolder.textView.setText(Html.fromHtml(itemModel.getTitle()));

        //Setting Item's Image
        Bitmap bitmap = BitmapFactory.decodeFile(itemModel.getThumbnail());
        if (bitmap != null) {
            customViewHolder.imageView.setImageBitmap(bitmap);
        }
        else{
            customViewHolder.imageView.setImageResource(R.drawable.noimage);
        }

        //Handle click event on both title and image click
        customViewHolder.textView.setOnClickListener(clickListener);
        customViewHolder.imageView.setOnClickListener(clickListener);

        customViewHolder.textView.setTag(customViewHolder);
        customViewHolder.imageView.setTag(customViewHolder);

    }

    //Item Click Event
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustomViewHolder holder = (CustomViewHolder) view.getTag();
            int position = holder.getPosition();

            System.out.println("Position Clicked:"+position);
            ItemModel itemModel = itemsList.get(position);

            Intent i = new Intent(mContext,ItemDetailsActivity.class);
            i.putExtra("ID",itemModel.getID());
            i.putExtra("NAME",itemModel.getTitle());
            i.putExtra("DESC",itemModel.getDesc());
            i.putExtra("IMAGE",itemModel.getThumbnail());
            i.putExtra("LOC",itemModel.getLoc());
            i.putExtra("COST",itemModel.getCost());
            mContext.startActivity(i);

        }
    };

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.title);
        }
    }


}
