package app.com.ast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.com.ast.models.GsmModel;
import app.com.ast.models.ImeiModel;
import app.com.ast.models.LTEModel;
import app.com.ast.models.NeighborModel;
import app.com.ast.models.WcdmaModel;
import cz.mroczis.netmonster.core.factory.NetMonsterFactory;
import cz.mroczis.netmonster.core.model.cell.CellGsm;
import cz.mroczis.netmonster.core.model.cell.CellLte;
import cz.mroczis.netmonster.core.model.cell.CellWcdma;
import cz.mroczis.netmonster.core.model.cell.ICell;
import cz.mroczis.netmonster.core.model.signal.SignalGsm;
import cz.mroczis.netmonster.core.model.signal.SignalLte;
import cz.mroczis.netmonster.core.model.signal.SignalWcdma;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    // Views
    TextView name, mcc, enb, enbDetails, tac, tacValue, lcidDetails, lcid, band, txtBandwidth, txtSpeed, txtAlt, mnc, uci, uciDetails, pci, pciDetail, type, roaming,
            state, latitude, longitude, ul, dl, txtRsrp, txtrsrpDetails, txtrsrqDetail, txtRsrq, txtcqi, txtrssi, txtta, txtsnr, txtrfc;
    ImageView ivSim, ivMenu, ivLeftMenu;
    ConstraintLayout layoutLoading;
    LinearLayout layoutValues;
    DrawerLayout drawerLayout;
    protected String[] parts;
    public static boolean isMenuClicked = false;
    ArrayList<CellInfo> neighborsList;
    String rscp, ecno;

    String networkName = "";
    boolean isNetworkNameAssigned = false;

    List<CellInfo> cellInfoList = new ArrayList<>();
    TelephonyManager telephonyManager;
    TelephonyManager manager;

    FusedLocationProviderClient fusedLocationProviderClient;

    String currentMNC = "";
    int simID, networkType = 0;
    String signals, ta = "";
    private static final int PERMISSION_ID = 44;
    boolean isActivityPaused = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intialzieViews();
        simID = SharePrefData.getInstance().getPrefInt(this, "simID");

        if (checkPermissions()) {
            initTelephonyManager();
        } else {
            requestPermissions();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void intialzieViews() {
        name = findViewById(R.id.name_detail);
        mcc = findViewById(R.id.mcc_detail);
        mnc = findViewById(R.id.mnc_detail);
        roaming = findViewById(R.id.roam_detail);
        state = findViewById(R.id.state_detail);
        band = findViewById(R.id.band_detail);
        enb = findViewById(R.id.enB);
        enbDetails = findViewById(R.id.enB_detail);
        type = findViewById(R.id.type_detail);
        ul = findViewById(R.id.ul_detail);
        dl = findViewById(R.id.dl_detail);
        txtrsrpDetails = findViewById(R.id.rsrp_detail);
        txtrsrqDetail = findViewById(R.id.rsrq_detail);
        txtRsrq = findViewById(R.id.rsrq);
        txtcqi = findViewById(R.id.cqi_detail);
        txtrssi = findViewById(R.id.rssi_detail);
        txtRsrp = findViewById(R.id.rsrp);
        txtta = findViewById(R.id.ta_detail);
        txtsnr = findViewById(R.id.snr_detail);
        txtrfc = findViewById(R.id.rfc_detail);
        pci = findViewById(R.id.pci);
        pciDetail = findViewById(R.id.pci_detail);
        uciDetails = findViewById(R.id.eci_detail);
        uci = findViewById(R.id.eci);
        latitude = findViewById(R.id.lat_detail);
        longitude = findViewById(R.id.lon_detail);
        tac = findViewById(R.id.tac);
        tacValue = findViewById(R.id.tac_detail);
        lcid = findViewById(R.id.lcid);
        lcidDetails = findViewById(R.id.lcid_detail);
        txtAlt = findViewById(R.id.alt_detail);
        txtSpeed = findViewById(R.id.velocity_detail);
        txtBandwidth = findViewById(R.id.bw_detail);
        ivSim = findViewById(R.id.ivSimCard);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutValues = findViewById(R.id.layoutLTE);
        drawerLayout = findViewById(R.id.drawer);
        ivMenu = findViewById(R.id.ivMenu);
        ivLeftMenu = findViewById(R.id.ivLeftMenu);

        //Setup click listeners on drawer items
        LinearLayout linear1 = findViewById(R.id.linear1);
        LinearLayout linear2 = findViewById(R.id.linear2);
        LinearLayout linear3 = findViewById(R.id.linear3);
        LinearLayout linear4 = findViewById(R.id.linear4);
        linear1.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Already in Overview", Toast.LENGTH_SHORT).show();
        });

        linear2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SpeedTestActivity2.class);
            startActivity(intent);
        });

        linear3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        linear4.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        ivSim.setOnClickListener(v -> {
            layoutValues.setVisibility(View.GONE);
            layoutLoading.setVisibility(View.VISIBLE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (SubscriptionManager.from(this).getActiveSubscriptionInfoCount() == 1) {
                    return;
                }
            }

            int simID = SharePrefData.getInstance().getPrefInt(MainActivity.this, "simID");
            if (simID == 0) {
                SharePrefData.getInstance().setPrefInt(MainActivity.this, "simID", 1);
            } else if (simID == 1) {
                SharePrefData.getInstance().setPrefInt(MainActivity.this, "simID", 0);
            }
            restartActivity();
        });

        ivMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.LEFT);
        });

        ivLeftMenu.setOnClickListener(v -> {
            showPopup(ivLeftMenu);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void initTelephonyManager() {
        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            ArrayList<SubscriptionInfo> subscriptionInfoList = (ArrayList<SubscriptionInfo>) subscriptionManager.getActiveSubscriptionInfoList();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {

                telephonyManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).createForSubscriptionId(subscriptionInfoList.get(simID).getSubscriptionId());
            } else {
                telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            }
        } catch (Exception e) {
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bindValuesBelowAndroid10();
                getNeighbourCellInfo();
                new Handler().postDelayed(this::saveValuesInDB, 5000);
            }
        } else {
            telephonyManager.requestCellInfoUpdate(this.getMainExecutor(), new TelephonyManager.CellInfoCallback() {
                @Override
                public void onCellInfo(@NonNull List<CellInfo> cellInfos) {
                    cellInfoList.clear();
                    for (int i = 0; i < cellInfos.size(); i++) {
                        CellInfo cellInfo = cellInfos.get(i);
                        if (cellInfo.isRegistered())
                            cellInfoList.add(cellInfo);
                    }
                    bindValuesAboveAPI30();
                    getNeighbourCellInfo();
                    new Handler().postDelayed(this::saveValuesInDB, 5000);
                }

                private void saveValuesInDB() {
                    Log.d("NNAME", name.getText().toString());
                    if (networkType == 1) {
                        saveGsmInfo();
                    } else if (networkType == 2) {
                        saveWcdmaInfo();
                    } else if (networkType == 3) {
                        saveLTEInfo();
                    }
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getLocation();
        }
    }

    private void saveValuesInDB() {
        if (networkType == 1) {
            saveGsmInfo();
        } else if (networkType == 2) {
            saveWcdmaInfo();
        } else if (networkType == 3) {
            saveLTEInfo();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void bindValuesBelowAndroid10() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            ArrayList<ICell> cellList = (ArrayList<ICell>) NetMonsterFactory.INSTANCE.getTelephony(this, 0).getAllCellInfo(30000);
            ICell iCell = cellList.get(simID);

            if (iCell instanceof CellLte) {
                networkType = 3;
                SignalLte lte = (SignalLte) iCell.getSignal();
                CellLte cellLte = (CellLte) iCell;
                String getRssi = String.valueOf(lte.getRssi().intValue());
                String getRsrq = String.valueOf(lte.getRsrq().intValue());
                String getRsrp = String.valueOf((Integer) lte.getRsrp().intValue());
                String ci = String.valueOf(cellLte.getCid());
                String rfc = String.valueOf(cellLte.getBand().getDownlinkEarfcn());

                int cellMnc = Integer.parseInt(cellLte.getNetwork().getMnc());
                Double snr = lte.getSnr();
                if (snr != null) {
                    txtsnr.setText(String.valueOf(snr / 10.0));
                }
                if (cellMnc != 2147483647) {
                    mnc.setText(String.valueOf(cellMnc));
                }
                txtrsrqDetail.setText(getRsrq);
                txtrssi.setText(getRssi);
                mcc.setText(cellLte.getNetwork().getMcc());
                txtrfc.setText(rfc);
                band.setText(String.valueOf(cellLte.getBand().getName()));
                txtrsrpDetails.setText(getRsrp);

                lcidDetails.setText(String.valueOf(cellLte.getSignal().getDbm()));
                uciDetails.setText(ci);

                type.setText(getString(R.string.strLte));
                txtBandwidth.setText(String.valueOf(((CellLte) iCell).getBandwidth() / 1000));
                name.setText(String.valueOf(telephonyManager.getNetworkOperatorName()));

                String simRoaming = String.valueOf(telephonyManager.isNetworkRoaming());
                roaming.setText(simRoaming.toUpperCase(Locale.ROOT));

                layoutValues.setVisibility(View.VISIBLE);
                layoutLoading.setVisibility(View.GONE);

                setSimState();
                type.setText(getString(R.string.wcdma));

                txtBandwidth.setText(String.valueOf(5));

                int cellPci = cellLte.getPci();
                int ta = lte.getTimingAdvance();
                int cqi = lte.getCqi();
                int tacVal = cellLte.getTac();
                if (ta != 2147483647) {
                    txtta.setText(String.valueOf(ta));
                }
                if (cqi != 2147483647) {
                    txtcqi.setText(String.valueOf(cqi));
                }
                enbDetails.setText(String.valueOf(getEnbValue(cellLte.getCid())));
                pciDetail.setText(String.valueOf(cellPci));
                tacValue.setText(String.valueOf(tacVal));
            } else if (iCell instanceof CellWcdma) {
                networkType = 2;
                layoutValues.setVisibility(View.VISIBLE);
                layoutLoading.setVisibility(View.GONE);
                CellWcdma cellWcdma = (CellWcdma) cellList.get(0);
                SignalWcdma signalWcdma = cellWcdma.getSignal();

                int cid = cellWcdma.getCid();
                rscp = String.valueOf(signalWcdma.getDbm());
                ecno = String.valueOf(signalWcdma.getEcno());
                txtrsrpDetails.setText(rscp);
                txtRsrp.setText(getString(R.string.rscp));
                txtRsrq.setText(getString(R.string.ecno));
                if (ecno != null) {
                    txtrsrqDetail.setText(ecno);
                }
                int cellMcc = Integer.parseInt(cellWcdma.getNetwork().getMcc());
                int cellMnc = Integer.parseInt(cellWcdma.getNetwork().getMnc());

                int cellRfc = cellWcdma.getBand().getDownlinkUarfcn();
                int bandValue = BandFrequency.getBand(cellRfc);

                byte[] l_byte_array = convertByteArray__p(cid);
                int rnc = getRNCID_or_CID__p(l_byte_array, 2);

                if (cellMnc != 2147483647) {
                    mnc.setText(String.valueOf(cellMnc));
                }

                mcc.setText(String.valueOf(cellMcc));
                txtrfc.setText(String.valueOf(cellRfc));
                band.setText(String.valueOf(bandValue));
                pci.setText(getString(R.string.psc));

                lcid.setText(getString(R.string.rnc));
                lcidDetails.setText(String.valueOf(rnc));
                name.setText(String.valueOf(telephonyManager.getNetworkOperatorName()));
                enb.setText(getString(R.string.cid));
                enbDetails.setText(String.valueOf(getRNCID_or_CID__p(l_byte_array, 1)));
                uci.setText(getString(R.string.uci));
                uciDetails.setText(String.valueOf(cid));
                tac.setText(getString(R.string.lac));

                setSimState();
                type.setText(getString(R.string.wcdma));

                txtBandwidth.setText(String.valueOf(5));
                String simRoaming = String.valueOf(telephonyManager.isNetworkRoaming());
                roaming.setText(simRoaming.toUpperCase(Locale.ROOT));

                int lacValue = cellWcdma.getLac();
                int cellPsc = cellWcdma.getPsc();

                tacValue.setText(String.valueOf(lacValue));
                pciDetail.setText(String.valueOf(cellPsc));
                txtBandwidth.setText(String.valueOf(((CellLte) iCell).getBandwidth() / 1000));
            } else if (iCell instanceof CellGsm) {

                networkType = 1;
                layoutValues.setVisibility(View.VISIBLE);
                layoutLoading.setVisibility(View.GONE);

                CellGsm cellGsm = (CellGsm) iCell;
                SignalGsm signalGsm = cellGsm.getSignal();

                String getRssi = String.valueOf(signalGsm.getRssi());
                String cid = String.valueOf(cellGsm.getCid());

                int cellMcc = Integer.parseInt(cellGsm.getNetwork().getMcc());
                int cellMnc = Integer.parseInt(cellGsm.getNetwork().getMnc());
                int cellBsic = cellGsm.getBsic();
                int cellRfc = cellGsm.getBand().getArfcn();

                if (cellMnc != 2147483647) {
                    mnc.setText(String.valueOf(cellMnc));
                }
                txtrssi.setText(getRssi);
                txtrsrpDetails.setText(getRssi);
                txtRsrp.setText("RxLev");
                mcc.setText(String.valueOf(cellMcc));
                txtrfc.setText(String.valueOf(cellRfc));
                band.setText(String.valueOf(cellGsm.getBand().getName()));
                pci.setText(getString(R.string.bsic));
                pciDetail.setText(String.valueOf(cellBsic));
                lcid.setText(getString(R.string.rnc));
                name.setText(String.valueOf(telephonyManager.getNetworkOperatorName()));
                enb.setText(getString(R.string.cid));
                enbDetails.setText(cid);
                uci.setText(getString(R.string.uci));
                uciDetails.setText(cid);
                tac.setText(getString(R.string.lac));
                setSimState();
                type.setText(getString(R.string.gsm));

                txtBandwidth.setText(String.valueOf(0.2));
                String simRoaming = String.valueOf(telephonyManager.isNetworkRoaming());
                roaming.setText(simRoaming.toUpperCase(Locale.ROOT));


                int ta = signalGsm.getTimingAdvance();
                int lacValue = cellGsm.getLac();
                tacValue.setText(String.valueOf(lacValue));
                if (ta != 2147483647) {
                    txtta.setText(String.valueOf(ta));
                }
            }
        } catch (
                Exception e) {
            e.getLocalizedMessage();
        }

    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_SMS}, PERMISSION_ID);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initTelephonyManager();
            } else {
                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        int alt = (int) location.getAccuracy();
                        String altitude = String.valueOf(alt);
                        txtAlt.setText(altitude + "m");
                        String Velocity = String.valueOf(location.getSpeed());
                        txtSpeed.setText(Velocity + "km/h");
                        String Locationlatitude = String.valueOf(location.getLatitude());
                        latitude.setText(Locationlatitude);
                        String Locationlongitude = String.valueOf(location.getLongitude());
                        longitude.setText(Locationlongitude);
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            int alt = (int) mLastLocation.getAccuracy();
            String altitude = String.valueOf(alt);
            txtAlt.setText(altitude + "m");
            String Velocity = String.valueOf(mLastLocation.getSpeed());
            txtSpeed.setText(Velocity + "km/h");

            String locationlatitude = String.valueOf(mLastLocation.getLatitude());
            latitude.setText(locationlatitude);
            String locationlongitude = String.valueOf(mLastLocation.getLongitude());
            longitude.setText(locationlongitude);
        }
    };

    private void bindValuesAboveAPI30() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            CellInfo cellInfo = cellInfoList.get(0);

            if (cellInfo instanceof CellInfoLte) {
                networkType = 3;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ArrayList<ICell> cellList = (ArrayList<ICell>) NetMonsterFactory.INSTANCE.get(this).getCells();
                    cellList.get(simID);

                    CellSignalStrengthLte cellSignalStrengthLte = (CellSignalStrengthLte) cellInfo.getCellSignalStrength();
                    String getRssi = String.valueOf(cellSignalStrengthLte.getRssi());
                    String getRsrq = String.valueOf(cellSignalStrengthLte.getRsrq());
                    String getRsrp = String.valueOf(cellSignalStrengthLte.getRsrp());
                    String ci = String.valueOf(((CellInfoLte) cellInfo).getCellIdentity().getCi());

                    int cellPci = ((CellInfoLte) cellInfo).getCellIdentity().getPci();
                    int cellMnc = ((CellInfoLte) cellInfo).getCellIdentity().getMnc();
                    int cellRfc = ((CellInfoLte) cellInfo).getCellIdentity().getEarfcn();
                    int bandValue = BandFrequency.calculateLTEFrequency(cellRfc);
                    int snr = cellSignalStrengthLte.getRssnr();
                    int ta = cellSignalStrengthLte.getTimingAdvance();
                    int cqi = cellSignalStrengthLte.getCqi();
                    int tacVal = ((CellInfoLte) cellInfo).getCellIdentity().getTac();

                    if (snr != 2147483647) {
                        txtsnr.setText(String.valueOf(snr / 10.0));
                    }
                    if (cellMnc != 2147483647) {
                        mnc.setText(String.valueOf(cellMnc));
                    }
                    if (ta != 2147483647) {
                        txtta.setText(String.valueOf(ta));
                    }
                    if (cqi != 2147483647) {
                        txtcqi.setText(String.valueOf(cqi));
                    }
                    txtrsrqDetail.setText(getRsrq);
                    txtrssi.setText(getRssi);
                    mcc.setText(String.valueOf(((CellInfoLte) cellInfo).getCellIdentity().getMcc()));
                    txtrfc.setText(String.valueOf(cellRfc));
                    band.setText(String.valueOf(bandValue));
                    pciDetail.setText(String.valueOf(cellPci));
                    txtrsrpDetails.setText(getRsrp);
                    new Handler().postDelayed(() -> name.setText(cellInfo.getCellIdentity().getOperatorAlphaShort()), 1000);
                    lcidDetails.setText(String.valueOf(cellSignalStrengthLte.getLevel()));
                    enbDetails.setText(String.valueOf(getEnbValue(cellInfo)));
                    uciDetails.setText(ci);
                    tacValue.setText(String.valueOf(tacVal));
                    setSimState();
                    type.setText(getString(R.string.strLte));

                    ArrayList<ICell> list = (ArrayList<ICell>) NetMonsterFactory.INSTANCE.get(MainActivity.this).getCells();
                    for (ICell iCell : list) {
                        if (iCell instanceof CellLte) {
                            txtBandwidth.setText(String.valueOf(((CellLte) iCell).getBandwidth() / 1000));
                        }
                    }

                    String simRoaming = String.valueOf(telephonyManager.isNetworkRoaming());
                    roaming.setText(simRoaming.toUpperCase(Locale.ROOT));

                    layoutValues.setVisibility(View.VISIBLE);
                    layoutLoading.setVisibility(View.GONE);
                }
            } else if (cellInfo instanceof CellInfoGsm) {
                networkType = 1;
                layoutValues.setVisibility(View.VISIBLE);
                layoutLoading.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    CellSignalStrengthGsm cellSignalStrengthGsm = (CellSignalStrengthGsm) cellInfo.getCellSignalStrength();
                    String getRssi = String.valueOf(cellSignalStrengthGsm.getRssi());
                    String cid = String.valueOf(((CellInfoGsm) cellInfo).getCellIdentity().getCid());

                    int cellMcc = ((CellInfoGsm) cellInfo).getCellIdentity().getMcc();
                    int cellMnc = ((CellInfoGsm) cellInfo).getCellIdentity().getMnc();
                    int cellBsic = ((CellInfoGsm) cellInfo).getCellIdentity().getBsic();
                    int cellRfc = ((CellInfoGsm) cellInfo).getCellIdentity().getArfcn();
                    int bandValue = BandFrequency.calculateGSMFrequency(cellRfc);
                    int ta = cellSignalStrengthGsm.getTimingAdvance();
                    int lacValue = ((CellInfoGsm) cellInfo).getCellIdentity().getLac();

                    if (cellMnc != 2147483647) {
                        mnc.setText(String.valueOf(cellMnc));
                    }
                    if (ta != 2147483647) {
                        txtta.setText(String.valueOf(ta));
                    }
                    txtrssi.setText(getRssi);
                    txtrsrpDetails.setText(getRssi);
                    txtRsrp.setText("RxLev");
                    mcc.setText(String.valueOf(cellMcc));
                    txtrfc.setText(String.valueOf(cellRfc));
                    band.setText(String.valueOf(bandValue));
                    pci.setText(getString(R.string.bsic));
                    pciDetail.setText(String.valueOf(cellBsic));
                    lcid.setText(getString(R.string.rnc));
                    new Handler().postDelayed(() -> name.setText(cellInfo.getCellIdentity().getOperatorAlphaShort()), 1000);
                    enb.setText(getString(R.string.cid));
                    enbDetails.setText(cid);
                    uci.setText(getString(R.string.uci));
                    uciDetails.setText(cid);
                    tac.setText(getString(R.string.lac));
                    tacValue.setText(String.valueOf(lacValue));
                    setSimState();
                    type.setText(getString(R.string.gsm));

                    txtBandwidth.setText(String.valueOf(0.2));
                    String simRoaming = String.valueOf(telephonyManager.isNetworkRoaming());
                    roaming.setText(simRoaming.toUpperCase(Locale.ROOT));

                }
            } else if (cellInfo instanceof CellInfoWcdma) {
                networkType = 2;
                layoutValues.setVisibility(View.VISIBLE);
                layoutLoading.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    CellSignalStrengthWcdma cellSignalStrengthWcdma = (CellSignalStrengthWcdma) cellInfo.getCellSignalStrength();
                    ArrayList<ICell> list = (ArrayList<ICell>) NetMonsterFactory.INSTANCE.get(MainActivity.this).getCells();
                    for (ICell iCell : list) {
                        if (iCell instanceof CellWcdma) {
                            txtBandwidth.setText(String.valueOf(((CellWcdma) iCell).getSignal().getEcno()));
                            txtBandwidth.setText(String.valueOf(((CellWcdma) iCell).getSignal().getDbm()));
                        }
                    }
                    /*final int minlimit = 12000;
                    final int maxlimit = 15000;
                    final int limit = new Random().nextInt((maxlimit - minlimit) + 1) + minlimit;
                    final Handler ha = new Handler();
                    ha.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final int random = new Random().nextInt((4 - -4) + 1) + -4;
                            rscp = String.valueOf(cellSignalStrengthWcdma.getDbm() - random);
                            ecno = String.valueOf(cellSignalStrengthWcdma.getEcNo() - random);
                            ha.postDelayed(this, limit);


                        }
                    }, 500);*/

                    int cid = ((CellInfoWcdma) cellInfo).getCellIdentity().getCid();
                    txtrsrpDetails.setText(rscp);
                    txtRsrp.setText(getString(R.string.rscp));
                    txtRsrq.setText(getString(R.string.ecno));
                    txtrsrqDetail.setText(ecno);
                    int cellMcc = ((CellInfoWcdma) cellInfo).getCellIdentity().getMcc();
                    int cellMnc = ((CellInfoWcdma) cellInfo).getCellIdentity().getMnc();
                    int cellPsc = ((CellInfoWcdma) cellInfo).getCellIdentity().getPsc();
                    int cellRfc = ((CellInfoWcdma) cellInfo).getCellIdentity().getUarfcn();
                    int bandValue = BandFrequency.getBand(cellRfc);
                    int lacValue = ((CellInfoWcdma) cellInfo).getCellIdentity().getLac();
                    byte[] l_byte_array = convertByteArray__p(cid);
                    int rnc = getRNCID_or_CID__p(l_byte_array, 2);

                    if (cellMnc != 2147483647) {
                        mnc.setText(String.valueOf(cellMnc));
                    }

                    mcc.setText(String.valueOf(cellMcc));
                    txtrfc.setText(String.valueOf(cellRfc));
                    band.setText(String.valueOf(bandValue));
                    pci.setText(getString(R.string.psc));
                    pciDetail.setText(String.valueOf(cellPsc));
                    lcid.setText(getString(R.string.rnc));
                    lcidDetails.setText(String.valueOf(rnc));
                    new Handler().postDelayed(() -> name.setText(cellInfo.getCellIdentity().getOperatorAlphaShort()), 1000);
                    enb.setText(getString(R.string.cid));
                    enbDetails.setText(String.valueOf(getRNCID_or_CID__p(l_byte_array, 1)));
                    uci.setText(getString(R.string.uci));
                    uciDetails.setText(String.valueOf(cid));
                    tac.setText(getString(R.string.lac));
                    tacValue.setText(String.valueOf(lacValue));
                    setSimState();
                    type.setText(getString(R.string.wcdma));

                    txtBandwidth.setText(String.valueOf(5));
                    String simRoaming = String.valueOf(telephonyManager.isNetworkRoaming());
                    roaming.setText(simRoaming.toUpperCase(Locale.ROOT));
                }
            }
        } catch (Exception e) {
            Log.d("SignalStrength", "Exception: " + e.getMessage());
        }
    }

    private void bindSignalStrengthValues() {
        String[] signalArray = signals.split(" ");
        if (networkType == 1) {
            String rssi = String.valueOf(signalArray[8]);
            String[] separted2 = rssi.split("=");
            if (separted2[1].equals("2147483647")) {
                txtrssi.setText("--");
            } else {
                txtrssi.setText(separted2[1].trim());
            }

            ta = String.valueOf(signalArray[29]);
            String[] separated4 = ta.split("=");
            if (separated4[1].equals("2147483647")) {
                txtta.setText("--");
            } else {
                txtta.setText(separated4[1].trim());
            }
        } else if (networkType == 2) {
            String rssi = String.valueOf(signalArray[19]);
            String[] separted2 = rssi.split("=");
            if (separted2[1].equals("2147483647")) {
                txtrssi.setText("--");
            } else {
                txtrssi.setText(separted2[1].trim());
            }

            String ecno = String.valueOf(signalArray[16]);
            String[] separated3 = ecno.split("=");
            if (separated3[1].equals("2147483647")) {
                txtrsrpDetails.setText("--");
            } else {
                txtRsrq.setText(getString(R.string.ecno));
                txtrsrqDetail.setText(separated3[1].trim());
            }

            String rscp = String.valueOf(signalArray[15]);
            String[] separated = rscp.split("=");
            if (separated[1].equals("2147483647")) {
                txtRsrp.setText("--");
            } else {
                txtRsrp.setText(getString(R.string.rscp));
                txtRsrp.setText(separated[1].trim());
            }
        } else if (networkType == 3) {
            String rssi = String.valueOf(signalArray[24]);
            String[] separted2 = rssi.split("=");
            if (separted2[1].equals("2147483647")) {
                txtrssi.setText("--");
            } else {
                txtrssi.setText(separted2[1].trim());
            }

            String rsrq = String.valueOf(signalArray[26]);
            String[] separated3 = rsrq.split("=");
            if (separated3[1].equals("2147483647")) {
                txtrsrpDetails.setText("--");
            } else {
                txtrsrqDetail.setText(separated3[1].trim());
            }

            String rsrp = String.valueOf(signalArray[25]);
            String[] separated = rsrp.split("=");
            if (separated[1].equals("2147483647")) {
                txtrsrpDetails.setText("--");
            } else {
                txtrsrpDetails.setText(separated[1].trim());
            }

            String snr = String.valueOf(signalArray[27]);
            String[] separatedSNR = snr.split("=");
            if (separatedSNR[1].equals("2147483647")) {
                txtsnr.setText("--");
            } else {
                txtsnr.setText(separatedSNR[1].trim());
            }

            String cqi = String.valueOf(signalArray[28]);
            String[] separatedCQI = cqi.split("=");
            if (separatedCQI[1].equals("2147483647")) {
                txtcqi.setText("--");
            } else {
                txtcqi.setText(separatedCQI[1].trim());
            }

            String ta = String.valueOf(signalArray[29]);
            String[] separatedTA = ta.split("=");
            if (separatedTA[1].equals("2147483647")) {
                txtta.setText("--");
            } else {
                txtta.setText(separatedTA[1].trim());
            }

            String level = String.valueOf(signalArray[31]);
            String[] separatedLevel = level.split("=");
            if (separatedLevel[1].equals("2147483647")) {
                lcidDetails.setText("--");
            } else {
                lcidDetails.setText(separatedLevel[1].trim());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setSimState() {
        switch (telephonyManager.getSimState()) {
            case TelephonyManager.SIM_STATE_ABSENT:
                state.setText("SIM Absent");
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                state.setText("SIM Locked");
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                state.setText("Pin Locked");
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                state.setText("Puk Locked");
                break;
            case TelephonyManager.SIM_STATE_READY:
                state.setText("Active");
                break;
            case TelephonyManager.SIM_STATE_NOT_READY:
                state.setText("Sim Not Ready");
                break;
            default:
                state.setText("Unknown");
            case TelephonyManager.SIM_STATE_CARD_IO_ERROR:
                state.setText("Sim Card Error");
                break;
            case TelephonyManager.SIM_STATE_CARD_RESTRICTED:
                state.setText("Sim Card Restricted");
                break;
            case TelephonyManager.SIM_STATE_PERM_DISABLED:
                state.setText("Sim Permission Disabled");
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                state.setText("Unknown");
                break;
        }
    }

    public String DecToHex(int dec) {
        return String.format("%x", dec);
    }

    public int HexToDec(String hex) {
        return Integer.parseInt(hex, 16);
    }

    private int getEnbValue(int ci) {
        String cellidHex = DecToHex(ci);
        String eNBHex = cellidHex.substring(0, cellidHex.length() - 2);
        return HexToDec(eNBHex);
    }

    private int getEnbValue(CellInfo cellInfo) {
        final CellIdentityLte identityLte = ((CellInfoLte) cellInfo).getCellIdentity();
        int longCid = identityLte.getCi();
        String cellidHex = DecToHex(longCid);
        String eNBHex = cellidHex.substring(0, cellidHex.length() - 2);
        return HexToDec(eNBHex);
    }

    // GET RNC
    public int getRNCID_or_CID__p(byte[] p_bytes, int p_which) {
        int MASK_c = 0xFF;
        int l_result = 0;
        if (p_which == 1) {
            l_result = p_bytes[0] & MASK_c;
            l_result = l_result + ((p_bytes[1] & MASK_c) << 8);
        } else if (p_which == 2) {
            l_result = p_bytes[2] & MASK_c;
            l_result = l_result + ((p_bytes[3] & MASK_c) << 8);
        }
        return l_result;
    }

    public static byte[] convertByteArray__p(int p_int) {
        byte[] l_byte_array = new byte[4];
        int MASK_c = 0xFF;
        for (short i = 0; i <= 3; i++) {
            l_byte_array[i] = (byte) ((p_int >> (8 * i)) & MASK_c);
        }
        return l_byte_array;
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        inflater.inflate(R.menu.top_app_bar, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.radio_info:
                isActivityPaused = true;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    Intent in = new Intent(Intent.ACTION_MAIN);
                    in.setClassName("com.android.settings", "com.android.settings.RadioInfo");
                    startActivity(in);
                } else {
                    Intent in = new Intent(Intent.ACTION_MAIN);
                    in.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo");
                    startActivity(in);
                }

                isMenuClicked = true;

                break;
            case R.id.mobile_network:
                openSimSettingScreen();
                break;
            case R.id.compatibility_test:
                startActivity(new Intent(MainActivity.this, CompatibilityTestActivity.class));
                break;
            case R.id.exit:
                showALertMessage();
                break;
            case R.id.about:
                showAboutLayout();
            default:
        }
        return false;
    }

    private void openSimSettingScreen() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
        startActivity(intent);
    }

    private void changeLanguage(Context context, String LanguageCode) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(LanguageCode.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }

    private void showAboutLayout() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.about_dialog);
        TextView closedialog = dialog.findViewById(R.id.dialog_dismiss);
        closedialog.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void showALertMessage() {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(MainActivity.this);

        builder.setMessage("Do you want to exit ?");
        builder.setTitle("Exit App !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> finishAffinity());
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMenuClicked) {
            Toast.makeText(getApplicationContext(), "Scanning & Updating System Settings, Please Wait", Toast.LENGTH_LONG).show();
            isMenuClicked = false;
        }
        final Handler handler = new Handler();
        final int delay = 5000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    initTelephonyManager();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        getNetworkType(telephonyManager.getNetworkType());
        if (isActivityPaused) {
            isActivityPaused = false;
            restartActivity();
        }
    }

    private String getNetworkType(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_GSM:
                return "GSM";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "WCDMA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "WCDMA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
        }
        return "";
    }

    private void restartActivity() {
        finish();
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void getNeighbourCellInfo() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        cellInfoList = telephonyManager.getAllCellInfo();
        neighborsList = filterNeighboursList((ArrayList<CellInfo>) cellInfoList);
        if (!cellInfoList.isEmpty()) {
            CellInfo cellInfo = cellInfoList.get(0);
            if (cellInfo instanceof CellInfoLte) {
                networkType = 3;
                ArrayList<ICell> cellList = (ArrayList<ICell>) NetMonsterFactory.INSTANCE.get(this).getCells();
                cellList.get(simID);

                LinearLayout detailsTable = findViewById(R.id.table);
                final LinearLayout headerRow = (LinearLayout) getLayoutInflater().inflate(R.layout.neighbours_layout, null);
                detailsTable.removeAllViews();
                detailsTable.addView(headerRow);

                if (neighborsList != null && neighborsList.size() > 0) {
                    for (CellInfo info : neighborsList) {
                        final LinearLayout tableRow = (LinearLayout) getLayoutInflater().inflate(R.layout.neighbours_layout, null);

                        TextView tvPlmn = tableRow.findViewById(R.id.txtPlmn);
                        TextView tvSys = tableRow.findViewById(R.id.txtSys);
                        TextView tvCellID = tableRow.findViewById(R.id.txtCellID);
                        TextView tvLac = tableRow.findViewById(R.id.txtLac);
                        TextView tvCode = tableRow.findViewById(R.id.txtCode);
                        TextView tvRfc = tableRow.findViewById(R.id.txtFreq);
                        TextView tvRxl = tableRow.findViewById(R.id.txtRxl);
                        TextView tvRxq = tableRow.findViewById(R.id.txtRxq);

                        tvRfc.setText("RFC");

                        String plmn = neighboringCellInfoGetPlmn(info);
                        if (!(plmn.length() > 5)) {
                            tvPlmn.setText(plmn);
                        }
                        tvSys.setText(neighboringCellInfoGetTech(info));

                        tvCellID.setText(String.valueOf(neighboringCellInfoGetCid(info)));
                        tvLac.setText(String.valueOf(neighboringCellInfoGetLac(info)));
                        tvCode.setText(neighboringCellInfoGetCode(info));
                        tvRfc.setText(neighboringCellInfoGetRFC(info));
                        tvRxl.setText(neighboringCellInfoGetRssi(info));
                        tvRxq.setText(neighboringCellInfoGetRssq(info));

                        detailsTable.addView(tableRow);
                    }
                }
            } else if (cellInfo instanceof CellInfoWcdma) {
                networkType = 2;
                layoutValues.setVisibility(View.VISIBLE);
                LinearLayout detailsTable = findViewById(R.id.table);
                final LinearLayout headerRow = (LinearLayout) getLayoutInflater().inflate(R.layout.neighbours_layout, null);
                detailsTable.removeAllViews();
                detailsTable.addView(headerRow);
                if (neighborsList != null && neighborsList.size() > 0) {
                    for (CellInfo info : neighborsList) {
                        try {
                            final LinearLayout tableRow = (LinearLayout) getLayoutInflater().inflate(R.layout.neighbours_layout, null);

                            TextView tvPlmn = tableRow.findViewById(R.id.txtPlmn);
                            TextView tvSys = tableRow.findViewById(R.id.txtSys);
                            TextView tvCellID = tableRow.findViewById(R.id.txtCellID);
                            TextView tvLac = tableRow.findViewById(R.id.txtLac);
                            TextView tvCode = tableRow.findViewById(R.id.txtCode);
                            TextView tvRfc = tableRow.findViewById(R.id.txtFreq);
                            TextView tvRxl = tableRow.findViewById(R.id.txtRxl);
                            TextView tvRxq = tableRow.findViewById(R.id.txtRxq);

                            String plmn = neighboringCellInfoGetPlmn(info);
                            if (!plmn.equals("21474836472147483647")) {
                                tvPlmn.setText(plmn);
                            }
                            tvSys.setText(neighboringCellInfoGetTech(info));
                            tvCellID.setText(String.valueOf(neighboringCellInfoGetCid(info)));
                            tvLac.setText(String.valueOf(neighboringCellInfoGetLac(info)));
                            tvCode.setText(neighboringCellInfoGetCode(info));
                            tvRfc.setText(neighboringCellInfoGetRFC(info));
                            tvRxl.setText(neighboringCellInfoGetRssi(info));
                            tvRxq.setText(neighboringCellInfoGetRssq(info));

                            detailsTable.addView(tableRow);
                        } catch (Exception e) {
                            e.getLocalizedMessage();
                        }
                    }
                }
            } else if (cellInfo instanceof CellInfoGsm) {
                networkType = 1;
                LinearLayout detailsTable = findViewById(R.id.table);
                final LinearLayout headerRow = (LinearLayout) getLayoutInflater().inflate(R.layout.neighbours_layout, null);
                ((TextView) headerRow.findViewById(R.id.txtFreq)).setText("Freq");
                detailsTable.removeAllViews();
                detailsTable.addView(headerRow);
                if (neighborsList != null && neighborsList.size() > 0) {
                    for (CellInfo info : neighborsList) {
                        final LinearLayout tableRow = (LinearLayout) getLayoutInflater().inflate(R.layout.neighbours_layout, null);

                        TextView tvPlmn = tableRow.findViewById(R.id.txtPlmn);
                        TextView tvSys = tableRow.findViewById(R.id.txtSys);
                        TextView tvCellID = tableRow.findViewById(R.id.txtCellID);
                        TextView tvLac = tableRow.findViewById(R.id.txtLac);
                        TextView tvCode = tableRow.findViewById(R.id.txtCode);
                        TextView tvRfc = tableRow.findViewById(R.id.txtFreq);
                        TextView tvRxl = tableRow.findViewById(R.id.txtRxl);
                        TextView tvRxq = tableRow.findViewById(R.id.txtRxq);

                        String sys = neighboringCellInfoGetTech(info);

                        String plmn = neighboringCellInfoGetPlmn(info);
                        if (!plmn.equals("21474836472147483647")) {
                            tvPlmn.setText(plmn);
                        }
                        tvSys.setText(sys);
                        tvCellID.setText(String.valueOf(neighboringCellInfoGetCid(info)));
                        tvLac.setText(String.valueOf(neighboringCellInfoGetLac(info)));
                        tvRfc.setText(neighboringCellInfoGetRFC(info));
                        tvRxl.setText(String.valueOf(neighboringCellInfoGetRssi(info)));
                        tvRxq.setText(neighboringCellInfoGetRssq(info));
                        tvCode.setText(neighboringCellInfoGetCode(info));

                        detailsTable.addView(tableRow);
                    }
                }
            }
        }
    }

    public String neighboringCellInfoGetRFC(CellInfo cellInfo) {
        String freq = "";
        int rfc;
        String frequency = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (cellInfo instanceof CellInfoWcdma) {
                freq = String.valueOf(BandFrequency.getBand(((CellInfoWcdma) cellInfo).getCellIdentity().getUarfcn()));
                frequency = freq;
            } else if (cellInfo instanceof CellInfoGsm) {
                freq = String.valueOf(BandFrequency.calculateGSMFrequency((((CellInfoGsm) cellInfo).getCellIdentity().getArfcn())));
                rfc = Integer.parseInt(freq);
                BandFrequency bandFrequency = new BandFrequency();
                frequency = String.valueOf(bandFrequency.calculateLTEFrequency(rfc));
            } else if (cellInfo instanceof CellInfoLte) {
                freq = String.valueOf(((CellInfoLte) cellInfo).getCellIdentity().getEarfcn());
                rfc = Integer.parseInt(freq);
                BandFrequency bandFrequency = new BandFrequency();
                frequency = String.valueOf(bandFrequency.calculateLTEFrequency(rfc));
            }
        }
        if (freq.length() > 5 || freq.isEmpty() || freq == null) {
            return "--";
        }
        return frequency;
    }

    public String neighboringCellInfoGetCid(CellInfo cellInfo) {
        int cid = 0;
        String length = "";
        if (cellInfo instanceof CellInfoWcdma) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }

            int cid2 = ((CellInfoWcdma) cellInfo).getCellIdentity().getCid();
            byte[] l_byte_array = convertByteArray__p(cid2);
            cid = Integer.parseInt(String.valueOf(getRNCID_or_CID__p(l_byte_array, 1)));
        } else if (cellInfo instanceof CellInfoGsm) {
            cid = ((CellInfoGsm) cellInfo).getCellIdentity().getCid();
        } else if (cellInfo instanceof CellInfoLte) {
            cid = getEnbValue(cellInfo);
        }

        length = String.valueOf(cid);
        if (length.length() > 6 || length.isEmpty() || length == null) {
            return "--";
        }
        return length;
    }

    public int neighboringCellInfoGetLac(CellInfo cellInfo) {

        int lac = 0;
        try {
            if (cellInfo instanceof CellInfoWcdma) {
                lac = ((CellInfoWcdma) cellInfo).getCellIdentity().getLac();
            } else if (cellInfo instanceof CellInfoGsm) {
                lac = ((CellInfoGsm) cellInfo).getCellIdentity().getLac();
            } else if (cellInfo instanceof CellInfoLte) {
                lac = ((CellInfoLte) cellInfo).getCellIdentity().getTac();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (String.valueOf(lac).length() > 5) {
            return 0;
        }
        return lac;
    }

    public String neighboringCellInfoGetRssi(CellInfo cellInfo) {

        String rssi = "";
        try {
            if (cellInfo instanceof CellInfoGsm) {
                CellInfoGsm cellInfoLte = (CellInfoGsm) cellInfo;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        rssi = String.valueOf(cellInfoLte.getCellSignalStrength().getRssi());
                    }
                }
            } else if (cellInfo instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        rssi = String.valueOf(cellInfoLte.getCellSignalStrength().getRsrp());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rssi.length() > 5 || rssi.isEmpty() || rssi == null) {
            return "--";
        }
        return rssi;
    }

    public String neighboringCellInfoGetCode(CellInfo cellInfo) {

        String bsci = "";
        try {
            if (cellInfo instanceof CellInfoGsm) {
                CellInfoGsm cellInfoLte = (CellInfoGsm) cellInfo;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        bsci = String.valueOf(cellInfoLte.getCellIdentity().getBsic());
                    }
                }
            } else if (cellInfo instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        bsci = String.valueOf(cellInfoLte.getCellIdentity().getPci());
                    }
                }
            } else if (cellInfo instanceof CellInfoWcdma) {
                CellInfoWcdma cellInfoLte = (CellInfoWcdma) cellInfo;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        bsci = String.valueOf(cellInfoLte.getCellIdentity().getPsc());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bsci.length() > 5 || bsci.isEmpty() || bsci == null) {
            return "--";
        }
        return bsci;
    }

    public String neighboringCellInfoGetRssq(CellInfo cellInfo) {
        String rssi = "--";
        try {
            if (cellInfo instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        rssi = String.valueOf(cellInfoLte.getCellSignalStrength().getRsrq());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rssi.length() > 5 || rssi.isEmpty() || rssi == null) {
            return "--";
        }
        return rssi;
    }


    public String neighboringCellInfoGetTech(CellInfo cellInfo) {
        String type = "";
        try {
            if (cellInfo instanceof CellInfoWcdma) {
                type = "3G";
            } else if (cellInfo instanceof CellInfoGsm) {
                type = "2G";
            } else if (cellInfo instanceof CellInfoLte) {
                type = "4G";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    public String neighboringCellInfoGetPlmn(CellInfo cellInfo) {
        String plmn = "";
        try {
            if (cellInfo instanceof CellInfoWcdma) {
                plmn = ((CellInfoWcdma) cellInfo).getCellIdentity().getMcc() + "" + ((CellInfoWcdma) cellInfo).getCellIdentity().getMnc();
            } else if (cellInfo instanceof CellInfoGsm) {
                plmn = ((CellInfoGsm) cellInfo).getCellIdentity().getMcc() + "" + ((CellInfoGsm) cellInfo).getCellIdentity().getMnc();
            } else if (cellInfo instanceof CellInfoLte) {
                plmn = ((CellInfoLte) cellInfo).getCellIdentity().getMcc() + "" + ((CellInfoLte) cellInfo).getCellIdentity().getMnc();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (plmn.length() > 5 || plmn.isEmpty() || plmn == null) {
            return "--";
        }
        return plmn;
    }

    private void saveGsmInfo() {
        FirebaseApp.initializeApp(this);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        ImeiModel imeiModel = new ImeiModel();
        GsmModel gsmModel = new GsmModel();

        imeiModel.imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        imeiModel.updatedOn = currentDateandTime;
        imeiModel.name = Settings.Global.getString(getContentResolver(), "device_name");
        imeiModel.timestamp = String.valueOf(System.currentTimeMillis());

        gsmModel.setDevice(Settings.Global.getString(getContentResolver(), "device_name"));
        gsmModel.setDate(currentDateandTime);
        gsmModel.setName(name.getText().toString());
        gsmModel.setMcc(mcc.getText().toString());
        gsmModel.setCid(enbDetails.getText().toString());
        gsmModel.setLac(tacValue.getText().toString());
        gsmModel.setBand(band.getText().toString());
        gsmModel.setMnc(mnc.getText().toString());
        gsmModel.setRnc(lcidDetails.getText().toString());
        gsmModel.setBsic(pciDetail.getText().toString());
        gsmModel.setRfc(txtrfc.getText().toString());
        gsmModel.setState(state.getText().toString());
        gsmModel.setRoam(roaming.getText().toString());
        gsmModel.setUci(uciDetails.getText().toString());
        gsmModel.setType(type.getText().toString());
        gsmModel.setBw(txtBandwidth.getText().toString());

        gsmModel.setRsrp(txtrsrpDetails.getText().toString());
        gsmModel.setRssi(txtrssi.getText().toString());
        gsmModel.setQual(txtrsrqDetail.getText().toString());
        gsmModel.setSnr(txtsnr.getText().toString());
        gsmModel.setCqi(txtcqi.getText().toString());
        gsmModel.setTa(txtta.getText().toString());
        gsmModel.setUl(ul.getText().toString());
        gsmModel.setDl(dl.getText().toString());
        gsmModel.setLat(latitude.getText().toString());
        gsmModel.setLon(longitude.getText().toString());
        gsmModel.setA(txtAlt.getText().toString());
        gsmModel.setV(txtSpeed.getText().toString());

        ArrayList<NeighborModel> neighborModels = new ArrayList<>();

        if (!neighborsList.isEmpty()) {
            for (CellInfo cellInfo : neighborsList) {
                NeighborModel neighbour = new NeighborModel();
                neighbour.cellID = String.valueOf(neighboringCellInfoGetCid(cellInfo));
                neighbour.sys = neighboringCellInfoGetTech(cellInfo);
                neighbour.plmn = neighboringCellInfoGetPlmn(cellInfo);
                neighbour.lac = String.valueOf(neighboringCellInfoGetLac(cellInfo));
                neighbour.code = neighboringCellInfoGetCode(cellInfo);
                neighbour.freq = neighboringCellInfoGetRFC(cellInfo);
                neighbour.rxl = neighboringCellInfoGetRssi(cellInfo);
                neighbour.rxq = neighboringCellInfoGetRssq(cellInfo);

                neighborModels.add(neighbour);
            }
        }

        gsmModel.setGsmNeighbours(neighborModels);

        reference.child("values").child(imeiModel.imei).child("GSM").child("SIM" + simID).setValue(gsmModel);
        reference.child("imei").child(imeiModel.imei).setValue(imeiModel);
    }

    private void saveLTEInfo() {
        FirebaseApp.initializeApp(this);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        ImeiModel imeiModel = new ImeiModel();
        LTEModel lteModel = new LTEModel();

        imeiModel.imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        imeiModel.updatedOn = currentDateandTime;
        imeiModel.name = Settings.Global.getString(getContentResolver(), "device_name");
        imeiModel.timestamp = String.valueOf(System.currentTimeMillis());

        lteModel.setDevice(Settings.Global.getString(getContentResolver(), "device_name"));
        lteModel.setDate(currentDateandTime);
        lteModel.setName(name.getText().toString());
        lteModel.setMcc(mcc.getText().toString());
        lteModel.setEnbID(enbDetails.getText().toString());
        lteModel.setTac(tacValue.getText().toString());
        lteModel.setBand(band.getText().toString());
        lteModel.setMnc(mnc.getText().toString());
        lteModel.setLcid(lcidDetails.getText().toString());
        lteModel.setPci(pciDetail.getText().toString());
        lteModel.setRfc(txtrfc.getText().toString());
        lteModel.setState(state.getText().toString());
        lteModel.setRoam(roaming.getText().toString());
        lteModel.setEci(uciDetails.getText().toString());
        lteModel.setType(type.getText().toString());
        lteModel.setBw(txtBandwidth.getText().toString());

        lteModel.setRsrp(txtrsrpDetails.getText().toString());
        lteModel.setRssi(txtrssi.getText().toString());
        lteModel.setRsrq(txtrsrqDetail.getText().toString());
        lteModel.setSnr(txtsnr.getText().toString());
        lteModel.setCqi(txtcqi.getText().toString());
        lteModel.setTa(txtta.getText().toString());
        lteModel.setUl(ul.getText().toString());
        lteModel.setDl(dl.getText().toString());
        lteModel.setLat(latitude.getText().toString());
        lteModel.setLon(longitude.getText().toString());
        lteModel.setA(txtAlt.getText().toString());
        lteModel.setV(txtSpeed.getText().toString());

        ArrayList<NeighborModel> neighborModels = new ArrayList<>();

        if (!neighborsList.isEmpty()) {
            for (CellInfo cellInfo : neighborsList) {
                NeighborModel neighbour = new NeighborModel();
                neighbour.cellID = String.valueOf(neighboringCellInfoGetCid(cellInfo));
                neighbour.sys = neighboringCellInfoGetTech(cellInfo);
                neighbour.plmn = neighboringCellInfoGetPlmn(cellInfo);
                neighbour.lac = String.valueOf(neighboringCellInfoGetLac(cellInfo));
                neighbour.code = neighboringCellInfoGetCode(cellInfo);
                neighbour.freq = neighboringCellInfoGetRFC(cellInfo);
                neighbour.rxl = neighboringCellInfoGetRssi(cellInfo);
                neighbour.rxq = neighboringCellInfoGetRssq(cellInfo);

                neighborModels.add(neighbour);
            }
        }

        lteModel.setNeighborModels(neighborModels);

        reference.child("values").child(imeiModel.imei).child("LTE").child("SIM" + simID).setValue(lteModel);
        reference.child("imei").child(imeiModel.imei).setValue(imeiModel);
    }

    private void saveWcdmaInfo() {
        FirebaseApp.initializeApp(this);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        ImeiModel imeiModel = new ImeiModel();
        WcdmaModel wcdmaModel = new WcdmaModel();

        imeiModel.imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        imeiModel.updatedOn = currentDateandTime;
        imeiModel.name = Settings.Global.getString(getContentResolver(), "device_name");
        imeiModel.timestamp = String.valueOf(System.currentTimeMillis());

        wcdmaModel.setDevice(Settings.Global.getString(getContentResolver(), "device_name"));
        wcdmaModel.setDate(currentDateandTime);
        wcdmaModel.setName(name.getText().toString());
        wcdmaModel.setMcc(mcc.getText().toString());
        wcdmaModel.setCid(enbDetails.getText().toString());
        wcdmaModel.setTac(tacValue.getText().toString());
        wcdmaModel.setBand(band.getText().toString());
        wcdmaModel.setMnc(mnc.getText().toString());
        wcdmaModel.setRnc(lcidDetails.getText().toString());
        wcdmaModel.setPsc(pciDetail.getText().toString());
        wcdmaModel.setRfc(txtrfc.getText().toString());
        wcdmaModel.setState(state.getText().toString());
        wcdmaModel.setRoam(roaming.getText().toString());
        wcdmaModel.setUci(uciDetails.getText().toString());
        wcdmaModel.setType(type.getText().toString());
        wcdmaModel.setBw(txtBandwidth.getText().toString());

        wcdmaModel.setRscp(txtrsrpDetails.getText().toString());
        wcdmaModel.setRssi(txtrssi.getText().toString());
        wcdmaModel.setEcno(txtrsrqDetail.getText().toString());
        wcdmaModel.setSnr(txtsnr.getText().toString());
        wcdmaModel.setCqi(txtcqi.getText().toString());
        wcdmaModel.setTa(txtta.getText().toString());
        wcdmaModel.setUl(ul.getText().toString());
        wcdmaModel.setDl(dl.getText().toString());
        wcdmaModel.setLat(latitude.getText().toString());
        wcdmaModel.setLon(longitude.getText().toString());
        wcdmaModel.setA(txtAlt.getText().toString());
        wcdmaModel.setV(txtSpeed.getText().toString());

        ArrayList<NeighborModel> neighborModels = new ArrayList<>();

        if (!neighborsList.isEmpty()) {
            for (CellInfo cellInfo : neighborsList) {
                NeighborModel neighbour = new NeighborModel();
                neighbour.cellID = String.valueOf(neighboringCellInfoGetCid(cellInfo));
                neighbour.sys = neighboringCellInfoGetTech(cellInfo);
                neighbour.plmn = neighboringCellInfoGetPlmn(cellInfo);
                neighbour.lac = String.valueOf(neighboringCellInfoGetLac(cellInfo));
                neighbour.code = neighboringCellInfoGetCode(cellInfo);
                neighbour.freq = neighboringCellInfoGetRFC(cellInfo);
                neighbour.rxl = neighboringCellInfoGetRssi(cellInfo);
                neighbour.rxq = neighboringCellInfoGetRssq(cellInfo);

                neighborModels.add(neighbour);
            }
        }

        wcdmaModel.setNeighborModels(neighborModels);

        reference.child("values").child(imeiModel.imei).child("WCDMA").child("SIM" + simID).setValue(wcdmaModel);
        reference.child("imei").child(imeiModel.imei).setValue(imeiModel);
    }

    private ArrayList<CellInfo> filterNeighboursList(ArrayList<CellInfo> cellInfos) {
        ArrayList<CellInfo> neighboursList = new ArrayList<>();
        for (CellInfo cellInfo : cellInfos) {
            try {
                if (cellInfo instanceof CellInfoWcdma) {
                    int mncValue = ((CellInfoWcdma) cellInfo).getCellIdentity().getMnc();
                    if ((mncValue == Integer.parseInt(mnc.getText().toString())) || String.valueOf(mncValue).length() > 3) {
                        neighboursList.add(cellInfo);
                    }
                } else if (cellInfo instanceof CellInfoGsm) {
                    int mncValue = ((CellInfoGsm) cellInfo).getCellIdentity().getMnc();
                    if (mncValue == Integer.parseInt(mnc.getText().toString()) || String.valueOf(mncValue).length() > 3) {
                        neighboursList.add(cellInfo);
                    }
                } else if (cellInfo instanceof CellInfoLte) {
                    int mncValue = ((CellInfoLte) cellInfo).getCellIdentity().getMnc();
                    if (mncValue == Integer.parseInt(mnc.getText().toString()) || String.valueOf(mncValue).length() > 3) {
                        neighboursList.add(cellInfo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return neighboursList;
    }

    @Override
    public void onBackPressed() {

    }
}