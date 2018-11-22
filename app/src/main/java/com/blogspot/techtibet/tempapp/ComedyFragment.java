package com.blogspot.techtibet.tempapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ComedyFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    RecyclerView mainRecycler;
    List<Videos> videoList;
    VideosRecyclerAdapter videosRecyclerAdapter;


    public ComedyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_comedy, container, false);
        mAuth=FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();
        mainRecycler=view.findViewById(R.id.comedy_recycler);
        videoList=new ArrayList<>();
        videosRecyclerAdapter=new VideosRecyclerAdapter(getContext(),videoList);
        mainRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mainRecycler.setHasFixedSize(true);
        mainRecycler.setAdapter(videosRecyclerAdapter);
        if(mAuth!=null){
            mStore.collection("Comedy").orderBy("real_time", Query.Direction.DESCENDING).addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if(documentSnapshots!=null){
                        if(!documentSnapshots.isEmpty()){
                            for(DocumentChange doc:documentSnapshots.getDocumentChanges()){
                                if(doc.getType()==DocumentChange.Type.ADDED){
                                    String video_id=doc.getDocument().getId();
                                    Videos videos=doc.getDocument().toObject(Videos.class).withId(video_id);
                                    videoList.add(videos);
                                    videosRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }

                }
            });
        }


        return view;
    }

}
