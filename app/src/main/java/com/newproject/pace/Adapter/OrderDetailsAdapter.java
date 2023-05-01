package com.newproject.pace.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.newproject.pace.AddDetailsActivity;
import com.newproject.pace.MainActivity;
import com.newproject.pace.Model.OrderDetails;
import com.newproject.pace.R;
import com.newproject.pace.TaskDetailsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;


public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    private List<OrderDetails> listdata;
    private Context mContext;
    private  Boolean fromDate;
  //  MainViewModel mainViewModel;
    boolean isEnable=false;
    boolean isSelectAll=false;
    ArrayList<OrderDetails> selectList=new ArrayList<>();
    TextView tvEmpty;

    CheckBox PaymentCompleted;
    String checkboxValue = null;
    private ProgressDialog pd;
    String dues1;






    public OrderDetailsAdapter(List<OrderDetails> listdata, Activity activity, TextView noDataText, Boolean fromDate) {
        this.listdata = listdata;
        this.mContext = activity;
        this.tvEmpty = noDataText;
        this.fromDate = fromDate;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orderdetailslist, null);
        ViewHolder orderDetailsViewHolder = new ViewHolder(view);




        return orderDetailsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderDetails orders = listdata.get(position);

        String totalPrice = orders.getTotalPrice();
        String paymentCompletedOrNot = orders.getCheckboxValue();
        String paymentReceived = orders.getPaymentDone();
        String uniqueId = orders.getUniqueId();
        String fullname = orders.getFullName();
        String phone = orders.getPhone();
        String orderId = orders.getOrderId();
        String orderName = orders.getOrderName();
        String deliverydate = orders.getDeliveryDate();
        String assignedToName = orders.getAssignedToName();
        String FetchDues = orders.getDues();


        if (totalPrice != null){
            int total = Integer.parseInt(totalPrice);
            int amountGot = Integer.parseInt(paymentReceived);
            int dues = total-amountGot;
            dues1 = String.valueOf(dues);
        }





        holder.TotalPrice.setText(totalPrice);
        holder.orderId.setText(orders.getOrderId());
        holder.orderName.setText(orders.getOrderName());

        if (fromDate == true){
            holder.data11.setText(orders.getFullName());
            holder.data11.setTextSize(15);
            holder.numbertxt.setText(phone);
            holder.rl11.setVisibility(View.VISIBLE);
            holder.deliveryDate.setText(orders.getDeliveryDate());


        }else{
            holder.deliveryDate.setText(orders.getDeliveryDate());
            holder.deliveryDate.setBackgroundResource(R.drawable.background_border_empty);
            holder.numbertxt.setText(phone);
            holder.rl11.setVisibility(View.VISIBLE);
            holder.data11.setVisibility(View.INVISIBLE);

        }


        holder.PaymentReceived.setText(orders.getPaymentDone());
        holder.assignedTo.setText(assignedToName);

        if (FetchDues.equalsIgnoreCase("0")){
            holder.dues.setText("No dues");
            holder.dues.setTextColor(Color.GREEN);

        }else{
            holder.dues.setText(FetchDues);
            holder.dues.setTextColor(Color.RED);
        }



        if (paymentCompletedOrNot.equals("No")){
            holder.paymentDoneImage.setImageResource(R.drawable.not_done);

        }else{
            holder.paymentDoneImage.setImageResource(R.drawable.done);
        }



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // check condition
                if (!isEnable)
                {
                    // when action mode is not enable
                    // initialize action mode
                    ActionMode.Callback callback=new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            // initialize menu inflater
                            MenuInflater menuInflater= mode.getMenuInflater();
                            // inflate menu
                            menuInflater.inflate(R.menu.selected_menu,menu);
                            // return true
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            // when action mode is prepare
                            // set isEnable true
                            isEnable=true;
                            // create method
                            ClickItem(holder);
                            // set observer on getText method
//                            mainViewModel.getText().observe((LifecycleOwner) mContext, new Observer<String>() {
//                                        @Override
//                                        public void onChanged(String s) {
//                                            // when text change
//                                            // set text on action mode title
//                                            mode.setTitle(String.format("%s Selected",s));
//                                        }
//                                    });
                            // return true
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            // when click on action mode item
                            // get item  id
                            int id=item.getItemId();
                            // use switch condition
                            switch(id)
                            {
                                case R.id.receipt:
                                    // when click on delete
                                    // use for loop
                                    for(OrderDetails s:selectList)
                                    {
                                        // remove selected item list
//                                        int totalDue = 0;
//                                        for (int i=0; i<selectList.size();i++){
//                                            int dues = (Integer.parseInt(s.getTotalPrice()) - Integer.parseInt(s.getPaymentDone()));
//                                            totalDue = totalDue+dues;
//
//                                            String due = String.valueOf(totalDue);
//                                            Toast.makeText(mContext, due, Toast.LENGTH_SHORT).show();
//
//
//                                            s =  selectList.get(i);
//                                        }

                                        Intent intent = new Intent(mContext, ReceiptActivity.class);
                                        intent.putExtra("private_list", new Gson().toJson(selectList));
                                        intent.putExtra("FullName",orders.getFullName());
                                        intent.putExtra("Number",orders.getPhone());
                                        mContext.startActivity(intent);





                                    }
                                    // check condition
                                    if(listdata.size()==0)
                                    {
                                        // when array list is empty
                                        // visible text view
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }
                                    // finish action mode
                                    mode.finish();
                                    break;

                                case R.id.menu_select_all:
                                    // when click on select all
                                    // check condition
                                    if(selectList.size()==listdata.size())
                                    {
                                        // when all item selected
                                        // set isselectall false
                                        isSelectAll=false;
                                        // create select array list
                                        selectList.clear();
                                    }
                                    else
                                    {
                                        // when  all item unselected
                                        // set isSelectALL true
                                        isSelectAll=true;
                                        // clear select array list
                                        selectList.clear();
                                        // add value in select array list
                                        selectList.addAll(listdata);
                                    }
                                    // set text on view model
                                    //mainViewModel.setText(String .valueOf(selectList.size()));
                                    // notify adapter
                                    notifyDataSetChanged();
                                    break;
                                case R.id.delete:
                                    // when click on delete
                                    // check condition

                                    for(OrderDetails s:selectList)
                                    {
                                        // remove selected item list
                                        if (selectList.size()>1){
                                            Toast.makeText(v.getContext(),"Sorry, more than one item can't be deleted at once. Please select one item", Toast.LENGTH_LONG).show();

                                        }else {

                                            String newId = s.getUniqueId();
                                            if (newId != null) {
                                                deleteData(newId, fullname, s, mode, deliverydate);

                                            } else {
                                                Toast.makeText(v.getContext(), "Sorry, this item can't be deleted", Toast.LENGTH_SHORT).show();
                                            }

                                        }




                                    }
                                    // check condition
                                    if(listdata.size()==0)
                                    {
                                        // when array list is empty
                                        // visible text view
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }
                                    // finish action mode

                                    break;

                            }
                            // return true
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            // when action mode is destroy
                            // set isEnable false
                            isEnable=false;
                            // set isSelectAll false
                            isSelectAll=false;
                            // clear select array list
                            selectList.clear();
                            // notify adapter
                            notifyDataSetChanged();
                        }
                    };
                    // start action mode
                    ((AppCompatActivity) v.getContext()).startActionMode(callback);
                }
                else
                {
                    // when action mode is already enable
                    // call method
                    ClickItem(holder);
                }
                // return true
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check condition
                if(isEnable)
                {
                    // when action mode is enable
                    // call method
                    ClickItem(holder);
                }
                else
                {
                    // when action mode is not enable
                    // display toast

                    if (uniqueId != null){
                        update(uniqueId, fullname,phone, totalPrice, paymentReceived, paymentCompletedOrNot,orderId, orderName , deliverydate, assignedToName, FetchDues);
                    }else{
                        Toast.makeText(v.getContext(),"Sorry, this item can't be updated", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        // check condition
        if(isSelectAll)
        {
            // when value selected
            // visible all check boc image
            holder.checkbox.setVisibility(View.VISIBLE);
            //set background color
            //holder.itemView.setBackgroundColor(Color.LTGRAY);
            holder.card.setCardBackgroundColor(Color.parseColor("#818181"));
        }
        else
        {
            // when all value unselected
            // hide all check box image
            holder.checkbox.setVisibility(View.GONE);
            // set background color
            //holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.card.setCardBackgroundColor(Color.parseColor("#F5FEFD"));

        }
    }

    private void deleteData(String uniqueId, String fullname, OrderDetails s, ActionMode mode, String deliverydate) {

        DatabaseReference reference, dbRef, reference2, dbRef2;
        reference = FirebaseDatabase.getInstance().getReference("contactsWithNames");
        reference2 = FirebaseDatabase.getInstance().getReference("contactsWithDates");
        dbRef = reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(fullname);
        dbRef2 = reference2.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        CharSequence options[]=new CharSequence[]{
                // select any from the value
                "Yes",
                "Cancel",
        };
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle("Are you sure?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if delete option is choosed
                // then call delete function
                if(which==0) {
                    Query query=dbRef.child(uniqueId);
                    Query query2=dbRef2.child(uniqueId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // remove the value at reference

                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                    // remove the value at reference
                                    dataSnapshot1.getRef().removeValue();
                                    dataSnapshot.getRef().removeValue();
                                    listdata.remove(s);
                                    mode.finish();
                                    notifyDataSetChanged();
                                    Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(mContext, "Failed "+String.valueOf(databaseError), Toast.LENGTH_SHORT).show();

                                }
                            });
                            notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(mContext, "Failed "+String.valueOf(databaseError), Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });
        builder.show();

    }


    private void update(String uniqueId, String fullname, String phone, String totalPrice, String paymentReceived, String paymentCompletedOrNot, String orderId, String orderName, String deliverydate, String assignedToName, String fetchDues) {


        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View mView = bottomSheetDialog.getLayoutInflater().inflate(R.layout.custom_dialog,null);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        // bottomSheetDialog.setContentView(R.layout.custom_dialog);

        TextView nameEditText = (TextView) mView.findViewById(R.id.name);

        TextView currentWeightEditText = (TextView) mView.findViewById(R.id.current_weight);
        String totalprice = currentWeightEditText.getText().toString();

        EditText heightEditText = (EditText) mView.findViewById(R.id.height);
        String paymentDone = heightEditText.getText().toString();

        EditText assignedToNameEditTxt = (EditText) mView.findViewById(R.id.assignedTo);
        String assignedName = heightEditText.getText().toString();

        TextView goalWeightEditText = (TextView) mView.findViewById(R.id.goal_weight);
        String OrderId = goalWeightEditText.getText().toString();

        TextView ageEditText = (TextView) mView.findViewById(R.id.age);
        String OrderName = ageEditText.getText().toString();

        TextView phoneEditText = (TextView) mView.findViewById(R.id.Phone);
        String phoneNumber = phoneEditText.getText().toString();

        TextView addressEditText = (TextView) mView.findViewById(R.id.address);
        String DeliveryDate = addressEditText.getText().toString();


        //initiate a check box
        PaymentCompleted = (CheckBox) mView.findViewById(R.id.conditions);




        nameEditText.setText(fullname);
        phoneEditText.setText(phone);
        currentWeightEditText.setText(totalPrice);
        heightEditText.setText(paymentReceived);
        goalWeightEditText.setText(orderId);
        addressEditText.setText(deliverydate);
        phoneEditText.setText(phone);
        ageEditText.setText(orderName);
        assignedToNameEditTxt.setText(assignedToName);



        Button btn_cancel = (Button)mView.findViewById(R.id.btn_cancel);
        Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
        bottomSheetDialog.setContentView(mView);
        bottomSheetDialog.onAttachedToWindow();


       // BottomSheetDialog alertDialog = bottomSheetDialog.create();
        bottomSheetDialog.setCancelable(false);


        PaymentCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (PaymentCompleted.isChecked()) {
                    // your code to checked checkbox
                    checkboxValue = "Yes";
                }
                else {
                    checkboxValue = "No";
                }
            }
        });



        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // String name = txt_inputText.getText().toString();
                DatabaseReference reference, dbRef, reference2, dbRef2;
                reference = FirebaseDatabase.getInstance().getReference("contactsWithNames");
                reference2 = FirebaseDatabase.getInstance().getReference("contactsWithDates");
                dbRef = reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(fullname);
                dbRef2 = reference2.child(FirebaseAuth.getInstance().getCurrentUser().getUid());



                EditText heightEditText = (EditText) mView.findViewById(R.id.height);
                EditText assignedTo = (EditText) mView.findViewById(R.id.assignedTo);

                String paymentDone = heightEditText.getText().toString();
                String assignedToName2 = assignedTo.getText().toString();



                pd = new ProgressDialog(mContext);


                if (paymentDone.equals(totalPrice) && !PaymentCompleted.isChecked()){
                    PaymentCompleted.setError("");
                    PaymentCompleted.requestFocus();
                    Toast.makeText(mContext, "Please check payment done because full amount received!", Toast.LENGTH_SHORT).show();
                }else if (!paymentDone.equals(totalPrice) && PaymentCompleted.isChecked()){
                    PaymentCompleted.setError(" ");
                    PaymentCompleted.requestFocus();
                    Toast.makeText(mContext, "Please uncheck payment done because full amount not received!", Toast.LENGTH_SHORT).show();
                }else if (checkboxValue == null){
                    Toast.makeText(mContext, "Please check is payment completed or not!", Toast.LENGTH_SHORT).show();

                }else if (assignedToName2.isEmpty()){
                    Toast.makeText(mContext, "Please fill all the details required!", Toast.LENGTH_SHORT).show();
                    assignedTo.requestFocus();
                    assignedTo.getError();

                }else if (paymentDone.isEmpty()){
                    Toast.makeText(mContext, "Please fill all the details required!", Toast.LENGTH_SHORT).show();
                    heightEditText.requestFocus();
                    heightEditText.getError();

                }else
                {
                    int dues = Integer.parseInt(totalPrice) - Integer.parseInt(paymentDone);

                    pd.setMessage("Please wait...");
                    pd.setTitle("Updating Data");
                    pd.show();
                    OrderDetails updateDetails = new OrderDetails(fullname, totalPrice, paymentDone, orderId , orderName, phone, deliverydate,checkboxValue, uniqueId , assignedToName2, String.valueOf(dues) );

                    dbRef.child(uniqueId).setValue(updateDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            dbRef2.child(uniqueId).setValue(updateDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(mContext, "Data updated", Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.dismiss();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception ) {
                                    pd.dismiss();
                                    Toast.makeText(mContext, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception ) {
                            pd.dismiss();
                            Toast.makeText(mContext, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    });


                }







            }
        });
        bottomSheetDialog.show();

    }

    private void ClickItem(ViewHolder holder) {

        // get selected item value
        OrderDetails s=listdata.get(holder.getAdapterPosition());
        // check condition
        if(holder.checkbox.getVisibility()==View.GONE)
        {
            // when item not selected
            // visible check box image
            holder.checkbox.setVisibility(View.VISIBLE);
            // set background color
            holder.card.setCardBackgroundColor(Color.parseColor("#818181"));
            // add value in select array list
            selectList.add(s);
        }
        else
        {
            // when item selected
            // hide check box image
            holder.checkbox.setVisibility(View.GONE);
            // set background color
           // holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.card.setCardBackgroundColor(Color.parseColor("#F5FEFD"));

            // remove value from select arrayList
            selectList.remove(s);

        }
        // set text on view model
      //  mainViewModel.setText(String.valueOf(selectList.size()));
    }




    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView  TotalPrice, orderId, orderName, deliveryDate, projectDelivered, PaymentReceived, paymentCompleted, dues, assignedTo, numbertxt, data11;
        ImageView paymentDoneImage, checkbox;
        MaterialCardView card;
        RelativeLayout rl11;


        public ViewHolder(View itemView) {
            super(itemView);

            TotalPrice = itemView.findViewById(R.id.TotalPrice);
            orderId = itemView.findViewById(R.id.orderId);
            orderName = itemView.findViewById(R.id.orderName);
            deliveryDate = itemView.findViewById(R.id.deliveryDate);
            projectDelivered = itemView.findViewById(R.id.projectDelivered);
            paymentDoneImage = itemView.findViewById(R.id.paymentDoneImage);
            PaymentReceived = itemView.findViewById(R.id.PaymentReceived);
            paymentCompleted = itemView.findViewById(R.id.paymentCompleted);
            dues = itemView.findViewById(R.id.dues);
            checkbox = itemView.findViewById(R.id.check_box);
            card = itemView.findViewById(R.id.card);
            assignedTo = itemView.findViewById(R.id.assignedTo);
            numbertxt = itemView.findViewById(R.id.numbertxt);
            rl11 = itemView.findViewById(R.id.rl11);
            data11 = itemView.findViewById(R.id.data11);


        }
    }
}

