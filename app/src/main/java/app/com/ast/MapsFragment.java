package app.com.ast;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    FusedLocationProviderClient fusedLocationProviderClient;
    SupportMapFragment mapFragment;
    TextView txtNet, txtTechnology, txtRSRP, txtRSRQ, txtCID, txtFrq, txtAZI, txtDist;
    TelephonyManager telephonyManager;
    protected String[] parts;
    ArrayList<String[]> data;
    protected SignalStrengthListener signalStrengthListener;
    protected List<CellInfo> cellInfoList;

    public MapsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtNet = view.findViewById(R.id.net_detail);
        txtTechnology = view.findViewById(R.id.technology_detail);
        txtRSRP = view.findViewById(R.id.rsrp_detail);
        txtRSRQ = view.findViewById(R.id.rsrq_detail);
        txtCID = view.findViewById(R.id.cid_detail);
        txtFrq = view.findViewById(R.id.frq_detail);
        txtAZI = view.findViewById(R.id.azi_detail);
        txtDist = view.findViewById(R.id.dist_detail);

        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private OnMapReadyCallback callback = googleMap -> {
    };

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }

                            googleMap.setMyLocationEnabled(true);
                            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            googleMap.addMarker(markerOptions);
                        }
                    });
                }
            }
        });
    }

    protected class SignalStrengthListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {
            String ltestr = signalStrength.toString();
            parts = ltestr.split(" ");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getDetails();
            }
            try {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                cellInfoList = telephonyManager.getAllCellInfo();
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoLte) {
                        CellIdentityLte identityLte = ((CellInfoLte) cellInfo).getCellIdentity();
                        txtCID.setText(String.valueOf(identityLte.getCi()));
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        CellIdentityWcdma identityWcdma = ((CellInfoWcdma) cellInfo).getCellIdentity();
                        txtCID.setText(String.valueOf(identityWcdma.getCid()));
                    } else if (cellInfo instanceof CellInfoGsm) {
                        CellIdentityGsm identityLte = ((CellInfoGsm) cellInfo).getCellIdentity();
                        txtCID.setText(String.valueOf(identityLte.getCid()));
                    }
                }
            } catch (Exception e) {
                Log.d("SignalStrength", "Exception: " + e.getMessage());
            }
            super.onSignalStrengthsChanged(signalStrength);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getDetails() {
        String simOperatorCode = telephonyManager.getSimOperator();
        txtNet.setText(simOperatorCode);

        if (parts == null || parts.length < 13) {
            return;
        }
        String rsrp = String.valueOf(parts[25]);
        String[] separated = rsrp.split("=");
        txtRSRP.setText(separated[1].trim());

        String rsrq = String.valueOf(parts[26]);
        String[] separated2 = rsrq.split("=");
        txtRSRQ.setText(separated2[1].trim());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String getDataNetwork = null;
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            switch (telephonyManager.getDataNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    txtTechnology.setText("GSM");
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    txtTechnology.setText("CDMA");
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    txtTechnology.setText("LTE");
                    break;
                case TelephonyManager.NETWORK_TYPE_NR:
                    txtTechnology.setText("NR");
                    break;
                default:
                    txtTechnology.setText("Unknown");
            }
        }
        DevicePolicyManager localDPM = (DevicePolicyManager) getActivity()
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            String getCID = localDPM.getEnrollmentSpecificId();
            txtCID.setText(getCID);
        }
    }
}