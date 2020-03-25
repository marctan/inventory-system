package com.inventory.myinventorysystem.inventorysystem.Screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.inventory.myinventorysystem.inventorysystem.R;
import com.inventory.myinventorysystem.inventorysystem.SearchProvider.SearchSuggestionProvider;
import com.inventory.myinventorysystem.inventorysystem.Adapters.SupplyAdapter;
import com.inventory.myinventorysystem.inventorysystem.AssortedUtility.SwipeToDelete;
import com.inventory.myinventorysystem.inventorysystem.database.Product;
import com.inventory.myinventorysystem.inventorysystem.databinding.ActivitySupplyBinding;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.ProductViewModel;
import com.inventory.myinventorysystem.inventorysystem.viewmodel.RequestViewModel;

import java.util.List;

public class SupplyActivity extends AppCompatActivity {

    SupplyAdapter adapter;
    SearchView searchView;
    ProductViewModel productViewModel;
    RequestViewModel requestViewModel;
    ActivitySupplyBinding binding;

    private void enableSwipeToDelete() {
        if (MainActivity.isAdmin) {
            SwipeToDelete swipeToDeleteCallback = new SwipeToDelete(this) {
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    final int position = viewHolder.getAdapterPosition();
                    Product product = productViewModel.getProducts().getValue().get(position);
                    productViewModel.delete(product);
                    requestViewModel.deleteByID(product.getId());
                }
            };

            ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
            itemTouchhelper.attachToRecyclerView(binding.rvSupply);
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
                adapter.setProducts(productViewModel.getProducts().getValue());
                return true;
            }
        });

        return true;
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            binding.progressBar.setVisibility(View.VISIBLE);

            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            productViewModel.queryProducts(query + "%");
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
        binding = ActivitySupplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar myToolbar = binding.myToolbar;
        myToolbar.setTitle("Supplies");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.progressBar.setVisibility(View.VISIBLE);
        requestViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(RequestViewModel.class);
        productViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(ProductViewModel.class);
        productViewModel.getProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                if (products.size() == 0) {
                    binding.noProducts.setVisibility(View.VISIBLE);
                } else {
                    binding.noProducts.setVisibility(View.GONE);
                }

                adapter.setProducts(products);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        productViewModel.getProductsFromQuery().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                if (products.size() == 0) {
                    binding.noProducts.setVisibility(View.VISIBLE);
                } else {
                    binding.noProducts.setVisibility(View.GONE);
                }
                adapter.setProducts(products);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        binding.rvSupply.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SupplyAdapter(SupplyActivity.this);
        binding.rvSupply.setAdapter(adapter);
        binding.rvSupply.setHasFixedSize(true);

        enableSwipeToDelete();

        binding.setAdmin(MainActivity.isAdmin);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupplyActivity.this, AddProductActivity.class);
                startActivityForResult(i, 1);
            }
        });

        if (MainActivity.isAdmin) {
            binding.rvSupply.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0 || dy < 0 && binding.fab.isShown()) {
                        binding.fab.hide();
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        binding.fab.show();
                    }

                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1 || requestCode == 2) && resultCode == Activity.RESULT_OK) {
            if (searchView != null) {
                binding.myToolbar.collapseActionView();
            }
        }
    }
}
