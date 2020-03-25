package com.inventory.myinventorysystem.inventorysystem.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inventory.myinventorysystem.inventorysystem.Adapters.ReportRequestAdapter;
import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.database.Request;
import com.inventory.myinventorysystem.inventorysystem.databinding.RequestReportFragmentBinding;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.RequestViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ReportRequestFragment extends Fragment {
    private List<Request> requests;

    private ReportRequestAdapter adapter;
    private RequestViewModel requestViewModel;
    private RequestReportFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RequestReportFragmentBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();

        requests = new ArrayList<>();

        adapter = new ReportRequestAdapter(getContext());
        binding.rvrequestreport.setAdapter(adapter);
        binding.rvrequestreport.setHasFixedSize(true);
        binding.rvrequestreport.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(RequestViewModel.class);

        requestViewModel.getApprovedRequestByMonth().observe(getViewLifecycleOwner(), new Observer<List<Request>>() {
            @Override
            public void onChanged(List<Request> newRequests) {
                requests.clear();
                requests.addAll(newRequests);
            }
        });

        requestViewModel.getProductFromRequest(true).observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> newProducts) {
                getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
                adapter.setItem(requests, newProducts);
            }
        });
    }
}