package com.am.pertaminapps.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.am.pertaminapps.Model.Promo;
import com.am.pertaminapps.R;

import java.util.List;

/**
 * Created by Laurensius D.S on 3/27/2018.
 */
public class AdapterPromo extends RecyclerView.Adapter<AdapterPromo.HolderPromo> {
    List<Promo> listPromo;
    public AdapterPromo(List<Promo>listPromo){
        this.listPromo =listPromo;
    }

    @Override
    public HolderPromo onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_promo,viewGroup,false);
        HolderPromo holderPromo = new HolderPromo(v);
        return holderPromo;
    }

    @Override
    public void onBindViewHolder(HolderPromo holderPromo,int i){
        holderPromo.ivIcon.setImageResource(listPromo.get(i).icon);
        holderPromo.tvIdPromo.setText(listPromo.get(i).id);
        holderPromo.tvNamaDealer.setText(listPromo.get(i).dealer_name);
        holderPromo.tvNamaPromo.setText(listPromo.get(i).promo_title);
        holderPromo.tvTaglinePromo.setText(listPromo.get(i).promo_tagline);
        holderPromo.tvEndDatePromo.setText(listPromo.get(i).promo_enddate);
    }

    @Override
    public int getItemCount(){
        return listPromo.size();
    }

    public Promo getItem(int position){
        return listPromo.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderPromo extends  RecyclerView.ViewHolder{
        CardView cvPromo;
        ImageView ivIcon;
        TextView tvIdPromo;
        TextView tvNamaDealer;
        TextView tvNamaPromo;
        TextView tvTaglinePromo;
        TextView tvEndDatePromo;

        HolderPromo(View itemView){
            super(itemView);
            cvPromo = (CardView) itemView.findViewById(R.id.cv_promo);
            ivIcon = (ImageView)itemView.findViewById(R.id.iv_icon);
            tvIdPromo = (TextView)itemView.findViewById(R.id.tv_id_promo);
            tvNamaDealer = (TextView)itemView.findViewById(R.id.tv_nama_dealer);
            tvNamaPromo = (TextView)itemView.findViewById(R.id.tv_nama_promo);
            tvTaglinePromo= (TextView)itemView.findViewById(R.id.tv_tagline);
            tvEndDatePromo = (TextView)itemView.findViewById(R.id.tv_enddate);
        }
    }
}

