package com.example.marcqtan.inventorysystem.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marcqtan.inventorysystem.Adapters.ReportRequestAdapter;
import com.example.marcqtan.inventorysystem.AdapterInterface.AdapterInterface;
import com.example.marcqtan.inventorysystem.R;
import com.example.marcqtan.inventorysystem.database.Product;
import com.example.marcqtan.inventorysystem.database.Request;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportRequestFragment extends Fragment implements AdapterInterface {

    List<Product> products;
    List<Request> requests;

    ReportRequestAdapter adapter;
    @BindView(R.id.rvrequestreport)
    RecyclerView rvrequest;


    public ReportRequestFragment(List<Product> products, List<Request> requests) {
        this.products = products;
        this.requests = requests;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.request_report_fragment,container,false);
        ButterKnife.bind(this, v);

        adapter = new ReportRequestAdapter(getContext(), requests, products);
        rvrequest.setAdapter(adapter);
        rvrequest.setHasFixedSize(true);
        rvrequest.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }

    @Override
    public void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }
}