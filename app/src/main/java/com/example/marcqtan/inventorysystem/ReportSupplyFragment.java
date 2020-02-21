package com.example.marcqtan.inventorysystem;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * Created by Marc Q. Tan on 21/02/2020.
 */
public class ReportSupplyFragment extends Fragment {

    List<Product> products;

    ReportSuppliesAdapter adapter;
    @BindView(R.id.rvsuppliesreport)
    RecyclerView rvsupply;


    public ReportSupplyFragment(List<Product> products) {
        this.products = products;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.supply_report_fragment,container,false);
        ButterKnife.bind(this, v);

        adapter = new ReportSuppliesAdapter(getContext(),products);
        rvsupply.setAdapter(adapter);
        rvsupply.setHasFixedSize(true);
        rvsupply.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }
}