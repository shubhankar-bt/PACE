package com.newproject.pace.ui.home;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newproject.pace.Adapter.NameViewAdapter;
import com.newproject.pace.Adapter.TaskViewPagerAdapter;
import com.newproject.pace.AddDetailsActivity;
import com.newproject.pace.ContactListActivity;
import com.newproject.pace.Model.getContactName;
import com.newproject.pace.R;
import com.newproject.pace.databinding.FragmentHomeBinding;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.transform.Result;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FloatingActionButton contacts;
    private static final int REQ_PICK_CONTACT = 3;
    private static final int CONTACT_PERMISSION_CODE = 1;


    private RecyclerView recyclerView;
    private List<getContactName> list;
    FirebaseAuth firebaseAuth;
    private DatabaseReference reference,dbRef;
    private NameViewAdapter adapter;
    private LottieAnimationView lottiProgress;
    private TextView noData;

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    float v = 0;


    public HomeFragment() {
        // required empty public constructor.
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        contacts = root.findViewById(R.id.contacts);



        ///
        tabLayout = root.findViewById(R.id.tabLayout);
        viewPager = root.findViewById(R.id.viewPager);


        tabLayout.addTab(tabLayout.newTab().setText("Clients"));
        tabLayout.addTab(tabLayout.newTab().setText("Orders"));

        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);
        tabLayout.setHorizontalScrollBarEnabled(false);
        tabLayout.setSmoothScrollingEnabled(true);

        PagerAdapter adapter = new TaskViewPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(new TaskViewPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });



     //   reference = FirebaseDatabase.getInstance().getReference("contactsWithNames");

     //   contactsView();



        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getContext(), ContactListActivity.class);
//                startActivity(i);

                if(checkContactPermission()) {
                    openContactsDialog();
                } else {
                    requestContactPermission();
                }
            }
        });


       // homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);



        return root;
    }

    // If permission is granted, then open contact box
    private void openContactsDialog() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQ_PICK_CONTACT);
    }

    // Check if the user has the permission granted
    private boolean checkContactPermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    // Invoke request permission dialog
    private void requestContactPermission() {
        String[] permission = {Manifest.permission.READ_CONTACTS};
        ActivityCompat.requestPermissions(getActivity(), permission, CONTACT_PERMISSION_CODE);
    }

    // After the permission is granted, open the contact dialog
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CONTACT_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openContactsDialog();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_CONTACT) {
            if (resultCode == RESULT_OK ) {
                Cursor cursor1, cursor2;
                Uri uri = data.getData();

                cursor1 = getContext().getContentResolver().query(uri, null, null, null, null);
                if(cursor1.moveToFirst()) {
                    String contactId = cursor1.getString(cursor1.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String contactName = cursor1.getString(cursor1.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String hasNumber = cursor1.getString(cursor1.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if("1".equals(hasNumber)) {
                        cursor2 = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                        while(cursor2.moveToNext()) {
                            String contactNumber = cursor2.getString(cursor2.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            // Use contactName and contactNumber for your purposes
                            Intent i = new Intent(getContext(), AddDetailsActivity.class);
                            i.putExtra("name", contactName);
                            i.putExtra("number", contactNumber);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            getContext().startActivity(i);
                        }
                        cursor2.close();
                    }
                }
                cursor1.close();
                //edtName.setText(contactName);
                //edtNumber.setText(number);
            }
        }
    }


    private void contactsView() {
        lottiProgress.setVisibility(View.VISIBLE);

        DatabaseReference dbref3 = reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        /*<<<<<<<<<<<<<<--------Hey my name is Shubhankar Das-------->>>>>>>>>>>>>>*/


        dbRef= reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
        }
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                List<getContactName> contactVOList = new ArrayList();
                getContactName contactVO;

                if(!datasnapshot.exists()){
                    lottiProgress.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);


                }else {
                    for (DataSnapshot snapshot: datasnapshot.getChildren()){
                        //getContactName data = snapshot.getKey();
                        //list.add(data);

                        String name = snapshot.getKey();

                        contactVO = new getContactName();
                        contactVO.setFullName(name);

                        contactVOList.add(contactVO);

                        Log.i("datachecking", name);
                        // Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();

                    }

                    lottiProgress.setVisibility(View.GONE);
                    noData.setVisibility(View.GONE);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new NameViewAdapter(contactVOList, getContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                lottiProgress.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}