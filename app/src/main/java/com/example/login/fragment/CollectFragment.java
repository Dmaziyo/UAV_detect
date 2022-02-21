package com.example.login.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.login.Data.UAV_Data;
import com.example.login.R;
import com.example.login.utils.RecycleAdapterDemo;

import java.util.ArrayList;
import java.util.List;


public class CollectFragment extends Fragment{
    public static RecyclerView recyclerView;
    public static List<UAV_Data> list;
    public static RecycleAdapterDemo adapterDome;
    private Activity myActivity;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myActivity= (Activity) context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_collect, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);
        list = new ArrayList<>();
        adapterDome =new RecycleAdapterDemo(myActivity,list);
        LinearLayoutManager manager=new LinearLayoutManager(myActivity);
        StaggeredGridLayoutManager stagger=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(stagger);
        recyclerView.setAdapter(adapterDome);
        return v;
    }


}