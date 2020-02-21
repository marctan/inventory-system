package com.example.marcqtan.inventorysystem;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marcqtan.inventorysystem.database.Product;
import com.example.marcqtan.inventorysystem.database.Request;

import java.util.List;
import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Marc Q. Tan on 21/02/2020.
 */
public class ReportRequestAdapter extends RecyclerView.Adapter<ReportRequestAdapter.MyViewHolder> {
    List<Request> requests;
    List<Product> products;
    Context ctx;

    public ReportRequestAdapter(Context ctx, List<Request> requests, List<Product> products) {
        this.requests = requests;
        this.products = products;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.request_reports_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Request req = requests.get(position);

        if (products.get(position).getImageURI() != null) {
            holder.productImage.setImageURI(Uri.parse(products.get(position).getImageURI()));
        } else {
            holder.productImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.avatar));
        }

        holder.requestorName.setText(req.getRequestorName());
        holder.productName.setText(req.getProductName());
        holder.qty.setText(String.valueOf(req.getQuantityRequest()));
        holder.dateRequested.setText(req.getDateRequested());
        holder.dateApproved.setText(req.getDateApproved());
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.productImage)
        ImageView productImage;
        @BindView(R.id.requestorName)
        TextView requestorName;
        @BindView(R.id.productName)
        TextView productName;
        @BindView(R.id.requestedQuantity)
        TextView qty;
        @BindView(R.id.dateRequested)
        TextView dateRequested;
        @BindView(R.id.dateApproved)
        TextView dateApproved;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
