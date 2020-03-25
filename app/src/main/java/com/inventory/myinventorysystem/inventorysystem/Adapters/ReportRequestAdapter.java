package com.inventory.myinventorysystem.inventorysystem.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.database.Request;
import com.inventory.myinventorysystem.inventorysystem.databinding.RequestReportsRowBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ReportRequestAdapter extends RecyclerView.Adapter<ReportRequestAdapter.MyViewHolder> {
    private List<Request> requests;
    private List<Product> products;
    private Context ctx;

    public ReportRequestAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RequestReportsRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
        ,R.layout.request_reports_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Request req = requests.get(position);
        holder.bind(req);
        if (products.get(position).getImageURI() != null) {
            holder.binding.productImage.setImageURI(Uri.parse(products.get(position).getImageURI()));
        } else {
            holder.binding.productImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.photo_placeholder_icon_80));
        }
    }

    @Override
    public int getItemCount() {
        if(requests == null) {
            return 0;
        }
        return requests.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RequestReportsRowBinding binding;

        private MyViewHolder(@NonNull RequestReportsRowBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void bind(Request request) {
            binding.setRequest(request);
            binding.executePendingBindings();
        }
    }

    public void setItem(List<Request> requests, List<Product> products) {
        this.requests = requests;
        this.products = products;
        notifyDataSetChanged();
    }
}
