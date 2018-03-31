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

import com.am.pertaminapps.Adapter.AdapterProduct;
import com.am.pertaminapps.Adapter.DetailDealer;
import com.am.pertaminapps.Adapter.DetailProduct;
import com.am.pertaminapps.AppController.AppController;
import com.am.pertaminapps.Listener.ProductListener;
import com.am.pertaminapps.Model.Product;
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

public class FragmentProduct extends Fragment {

    private AdapterProduct adapterProduct= null;
    private RecyclerView rvProduct;
    RecyclerView.LayoutManager mLayoutManager;
    List<Product> listProduct = new ArrayList<>();

    public FragmentProduct() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflaterProduct = inflater.inflate(R.layout.fragment_product, container, false);
        rvProduct = (RecyclerView)inflaterProduct.findViewById(R.id.rv_product);
        return inflaterProduct;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        rvProduct.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvProduct.setLayoutManager(mLayoutManager);
        adapterProduct= new AdapterProduct(listProduct);
        adapterProduct.notifyDataSetChanged();
        rvProduct.setAdapter(adapterProduct);
        rvProduct.addOnItemTouchListener(new ProductListener(getActivity(), new ProductListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childVew, int position) {
                Product selectedProduct = adapterProduct.getItem(position);
                Intent intent = new Intent(getActivity(), DetailProduct.class);
                intent.putExtra("id_product",selectedProduct.id);
                startActivity(intent);
            }
        }));
        loadProduct();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void loadProduct(){
        String tag_request_product = "request_product";
        String url = getResources().getString(R.string.api_endpoint).concat(getResources().getString(R.string.api_product_list));
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
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_request_product);
    }

    void parseData(JSONObject response){
        try{
            JSONObject jsonObject = response;
            int data_count = Integer.parseInt(jsonObject.getString("data_count"));
            if(data_count > 0){
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                int c = 0;
                for(int i=0;i<data_count;i++){
                    JSONObject object_dealer = jsonArray.getJSONObject(i);
                    listProduct.add(new Product(
                            R.mipmap.menu_productpertaminapng,
                            object_dealer.getString("id"),
                            object_dealer.getString("product_name")));
                    c++;
                }
            }
        }catch (JSONException e){
            Log.d("Error : " , e.getMessage());
        }
        adapterProduct.notifyDataSetChanged();
    }

}
