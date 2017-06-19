package com.example.bruno.awarenesstest;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by bruno on 19/06/17.
 */

public class Hole {


    public LatLng latLng;
    public long id;

    public Hole() {
        this.id = 0;
        this.latLng = new LatLng(0,0);
    }


}
