package com.inventory.myinventorysystem.inventorysystem.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.Screens.ProductDetail;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.databinding.SupplyRowBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class SupplyAdapter extends RecyclerView.Adapter<SupplyAdapter.MyViewHolder> {
    private List<Product> products;
    private Context ctx;

    public SupplyAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SupplyRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.supply_row, parent, false);
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
        if (products != null) {
            return products.size();
        } else {
            return 0;
        }
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        SupplyRowBinding binding;

        private void bind(Product product) {
            binding.setProduct(product);
            binding.executePendingBindings();
        }

        private MyViewHolder(@NonNull SupplyRowBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            itemView.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ctx, ProductDetail.class);
                    i.putExtra("product_id", products.get(getAdapterPosition()).getId());
                    i.putExtra("imageURI", products.get(getAdapterPosition()).getImageURI());
                    ((Activity) ctx).startActivityForResult(i, 2);
                }
            });
        }
    }

}
