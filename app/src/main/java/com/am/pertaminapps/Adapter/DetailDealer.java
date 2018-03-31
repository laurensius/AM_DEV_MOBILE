package com.am.pertaminapps.Adapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.am.pertaminapps.AppController.AppController;
import com.am.pertaminapps.Model.DealerProduct;
import com.am.pertaminapps.Model.Promo;
import com.am.pertaminapps.R;
import com.am.pertaminapps.UserCostumer;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class DetailDealer extends AppCompatActivity {

    private TextView tvNamaDealer,tvAddr;
    private RecyclerView rvDealerProduct,rvDealerPromo;
    private AdapterDealerProduct adapterDealerProduct = null;
    private AdapterPromo adapterPromo= null;
    List<DealerProduct> listDealerProduct = new ArrayList<>();
    List<Promo> listPromo = new ArrayList<>();
    RecyclerView.LayoutManager mLayoutManager;
    private MapView mvDetaiDealer;
    private ScaleBarOverlay mScaleBarOverlay;
    private IMapController mapController;
    private String id_dealer;
    Marker userMarker;
    Timer timer = new Timer();

    double recent_lat = 0.0;
    double recent_lon = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dealer);


        getSupportActionBar().setTitle("SPBU");
        
        rvDealerProduct = (RecyclerView)findViewById(R.id.rv_dealer_product);
        rvDealerProduct.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this);
        rvDealerProduct.setLayoutManager(mLayoutManager);
        adapterDealerProduct = new AdapterDealerProduct(listDealerProduct);
        adapterDealerProduct.notifyDataSetChanged();
        rvDealerProduct.setAdapter(adapterDealerProduct);

        rvDealerPromo = (RecyclerView)findViewById(R.id.rv_promo_terbaru);
        rvDealerPromo.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this);
        rvDealerPromo.setLayoutManager(mLayoutManager);
        adapterPromo = new AdapterPromo(listPromo);
        adapterDealerProduct.notifyDataSetChanged();
        rvDealerPromo.setAdapter(adapterPromo);

        mvDetaiDealer = (MapView)findViewById(R.id.mv_detaildealer);
        mvDetaiDealer.setTileSource(TileSourceFactory.MAPNIK);
        mvDetaiDealer.setBuiltInZoomControls(true);
        mvDetaiDealer.setMultiTouchControls(true);
        //Kontrol Skala
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        mScaleBarOverlay = new ScaleBarOverlay(mvDetaiDealer);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(width / 2, 10);
        mvDetaiDealer.getOverlays().add(mScaleBarOverlay);
        mvDetaiDealer.invalidate();
        tvNamaDealer = (TextView)findViewById(R.id.tv_nama_dealer);
        tvAddr = (TextView)findViewById(R.id.tv_addr);

        userMarker = new Marker(mvDetaiDealer);
        setUserMarker(UserCostumer.lat,UserCostumer.lon);

        class MyTimerTask extends TimerTask {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        removeUserMarker();
                        Log.d("User run :", "on Runable");
                        Log.d("User lat :", String.valueOf(UserCostumer.lat));
                        Log.d("User lon :", String.valueOf(UserCostumer.lon));
                        setUserMarker(UserCostumer.lat,UserCostumer.lon);
                        recent_lat = UserCostumer.lat;
                        recent_lon = UserCostumer.lon;
                    }
                });
            }
        }
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask,1000,5000);


        Intent intent = getIntent();
        id_dealer = intent.getStringExtra("id_dealer");
        detailDealer();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        timer.cancel();
    }

    public void detailDealer(){
        String request_dealer_detail = "request_dealer_detail";
        String url = getResources().getString(R.string.api_endpoint).concat(getResources().getString(R.string.api_dealer_detail)).concat("/").concat(id_dealer).concat("/");
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading . . . ");
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.d("Response : ", response.toString());
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(DetailDealer.this,"Error",Toast.LENGTH_LONG).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, request_dealer_detail);
    }

    void parseData(JSONObject response){
        try{
            JSONObject jsonResponse = response;
            int data_count = Integer.parseInt(jsonResponse.getString("data_count"));
            if(data_count > 0){
                JSONObject objData = jsonResponse.getJSONObject("data");
                JSONArray arrayDealer = objData.getJSONArray("dealer");
                JSONObject objDealer = arrayDealer.getJSONObject(0);
                String address = new String("Alamat : ");
                String jalan = objDealer.getString("dealer_addr_street");
                String kelurahan = objDealer.getString("dealer_addr_kelurahan");
                String kecamatan = objDealer.getString("dealer_addr_kecamatan");
                String kabupaten = objDealer.getString("dealer_addr_kabupaten");
                String provinsi = objDealer.getString("dealer_addr_provinsi");
                Double lat = Double.parseDouble(objDealer.getString("geo_lat"));
                Double lon = Double.parseDouble(objDealer.getString("geo_lon"));
                Drawable iconMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.dw_marker_spbu_merah, null);
                Marker marker = new Marker(mvDetaiDealer);
                marker.setIcon(iconMarker);
//                marker.setIcon(getResources().getDrawable(R.drawable.marker));
                marker.setPosition(new GeoPoint(lat, lon));
                marker.setTitle(objDealer.getString("dealer_name"));
                marker.showInfoWindow();
                mapController = mvDetaiDealer.getController();
                mapController.setZoom(13);
                GeoPoint startPoint = new GeoPoint(lat, lon);
                mapController.setCenter(startPoint);
                mvDetaiDealer.getOverlays().add(marker);
                mvDetaiDealer.invalidate();
                tvNamaDealer.setText(objDealer.getString("dealer_name"));
                tvAddr.setText(address.concat(jalan)
                        .concat(", ")
                        .concat(kelurahan)
                        .concat(", ")
                        .concat(kecamatan)
                        .concat(", ")
                        .concat(kabupaten)
                        .concat(", ")
                        .concat(provinsi));
                JSONArray arrayDealerProduct = objData.getJSONArray("product");
                if(arrayDealerProduct.length()>0){
                    for(int x=0;x<arrayDealerProduct.length();x++){
                        JSONObject objDealerProduct = arrayDealerProduct.getJSONObject(x);
                        int icon = 0;
                        if(objDealerProduct.getString("id_product").equals("1")){
                            icon = R.drawable.dw_product_premium;
                        }else
                        if(objDealerProduct.getString("id_product").equals("2")){
                            icon = R.drawable.dw_product_pertamax;
                        }else
                        if(objDealerProduct.getString("id_product").equals("3")){
                            icon = R.drawable.dw_product_pertalite;
                        }else{
                            icon = R.drawable.dw_product_biosolar;
                        }
                        listDealerProduct.add(new DealerProduct(
                                icon,
                                objDealerProduct.getString("id"),
                                objDealerProduct.getString("product_name"),
                                "Rp " + objDealerProduct.getString("price")));
                    }
                }
                JSONArray arrayDealerPromo = objData.getJSONArray("promo");
                if(arrayDealerPromo.length()>0){
                    for(int x=0;x<arrayDealerPromo.length();x++){
                        JSONObject objDealerPromo = arrayDealerPromo.getJSONObject(x);
                        listPromo.add(new Promo(
                                R.mipmap.carov_image_1,
                                objDealerPromo.getString("id"),
                                objDealerPromo.getString("promo_title"),
                                objDealerPromo.getString("promo_tagline"),
                                objDealerPromo.getString("promo_desciption"),
                                objDealerPromo.getString("start_date"),
                                "Belaku sampai :".concat(objDealerPromo.getString("end_date")),
                                objDealerPromo.getString("dealer_name"),
                                objDealerPromo.getString("dealer_addr_street")
                                        .concat(", ")
                                        .concat(objDealerPromo.getString("dealer_addr_kelurahan"))
                                        .concat(", ")
                                        .concat(objDealerPromo.getString("dealer_addr_kecamatan"))
                                        .concat(", ")
                                        .concat(objDealerPromo.getString("dealer_addr_kabupaten"))
                                        .concat(", ")
                                        .concat(objDealerPromo.getString("dealer_addr_provinsi")).toString(),
                                objDealerPromo.getString("geo_lat"),
                                objDealerPromo.getString("geo_lon")
                        ));
                    }
                }

            }
        }catch (JSONException e){
            Log.d("Error : " , e.getMessage());
        }
        adapterDealerProduct.notifyDataSetChanged();
        adapterPromo.notifyDataSetChanged();
    }

    void setUserMarker(double lat, double lon){
        Drawable iconMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.dw_myloc, null);
        userMarker.setIcon(iconMarker);
        userMarker.setPosition(new GeoPoint(lat, lon));
        userMarker.setTitle("You Are Here!");
        userMarker.showInfoWindow();
        mvDetaiDealer.getOverlays().add(userMarker);
        mvDetaiDealer.invalidate();
    }

    void removeUserMarker(){
        mvDetaiDealer.getOverlays().remove(userMarker);
        mvDetaiDealer.invalidate();
    }

}
