package com.am.pertaminapps.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.am.pertaminapps.Model.DealerProduct;
import com.am.pertaminapps.R;

import java.util.List;

/**
 * Created by Laurensius D.S on 3/27/2018.
 */
public class AdapterDealerProduct extends RecyclerView.Adapter<AdapterDealerProduct.HolderDealerProduct> {
    List<DealerProduct> listDealerProduct;
    public AdapterDealerProduct(List<DealerProduct>listDealerProduct){
        this.listDealerProduct = listDealerProduct;
    }

    @Override
    public HolderDealerProduct onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_dealer_product,viewGroup,false);
        HolderDealerProduct holderDealerProduct = new HolderDealerProduct(v);
        return holderDealerProduct;
    }

    @Override
    public void onBindViewHolder(HolderDealerProduct holderDealerProduct,int i){
        holderDealerProduct.ivIcon.setImageResource(listDealerProduct.get(i).icon);
        holderDealerProduct.tvIdDealerProduct.setText(listDealerProduct.get(i).id);
        holderDealerProduct.tvNamaProduct.setText(listDealerProduct.get(i).nama_product);
        holderDealerProduct.tvHarga.setText(listDealerProduct.get(i).harga);
    }

    @Override
    public int getItemCount(){
        return listDealerProduct.size();
    }

    public DealerProduct getItem(int position){
        return listDealerProduct.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderDealerProduct extends  RecyclerView.ViewHolder{
        CardView cvDealerProduct;
        ImageView ivIcon;
        TextView tvIdDealerProduct;
        TextView tvNamaProduct;
        TextView tvHarga;

        HolderDealerProduct(View itemView){
            super(itemView);
            cvDealerProduct = (CardView) itemView.findViewById(R.id.cv_dealer_product);
            ivIcon = (ImageView)itemView.findViewById(R.id.iv_icon);
            tvIdDealerProduct = (TextView)itemView.findViewById(R.id.tv_id_dealer_product);
            tvNamaProduct = (TextView)itemView.findViewById(R.id.tv_nama_product);
            tvHarga = (TextView)itemView.findViewById(R.id.tv_harga);
        }
    }
}

