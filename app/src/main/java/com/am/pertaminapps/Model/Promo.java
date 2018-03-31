package com.am.pertaminapps.Model;

/**
 * Created by Laurensius D.S on 3/25/2018.
 */

public class Promo {
    public int icon;
    public String id;
    public String promo_title;
    public String promo_tagline;
    public String promo_description;
    public String promo_startdate;
    public String promo_enddate;
    public String dealer_name;
    public String dealer_address;
    public String dealer_lat;
    public String dealer_lon;

    public Promo(
            int icon,
            String id,
            String promo_title,
            String promo_tagline,
            String promo_description,
            String promo_startdate,
            String promo_enddate,
            String dealer_name,
            String dealer_address,
            String dealer_lat,
            String dealer_lon
    ){
        this.icon = icon;
        this.id = id;
        this.promo_title = promo_title;
        this.promo_tagline = promo_tagline;
        this.promo_description = promo_description;
        this.promo_startdate = promo_startdate;
        this.promo_enddate = promo_enddate;
        this.dealer_name = dealer_name;
        this.dealer_address = dealer_address;
        this.dealer_lat = dealer_lat;
        this.dealer_lon = dealer_lon;
    }


}
