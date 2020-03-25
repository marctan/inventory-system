package com.inventory.myinventorysystem.inventorysystem.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.databinding.SupplyReportsRowBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ReportSuppliesAdapter extends RecyclerView.Adapter<ReportSuppliesAdapter.MyViewHolder> {
    private List<Product> products;
    private Context ctx;

    public ReportSuppliesAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SupplyReportsRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.supply_reports_row
        ,parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
        if (product.getImageURI() != null) {
            holder.binding.productImage.setImageURI(Uri.parse(product.getImageURI()));
        } else {
            holder.binding.productImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.photo_placeholder_icon_80));
        }
    }

    @Override
    public int getItemCount() {
        if(products == null) {
            return 0;
        }
        return products.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        SupplyReportsRowBinding binding;

        private MyViewHolder(@NonNull SupplyReportsRowBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Product product) {
            binding.setProduct(product);
            binding.executePendingBindings();
        }
    }

    public void setItem(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }
}
