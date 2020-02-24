package com.inventory.myinventorysystem.inventorysystem.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.Screens.ProductDetail;
import com.inventory.myinventorysystem.inventorysystem.database.Product;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SupplyAdapter extends RecyclerView.Adapter<SupplyAdapter.MyViewHolder> {
    List<Product> products;
    Context ctx;

    public SupplyAdapter(Context ctx, List<Product> products) {
        this.products = products;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supply_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productName.setText(product.getName());
        holder.description.setText(product.getDescription());
        holder.quantity.setText(String.valueOf(product.getQuantity()));
        if (product.getImageURI() != null) {
            holder.productImage.setImageURI(Uri.parse(product.getImageURI()));
        } else {
            holder.productImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.avatar));
        }
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
        @BindView(R.id.productQty)
        TextView quantity;
        @BindView(R.id.description)
        TextView description;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
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
