package com.am.pertaminapps.AppFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.am.pertaminapps.Adapter.AdapterPromo;
import com.am.pertaminapps.AppController.AppController;
import com.am.pertaminapps.Model.Promo;
import com.am.pertaminapps.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentBeranda extends Fragment {

    private CarouselView carovBeranda;
    private LinearLayout llMenuCariSPBU, llMenuProdukPertamina, llMenuRiwayatTransaksi, llMenuBantuan;
    int[] carovImages = {R.mipmap.carov_image_1, R.mipmap.carov_image_2, R.mipmap.carov_image_3, R.mipmap.carov_image_4, R.mipmap.carov_image_5};

    private AdapterPromo adapterPromo = null;
    private RecyclerView rvPromoTerbaru;
    RecyclerView.LayoutManager mLayoutManager;
    List<Promo> listPromo = new ArrayList<>();


    public FragmentBeranda() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View inflaterBeranda = inflater.inflate(R.layout.fragment_beranda, container, false);
        carovBeranda = (CarouselView)inflaterBeranda.findViewById(R.id.carov_beranda);
        carovBeranda.setImageListener(imageListener);

        llMenuCariSPBU = (LinearLayout)inflaterBeranda.findViewById(R.id.ll_shortcut_1);
        llMenuProdukPertamina = (LinearLayout)inflaterBeranda.findViewById(R.id.ll_shortcut_2);
        llMenuRiwayatTransaksi= (LinearLayout)inflaterBeranda.findViewById(R.id.ll_shortcut_3);
        llMenuBantuan= (LinearLayout)inflaterBeranda.findViewById(R.id.ll_shortcut_4);
        rvPromoTerbaru = (RecyclerView)inflaterBeranda.findViewById(R.id.rv_promo_terbaru);
        return inflaterBeranda;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        llMenuCariSPBU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentCariSPBU fragmentCariSPBU= new FragmentCariSPBU();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_usercustomer, fragmentCariSPBU);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        llMenuProdukPertamina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                navigationView.setCheckedItem(R.id.nav_product);
                FragmentProduct fragmentProduct = new FragmentProduct();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fl_usercustomer, fragmentProduct);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        carovBeranda.setPageCount(carovImages.length);
        rvPromoTerbaru.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvPromoTerbaru.setLayoutManager(mLayoutManager);
        adapterPromo= new AdapterPromo(listPromo);
        adapterPromo.notifyDataSetChanged();
        rvPromoTerbaru.setAdapter(adapterPromo);
        loadPromo();
    }
    
    public void loadPromo(){
        String tag_promo_beranda = "request_promo_beranda";
        String url = getResources().getString(R.string.api_endpoint).concat(getResources().getString(R.string.api_promo_list));
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_promo_beranda);
    }

    void parseData(JSONObject response){
        try{
            JSONObject jsonObject = response;
            int data_count = Integer.parseInt(jsonObject.getString("data_count"));
            if(data_count > 0){
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for(int i=0;i<data_count;i++){
                    JSONObject object_promo = jsonArray.getJSONObject(i);
                    listPromo.add(new Promo(
                            R.mipmap.carov_image_1,
                            object_promo.getString("id"),
                            object_promo.getString("promo_title"),
                            object_promo.getString("promo_tagline"),
                            object_promo.getString("promo_desciption"),
                            object_promo.getString("start_date"),
                            "Belaku sampai :".concat(object_promo.getString("end_date")),
                            object_promo.getString("dealer_name"),
                            object_promo.getString("dealer_addr_street")
                            .concat(", ")
                            .concat(object_promo.getString("dealer_addr_kelurahan"))
                            .concat(", ")
                            .concat(object_promo.getString("dealer_addr_kecamatan"))
                            .concat(", ")
                            .concat(object_promo.getString("dealer_addr_kabupaten"))
                            .concat(", ")
                            .concat(object_promo.getString("dealer_addr_provinsi")).toString(),
                            object_promo.getString("geo_lat"),
                            object_promo.getString("geo_lon")
                            ));
                   }
            }
        }catch (JSONException e){
            Log.d("Error : " , e.getMessage());
        }
        adapterPromo.notifyDataSetChanged();
    }



    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(carovImages[position]);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
