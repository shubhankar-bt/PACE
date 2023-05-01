package com.newproject.pace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.newproject.pace.Adapter.ContactAdapter;
import com.newproject.pace.Model.contacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ContactListActivity extends AppCompatActivity {

    Cursor cursor;
    RecyclerView listView;
    Button button;
    TextView noContactText;
    SimpleCursorAdapter adapter;
    ImageView search;
    String number ;
    String name ;
    String id ;
    ProgressDialog pd;



    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);


        // declaring listView using findViewById()
        listView = findViewById(R.id.ListView);
        noContactText = findViewById(R.id.noContactText);
        search = findViewById(R.id.search);

        getContacts();

       search.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Toast.makeText(ContactListActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
           }
       });



    }

    public void getContacts() {

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.


            ContactAdapter contactAdapter = new ContactAdapter(getContacts3(), getApplicationContext());
            listView.setHasFixedSize(true);
            listView.setLayoutManager(new LinearLayoutManager(this));
            listView.setAdapter(contactAdapter);

           // getAllContacts();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private List<String> getContactNames() {
//        List<String> contacts = new ArrayList<>();
//        // Get the ContentResolver
//        ContentResolver cr = getContentResolver();
//        // Get the Cursor of all the contacts
//        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//
//        // Move the cursor to first. Also check whether the cursor is empty or not.
//        if (cursor.moveToFirst()) {
//            // Iterate through the cursor
//            do {
//                // Get the contacts name
//               @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//               @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                contacts.add(name);
//                contacts.add(number);
//            } while (cursor.moveToNext());
//        }
//        // Close the curosor
//        cursor.close();
//
//        return contacts;
//    }

//    public void getContacts2() {
//
//        // create cursor and query the data
//        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//        startManagingCursor(cursor);
//
//        // data is a array of String type which is
//        // used to store Number ,Names and id.
//        String[] data = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID};
//
//
//        List<contacts> contact;
//        contact = new ArrayList<>();
//
//        contact.add(data);
//
//
//        int[] to = {android.R.id.text1, android.R.id.text2};
//        if (data == null){
//            noContactText.setVisibility(View.VISIBLE);
//        }else{
//            noContactText.setVisibility(View.GONE);
//            // creation of adapter using SimpleCursorAdapter class
//
//

//
//           // adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, data, to);
//
//
//        }
//
//
//    }



    @SuppressLint("Range")
    private void getAllContacts() {
        List<contacts> contactVOList = new ArrayList();
        contacts contactVO;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactVO = new contacts();
                    contactVO.setName(name);

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactVO.setNumber(phoneNumber);
                    }

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    contactVOList.add(contactVO);
                }
            }

            ContactAdapter contactAdapter = new ContactAdapter(contactVOList, getApplicationContext());
            listView.setLayoutManager(new LinearLayoutManager(this));
            listView.setAdapter(contactAdapter);
        }
    }



    @SuppressLint("Range")
    private List<contacts> getContacts3() {

        ArrayList<contacts> list = new ArrayList<>();
        Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        cursor.moveToFirst();

        while (cursor.moveToNext()) {

            list.add(new contacts(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    , cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID))));

        }
        cursor.close();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            List<contacts> distinctList = list.stream().filter(distinctByKey(c -> c.getName()))
                    .collect(Collectors.toList());

            return distinctList;
        }
        else {

            return list;
        }
    }

    public static <T> Predicate<T> distinctByKey (final Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }




}