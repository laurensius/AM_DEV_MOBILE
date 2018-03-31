package com.am.pertaminapps.AppFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.am.pertaminapps.Adapter.AdapterDealer;
import com.am.pertaminapps.Adapter.DetailDealer;
import com.am.pertaminapps.AppController.AppController;
import com.am.pertaminapps.Listener.DealerListener;
import com.am.pertaminapps.Model.Dealer;
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
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class FragmentCariSPBU extends Fragment{

    private MapView mvCariSPBU = null;
    private ScaleBarOverlay mScaleBarOverlay;
    private IMapController mapController;
    private AdapterDealer adapterDealer = null;
    private RecyclerView rvDealer;
    RecyclerView.LayoutManager mLayoutManager;
    List<Dealer> listDealer = new ArrayList<>();
    Timer timer = new Timer();

    Marker userMarker;

    double recent_lat = 0.0;
    double recent_lon = 0.0;


    public FragmentCariSPBU() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getActivity().getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterCariSPBU = inflater.inflate(R.layout.fragment_cari_spbu, container, false);
        mvCariSPBU = (MapView)inflaterCariSPBU.findViewById(R.id.mv_carispbu);
        rvDealer = (RecyclerView)inflaterCariSPBU.findViewById(R.id.rv_dealer);
        return inflaterCariSPBU;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        mvCariSPBU.setTileSource(TileSourceFactory.MAPNIK);
        mvCariSPBU.setBuiltInZoomControls(true);
        mvCariSPBU.setMultiTouchControls(true);
        //Kontrol Skala
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        mScaleBarOverlay = new ScaleBarOverlay(mvCariSPBU);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(width / 2, 10);
        mvCariSPBU.getOverlays().add(mScaleBarOverlay);
        mvCariSPBU.invalidate();

        rvDealer.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvDealer.setLayoutManager(mLayoutManager);
        adapterDealer = new AdapterDealer(listDealer);
        adapterDealer.notifyDataSetChanged();
        rvDealer.setAdapter(adapterDealer);
        rvDealer.addOnItemTouchListener(new DealerListener(getActivity(), new DealerListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childVew, int position) {
                Dealer selectedDealer = adapterDealer.getItem(position);
                Intent intent = new Intent(getActivity(), DetailDealer.class);
                intent.putExtra("id_dealer",selectedDealer.id);
                startActivity(intent);
            }
        }));
        loadSPBU();
        userMarker = new Marker(mvCariSPBU);
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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        timer.cancel();
    }


    public void loadSPBU(){
        String tag_json_obj = "json_obj_req";
        String url = getResources().getString(R.string.api_endpoint).concat(getResources().getString(R.string.api_dealer_list));
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
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
                        Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    void parseData(JSONObject response){
        try{
            JSONObject jsonObject = response;
            boolean init = true;
            int data_count = Integer.parseInt(jsonObject.getString("data_count"));
            if(data_count > 0){
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for(int i=0;i<data_count;i++){
                    JSONObject object_dealer = jsonArray.getJSONObject(i);
                    listDealer.add(new Dealer(
                            R.mipmap.cvspbu,
                            object_dealer.getString("id"),
                            object_dealer.getString("dealer_name"),
                            object_dealer.getString("dealer_addr_street")
                            .concat(", ")
                            .concat(object_dealer.getString("dealer_addr_kelurahan"))
                            .concat(", ")
                            .concat(object_dealer.getString("dealer_addr_kecamatan"))
                            .concat(", ")
                            .concat(object_dealer.getString("dealer_addr_kabupaten"))
                            .concat(", ")
                            .concat(object_dealer.getString("dealer_addr_provinsi")).toString()));
                    if(init){
                        setCenter(Double.parseDouble(object_dealer.getString("geo_lat")),Double.parseDouble(object_dealer.getString("geo_lon")));
                        init = false;
                    }
                    setSPBUMarker(object_dealer.getString("dealer_name"),Double.parseDouble(object_dealer.getString("geo_lat")),Double.parseDouble(object_dealer.getString("geo_lon")));
                }
            }
        }catch (JSONException e){
            Log.d("Error : " , e.getMessage());
        }
        adapterDealer.notifyDataSetChanged();
    }

    void setSPBUMarker(String title,double lat, double lon){
        Drawable iconMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.dw_marker_spbu_merah, null);
        Marker marker = new Marker(mvCariSPBU);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setIcon(iconMarker);
        marker.setTitle(title);
        marker.showInfoWindow();
        mvCariSPBU.getOverlays().add(marker);
        mvCariSPBU.invalidate();
    }

    void setUserMarker(double lat, double lon){
        Drawable iconMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.dw_myloc, null);
        userMarker.setIcon(iconMarker);
        userMarker.setPosition(new GeoPoint(lat, lon));
        userMarker.setTitle("You Are Here!");
        userMarker.showInfoWindow();
        mvCariSPBU.getOverlays().add(userMarker);
        mvCariSPBU.invalidate();
    }

    void removeUserMarker(){
        mvCariSPBU.getOverlays().remove(userMarker);
        mvCariSPBU.invalidate();
    }

    void setCenter(double lat,double lon){
        mapController = mvCariSPBU.getController();
        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(lat, lon);
        mapController.setCenter(startPoint);
    }
}
