package com.newproject.pace.Adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newproject.pace.AddDetailsActivity;
import com.newproject.pace.Model.contacts;
import com.newproject.pace.R;
import java.util.List;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<contacts> listdata;
    private Context mContext;


    public ContactAdapter(List<contacts> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_contact_view, null);
        ViewHolder contactViewHolder = new ViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        contacts contactVO = listdata.get(position);
        holder.tvContactName.setText(contactVO.getName());
        holder.tvPhoneNumber.setText(contactVO.getNumber());

        holder.ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, AddDetailsActivity.class);
                i.putExtra("name", contactVO.name);
                i.putExtra("number", contactVO.number);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                mContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivContactImage;
        TextView tvContactName;
        TextView tvPhoneNumber;
        LinearLayout ll1;

        public ViewHolder(View itemView) {
            super(itemView);

            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            ll1 = (LinearLayout) itemView.findViewById(R.id.ll1);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);

        }
    }
}
