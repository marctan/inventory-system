package com.example.marcqtan.inventorysystem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcqtan.inventorysystem.database.Product;
import com.example.marcqtan.inventorysystem.database.Request;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
    List<Request> requests;
    List<Product> products;
    Context ctx;

    public RequestAdapter(Context ctx, List<Request> requests, List<Product> products) {
        this.requests = requests;
        this.ctx = ctx;
        this.products = products;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Request request = requests.get(position);
        Product requested_product = products.get(position);
        holder.productName.setText(request.getProductName());
        holder.requestedQuantity.setText(String.valueOf(request.getQuantityRequest()));
        holder.requestorName.setText(request.getRequestorName());
        if (requested_product.getImageURI() != null) {
            holder.productImage.setImageURI(Uri.parse(requested_product.getImageURI()));
        } else {
            holder.productImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.avatar));
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.productImage)
        ImageView productImage;
        @BindView(R.id.productName)
        TextView productName;
        @BindView(R.id.requestedQuantity)
        TextView requestedQuantity;
        @BindView(R.id.requestorName)
        TextView requestorName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
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

}
