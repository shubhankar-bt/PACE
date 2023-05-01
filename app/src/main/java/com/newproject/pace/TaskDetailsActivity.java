package com.newproject.pace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.newproject.pace.Adapter.NameViewAdapter;
import com.newproject.pace.Adapter.OrderDetailsAdapter;
import com.newproject.pace.Adapter.ReceiptActivity;
import com.newproject.pace.Model.OrderDetails;
import com.newproject.pace.Model.getContactName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView customerName, noDataText, duesTxt, count;
    private List<OrderDetails> OrderList;
    private List<OrderDetails> sortedList;
    FirebaseAuth firebaseAuth;
    private DatabaseReference reference,dbRef, reference2, dbRef2;
    private OrderDetailsAdapter adapter;
    String Name;
    String phone;
    ImageView moreBtn, whatsApp;
    AppBarLayout layout_group_chat;
    int dues;
    Boolean fromDate;
    TextView customerNumber;
    FloatingActionButton mAddFab, mAddAlarmFab, mAddPersonFab;
    private int amount;

    // These are taken to make visible and invisible along with FABs
    TextView addAlarmActionText, addPersonActionText;

    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;
    int MoneySent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        recyclerView = findViewById(R.id.recyclerTask);
        customerName = findViewById(R.id.customerName);
        moreBtn = findViewById(R.id.moreOptions);
        noDataText = findViewById(R.id.noDataText);
        layout_group_chat = findViewById(R.id.layout_group_chat);
        duesTxt = findViewById(R.id.dues);
        count = findViewById(R.id.count);
        customerNumber = findViewById(R.id.customerNumber);
        whatsApp = findViewById(R.id.whatsApp);

        // Register all the FABs with their IDs This FAB button is the Parent
        mAddFab = findViewById(R.id.add_fab);

        // FAB button
        mAddAlarmFab = findViewById(R.id.add_alarm_fab);
        mAddPersonFab = findViewById(R.id.add_person_fab);

        addAlarmActionText = findViewById(R.id.add_alarm_action_text);
        addPersonActionText = findViewById(R.id.add_person_action_text);

        // Now set all the FABs and all the action name texts as GONE
        mAddAlarmFab.setVisibility(View.GONE);
        mAddPersonFab.setVisibility(View.GONE);
        addAlarmActionText.setVisibility(View.GONE);
        addPersonActionText.setVisibility(View.GONE);
        isAllFabsVisible = false;








        Intent i = getIntent();
        Name = i.getStringExtra("CustomerName");
        fromDate = i.getBooleanExtra("fromDate", false);

        if (Name != null){
            customerName.setText(Name);
        }

        if (!fromDate){
            reference = FirebaseDatabase.getInstance().getReference("contactsWithNames");
        }else{
            reference = FirebaseDatabase.getInstance().getReference("contactsWithDates");
            customerNumber.setVisibility(View.GONE);

        }

        loadSortedData();




        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(TaskDetailsActivity.this, moreBtn);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.options, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked

                        switch (menuItem.getItemId()) {

                            case R.id.item1:
                                fetchPaymentDoneData();
                                return true;

                            case R.id.item2:
                                // Some other methods
                                fetchPaymentNotDoneData();
                                return true;
                            case R.id.item3:
                                // Some other methods
                                fetchAllData();
                                return true;


                        }
                        Toast.makeText(TaskDetailsActivity.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });





        mAddFab.setOnClickListener(view -> {
            if (!isAllFabsVisible) {
                // when isAllFabsVisible becomes true make all
                // the action name texts and FABs VISIBLE
                mAddAlarmFab.show();
                mAddPersonFab.show();

                // make the boolean variable true as we
                // have set the sub FABs visibility to GONE
                isAllFabsVisible = true;
            } else {
                // when isAllFabsVisible becomes true make
                // all the action name texts and FABs GONE.
                mAddAlarmFab.hide();
                mAddPersonFab.hide();
                // make the boolean variable false as we
                // have set the sub FABs visibility to GONE
                isAllFabsVisible = false;
            }
        });
        // below is the sample action to handle add person FAB. Here it shows simple Toast msg.

        mAddPersonFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDialog();

                if (sortedList!= null){
                    Intent intent = new Intent(TaskDetailsActivity.this, MoneyAddActivity.class);
                    intent.putExtra("sorted_list", new Gson().toJson(sortedList));
                    intent.putExtra("namefromTask", Name);
                    intent.putExtra("dueFromTask", dues);
                    startActivity(intent);
                }else{
                    Snackbar.make(getWindow().getDecorView(), "Please wait", Snackbar.LENGTH_SHORT).show();
                    loadSortedData();
                }
            }});


        mAddAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TaskDetailsActivity.this, AddDetailsActivity.class);
                i.putExtra("name", Name);
                i.putExtra("number", phone);
                i.putExtra("fromTaskDetails", true);
                startActivity(i);

            }});

        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWhatsapp(TaskDetailsActivity.this, phone);
                //  Toast.makeText(TaskDetailsActivity.this, "bcjbscscscs", Toast.LENGTH_SHORT).show();
            }
        });








        fetchAllData();

    }

