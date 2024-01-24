package com.example.pharmafind;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pharmafind.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.model.RectangularBounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 2;
    private GoogleMap mMap;
    ArrayList<MarkerOptions> arrayList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize Places API
        Places.initialize(getApplicationContext(), "AIzaSyDg4elIflyxujSOnzJ0SmolEP_Gv0_o5oQ");
        placesClient = Places.createClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Add hospitals to the arrayList
        arrayList.add(new MarkerOptions().position(new LatLng(3.0677993652174194, 101.48890717770531)).title("Vcare Pharmacy Seksyen 7"));
        arrayList.add(new MarkerOptions().position(new LatLng(3.06666484902148, 101.48907708950229)).title("Health Lane Family Pharmacy Seksyen 7"));
        arrayList.add(new MarkerOptions().position(new LatLng(3.0628060710693052, 101.48217867620068)).title("CARiNG Pharmacy Central i-City"));
        arrayList.add(new MarkerOptions().position(new LatLng(3.0623373548861945, 101.48218538172281)).title("Watsons I-City Shah Alam"));
        arrayList.add(new MarkerOptions().position(new LatLng(3.05891235659538, 101.50562682425411)).title("FirstCare Pharmacy"));
        arrayList.add(new MarkerOptions().position(new LatLng(3.0768148764533385, 101.494622660439)).title("Jovian Selcare Pharmacy (UNISEL)"));
        arrayList.add(new MarkerOptions().position(new LatLng(3.0762430413446697, 101.49695909471536)).title("U.N.I FARMASI SEK 7 SHAH ALAM"));
        arrayList.add(new MarkerOptions().position(new LatLng(3.081730829518426, 101.49232243895051)).title("Guardian Seksyen7 Shah Alam"));
        arrayList.add(new MarkerOptions().position(new LatLng(3.075393076462164, 101.48616624570177)).title("MedAid Pharmacy"));
    }

    private void setCurrentLocation(double latitude, double longitude) {
        LatLng specificLatLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(specificLatLng, 15f));
        addMarkersForNearestHospitals(specificLatLng);
    }

    private void addMarkersForNearestHospitals(LatLng currentLatLng) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(currentLatLng.latitude - 0.1, currentLatLng.longitude - 0.1),
                new LatLng(currentLatLng.latitude + 0.1, currentLatLng.longitude + 0.1));

        // Set the type filter to hospitals
        Autocomplete.IntentBuilder intentBuilder = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeFields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setCountry("YOUR_COUNTRY_CODE")
                .setLocationBias(bounds);

        // Start the autocomplete intent
        Intent intent = intentBuilder.build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng placeLatLng = place.getLatLng();
                String placeName = place.getName();

                // Add a marker for the selected place
                mMap.addMarker(new MarkerOptions()
                        .position(placeLatLng)
                        .title(placeName));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            setCurrentLocation(3.068424820822796, 101.49355750168766);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String destinationTitle = marker.getTitle();

        // Show the location name in a Toast message
        Toast.makeText(this, "Location: " + destinationTitle, Toast.LENGTH_SHORT).show();

        return false; // Return false to allow further processing of the click event.
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (MarkerOptions markerOptions : arrayList) {
            Marker marker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerOptions.getPosition()));

            // Set the OnMarkerClickListener for each marker
            mMap.setOnMarkerClickListener(this);

            // Set an OnClickListener for the info window (title)
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null; // Return null to use the default info window
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View infoView = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                    TextView titleTextView = infoView.findViewById(R.id.titleTextView);

                    // Set the title (location name) in the info window
                    titleTextView.setText(marker.getTitle());

                    // Set an OnClickListener for the info window (the entire info window)
                    infoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Open Google Maps for the selected location
                            Uri gmmIntentUri = Uri.parse("geo:" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps"); // Specify the package for Google Maps

                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(mapIntent);
                            } else {
                                Toast.makeText(MapsActivity.this, "Google Maps app not installed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    return infoView;
                }
            });
        }

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            setCurrentLocation(3.068424820822796, 101.49355750168766);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}