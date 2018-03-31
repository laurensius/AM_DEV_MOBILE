package com.am.pertaminapps;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.am.pertaminapps.AppFragment.FragmentBeranda;
import com.am.pertaminapps.AppFragment.FragmentCariSPBU;
import com.am.pertaminapps.AppFragment.FragmentProduct;
import com.am.pertaminapps.AppFragment.FragmentPromo;

public class UserCostumer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment fragment;
    private Dialog dialBox;
    private TextView tvHeaderUsername,tvHeaderEmail;

    private LocationManager locationManager;
    private LocationListener listener;

    public static double lat;
    public static double lon;

    public static String pref_user_id;
    public static String pref_user_username;
    public static String pref_user_password;
    public static String pref_user_full_name;
    public static String pref_user_dob;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_costumer);

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
        editorPreferences = sharedPreferences.edit();
        pref_user_id =  sharedPreferences.getString("pref_user_id",null);
        pref_user_username=  sharedPreferences.getString("pref_user_username",null);
        pref_user_password=  sharedPreferences.getString("pref_user_password",null);
        pref_user_full_name =  sharedPreferences.getString("pref_user_full_name",null);
        pref_user_dob =  sharedPreferences.getString("pref_user_dob",null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        View header = navigationView.getHeaderView(0);
        tvHeaderUsername = (TextView)header.findViewById(R.id.tv_header_username);
        tvHeaderEmail= (TextView)header.findViewById(R.id.tv_header_email);
        tvHeaderUsername.setText(pref_user_username);
        tvHeaderEmail.setText(pref_user_full_name);


        getSupportActionBar().setTitle("Beranda");
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fl_usercustomer, new FragmentBeranda());
        tx.commit();

        dialBox = createDialogBox();

        lat = 0.0;
        lon = 0.0;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.d("LATITUDE  :", String.valueOf(lat));
                Log.d("LONGITUDE :", String.valueOf(lon));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_gps();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_gps();
                break;
            default:
                break;
        }
    }

    void configure_gps() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            dialBox.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_costumer_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        jalankanFragment(id);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        jalankanFragment(item.getItemId());
        return true;
    }

    public void jalankanFragment(int id){
        fragment = null;
        if (id == R.id.nav_beranda) {
            fragment = new FragmentBeranda();
            getSupportActionBar().setTitle("Beranda");
        }else
        if (id == R.id.nav_search) {
            fragment = new FragmentCariSPBU();
            getSupportActionBar().setTitle("Cari SPBU");
        }else
        if (id == R.id.nav_product) {
            fragment = new FragmentProduct();
            getSupportActionBar().setTitle("Produk PERTAMINA");
        }else
        if (id == R.id.nav_promo) {
            fragment = new FragmentPromo();
            getSupportActionBar().setTitle("Promo");
        }else
        if ( id == R.id.nav_logout){
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("BPBD_ON_MOBILE", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            Intent i = new Intent(UserCostumer.this,Login.class);
            startActivity(i);
            finish();
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_usercustomer, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private Dialog createDialogBox(){
        dialBox = new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah Anda yakin akan keluar dari aplikasi ?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialBox.dismiss();
                    }
                })
                .create();
        return dialBox;
    }
}
