package com.example.gezikitabim;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Konum_Ekle extends AppCompatActivity {

    static ArrayList<String> names = new ArrayList<String>();
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();

    static ArrayAdapter arrayAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.yer_ekle,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.yer_ekle)
        {
            Intent ıntent=new Intent(getApplicationContext(),MapsActivity.class);
            ıntent.putExtra("info","new");
            startActivity(ıntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konum__ekle);
        ListView listView = (ListView) findViewById(R.id.listView);
        try {
            MapsActivity.database = this.openOrCreateDatabase("Yerler", MODE_PRIVATE, null);
            Cursor cursor = MapsActivity.database.rawQuery("SELECT * FROM yerler", null);
            int nameIx = cursor.getColumnIndex("name");
            int latitudeIx = cursor.getColumnIndex("latitude");
            int longitudeIx = cursor.getColumnIndex("longitude");

            while (cursor.moveToNext()) {
                String nameFromDatabase = cursor.getString(nameIx);
                String latitudeFromdatabase = cursor.getString(latitudeIx);
                String longitudeFromdatabase = cursor.getString(longitudeIx);

                names.add(nameFromDatabase);
                Double l1 = Double.parseDouble(latitudeFromdatabase);
                Double l2 = Double.parseDouble(longitudeFromdatabase);
                LatLng locationFromDatabase = new LatLng(l1, l2);
                locations.add(locationFromDatabase);
                System.out.println("name"+nameFromDatabase);

            }
            cursor.close();
        } catch (Exception e) {

        }
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ıntent = new Intent(getApplicationContext(), MapsActivity.class);
                ıntent.putExtra("info", "old");
                ıntent.putExtra("position", position);
                startActivity(ıntent);
            }


        });




    }


}