//    public static void openWhatsAppConversation(Context context, String number) {
//
//        number = number.replace(" ", "").replace("+", "");
//
//        Intent sendIntent = new Intent("android.intent.action.MAIN");
//
//        sendIntent.setType("text/plain");
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "");
//        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");
//
//        context.startActivity(sendIntent);
//    }

    public static void openWhatsapp(Context context, String numberWithCountryCode) {

        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + numberWithCountryCode + "&text=" );

        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);

        context.startActivity(sendIntent);
    }

    private void loadSortedData() {
        dbRef= reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Name);

        Query myMostViewedPostsQuery = dbRef.orderByChild("dues");

        myMostViewedPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                sortedList = new ArrayList<>();

                if (!datasnapshot.exists()) {
//                    recyclerView.setVisibility(View.GONE);
//                    noDataText.setVisibility(View.VISIBLE);
//                    duesTxt.setText("0");
//                    count.setText("0");
                }
                else{
                    for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                        OrderDetails data = snapshot.getValue(OrderDetails.class);
                        sortedList.add(data);



                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("databaseErrorWhileSorting","error--"+error);

            }
        });

    }

    private void showDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(TaskDetailsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.alert_dialog_1,null);
        final EditText txt_inputText1 = (EditText)mView.findViewById(R.id.txt_input1);
        final EditText txt_inputText2 = (EditText)mView.findViewById(R.id.txt_input2);
        Button btn_cancel = (Button)mView.findViewById(R.id.btn_cancel);
        Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moneySent = txt_inputText1.getText().toString();
                String moneyReceived = txt_inputText2.getText().toString();

                if (sortedList!= null){
                    updateDues(moneySent, moneyReceived);
                    alertDialog.dismiss();

                }else{
                    Snackbar.make(getWindow().getDecorView(), "Please wait", Snackbar.LENGTH_SHORT).show();
                }





            }
        });
        alertDialog.show();
    }

    private void updateDues(String moneySent, String moneyReceived) {
        if (moneySent != null){
             amount = Integer.parseInt(moneySent);
        }

       // int bankReceived = Integer.parseInt(moneySent);

        if (sortedList != null) {
            if (amount>0){

                for (int i = 0; i < sortedList.size(); i++) {

                    int dues = Integer.parseInt(sortedList.get(i).getDues());//60
                    String uniqueId = sortedList.get(i).getUniqueId();

                    if (dues <= amount && dues > 0) {
                        int totalPrice = Integer.parseInt(sortedList.get(i).getTotalPrice()); ///80
                        int paymentReceived = totalPrice - dues; //20

                        int newPayment = paymentReceived + dues; //40

                        int newDues = totalPrice - newPayment; //0

                        //   initFirebase(newPayment, newDues, uniqueId, "Yes");
                        reference = FirebaseDatabase.getInstance().getReference("contactsWithNames");
                        reference2 = FirebaseDatabase.getInstance().getReference("contactsWithDates");
                        dbRef = reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Name);
                        dbRef2 = reference2.child(FirebaseAuth.getInstance().getCurrentUser().getUid());


                        Map<String, Object> updates = new HashMap<String,Object>();

                        updates.put("paymentDone", String.valueOf(newPayment));
                        updates.put("dues", String.valueOf(newDues));
                        updates.put("checkboxValue", "Yes");


                        dbRef.child(uniqueId).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                dbRef2.child(uniqueId).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(TaskDetailsActivity.this, "Data updated", Toast.LENGTH_SHORT).show();
                                        adapter.notifyDataSetChanged();


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception ) {
                                        Toast.makeText(TaskDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception ) {
                                Toast.makeText(TaskDetailsActivity.this, "Something Went Wrong"+exception, Toast.LENGTH_SHORT).show();

                            }
                        });
                        amount = amount - dues; //30


                    } else if (dues > amount && dues > 0) {

                        int totalPrice = Integer.parseInt(sortedList.get(i).getTotalPrice()); ///80

                        int paymentReceived = totalPrice - dues; //20

                        int newPayment = paymentReceived + amount; //70

                        int newDues = totalPrice - newPayment; //10

                        // initFirebase(newPayment, newDues, uniqueId, "No");
                        reference = FirebaseDatabase.getInstance().getReference("contactsWithNames");
                        reference2 = FirebaseDatabase.getInstance().getReference("contactsWithDates");
                        dbRef = reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Name);
                        dbRef2 = reference2.child(FirebaseAuth.getInstance().getCurrentUser().getUid());


                        Map<String, Object> updates = new HashMap<String,Object>();

                        updates.put("paymentDone", String.valueOf(newPayment));
                        updates.put("dues", String.valueOf(newDues));
                        updates.put("checkboxValue", "No");


                        dbRef.child(uniqueId).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                dbRef2.child(uniqueId).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(TaskDetailsActivity.this, "Data updated", Toast.LENGTH_SHORT).show();


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception ) {
                                        Toast.makeText(TaskDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception ) {
                                Toast.makeText(TaskDetailsActivity.this, "Something Went Wrong"+exception, Toast.LENGTH_SHORT).show();

                            }
                        });

                        amount = 0; //0
                    }


                }
            }
        }




    }

    private void initFirebase(int newPayment, int newDues, String uniqueId, String checkboxValue) {

        reference = FirebaseDatabase.getInstance().getReference("contactsWithNames");
        reference2 = FirebaseDatabase.getInstance().getReference("contactsWithDates");
        dbRef = reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Name);
        dbRef2 = reference2.child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        Map<String, Object> updates = new HashMap<String,Object>();

        updates.put("paymentDone", String.valueOf(newPayment));
        updates.put("dues", String.valueOf(newDues));
        updates.put("checkboxValue", checkboxValue);


        dbRef.child(uniqueId).setValue(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                dbRef2.child(uniqueId).setValue(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TaskDetailsActivity.this, "Data updated", Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception ) {
                        Toast.makeText(TaskDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception ) {
                Toast.makeText(TaskDetailsActivity.this, "Something Went Wrong"+exception, Toast.LENGTH_SHORT).show();

            }
        });
    }



    private ArrayList<Integer> getIntegerArray(ArrayList<String> stringArray) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(String stringValue : stringArray) {
            try {
                //Convert String to Integer, and store it into integer array list.
                result.add(Integer.parseInt(stringValue.trim()));
            } catch(NumberFormatException nfe) {
                //System.out.println("Could not parse " + nfe);
                Log.w("NumberFormat", "Parsing failed! " + stringValue + " can not be an integer");
            }
        }
        return result;
    }

    private void fetchPaymentNotDoneData() {
        dbRef= reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Name);

        dbRef.orderByChild("checkboxValue").equalTo("No").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                OrderList = new ArrayList<>();

                if(!datasnapshot.exists()){
                    recyclerView.setVisibility(View.GONE);
                    noDataText.setVisibility(View.VISIBLE);
                    duesTxt.setText("0");
                    count.setText("0");

                }else {
                    for (DataSnapshot snapshot: datasnapshot.getChildren()){
                        OrderDetails data = snapshot.getValue(OrderDetails.class);
                        OrderList.add(data);

                        if(snapshot.child("phone").exists()) {

                            phone = snapshot.child("phone").getValue().toString();
                            customerNumber.setText(phone);
                            if (OrderList != null){
                                ArrayList<String> TotalPriceList = new ArrayList<>();
                                ArrayList<String> TotalPaymentDoneList = new ArrayList<>();
                                ArrayList<String> dueList = new ArrayList<>();

                                for (int j =0; j<OrderList.size(); j++){
                                    TotalPriceList.add(OrderList.get(j).getTotalPrice());
                                    TotalPaymentDoneList.add(OrderList.get(j).getPaymentDone());
                                    dueList.add(OrderList.get(j).getDues());
                                }

                                ArrayList<Integer> totalList = getIntegerArray(TotalPriceList); //strArrayList is a collection of Strings as you defined.
                                ArrayList<Integer> doneList = getIntegerArray(TotalPaymentDoneList); //strArrayList is a collection of Strings as you defined.
                                ArrayList<Integer> duesList = getIntegerArray(dueList); //strArrayList is a collection of Strings as you defined.


                                Log.wtf("listinteger", String.valueOf(totalList));

                                int total = totalList.stream().mapToInt(Integer::intValue).sum();
                                int done = doneList.stream().mapToInt(Integer::intValue).sum();
                                int finalDues = duesList.stream().mapToInt(Integer::intValue).sum();
                                dues = (total - done);
                                duesTxt.setText(String.valueOf(finalDues));
                                int countNo = OrderList.size();
                                count.setText(String.valueOf(countNo));

                            }


                        }

                        // String name = snapshot.getKey();



                    }
                    noDataText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);


                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(TaskDetailsActivity.this));
                    adapter = new OrderDetailsAdapter(OrderList, TaskDetailsActivity.this, noDataText,   fromDate);
                    recyclerView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TaskDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void fetchPaymentDoneData() {
        dbRef= reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Name);

        dbRef.orderByChild("checkboxValue").equalTo("Yes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                OrderList = new ArrayList<>();

                if(!datasnapshot.exists()){
                    noDataText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    duesTxt.setText("0");
                    count.setText("0");



                }else {
                    for (DataSnapshot snapshot: datasnapshot.getChildren()){
                        OrderDetails data = snapshot.getValue(OrderDetails.class);
                        OrderList.add(data);

                        if(snapshot.child("phone").exists()) {

                            phone = snapshot.child("phone").getValue().toString();
                            customerNumber.setText(phone);
                            if (OrderList != null){
                                ArrayList<String> TotalPriceList = new ArrayList<>();
                                ArrayList<String> TotalPaymentDoneList = new ArrayList<>();
                                ArrayList<String> dueList = new ArrayList<>();

                                for (int j =0; j<OrderList.size(); j++){
                                    TotalPriceList.add(OrderList.get(j).getTotalPrice());
                                    TotalPaymentDoneList.add(OrderList.get(j).getPaymentDone());
                                    dueList.add(OrderList.get(j).getDues());
                                }

                                ArrayList<Integer> totalList = getIntegerArray(TotalPriceList); //strArrayList is a collection of Strings as you defined.
                                ArrayList<Integer> doneList = getIntegerArray(TotalPaymentDoneList); //strArrayList is a collection of Strings as you defined.
                                ArrayList<Integer> duesList = getIntegerArray(dueList); //strArrayList is a collection of Strings as you defined.


                                Log.wtf("listinteger", String.valueOf(totalList));

                                int total = totalList.stream().mapToInt(Integer::intValue).sum();
                                int done = doneList.stream().mapToInt(Integer::intValue).sum();
                                int finalDues = duesList.stream().mapToInt(Integer::intValue).sum();
                                dues = (total - done);
                                duesTxt.setText(String.valueOf(finalDues));
                                int countNo = OrderList.size();
                                count.setText(String.valueOf(countNo));

                            }


                        }

                        // String name = snapshot.getKey();



                    }
                    noDataText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);


                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(TaskDetailsActivity.this));
                    adapter = new OrderDetailsAdapter(OrderList, TaskDetailsActivity.this, noDataText,   fromDate);
                    recyclerView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TaskDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void fetchAllData() {
        dbRef= reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Name);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                OrderList = new ArrayList<>();

                if(!datasnapshot.exists()){
                    noDataText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    duesTxt.setText("0");
                    count.setText("0");


                }else {
                    for (DataSnapshot snapshot: datasnapshot.getChildren()){
                        OrderDetails data = snapshot.getValue(OrderDetails.class);
                        OrderList.add(data);

                        if(snapshot.child("phone").exists()) {

                            phone = snapshot.child("phone").getValue().toString();
                            customerNumber.setText(phone);
                            if (OrderList != null){
                                ArrayList<String> TotalPriceList = new ArrayList<>();
                                ArrayList<String> TotalPaymentDoneList = new ArrayList<>();
                                ArrayList<String> dueList = new ArrayList<>();

                                for (int j =0; j<OrderList.size(); j++){
                                    TotalPriceList.add(OrderList.get(j).getTotalPrice());
                                    TotalPaymentDoneList.add(OrderList.get(j).getPaymentDone());
                                    dueList.add(OrderList.get(j).getDues());
                                }

                                ArrayList<Integer> totalList = getIntegerArray(TotalPriceList); //strArrayList is a collection of Strings as you defined.
                                ArrayList<Integer> doneList = getIntegerArray(TotalPaymentDoneList); //strArrayList is a collection of Strings as you defined.
                                ArrayList<Integer> duesList = getIntegerArray(dueList); //strArrayList is a collection of Strings as you defined.


                                Log.wtf("listinteger", String.valueOf(totalList));

                                int total = totalList.stream().mapToInt(Integer::intValue).sum();
                                int done = doneList.stream().mapToInt(Integer::intValue).sum();
                                int finalDues = duesList.stream().mapToInt(Integer::intValue).sum();
                                dues = (total - done);
                                duesTxt.setText(String.valueOf(finalDues));
                                int countNo = OrderList.size();
                                count.setText(String.valueOf(countNo));

                            }


                        }

                        // String name = snapshot.getKey();



                    }
                    noDataText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);


                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(TaskDetailsActivity.this));
                    adapter = new OrderDetailsAdapter(OrderList, TaskDetailsActivity.this, noDataText,   fromDate);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TaskDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}