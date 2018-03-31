package com.am.pertaminapps.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.am.pertaminapps.Model.Dealer;
import com.am.pertaminapps.R;

import java.util.List;

/**
 * Created by Laurensius D.S on 3/27/2018.
 */
public class AdapterDealer extends RecyclerView.Adapter<AdapterDealer.HolderDealer> {
    List<Dealer> listDealer;
    public AdapterDealer(List<Dealer>listDealer){
        this.listDealer =listDealer;
    }

    @Override
    public HolderDealer onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_dealer,viewGroup,false);
        HolderDealer holderDealer = new HolderDealer(v);
        return holderDealer;
    }

    @Override
    public void onBindViewHolder(HolderDealer holderDealer,int i){
        holderDealer.ivIcon.setImageResource(listDealer.get(i).icon);
        holderDealer.tvIdDealer.setText(listDealer.get(i).id);
        holderDealer.tvNamaDealer.setText(listDealer.get(i).nama);
        holderDealer.tvAlamatDealer.setText(listDealer.get(i).alamat);
    }

    @Override
    public int getItemCount(){
        return listDealer.size();
    }

    public Dealer getItem(int position){
        return listDealer.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderDealer extends  RecyclerView.ViewHolder{
        CardView cvDealer;
        ImageView ivIcon;
        TextView tvIdDealer;
        TextView tvNamaDealer;
        TextView tvAlamatDealer;

        HolderDealer(View itemView){
            super(itemView);
            cvDealer = (CardView) itemView.findViewById(R.id.cv_dealer);
            ivIcon = (ImageView)itemView.findViewById(R.id.iv_icon);
            tvIdDealer = (TextView)itemView.findViewById(R.id.tv_id_dealer);
            tvNamaDealer = (TextView)itemView.findViewById(R.id.tv_nama_dealer);
            tvAlamatDealer = (TextView)itemView.findViewById(R.id.tv_alamat_dealer);
        }
    }
}

