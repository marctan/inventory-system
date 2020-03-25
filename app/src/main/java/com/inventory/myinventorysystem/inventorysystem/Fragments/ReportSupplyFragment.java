package com.inventory.myinventorysystem.inventorysystem.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inventory.myinventorysystem.inventorysystem.Adapters.ReportSuppliesAdapter;
import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.databinding.SupplyReportFragmentBinding;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.ProductViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ReportSupplyFragment extends Fragment {

    private ReportSuppliesAdapter adapter;
    private ProductViewModel productViewModel;

    private SupplyReportFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SupplyReportFragmentBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();

        adapter = new ReportSuppliesAdapter(getContext());
        binding.rvsuppliesreport.setAdapter(adapter);
        binding.rvsuppliesreport.setHasFixedSize(true);
        binding.rvsuppliesreport.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        productViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance((getActivity().getApplication()))).get(ProductViewModel.class);

        productViewModel.getAddedProductByMonth().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> newProducts) {
                getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
                adapter.setItem(newProducts);
            }
        });
    }
}