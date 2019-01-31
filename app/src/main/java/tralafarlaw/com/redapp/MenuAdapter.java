package tralafarlaw.com.redapp;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ElHolder> {
    List<String> nombres;
    MapView mapa;
    Switch sw;
    public MenuAdapter (List<String> nombre, MapView mapView){
        nombres = nombre;
        mapa = mapView;
    }
    @NonNull
    @Override
    public ElHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ConstraintLayout sw= (ConstraintLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_item, viewGroup, false);
        return new ElHolder(sw);
    }

    @Override
    public void onBindViewHolder(@NonNull final ElHolder elHolder, int i) {
        elHolder.aSwitch.setText(nombres.get(i));
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        String string = auth.getEmail();
        String nombre = string.substring(0,string.length()-10);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/blue/conductores/");
        ref.child(nombres.get(i)).child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Integer.class)==-1){
                    elHolder.img.setImageResource(R.drawable.menu_ico_off);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final int j = i;
        elHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    for (Overlay o : mapa.getOverlays()){

                        Marker mk = (Marker) o;
                        if(mk.getTitle().equals(nombres.get(j))){
                            if(!isChecked) {
                                mk.setVisible(false);
                            }else {
                                mk.setVisible(true);
                            }
                        }
                    }

            }
        });
        elHolder.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Overlay overlay:
                        mapa.getOverlays()){
                    Marker mk = (Marker) overlay;
                    if(mk.getTitle().equals(nombres.get(j))){
                        mapa.getController().setCenter(mk.getPosition());
                    }
                }
                DrawerLayout drawer = (DrawerLayout) elHolder.itemView.findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }


    @Override
    public int getItemCount() {
        return nombres.size();
    }

    public class ElHolder extends RecyclerView.ViewHolder{
        Switch aSwitch;
        ImageView img;
        public ElHolder(@NonNull View itemView) {
            super(itemView);
            aSwitch = (Switch) itemView.findViewById(R.id.switch1);
            img = (ImageView) itemView.findViewById(R.id.image_status);
        }
    }
}
