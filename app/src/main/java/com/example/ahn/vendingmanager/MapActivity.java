package com.example.ahn.vendingmanager;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Intent intent;
    private String name, addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        intent = getIntent();
        name = intent.getStringExtra("NAME");
        addr = intent.getStringExtra("ADDRESS");

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        Location loc = findGeoPoint(this, addr);
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(name);
        markerOptions.snippet(addr);
        map.addMarker(markerOptions);
        map.getUiSettings().setZoomControlsEnabled(true);

        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    // 주소명을 LatLng로 바꿔주는 메소드
    public static Location findGeoPoint(Context mContext, String address) {
        Location loc = new Location("");
        Geocoder coder = new Geocoder(mContext);
        List<Address> addr = null;  // 한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 설정

        Log.e("--- address", address);
        try {
            addr = coder.getFromLocationName(address, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }   // 몇 개까지의 주소를 원하는지 지정 1~5개 정도가 적당
        if (addr != null) {
            for (int i = 0; i < addr.size(); i++) {
                Address lating = addr.get(i);
                double lat = lating.getLatitude(); // 위도가져오기
                double lon = lating.getLongitude(); // 경도가져오기
                loc.setLatitude(lat);
                loc.setLongitude(lon);
            }
        }
        return loc;
    }

}