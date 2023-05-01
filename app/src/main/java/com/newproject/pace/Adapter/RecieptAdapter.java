package com.newproject.pace.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.newproject.pace.Model.OrderDetails;
import com.newproject.pace.Model.getContactName;
import com.newproject.pace.R;
import com.newproject.pace.TaskDetailsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {

    private List<OrderDetails> listdata;
    private Context mContext;


    public ReceiptAdapter(List<OrderDetails> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.reciept_list, null);
        ViewHolder receiptHolder = new ViewHolder(view);
        return receiptHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderDetails orderDetails = listdata.get(position);
        holder.orderId.setText(orderDetails.getOrderId());
        holder.TotalPrice.setText(orderDetails.getTotalPrice());
        holder.orderName.setText(orderDetails.getOrderName());

        String totalPrice = orderDetails.getTotalPrice();
        String deliveryDate = orderDetails.getDeliveryDate();
        String paymentCompletedOrNot = orderDetails.getCheckboxValue();
        String paymentReceived = orderDetails.getPaymentDone();


        int total = Integer.parseInt(totalPrice);
        int amountGot = Integer.parseInt(paymentReceived);
        int dues = total-amountGot;
        String dues1 = String.valueOf(dues);

        if (dues1.equalsIgnoreCase("0")){
            holder.dues.setText("No dues");

        }else{
            holder.dues.setText(dues1);
            holder.dues.setTextColor(Color.RED);
        }



        // Log.wtf("name", contactVO.getFullName());

//        holder.ll1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(mContext, TaskDetailsActivity.class);
//                i.putExtra("CustomerName", contactVO.getFullName());
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(i);
//            }
//        });
//



    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderId, orderName, TotalPrice, dues;
        RelativeLayout ll1;
        ImageView iconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            TotalPrice = (TextView) itemView.findViewById(R.id.TotalPrice);
            orderId = (TextView) itemView.findViewById(R.id.orderId);
            orderName = (TextView) itemView.findViewById(R.id.orderName);
            dues = (TextView) itemView.findViewById(R.id.Dues);


        }
    }
}

