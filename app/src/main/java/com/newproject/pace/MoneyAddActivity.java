package com.newproject.pace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newproject.pace.Model.Expense;
import com.newproject.pace.Model.OrderDetails;
import com.newproject.pace.Model.ReceivedMoneyInBank;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MoneyAddActivity extends AppCompatActivity {

    ArrayList<OrderDetails> sortedList = new ArrayList<>();
    private int amount;
    private DatabaseReference reference,dbRef, reference2, dbRef2, databaseReference;
    RelativeLayout layout;
    TextView addedAmount;
    String Name;
    int due;
    ProgressDialog progressDialog;
    TextInputEditText txt_inputText1, txt_inputText2;
    ProgressBar dialog;
    String startDate;
    String today;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_add);

        txt_inputText1 = (TextInputEditText)findViewById(R.id.txt_input1);
        txt_inputText2 = (TextInputEditText)findViewById(R.id.txt_input2);



        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        Button btn_okay = (Button)findViewById(R.id.btn_okay);
        addedAmount = (TextView) findViewById(R.id.addedAmount);
        layout = (RelativeLayout)findViewById(R.id.layout);
        dialog = (ProgressBar) findViewById(R.id.dialog);

        Type type = new TypeToken<List<OrderDetails>>() {
        }.getType();
        sortedList = new Gson().fromJson(getIntent().getStringExtra("sorted_list"), type);

        Intent i = getIntent();
        Name = i.getStringExtra("namefromTask");
        due = i.getIntExtra("dueFromTask", 0);


        TextView customTitle = new TextView(this);
        customTitle.setText(getResources().getString(R.string.please_wait));
        customTitle.setPadding(20, 30, 20, 30);
        customTitle.setTextSize(20F);
        customTitle.setBackgroundColor(getResources().getColor(R.color.red_active));
        customTitle.setTextColor(Color.WHITE);

        progressDialog = new ProgressDialog(MoneyAddActivity.this);



        Date currentDate = new Date();
        today = new SimpleDateFormat("yyyy-M-d").format(currentDate);
        startDate = new SimpleDateFormat("yyyy-M-d").format(getFirstDateOfCurrentMonth());




        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moneySent = txt_inputText1.getText().toString().trim();
                String moneyReceived = txt_inputText2.getText().toString().trim();

                if (sortedList== null){
                    Snackbar.make(getWindow().peekDecorView(), "Please wait", Snackbar.LENGTH_SHORT).show();
                }else if(moneySent.isEmpty()){
                    Snackbar.make(getWindow().peekDecorView(), "Please add some value in money sent by client", Snackbar.LENGTH_LONG).show();

                }else if(Integer.parseInt(moneySent)>due) {
                    Snackbar.make(getWindow().peekDecorView(), "Sorry, received amount can't be more than total dues", Snackbar.LENGTH_LONG).show();
                }else{
                    updateDues(moneySent, moneyReceived);
                    progressDialog.show();
                    progressDialog.setMessage("Updating");
                }

                if (!moneyReceived.isEmpty()){
                    addRecievedMoney(moneyReceived, today);
                }




            }
        });
    }

    private Date getFirstDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }


    private void addRecievedMoney(String moneyReceived,String today ){
        databaseReference = FirebaseDatabase.getInstance().getReference("ReceivedAmountInBank").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final String uniqueId = databaseReference.push().getKey();

        ReceivedMoneyInBank receivedMoneyInBank = new ReceivedMoneyInBank(moneyReceived, today);

        databaseReference.child(uniqueId).setValue(receivedMoneyInBank).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Toast.makeText(MoneyAddActivity.this, "Added", Toast.LENGTH_SHORT).show();
                //getActivity().recreate();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MoneyAddActivity.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void updateDues(String moneySent, String moneyReceived) {
        amount = Integer.parseInt(moneySent);

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
                                        Toast.makeText(MoneyAddActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                                       // progressDialog.dismiss();
                                        txt_inputText1.setText(" ");
                                        if (amount == 0){
                                            progressDialog.dismiss();
                                            Toast.makeText(MoneyAddActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();

                                        }
                                        Log.d("1st case--", String.valueOf(dues));




                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception ) {
                                        Toast.makeText(MoneyAddActivity.this, "Please wait", Toast.LENGTH_SHORT).show();

                                        progressDialog.dismiss();


                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception ) {
                                Toast.makeText(MoneyAddActivity.this, "Something Went Wrong"+exception, Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();

                            }
                        });

                        amount = amount - dues; //30




                    } else if (dues > amount && dues > 0) {

                        int totalPrice = Integer.parseInt(sortedList.get(i).getTotalPrice()); ///80

                        int paymentReceived = totalPrice - dues; //20

                        int newPayment = paymentReceived + amount; //70
                        Log.d("2nd case--", String.valueOf(newPayment));


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
                                        Toast.makeText(MoneyAddActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();

                                        txt_inputText1.setText(" ");
                                        progressDialog.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception ) {
                                        Toast.makeText(MoneyAddActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                                        progressDialog.dismiss();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception ) {
                                Toast.makeText(MoneyAddActivity.this, "Something Went Wrong"+exception, Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();

                            }
                        });

                        amount = 0; //0



                    }


                }

            }


        }




    }




}