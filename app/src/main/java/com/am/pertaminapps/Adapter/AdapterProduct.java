package com.am.pertaminapps.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.am.pertaminapps.Model.Product;
import com.am.pertaminapps.R;

import java.util.List;

/**
 * Created by Laurensius D.S on 3/27/2018.
 */
public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.HolderProduct> {
    List<Product> listProduct;
    public AdapterProduct(List<Product>listProduct){
        this.listProduct = listProduct;
    }

    @Override
    public HolderProduct onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_product,viewGroup,false);
        HolderProduct holderProduct = new HolderProduct(v);
        return holderProduct;
    }

    @Override
    public void onBindViewHolder(HolderProduct holderProduct,int i){
        holderProduct.ivIcon.setImageResource(listProduct.get(i).icon);
        holderProduct.tvId.setText(listProduct.get(i).nama);
        holderProduct.tvNama.setText(listProduct.get(i).nama);
    }

    @Override
    public int getItemCount(){
        return listProduct.size();
    }

    public Product getItem(int position){
        return listProduct.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderProduct extends  RecyclerView.ViewHolder{
        CardView cvProduct;
        ImageView ivIcon;
        TextView tvId;
        TextView tvNama;

        HolderProduct(View itemView){
            super(itemView);
            cvProduct = (CardView) itemView.findViewById(R.id.cv_product);
            ivIcon = (ImageView)itemView.findViewById(R.id.iv_icon);
            tvId= (TextView)itemView.findViewById(R.id.tv_id_product);
            tvNama= (TextView)itemView.findViewById(R.id.tv_nama_product);
        }
    }
}

