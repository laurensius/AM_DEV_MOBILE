package com.am.pertaminapps.AppFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.am.pertaminapps.Adapter.AdapterPromo;
import com.am.pertaminapps.AppController.AppController;
import com.am.pertaminapps.Model.Promo;
import com.am.pertaminapps.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentPromo extends Fragment {

    private AdapterPromo adapterPromo= null;
    private RecyclerView rvPromo;
    RecyclerView.LayoutManager mLayoutManager;
    List<Promo> listPromo = new ArrayList<>();

    public FragmentPromo() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterPromo = inflater.inflate(R.layout.fragment_promo, container, false);
        rvPromo = (RecyclerView)inflaterPromo.findViewById(R.id.rv_promo);
        return inflaterPromo;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        rvPromo.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvPromo.setLayoutManager(mLayoutManager);
        adapterPromo= new AdapterPromo(listPromo);
        adapterPromo.notifyDataSetChanged();
        rvPromo.setAdapter(adapterPromo);
        loadPromo();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

}
