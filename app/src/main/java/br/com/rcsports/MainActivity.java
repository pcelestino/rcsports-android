package br.com.rcsports;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.rcsports.adapter.ClientsListAdapter;
import br.com.rcsports.adapter.ProductsListAdapter;
import br.com.rcsports.adapter.SalesListAdapter;
import br.com.rcsports.dao.DAO;
import br.com.rcsports.fragment.ClientsFragment;
import br.com.rcsports.fragment.ProductsFragment;
import br.com.rcsports.fragment.SalesFragment;

/**
 * Created by Pedro on 14/12/2014.
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private ActionBar actionBar;
    private ViewPager viewPager;
    private int indexTab;

    // Search components
    private SearchView searchView;

    // Esconde o Keyboard: Utilizado ao precionar o botão ic_add
    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewPager
        MyPagerAdapter mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
//                searchView.setQuery("", false);
                actionBar.setSelectedNavigationItem(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        // ActionBar
        addActionBar();

        // Recupera o estado anterior
        if (savedInstanceState != null) {
            int indexTab = savedInstanceState.getInt("indexTab");
            getActionBar().setSelectedNavigationItem(indexTab);
        }
    }

    private void addActionBar() {

        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab salesTab = actionBar.newTab();
        salesTab.setText("Vendas");
        salesTab.setTabListener(this);
        actionBar.addTab(salesTab);

        ActionBar.Tab clientsTab = actionBar.newTab();
        clientsTab.setText("Clientes");
        clientsTab.setTabListener(this);
        actionBar.addTab(clientsTab);

        ActionBar.Tab productsTab = actionBar.newTab();
        productsTab.setText("Produtos");
        productsTab.setTabListener(this);
        actionBar.addTab(productsTab);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (searchView != null) {
            if (tab.getPosition() == 0) {
                indexTab = 0;
                searchView.setQueryHint("Localizar venda...");
            } else if (tab.getPosition() == 1) {
                indexTab = 1;
                searchView.setQueryHint("Localizar cliente...");
            } else {
                indexTab = 2;
                searchView.setQueryHint("Localizar produto...");
            }
        }
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Search View
        searchView = (SearchView) menu.findItem(R.id.search_menu).getActionView();
        searchView.setFocusable(false);

        if (indexTab == 0) {
            searchView.setQueryHint("Localizar venda...");
        } else if (indexTab == 1) {
            searchView.setQueryHint("Localizar cliente...");
        } else {
            searchView.setQueryHint("Localizar produto...");
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                hideSoftKeyboard(MainActivity.this, searchView);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (indexTab == 0) {

                    ListView listSales = (ListView) findViewById(R.id.list_sales);
                    SalesListAdapter salesListAdapter = (SalesListAdapter) listSales.getAdapter();
                    salesListAdapter.getFilter().filter(newText);

                } else if (indexTab == 1) {

                    ListView listClients = (ListView) findViewById(R.id.list_clients);
                    ClientsListAdapter clientsListAdapter = (ClientsListAdapter) listClients.getAdapter();
                    clientsListAdapter.getFilter().filter(newText);

                } else if (indexTab == 2) {

                    ListView listProducts = (ListView) findViewById(R.id.list_products);
                    ProductsListAdapter productsListAdapter = (ProductsListAdapter) listProducts.getAdapter();
                    productsListAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        if (item.getItemId() == R.id.import_database) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Seus dados serão substituídos!")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DAO.open(MainActivity.this).importDatabase();
                            redraw(indexTab);
                        }
                    })
                    .setNegativeButton(R.string.leave, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            builder.show();

        } else if (item.getItemId() == R.id.export_database) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Deseja exportar os dados?")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DAO.open(MainActivity.this).exportDatabase();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();

        } else if (item.getItemId() == R.id.menu_finish) {
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void redraw(int item) {
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("indexTab", getActionBar().getSelectedNavigationIndex());
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SalesFragment();

                case 1:
                    return new ClientsFragment();

                default:
                    return new ProductsFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
