package tralafarlaw.com.redapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    Menu names;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    final String mail = firebaseUser.getEmail();
    final String user_name = mail.substring(0,mail.length()-10);
    List<String> nombres = new ArrayList<>();
    MapView map;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Permiso de Logout concedido", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        names = navigationView.getMenu();
        init();

        instanciar_nombres();

        //instaciar_marcadores();




    }
    public void instaciar_marcadores (){
        reference.child("blue").child("conductores").addValueEventListener(new mark(map, nombres, this));


        for (Overlay overlay:
                map.getOverlays()){
            Marker mk = (Marker) overlay;
            map.getController().setCenter(mk.getPosition());
            break;
        }
    }
    public void init (){
        map =(MapView) findViewById(R.id.map);
        map.setMultiTouchControls(true);
        map.getController().setZoom(19.3);
        map.setTileSource(TileSourceFactory.MAPNIK);

    }
    public void instanciar_nombres (){
        reference.child("red").child("usuarios").child(user_name).addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data :
                        dataSnapshot.getChildren()) {
                    String q = data.getValue(String.class);
                    names.add(q);
                    nombres.add(q+"");
                }
                instaciar_marcadores();
                int i = map.getOverlays().size();
                for (Overlay overlay:
                        map.getOverlays()){
                    Marker mk = (Marker) overlay;
                    map.getController().setCenter(mk.getPosition());
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String tile = item.getTitle().toString();
        for (Overlay overlay:
        map.getOverlays()){
            Marker mk = (Marker) overlay;
            if(mk.getTitle().equals(tile)){
                map.getController().setCenter(mk.getPosition());
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
class mark implements ValueEventListener{
    MapView map;
    boolean sw = true;
    List<String> nombres;
    TrackActivity act;
    public mark(MapView mapi, List<String> nombresi, TrackActivity tk){
        this.map = mapi;
        nombres = nombresi;
        act = tk;
    }
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot data :
                dataSnapshot.getChildren()) {
            String n = data.child("Nombre").getValue(String.class);
            for (String a :
                    nombres) {
                boolean sw = true;
                if(a.equals(n)){
                    Double lat, lon;
                    lat = data.child("Lat").getValue(Double.class);
                    lon = data.child("Lon").getValue(Double.class);
                    for (Overlay overlay :
                            map.getOverlays()) {
                        Marker marker = (Marker) overlay;
                        if(marker.getTitle().equals(n)){
                            marker.setPosition(new GeoPoint(lat,lon));
                            sw = false;
                        }
                        int st = data.child("Status").getValue(Integer.class);
                        if(st == 1){
                            marker.setIcon(act.getResources().getDrawable(R.drawable.verde));
                        }else if(st ==2){
                            marker.setIcon(act.getResources().getDrawable(R.drawable.naranja));
                        }
                        else {
                            marker.setIcon(act.getResources().getDrawable(R.drawable.rojo));
                        }

                    }
                    if(sw){
                        Marker mk = new Marker(map);
                        mk.setPosition(new GeoPoint(lat,lon));

                        mk.setTitle(n);
                        mk.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        int st = data.child("Status").getValue(Integer.class);
                        if(st == 1){
                            mk.setIcon(act.getResources().getDrawable(R.drawable.verde));
                        }else if(st ==2){
                            mk.setIcon(act.getResources().getDrawable(R.drawable.naranja));
                        }
                        else {
                            mk.setIcon(act.getResources().getDrawable(R.drawable.rojo));
                        }
                        map.getOverlays().add(mk);
                        if(sw){
                            map.getController().setCenter(mk.getPosition());
                            sw = false;
                        }
                        map.invalidate();
                    }
                }
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}