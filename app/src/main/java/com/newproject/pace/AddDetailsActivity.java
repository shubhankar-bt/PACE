package com.newproject.pace;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.newproject.pace.Adapter.NameViewAdapter;
import com.newproject.pace.Model.AppPreferences;
import com.newproject.pace.Model.OrderDetails;
import com.newproject.pace.Model.getContactName;
import com.newproject.pace.ui.dashboard.DashboardFragment;
import com.newproject.pace.ui.home.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.text.Regex;

public class AddDetailsActivity extends AppCompatActivity {

    StorageReference storageReference;
    private ProgressDialog pd;
    private DatabaseReference reference1, dbRef1, reference2, dbRef2;
    private Button buttonSubmit;
    private Bitmap bitmap = null;
    Boolean checkBoxState;
    CheckBox PaymentCompleted;
    String checkboxValue = "No";
    private ArrayList<getContactName> list;
    Boolean fromTaskDetails;
    int appCount = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);


        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String number = intent.getStringExtra("number");
        fromTaskDetails = intent.getBooleanExtra("fromTaskDetails", false);


        EditText nameEditText = (EditText) findViewById(R.id.name);
        String fullName = nameEditText.getText().toString();

        EditText currentWeightEditText = (EditText) findViewById(R.id.current_weight);
        String totalPrice = currentWeightEditText.getText().toString();

        EditText heightEditText = (EditText) findViewById(R.id.height);
        String paymentDone = heightEditText.getText().toString();

        EditText goalWeightEditText = (EditText) findViewById(R.id.goal_weight);
        String OrderId = goalWeightEditText.getText().toString();

        EditText ageEditText = (EditText) findViewById(R.id.age);
        String OrderName = ageEditText.getText().toString();

        EditText phoneEditText = (EditText) findViewById(R.id.Phone);
        String phone = phoneEditText.getText().toString();

        EditText addressEditText = (EditText) findViewById(R.id.address);
        String DeliveryDate = addressEditText.getText().toString();


        EditText assignedTo = (EditText) findViewById(R.id.assignedTo);
        String assignedToName = addressEditText.getText().toString();

        //initiate a check box
        PaymentCompleted = (CheckBox) findViewById(R.id.conditions);

        //check current state of the check box
        checkBoxState = PaymentCompleted.isChecked();

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


        addressEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        AddDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");


                                addressEditText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });


        appCount = AppPreferences.getInstance().getPref().getInt("appCount", 1);
        Log.i("TAG1", "appCount: " + appCount);

        String orderIdUnique = System.currentTimeMillis()+"_PaceOrder_"+String.valueOf(appCount);
        goalWeightEditText.setText(orderIdUnique);



        nameEditText.setText(name);
        phoneEditText.setText(number);


        buttonSubmit = findViewById(R.id.buttonSubmit);




        pd = new ProgressDialog(this);
        reference1 = FirebaseDatabase.getInstance().getReference("contactsWithNames");
        reference2 = FirebaseDatabase.getInstance().getReference("contactsWithDates");

     //   reference2 = FirebaseDatabase.getInstance().getReference("NameList");

        if (fromTaskDetails){
            nameEditText.setFocusable(false);
            nameEditText.setInputType(InputType.TYPE_NULL);
            phoneEditText.setFocusable(false);
            phoneEditText.setInputType(InputType.TYPE_NULL);

        }

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            // @Override
            public void onClick(View v) {

                EditText nameEditText = (EditText) findViewById(R.id.name);
                String fullName = nameEditText.getText().toString();

                EditText currentWeightEditText = (EditText) findViewById(R.id.current_weight);
                String totalPrice = currentWeightEditText.getText().toString();

                EditText heightEditText = (EditText) findViewById(R.id.height);
                String paymentDone = heightEditText.getText().toString();

                EditText goalWeightEditText = (EditText) findViewById(R.id.goal_weight);
                String OrderId = goalWeightEditText.getText().toString();

                EditText ageEditText = (EditText) findViewById(R.id.age);
                String OrderName = ageEditText.getText().toString();

                EditText phoneEditText = (EditText) findViewById(R.id.Phone);
                String phone = phoneEditText.getText().toString();

                EditText addressEditText = (EditText) findViewById(R.id.address);
                String DeliveryDate = addressEditText.getText().toString();

                EditText assignedTo = (EditText) findViewById(R.id.assignedTo);
                String assignedToName = assignedTo.getText().toString();











                if (!isValidName(nameEditText.getText().toString().trim())){
                    nameEditText.setError("Can contain only text, Please check and enter valid name");
                    nameEditText.requestFocus();
                }else  if (totalPrice.isEmpty()){
                    Toast.makeText(getBaseContext(), "Enter total price", Toast.LENGTH_LONG).show();
                    currentWeightEditText.requestFocus();
                }
                else  if (totalPrice.isEmpty()){
                    Toast.makeText(getBaseContext(), "Enter total price", Toast.LENGTH_LONG).show();
                    currentWeightEditText.requestFocus();
                }else  if (paymentDone.isEmpty()){
                    Toast.makeText(getBaseContext(), "Please Enter Received amount", Toast.LENGTH_SHORT).show();
                    heightEditText.requestFocus();
                }else  if (assignedToName.isEmpty()){
                    Toast.makeText(getBaseContext(), "Please Provide All The Details", Toast.LENGTH_SHORT).show();
                    assignedTo.requestFocus();
                }else  if (OrderId.isEmpty()){
                    Toast.makeText(getBaseContext(), "Please Provide All The Details", Toast.LENGTH_SHORT).show();
                    goalWeightEditText.requestFocus();
                }else  if (OrderName.isEmpty()){
                    Toast.makeText(getBaseContext(), "Please Provide All The Details", Toast.LENGTH_SHORT).show();
                    ageEditText.requestFocus();
                }else  if (phone.isEmpty()){
                    Toast.makeText(getBaseContext(), "Please Provide All The Details", Toast.LENGTH_SHORT).show();
                    phoneEditText.requestFocus();
                }else  if (DeliveryDate.isEmpty()){
                    Toast.makeText(getBaseContext(), "Please Provide All The Details", Toast.LENGTH_SHORT).show();
                    addressEditText.requestFocus();
                }else if (paymentDone.equals(totalPrice) && !PaymentCompleted.isChecked()){
                    PaymentCompleted.setError("");
                    PaymentCompleted.requestFocus();
                    Toast.makeText(getBaseContext(), "Please check payment done because full amount received!", Toast.LENGTH_SHORT).show();
                }else if (!paymentDone.equals(totalPrice) && PaymentCompleted.isChecked()){
                    PaymentCompleted.setError(" ");
                    PaymentCompleted.requestFocus();
                    Toast.makeText(getBaseContext(), "Please uncheck payment done because full amount not received!", Toast.LENGTH_SHORT).show();
                }
                else if (bitmap == null){
                    pd.setMessage("Adding...");
                    pd.show();
                    int dues = Integer.parseInt(totalPrice) - Integer.parseInt(paymentDone);

                    insertData(fullName,totalPrice , paymentDone, orderIdUnique, OrderName, phone, DeliveryDate, assignedToName, String.valueOf(dues));
                }
            }

        });




    }


    public boolean isValidName(String name)
    {
        String specialCharacters="!#$%&'()*+,-./:;<=>?@[]^_`{|}~";
        String str2[]=name.split("");
        int count=0;
        for (int i=0;i<str2.length;i++)
        {
            if (specialCharacters.contains(str2[i]))
            {
                count++;
            }
        }

        if (name!=null && count==0 )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void insertData(String fullName, String totalPrice, String paymentDone, String orderId, String orderName, String phone, String deliveryDate, String assignedToName, String dues) {


        dbRef1 = reference1.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbRef2 = reference2.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
      //  dbRef2 = reference2.child(FirebaseAuth.getInstance().getCurrentUser().getUid());



//        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                list = new ArrayList<>();
//
//                getContactName newGetContact = new getContactName(fullName, phone);
//                final String uniqueId = dbRef.push().getKey();
//                newGetContact.setUniqueId(uniqueId);
//
//                if(!snapshot.exists()){
//
//                }else {
//                    for (DataSnapshot snapshot2: snapshot.getChildren()){
//                        getContactName data = snapshot2.getValue(getContactName.class);
//
//                        if (data.getFullName().equals(fullName)){
//                            newGetContact = data;
//                            Log.i("datacheckingss", String.valueOf(data.getFullName()));
//                           // Log.e("fefefefefff", String.valueOf(newGetContact.getAllOrder().size()));
//
//                        }
//
//
//                    }
//
//
//
//
//                }
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        final String uniqueId = dbRef1.push().getKey();



        OrderDetails orderDetails = new OrderDetails(fullName, totalPrice, paymentDone, orderId , orderName, phone, deliveryDate,checkboxValue, uniqueId, assignedToName, dues);

     //   newGetContact.addOrder(orderDetails);




        dbRef1.child(fullName).child(uniqueId).setValue(orderDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                /////
                dbRef2.child(uniqueId).setValue(orderDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        pd.dismiss();
                        Toast.makeText(AddDetailsActivity.this, "Data added", Toast.LENGTH_SHORT).show();

                        appCount++;
                        AppPreferences.getInstance().getEditor().putInt("appCount", appCount).apply();
                        Log.i("TAG2", "appCount: " + appCount);

                        if(fromTaskDetails){
                            Intent intent = new Intent(AddDetailsActivity.this, TaskDetailsActivity.class);
                            intent.putExtra("FromAddDetailsActivity", "1");
                            intent.putExtra("CustomerName",fullName);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(AddDetailsActivity.this, MainActivity.class);
                            intent.putExtra("FromAddDetailsActivity", "0");
                            startActivity(intent);
                            finish();


                        }


                    }




                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception ) {
                        pd.dismiss();
                        Toast.makeText(AddDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                    }
                });


                ///////

            }




        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception ) {
                pd.dismiss();
                Toast.makeText(AddDetailsActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

            }
        });




    }




}