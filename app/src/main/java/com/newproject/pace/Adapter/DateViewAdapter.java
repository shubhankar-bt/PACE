package com.newproject.pace.Adapter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.newproject.pace.Model.GetContactWithDate;
import com.newproject.pace.Model.getContactName;
import com.newproject.pace.R;
import com.newproject.pace.TaskDetailsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DateViewAdapter extends RecyclerView.Adapter<DateViewAdapter.ViewHolder> {

    private List<GetContactWithDate> listdata;
    private Context mContext;
    private ArrayList<Integer> arraylist = new ArrayList<>(); // use a dynamic array.


    public DateViewAdapter(List<GetContactWithDate> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fetch_contact_list, null);
        ViewHolder contactViewHolder = new ViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GetContactWithDate contactVO = listdata.get(position);
        holder.tvContactName.setText(contactVO.getDate());

      //  Log.d("shubhnakjarsize", String.valueOf(listdata.size()));

        Log.wtf("name", contactVO.getDate());

        holder.ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, TaskDetailsActivity.class);
                i.putExtra("CustomerName", contactVO.getDate());
                i.putExtra("fromDate", true);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });

        Random random=new Random();
        arraylist.add(R.drawable.customer_icon);
        arraylist.add(R.drawable.customer1);
        arraylist.add(R.drawable.customer2);
        arraylist.add(R.drawable.customer3);
        arraylist.add(R.drawable.customer4);
        arraylist.add(R.drawable.customer6);


        final int po = random.nextInt(arraylist.size());

        int imageResource = arraylist.get(po);
        // arraylist.remove(po); // if you don't want to lose it, you can store it in another ArrayList before removal
        holder.iconImageView.setImageResource(imageResource);



    }

    @Override
    public int getItemCount() {
        return listdata.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvContactName;
        RelativeLayout ll1;
        ImageView iconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvContactName = (TextView) itemView.findViewById(R.id.name);
            ll1 = (RelativeLayout) itemView.findViewById(R.id.ll1);
            iconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);

        }
    }
}

