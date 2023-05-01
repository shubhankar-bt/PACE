package com.newproject.pace.Adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadinganimation.LoadingAnimation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newproject.pace.Model.OrderDetails;
import com.newproject.pace.R;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ReceiptActivity extends AppCompatActivity {

    private RecyclerView recyclerReceipt;
    private ScrollView scrollview;
    private TextView Fullname, Fnumber, overallTotal, overallDues, shareButton, currentDate;
    ArrayList<OrderDetails> listPrivate = new ArrayList<>();
    private ReceiptAdapter adapter;
    private RelativeLayout layoutView;
    String name;
    int dues;
    private LoadingAnimation loadingAnim;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        recyclerReceipt = findViewById(R.id.recyclerReceipt);
        Fullname = findViewById(R.id.name);
        Fnumber = findViewById(R.id.number);
        overallTotal = findViewById(R.id.overallTotal);
        overallDues = findViewById(R.id.overallDues);
        shareButton = findViewById(R.id.shareButton);
        layoutView = findViewById(R.id.layoutView);
        currentDate = findViewById(R.id.currentDate);
        scrollview = findViewById(R.id.scrollview);
        loadingAnim = findViewById(R.id.loadingAnim);


        loadingAnim.setVisibility(View.VISIBLE);
        scrollview.setVisibility(View.INVISIBLE);
        shareButton.setVisibility(View.INVISIBLE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingAnim.setVisibility(View.GONE);
                scrollview.setVisibility(View.VISIBLE);
                shareButton.setVisibility(View.VISIBLE);


            }
        }, 6000);

        Type type = new TypeToken<List<OrderDetails>>() {
        }.getType();
        listPrivate = new Gson().fromJson(getIntent().getStringExtra("private_list"), type);


        Intent in = getIntent();
        name = in.getStringExtra("FullName");
        String number = in.getStringExtra("Number");

        Fullname.setText(name);
        Fnumber.setText(number);


        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        currentDate.setText(formattedDate);



        ArrayList<String> TotalPriceList = new ArrayList<>();
        ArrayList<String> TotalPaymentDoneList = new ArrayList<>();

        for (int i =0; i<listPrivate.size(); i++){
            TotalPriceList.add(listPrivate.get(i).getTotalPrice());
            TotalPaymentDoneList.add(listPrivate.get(i).getPaymentDone());
        }

        ArrayList<Integer> totalList = getIntegerArray(TotalPriceList); //strArrayList is a collection of Strings as you defined.
        ArrayList<Integer> doneList = getIntegerArray(TotalPaymentDoneList); //strArrayList is a collection of Strings as you defined.


        Log.wtf("listinteger", String.valueOf(totalList));

        int total = totalList.stream().mapToInt(Integer::intValue).sum();
        int done = doneList.stream().mapToInt(Integer::intValue).sum();
        dues = (total - done);


        overallTotal.setText(String.valueOf(total));
        overallDues.setText(String.valueOf(dues));






        recyclerReceipt.setHasFixedSize(true);
        recyclerReceipt.setLayoutManager(new LinearLayoutManager(ReceiptActivity.this));
        adapter = new ReceiptAdapter(listPrivate, ReceiptActivity.this);
        recyclerReceipt.setAdapter(adapter);


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = getScreenShotFromView(layoutView);

                if (bitmap != null) {
                    share(bitmap);
                }

            }
        });


    }


    private Bitmap getScreenShotFromView(View v) {

        Bitmap screenshot = null;
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            // Now draw this bitmap on a canvas
            Canvas canvas = new Canvas(screenshot);
            v.draw(canvas);
        } catch (Exception e) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.getMessage());
        }


        return screenshot;
    }

    private void share(Bitmap bitmap){
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd", now);

        String fileName = System.currentTimeMillis() + ".jpg";

        String mal = "Hey "+name+", Kindly find the attached receipt along with this message and clear your pending dues with *PACE*. Your remaining\n" +
                "dues are "+dues+" Rs/-. Click on this below link to pay.\n" +
                "Link - https://tools.apgy.in/upi/Akash+Agarwal/akash.aga314@okicici/ \n\n" +
                "ThankYou";

        String pathofBmp= MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, (fileName), null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "PACE");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mal);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(shareIntent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }
       // ReceiptActivity.this.startActivity(Intent.createChooser(shareIntent, "Choose to share"));
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
}