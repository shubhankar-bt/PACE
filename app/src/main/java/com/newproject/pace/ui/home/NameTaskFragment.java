package com.newproject.pace.ui.home;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newproject.pace.Adapter.NameViewAdapter;
import com.newproject.pace.Model.getContactName;
import com.newproject.pace.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NameTaskFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<getContactName> list;
    FirebaseAuth firebaseAuth;
    private DatabaseReference reference,dbRef;
    private NameViewAdapter adapter;
    private LottieAnimationView lottiProgress;
    private TextView noData;


    String name ;

    public NameTaskFragment() {
        // required empty public constructor.
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.name_task, container, false);


        recyclerView = view.findViewById(R.id.CardR);
        lottiProgress = view.findViewById(R.id.lottiProgress);
        noData = view.findViewById(R.id.DebitCardNoData);


        reference = FirebaseDatabase.getInstance().getReference("contactsWithNames");
        contactsView();



        return view;
    }



    private void contactsView() {
        lottiProgress.setVisibility(View.VISIBLE);

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

                         name = snapshot.getKey();



                        contactVO = new getContactName();
                        contactVO.setFullName(name);

                        contactVOList.add(contactVO);

                        Log.i("datachecking", name);
                        // Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();

                    }

                    DatabaseReference resultRef = dbRef.child(name);


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
    }
}
