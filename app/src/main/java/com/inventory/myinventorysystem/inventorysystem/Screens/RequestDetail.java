package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.database.InventoryDatabase;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.database.Request;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestDetail extends AppCompatActivity {

    @BindView(R.id.txtProductName)
    TextView productName;
    @BindView(R.id.txtDateRequested)
    TextView dateRequested;
    @BindView(R.id.txtQty)
    TextView qty;
    @BindView(R.id.txtRequestor)
    TextView requestor;
    @BindView(R.id.productImage)
    ImageView productImage;
    @BindView(R.id.btnApprove)
    Button approve;
    @BindView(R.id.btnDeny)
    Button deny;
    @BindView(R.id.txtStatus)
    TextView status;
    @BindView(R.id.txtStatusView)
    TextView statusLabel;

    Product product;
    Request request;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        ButterKnife.bind(this);

        int product_id = getIntent().getIntExtra("product_id", 0);
        int request_id = getIntent().getIntExtra("request_id", 0);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Request Details");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new GetRequestedProduct(this, request_id, product_id).execute();

        if (MainActivity.isAdmin) {
            statusLabel.setVisibility(View.GONE);
            status.setVisibility(View.GONE);
            approve.setVisibility(View.VISIBLE);
            deny.setVisibility(View.VISIBLE);
            approve.setText("APPROVE");
            approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ApproveRequest(RequestDetail.this, request_id, product_id).execute();
                }
            });

            deny.setText("DENY");
            deny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DenyRequest(RequestDetail.this, request_id).execute();
                }
            });
        } else {
            statusLabel.setVisibility(View.VISIBLE);
            status.setVisibility(View.VISIBLE);
            approve.setText("CANCEL REQUEST");
            deny.setVisibility(View.GONE);
            approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CancelRequest(RequestDetail.this, request_id).execute();
                }
            });

        }
    }


    private static class ApproveRequest extends AsyncTask<Void, Void, Void> {
        WeakReference<RequestDetail> act;
        int requestId;
        int productId;

        ApproveRequest(RequestDetail act, int requestId, int productId) {
            this.act = new WeakReference<>(act);
            this.productId = productId;
            this.requestId = requestId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String formatedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            //update the request table
            InventoryDatabase.getInstance(act.get().getApplicationContext()).requestDao().approveRequest(
                    formatedDate, MainActivity.userID, true, requestId, 1);

            //update the qty in products table
            InventoryDatabase.getInstance(act.get().getApplicationContext()).productsDao().updateQuantity(
                    productId, act.get().request.getQuantityRequest());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(act.get().getApplicationContext(), "Request Approved!", Toast.LENGTH_SHORT).show();
            act.get().finish();
        }
    }

    private static class DenyRequest extends AsyncTask<Void, Void, Void> {
        WeakReference<RequestDetail> act;
        int requestId;

        DenyRequest(RequestDetail act, int requestId) {
            this.act = new WeakReference<>(act);
            this.requestId = requestId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String formatedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            //update the request table
            InventoryDatabase.getInstance(act.get().getApplicationContext()).requestDao().approveRequest(
                    formatedDate, MainActivity.userID, false, requestId, 2);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(act.get().getApplicationContext(), "Request Denied!", Toast.LENGTH_SHORT).show();
            act.get().finish();
        }
    }

    private static class CancelRequest extends AsyncTask<Void, Void, Void> {
        WeakReference<RequestDetail> act;
        int requestId;

        CancelRequest(RequestDetail act, int requestId) {
            this.act = new WeakReference<>(act);
            this.requestId = requestId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            InventoryDatabase.getInstance(act.get().getApplicationContext()).requestDao().cancelRequest(requestId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(act.get().getApplicationContext(), "Request Cancelled!", Toast.LENGTH_SHORT).show();
            act.get().finish();
        }
    }

    private static class GetRequestedProduct extends AsyncTask<Void, Void, Void> {
        WeakReference<RequestDetail> act;
        int requestId;
        int productId;

        GetRequestedProduct(RequestDetail act, int requestId, int productId) {
            this.act = new WeakReference<>(act);
            this.requestId = requestId;
            this.productId = productId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            act.get().product = InventoryDatabase.getInstance(act.get().getApplicationContext()).productsDao().getProduct(productId);
            act.get().request = InventoryDatabase.getInstance(act.get().getApplicationContext()).requestDao().getRequest(requestId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            RequestDetail activity = act.get();
            String status = "Unknown";
            activity.productName.setText(activity.request.getProductName());
            activity.dateRequested.setText(activity.request.getDateRequested());
            activity.qty.setText(String.valueOf(activity.request.getQuantityRequest()));
            activity.requestor.setText(activity.request.getRequestorName());

            switch (activity.request.getStatus()) {
                case 0:
                    status = "PENDING";
                    break;
                case 1:
                    status = "APPROVED";
                    break;
                case 2:
                    status = "DENIED";
                    break;
                case 3:
                    status = "CANCELLED";
                    break;
            }
            activity.status.setText(status);

            if (activity.product.getImageURI() != null) {
                activity.productImage.setImageURI(Uri.parse(activity.product.getImageURI()));
            } else {
                activity.productImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.photo_placeholder_icon_250));
            }

            if(status.equals("PENDING")) {
                activity.approve.setVisibility(View.VISIBLE);
            } else {
                activity.approve.setVisibility(View.GONE);
            }
            super.onPostExecute(aVoid);
        }
    }
}
