package distributor.w2a.com.distributor.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import distributor.w2a.com.distributor.R;
import distributor.w2a.com.distributor.fragment.AbstractFragment;
import distributor.w2a.com.distributor.fragment.AbstractSearchBottomSheetFragment;
import distributor.w2a.com.distributor.fragment.AbstractSearchFragment;
import distributor.w2a.com.distributor.interfaces.AbstractSearchInterface;
import distributor.w2a.com.distributor.network.ApiClient;

public abstract class AbstractDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem item;
    private SearchView searchView;
    private AbstractSearchInterface fragment;

    protected abstract int getMenuId();

    public void hideItem() {
        if (item != null && item.getActionView().isShown()) {
            item.collapseActionView();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abstract);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                , drawer
                , toolbar
                , R.string.navigation_drawer_open
                , R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ((NavigationMenuView) navigationView.getChildAt(0)).addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        navigationView.inflateMenu(getMenuId());
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_abstract_dashboard, menu);
        return true;
    }

    public void setVisibleFragment(AbstractSearchInterface fragment) {
        this.fragment = fragment;
    }

    public void setActionBarTitle(String pageTitle) {
        try {
            getSupportActionBar().setTitle(pageTitle);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.action_search:
                    if (AbstractDashboardActivity.this.fragment instanceof AbstractSearchFragment) {
                    } else if (AbstractDashboardActivity.this.fragment instanceof AbstractSearchBottomSheetFragment) {
                    } else return false;

                    if (fragment.getAdapter() == null)
                        return false;

                    item.expandActionView();
                    this.item = item;
                    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                    searchView = (SearchView) item.getActionView();
                    searchView.setSearchableInfo(searchManager
                            .getSearchableInfo(getComponentName()));
                    searchView.setMaxWidth(Integer.MAX_VALUE);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            if (fragment.getAdapter() == null)
                                return false;
                            fragment.getAdapter().getFilter().filter(s);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            fragment.getAdapter().getFilter().filter(s);
                            return false;
                        }
                    });
                    return true;
            }
        } catch (Exception e) {

        }
        return super.onOptionsItemSelected(item);
    }

    public void clearSearchView() {
        try {
            searchView.setQuery("", true);
        } catch (Exception e) {
        }
    }

    public void searchWithoutUser() {
        try {
            searchView.setQuery(searchView.getQuery(), true);
        } catch (Exception e) {
        }
    }

    public boolean doesSearchContainText() {
        try {
            return searchView.getQuery().length() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void addFragmentToBackStack(Fragment fragment) {
        addFragmentToBackStack(fragment, fragment.getClass().getSimpleName());
    }

    public void addFragmentToBackStack(Fragment fragment, String tag) {
        if (fragment == null) {
            setDrawerOpen(false);
            return;
        }
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (f != null && f.getClass() == fragment.getClass()) {
            // Pop last fragment if a request was made to add a fragment to back stack
            // that was already being displayed.
            getSupportFragmentManager().popBackStackImmediate();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment, tag)
                .addToBackStack(null)
                .commit();
        setDrawerOpen(false);
    }

    public void setDrawerOpen(boolean shouldOpen) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (shouldOpen) {
                if (!drawer.isDrawerOpen(Gravity.START)) {
                    drawer.openDrawer(Gravity.START);
                }
            } else {
                if (drawer.isDrawerOpen(Gravity.START)) {
                    drawer.closeDrawer(Gravity.START);
                }
            }
        }
    }

    public void forceBackPress() {
        super.onBackPressed();
        try {
            getSupportFragmentManager().getFragments().get(0);
        } catch (Exception e) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = null;
            try {
                fragment = getSupportFragmentManager().getFragments().get(0);
            } catch (Exception e) {
                forceBackPress();
                return;
            }
            ((AbstractFragment) fragment).onBackPressed();
        }
    }
}