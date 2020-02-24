package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.SearchProvider.SearchSuggestionProvider;
import com.inventory.myinventorysystem.inventorysystem.Adapters.SupplyAdapter;
import com.inventory.myinventorysystem.inventorysystem.AssortedUtility.SwipeToDelete;
import com.inventory.myinventorysystem.inventorysystem.database.InventoryDatabase;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    SearchView searchView;

    @BindView(R.id.my_toolbar)
    Toolbar myToolbar;

    private void enableSwipeToDelete() {
        if (MainActivity.isAdmin) {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        ComponentName componentName = new ComponentName(this, SupplyActivity.class);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        searchView.setQueryHint("Search product name...");

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(2);//2 is the index of col containing suggestion name.
                searchView.setQuery(suggestion, true);//setting suggestion
                return true;
            }
        });

        MenuItem item = menu.findItem(R.id.action_search);
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                new AllProductsAsync(SupplyActivity.this, null).execute(); //show ALL again when hitting the back button
                return true;
            }
        });

        return true;
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            new AllProductsAsync(this, query).execute();//filter search
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SupplyActivity.this,
                SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
        suggestions.clearHistory();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
        setContentView(R.layout.activity_supply);
        ButterKnife.bind(this);

        myToolbar.setTitle("Supplies");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv.setLayoutManager(new LinearLayoutManager(this));
        products = new ArrayList<>();
        adapter = new SupplyAdapter(SupplyActivity.this, products);
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
        //rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));

        enableSwipeToDelete();

        if (MainActivity.isAdmin) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupplyActivity.this, AddProductActivity.class);
                startActivityForResult(i, 1);
            }
        });

        if (MainActivity.isAdmin) {
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0 || dy < 0 && fab.isShown()) {
                        fab.hide();
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        fab.show();
                    }

                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }

        new AllProductsAsync(this, null).execute();//show all
    }

    static class AllProductsAsync extends AsyncTask<Void, Void, List<Product>> {
        WeakReference<SupplyActivity> activity;
        String searchQuery;

        AllProductsAsync(SupplyActivity activity, String query) {
            this.activity = new WeakReference<>(activity);
            this.searchQuery = query;
        }

        @Override
        protected void onPreExecute() {
            activity.get().progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected List<Product> doInBackground(Void... voids) {
            if (searchQuery == null) {
                return InventoryDatabase.getInstance(activity.get().getApplicationContext())
                        .productsDao().getAllProducts();
            } else {
                return InventoryDatabase.getInstance(activity.get().getApplicationContext())
                        .productsDao().getAllProductsByQuery(searchQuery + "%");
            }
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            SupplyActivity sa = activity.get();
            sa.progressBar.setVisibility(View.GONE);
            sa.products.clear();
            sa.products.addAll(products);
            sa.adapter.notifyDataSetChanged();
            if (products.size() == 0) {
                sa.no_products.setVisibility(View.VISIBLE);
            } else {
                sa.no_products.setVisibility(View.GONE);
            }

            super.onPostExecute(products);
        }
    }

    static class DeleteProduct extends AsyncTask<Void, Void, Void> {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1 || requestCode == 2) && resultCode == Activity.RESULT_OK) {
            if (searchView != null) {
                myToolbar.collapseActionView();
            }
            new AllProductsAsync(this, null).execute();
        }
    }
}
