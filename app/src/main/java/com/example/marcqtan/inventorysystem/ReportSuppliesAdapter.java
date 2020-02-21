package com.example.marcqtan.inventorysystem;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marcqtan.inventorysystem.database.Product;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Marc Q. Tan on 21/02/2020.
 */
public class ReportSuppliesAdapter extends RecyclerView.Adapter<ReportSuppliesAdapter.MyViewHolder> {
    List<Product> products;
    Context ctx;

    public ReportSuppliesAdapter(Context ctx, List<Product> products) {
        this.products = products;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.supply_reports_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = products.get(position);
        if (product.getImageURI() != null) {
            holder.productImage.setImageURI(Uri.parse(product.getImageURI()));
        } else {
            holder.productImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.avatar));
        }
        holder.productName.setText(product.getName());
        holder.productDesc.setText(product.getDescription());
        holder.dateAdded.setText(product.getDateAdded());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.productImage)
        ImageView productImage;
        @BindView(R.id.productName)
        TextView productName;
        @BindView(R.id.dateAdded)
        TextView dateAdded;
        @BindView(R.id.productDesc)
        TextView productDesc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
