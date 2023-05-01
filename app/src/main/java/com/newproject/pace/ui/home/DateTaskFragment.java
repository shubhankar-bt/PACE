package com.newproject.pace.ui.home;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newproject.pace.Adapter.DateViewAdapter;
import com.newproject.pace.Adapter.NameViewAdapter;
import com.newproject.pace.Adapter.OrderDetailsAdapter;
import com.newproject.pace.AddDetailsActivity;
import com.newproject.pace.Model.GetContactWithDate;
import com.newproject.pace.Model.OrderDetails;
import com.newproject.pace.Model.getContactName;
import com.newproject.pace.R;
import com.newproject.pace.TaskDetailsActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import kotlin.random.Random;


public class DateTaskFragment extends Fragment {




    private RecyclerView recyclerView;
    private List<GetContactWithDate> list;
    FirebaseAuth firebaseAuth;
    private DatabaseReference reference,dbRef;
    private OrderDetailsAdapter adapter;
    private LottieAnimationView lottiProgress;
    private TextView noData;
    private List<OrderDetails> OrderList;
    private EditText from;
    private EditText to;
    private Button search;
    private RelativeLayout relativeLayout;
    private NestedScrollView myScrollViw;

    private SmartMaterialSpinner<String> spProvince;
    private List<String> provinceList;



    public DateTaskFragment() {
        // required empty public constructor.
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_task, container, false);


        recyclerView = view.findViewById(R.id.DebitCardR);
        lottiProgress = view.findViewById(R.id.lottiProgress);
        noData = view.findViewById(R.id.DebitCardNoData);
        from = view.findViewById(R.id.from);
        to = view.findViewById(R.id.to);
        search = view.findViewById(R.id.serachBtn);
        relativeLayout = view.findViewById(R.id.relative_layout);
        myScrollViw = view.findViewById(R.id.myScrollViw);




        relativeLayout.setVisibility(View.GONE);
        recyclerView.setNestedScrollingEnabled(false);



        reference = FirebaseDatabase.getInstance().getReference("contactsWithDates");



        spProvince = view.findViewById(R.id.spinner1);
       // spEmptyItem = view.findViewById(R.id.sp_empty_item);
        provinceList = new ArrayList<>();

        provinceList.add("All orders");
        provinceList.add("Orders of last 1 Week");
        provinceList.add("Orders of last 1 Month");
        provinceList.add("Orders of last 2 Months");
        provinceList.add("Custom");


        spProvince.setItem(provinceList);
        spProvince.setSelection(0);


        spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
               // Toast.makeText(requireContext(), provinceList.get(position), Toast.LENGTH_SHORT).show();
                spProvince.setErrorText("You are viewing "+provinceList.get(position));
                spProvince.setErrorTextColor(Color.parseColor("#fdad5c"));
                spProvince.setErrorTextAlignment(SmartMaterialSpinner.TextAlignment.ALIGN_LEFT);

                showOrders(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });



        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");


                                from.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

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
        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");


                                to.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

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

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromtxt = from.getText().toString().trim();
                String toTxt = to.getText().toString().trim();

                if (fromtxt.isEmpty()){
                    Toast.makeText(requireContext(), "Please select start date", Toast.LENGTH_SHORT).show();
                }else if(toTxt.isEmpty()){
                    Toast.makeText(requireContext(), "Please select start date", Toast.LENGTH_SHORT).show();
                }else if (!isDateAfter(fromtxt,toTxt)){
                    Toast.makeText(requireContext(), "End date can't be before start date", Toast.LENGTH_SHORT).show();

                }else{
                    showFilteredData(fromtxt, toTxt);
                }
            }
        });









        return view;
    }





    public static boolean isDateAfter(String startDate,String endDate)
    {
        try
        {
            String myFormatString = "yyyy-M-dd"; // for example
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            if (date1.after(startingDate))
                return true;
            else
                return false;
        }
        catch (Exception e)
        {

            return false;
        }
    }




    private void showOrders(int position) {



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        Date currentDate = new Date();


        String today = new SimpleDateFormat("yyyy-M-d").format(currentDate);


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date date = calendar.getTime();
        String oneMonthAgo = sdf.format(date);


        calendar.add(Calendar.MONTH, -2);
        Date date2 = calendar.getTime();
        String twoMonthAgo = sdf.format(date2);


        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -7);
        Date previousDate = c.getTime();
        String oneWeekAgo = sdf.format(previousDate);


        Log.d("oneWeekAgo", String.valueOf(oneWeekAgo));
        Log.d("twoMonthAgo", String.valueOf(twoMonthAgo));
        Log.d("oneMonthAgo", String.valueOf(oneMonthAgo));


        if (position == 1){
            showFilteredData(oneWeekAgo, today);
            relativeLayout.setVisibility(View.GONE);
        }else if (position ==2){
            showFilteredData(oneMonthAgo, today);
            relativeLayout.setVisibility(View.GONE);
        }else if (position == 3){
            showFilteredData(twoMonthAgo, today);
            relativeLayout.setVisibility(View.GONE);
        }else if(position==4){
            recyclerView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);

        }else{
            showAll();
            relativeLayout.setVisibility(View.GONE);
        }

    }

    private void showAll() {
        lottiProgress.setVisibility(View.VISIBLE);
        dbRef= reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        OrderList = new ArrayList<>();

                        if (!dataSnapshot.exists()) {
                            lottiProgress.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show();
                            Log.d("listwithDates", String.valueOf(dataSnapshot));


                        } else {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                OrderDetails data = snapshot.getValue(OrderDetails.class);
                                OrderList.add(data);
                                Log.d("listwithDates", String.valueOf(data));


                            }

                            lottiProgress.setVisibility(View.GONE);
                            noData.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            adapter = new OrderDetailsAdapter(OrderList, getActivity(), noData, true);
                            recyclerView.setAdapter(adapter);


                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        lottiProgress.setVisibility(View.GONE);

                    }

                });

    }

    private void showFilteredData(String startAt, String today) {
        lottiProgress.setVisibility(View.VISIBLE);
        dbRef= reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Log.w("startatandendat", startAt+"  &  " +today);

        dbRef.orderByChild("deliveryDate")
                .startAt(startAt)
                .endAt(today)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        OrderList = new ArrayList<>();

                        if (!dataSnapshot.exists()) {
                            lottiProgress.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                           // noData.setVisibility(View.VISIBLE);
                            Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show();
                            Log.d("listwithDates", String.valueOf(dataSnapshot));


                        } else {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                OrderDetails data = snapshot.getValue(OrderDetails.class);
                                OrderList.add(data);
                                Log.d("listwithDates", String.valueOf(data));


                            }


                            lottiProgress.setVisibility(View.GONE);
                            noData.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setHasFixedSize(true);
                            focusOnView();
                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            adapter = new OrderDetailsAdapter(OrderList, getActivity(), noData, true);
                            recyclerView.setAdapter(adapter);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        lottiProgress.setVisibility(View.GONE);

                    }

                });

    }

    private final void focusOnView(){
        myScrollViw.post(new Runnable() {
            @Override
            public void run() {
                myScrollViw.smoothScrollTo(0, recyclerView.getTop());
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
