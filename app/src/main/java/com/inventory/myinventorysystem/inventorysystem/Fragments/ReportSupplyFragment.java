package com.inventory.myinventorysystem.inventorysystem.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inventory.myinventorysystem.inventorysystem.Adapters.ReportSuppliesAdapter;
import com.inventory.myinventorysystem.inventorysystem.AdapterInterface.AdapterInterface;
import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.database.Product;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportSupplyFragment extends Fragment implements AdapterInterface {

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

    @Override
    public void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }
}