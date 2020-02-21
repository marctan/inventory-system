package com.example.marcqtan.inventorysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcqtan.inventorysystem.database.InventoryDatabase;
import com.example.marcqtan.inventorysystem.database.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SupplyActivity extends AppCompatActivity {

    @BindView(R.id.rvSupply)
    RecyclerView rv;
    SupplyAdapter adapter;
    List<Product> products;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.no_products)
    TextView no_products;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private void enableSwipeToDelete() {
        SwipeToDelete swipeToDeleteCallback = new SwipeToDelete(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();

                new DeleteProduct(SupplyActivity.this, position).execute();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Supplies");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv.setLayoutManager(new LinearLayoutManager(this));
        products = new ArrayList<>();
        adapter = new SupplyAdapter(this, products);
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
        //rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
        enableSwipeToDelete();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupplyActivity.this, AddProductActivity.class);
                startActivity(i);
            }
        });

        rv.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 && fab.isShown())
                {
                    fab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AllProductsAsync(this).execute();
    }

    static class AllProductsAsync extends AsyncTask<Void, Void, List<Product>> {
        WeakReference<SupplyActivity> activity;

        AllProductsAsync (SupplyActivity activity){
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            activity.get().progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<Product> doInBackground(Void... voids) {
            return InventoryDatabase.getInstance(activity.get().getApplicationContext())
                    .productsDao().getAllProducts();
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            SupplyActivity sa = activity.get();
            sa.progressBar.setVisibility(View.GONE);
            sa.products.clear();
            sa.products.addAll(products);
            sa.adapter.notifyDataSetChanged();
            if(products.size() == 0) {
                sa.no_products.setVisibility(View.VISIBLE);
            } else {
                sa.no_products.setVisibility(View.GONE);
            }
            super.onPostExecute(products);
        }
    }

    static class DeleteProduct extends AsyncTask<Void ,Void ,Void> {
        WeakReference<SupplyActivity> activity;
        int position;

        DeleteProduct(SupplyActivity activity, int position) {
            this.activity = new WeakReference<>(activity);
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //When deleting a product, Delete it from request also
            InventoryDatabase.getInstance(activity.get().getApplicationContext()).requestDao().deleteRequestByProductId(
                    activity.get().products.get(position).getId());


            InventoryDatabase.getInstance(activity.get().getApplicationContext()).productsDao().deleteProduct(activity.get().products.get(position));
            activity.get().products.remove(position);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            activity.get().adapter.notifyDataSetChanged();
        }
    }

}
