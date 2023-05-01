package com.newproject.pace.ui.notifications;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kal.rackmonthpicker.RackMonthPicker;
import com.kal.rackmonthpicker.listener.DateMonthDialogListener;
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener;
import com.newproject.pace.Adapter.OrderDetailsAdapter;
import com.newproject.pace.LoginActivity;
import com.newproject.pace.MainActivity;
import com.newproject.pace.Model.Expense;
import com.newproject.pace.Model.OrderDetails;
import com.newproject.pace.Model.ReceivedMoneyInBank;
import com.newproject.pace.R;
import com.newproject.pace.databinding.FragmentNotificationsBinding;
import com.newproject.pace.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private TextView ProfileName, Profilemail, number, revenue, profit, totalPrice, expense, emailTv, count, dues, paymentInBank;
    private ImageView profileImage, logout, back;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference,dbRef, databaseReference;
    private List<OrderDetails> OrderList;
    private List<Expense> expenseList;
    private List<ReceivedMoneyInBank> moneyInBankList;
    private TextView pastMonth, monthname;
    private RelativeLayout addExpense;
    String lastDate;
    String today;
    int total;
    int expenses, bankPayment;
    String startDate;
    int done;
    int profits;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        reference = FirebaseDatabase.getInstance().getReference("contactsWithDates");



        ProfileName = root.findViewById(R.id.ProfileName);
        Profilemail = root.findViewById(R.id.email);
        profileImage = root.findViewById(R.id.profilePic);
        number = root.findViewById(R.id.number);
        logout = root.findViewById(R.id.logout);
        revenue = root.findViewById(R.id.revenue);
        profit = root.findViewById(R.id.profit);
        totalPrice = root.findViewById(R.id.total);
        expense = root.findViewById(R.id.expense);
        emailTv = root.findViewById(R.id.emailTv);
        pastMonth = root.findViewById(R.id.pastMonth);
        monthname = root.findViewById(R.id.monthname);
        addExpense = root.findViewById(R.id.addexpense);
        count = root.findViewById(R.id.count);
        dues = root.findViewById(R.id.dues);
        back = root.findViewById(R.id.back);
        paymentInBank = root.findViewById(R.id.paymentInBank);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null){
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String Pnumber = user.getPhoneNumber();

            ProfileName.setText(name);
            emailTv.setText(email);
            if (Pnumber != null){
                number.setText(Pnumber);
            }else{
                number.setText("No data");
            }

            Picasso.get().load(photoUrl).noFade().into(profileImage);

            logout.setOnClickListener(view -> {
                AuthUI.getInstance()
                        .signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        });
            });

        }

        Date currentDate = new Date();
        today = new SimpleDateFormat("yyyy-M-d").format(currentDate);
        startDate = new SimpleDateFormat("yyyy-M-d").format(getFirstDateOfCurrentMonth());



        Log.d("getFirstDateOfCurrentMonth", startDate);


        showFilteredData(startDate, today);
        fetchExpense(startDate, today);
        paymentReceivedInBank(startDate, today);

        Calendar cal= Calendar.getInstance();

        SimpleDateFormat month_date = new SimpleDateFormat("MMM, yyyy");
        String ma=month_date.format(cal.getTime());
        monthname.setText(ma);

        pastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMonthDialog();
            }
        });

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Add expense of this month")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String text = input.getText().toString().trim();
                                addExpense(text, today);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.expense)
                        .setView(input)
                        .setCancelable(false)
                        .show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });



        return root;
    }

    private void paymentReceivedInBank(String startDate, String today) {
        databaseReference = FirebaseDatabase.getInstance().getReference("ReceivedAmountInBank").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.orderByChild("date")
                .startAt(startDate)
                .endAt(today)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        moneyInBankList = new ArrayList<>();

                        if (!dataSnapshot.exists()) {

                            paymentInBank.setText("0");
                            //profit.setText(String.valueOf(done));


                        } else {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                ReceivedMoneyInBank data = snapshot.getValue(ReceivedMoneyInBank.class);
                                moneyInBankList.add(data);
                                if (moneyInBankList != null) {
                                    ArrayList<String> moneyInBankList1 = new ArrayList<>();


                                    for (int j = 0; j < moneyInBankList.size(); j++) {
                                        moneyInBankList1.add(moneyInBankList.get(j).getReceived());
                                    }

                                    ArrayList<Integer> moneyInBankListMain = getIntegerArray(moneyInBankList1); //strArrayList is a collection of Strings as you defined.


                                    Log.wtf("expenseMainList", String.valueOf(moneyInBankListMain));

                                    bankPayment = moneyInBankListMain.stream().mapToInt(Integer::intValue).sum();

                                    paymentInBank.setText(String.valueOf(bankPayment));





                                }
                            }




                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();


                    }

                });
    }

    private void fetchExpense(String startDate, String today) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Expense").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.orderByChild("date")
                .startAt(startDate)
                .endAt(today)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        expenseList = new ArrayList<>();

                        if (!dataSnapshot.exists()) {

                            expense.setText("0");
                            profit.setText(String.valueOf(done));


                        } else {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                Expense data = snapshot.getValue(Expense.class);
                                expenseList.add(data);
                                if (expenseList != null) {
                                    ArrayList<String> expenseList1 = new ArrayList<>();


                                    for (int j = 0; j < expenseList.size(); j++) {
                                        expenseList1.add(expenseList.get(j).getExpense());
                                    }

                                    ArrayList<Integer> expenseMainList = getIntegerArray(expenseList1); //strArrayList is a collection of Strings as you defined.


                                    Log.wtf("expenseMainList", String.valueOf(expenseMainList));

                                    expenses = expenseMainList.stream().mapToInt(Integer::intValue).sum();

                                    expense.setText(String.valueOf(expenses));



                                    if (done == 0){
                                        profits = 0;
                                    }else{
                                        profits = done-expenses;
                                    }
                                    profit.setText(String.valueOf(profits));





                                }
                            }




                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();


                    }

                });
    }

    private void addExpense(String text, String ma) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Expense").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final String uniqueId = databaseReference.push().getKey();

        Expense expense = new Expense(text, ma);

        databaseReference.child(uniqueId).setValue(expense).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
                fetchExpense(startDate, today);
                //getActivity().recreate();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();


            }
        });



    }

    private void showMonthDialog() {
        new RackMonthPicker(getActivity())
                .setLocale(Locale.ENGLISH)
                .setColorTheme(Color.parseColor("#e57373"))
                .setPositiveButton(new DateMonthDialogListener() {
                    @Override
                    public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M");
                        String firstDate = year + "-" + (month) + "-" + startDate;

                        Calendar calendar = Calendar.getInstance();
                        int cMonth = calendar.get(Calendar.MONTH) + 1;
                        int cYear = calendar.get(Calendar.YEAR);


                        if (cMonth == month && cYear == year){
                             lastDate = today;
                        }else{
                             lastDate = year + "-" + (month) + "-" + endDate;
                        }


                        Log.wtf("Cmonth", String.valueOf(monthLabel));
                        Log.wtf("endDate", String.valueOf(lastDate));

                        monthname.setText(monthLabel);
                        showFilteredData(firstDate, lastDate);

                        fetchExpense(firstDate, lastDate);
                        paymentReceivedInBank(firstDate, lastDate);


                    }
                })
                .setNegativeButton(new OnCancelMonthDialogListener() {
                    @Override
                    public void onCancel(AlertDialog dialog) {

                        dialog.dismiss();
                    }
                }).show();
    }


    private Date getFirstDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    private void showFilteredData(String startAt, String today) {
        dbRef= reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Log.w("sshvshsvcshbcsb", startAt+"  &  " +today);

        dbRef.orderByChild("deliveryDate")
                .startAt(startAt)
                .endAt(today)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        OrderList = new ArrayList<>();

                        if (!dataSnapshot.exists()) {

                            revenue.setText("No data");
                            totalPrice.setText("No data");
                            dues.setText("No data");
                            count.setText("No data");
                            profit.setText("No data");
                            done = 0;



                            Log.d("listwithDates", String.valueOf(dataSnapshot));


                        } else {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                OrderDetails data = snapshot.getValue(OrderDetails.class);
                                OrderList.add(data);
                                if (OrderList != null) {
                                    ArrayList<String> TotalPriceList = new ArrayList<>();
                                    ArrayList<String> TotalPaymentDoneList = new ArrayList<>();
                                    ArrayList<String> dueList = new ArrayList<>();

                                    for (int j = 0; j < OrderList.size(); j++) {
                                        TotalPriceList.add(OrderList.get(j).getTotalPrice());
                                        TotalPaymentDoneList.add(OrderList.get(j).getPaymentDone());
                                        dueList.add(OrderList.get(j).getDues());
                                    }

                                    ArrayList<Integer> totalList = getIntegerArray(TotalPriceList); //strArrayList is a collection of Strings as you defined.
                                    ArrayList<Integer> doneList = getIntegerArray(TotalPaymentDoneList); //strArrayList is a collection of Strings as you defined.
                                    ArrayList<Integer> duesList = getIntegerArray(dueList); //strArrayList is a collection of Strings as you defined.


                                    Log.wtf("listinteger", String.valueOf(totalList));

                                    total = totalList.stream().mapToInt(Integer::intValue).sum();
                                    done = doneList.stream().mapToInt(Integer::intValue).sum();

                                    int finalDues = duesList.stream().mapToInt(Integer::intValue).sum();

                                    int due = (total - done);
                                    int countNo = OrderList.size();


                                    revenue.setText(String.valueOf(done));
                                    totalPrice.setText(String.valueOf(total));
                                    count.setText(String.valueOf(countNo));
                                    dues.setText(String.valueOf(due));



                                }
                            }




                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();


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



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}