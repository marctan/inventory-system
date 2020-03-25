package com.inventory.myinventorysystem.inventorysystem.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.Screens.RequestDetail;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.database.Request;
import com.inventory.myinventorysystem.inventorysystem.databinding.RequestRowBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
    private List<Request> requests;
    private List<Product> products;
    private Context ctx;

    public RequestAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RequestRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.request_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Request request = requests.get(position);
        holder.bind(request);
        Product requested_product = products.get(position);
        if (requested_product.getImageURI() != null) {
            holder.binding.productImage.setImageURI(Uri.parse(requested_product.getImageURI()));
        } else {
            holder.binding.productImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.photo_placeholder_icon_80));
        }
    }

    @Override
    public int getItemCount() {
        if (requests == null) {
            return 0;
        }
        return requests.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RequestRowBinding binding;

        private void bind(Request request) {
            binding.setRequest(request);
            binding.executePendingBindings();
        }

        private MyViewHolder(@NonNull RequestRowBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            itemView.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ctx, RequestDetail.class);
                    i.putExtra("product_id", products.get(getAdapterPosition()).getId());
                    i.putExtra("request_id", requests.get(getAdapterPosition()).getId());
                    ctx.startActivity(i);
                }
            });
        }
    }

    public void setRequests(List<Request> requests, List<Product> products) {
        this.requests = requests;
        this.products = products;
        notifyDataSetChanged();
    }

}
