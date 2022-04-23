package app.com.ast

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import app.com.ast.speedtest.*
import com.airbnb.lottie.LottieAnimationView
import java.lang.reflect.Method


class SpeedTestActivity1 : AppCompatActivity(), LocationInterface {

    var isWifi = false
    var isThreeG = false
    var isFourG = false
    var locationUtils: LocationUtils? = null

    internal var getSpeedTestHostsHandler: GetSpeedTestHostsHandler? = null
    internal var tempBlackList: HashSet<String>? = null

    lateinit var goAnim: LottieAnimationView
    lateinit var wifiname: TextView
    lateinit var connectionTv: TextView
    lateinit var serverTv: TextView
    lateinit var serverImg: ImageView
    lateinit var progressBar: ProgressBar
    var drawerLayout: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_test1)

        init()
    }

    fun init() {
        locationUtils = LocationUtils(this, this@SpeedTestActivity1)
        goAnim = findViewById(R.id.goAnim)
        wifiname = findViewById(R.id.wifiname)
        connectionTv = findViewById(R.id.connectionTv)
        serverTv = findViewById(R.id.serverTv)
        serverImg = findViewById(R.id.serverImg)
        progressBar = findViewById(R.id.progressBar)
        drawerLayout = findViewById(R.id.drawer)

        goAnim.setOnClickListener {
            checkConnection()
            if (!isWifi && !isThreeG && !isFourG) {
                showNoInternetDialog()
            } else {
                setWifiNameOnTop()
                val intent = Intent(this, SpeedTestActivity2::class.java)
                intent.putExtra("wifiname", wifiname.text.toString())
                startActivity(intent)
            }
        }
        setWifiNameOnTop()
    }

    fun ClickMenu() {
        openDrawer(drawerLayout!!)
    }

    private fun openDrawer(drawerLayout: DrawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this@SpeedTestActivity1)
        builder.setTitle("No internet!")
        builder.setMessage("Do you want to exit or open wifi settings?")
        builder.setPositiveButton("Open Wifi Settings") { dialog, which ->
            dialog.dismiss()
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))

        }
        builder.setNegativeButton("Exit") { dialog, which ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun checkConnection() {
        when (AppUtils.getNetworkType(this@SpeedTestActivity1)) {

            Constants.NOT_CONNECTED -> {
                Log.d("connection", "not connected")
                isWifi = false
                isThreeG = false
                isFourG = false
                showNoInternetDialog()

            }
            Constants.WIFI_CONST -> {
                Log.d("connection", "wifi")
                isWifi = true
                isThreeG = false
                isFourG = false

            }
            Constants.THREEG, Constants.NETWORK_TYPE_EVDO_B, Constants.NETWORK_TYPE_HSDPA, Constants.NETWORK_TYPE_HSPA, Constants.NETWORK_TYPE_HSPAP, Constants.NETWORK_TYPE_EVDO_0, Constants.NETWORK_TYPE_EVDO_A, Constants.NETWORK_TYPE_CDMA -> {
                Log.d("connection", "3g")
                isThreeG = true
                isFourG = false
                isWifi = false

            }
            Constants.FOURG, Constants.NETWORK_TYPE_LTE -> {
                Log.d("connection", "4g")
                isFourG = true
                isThreeG = false
                isWifi = false

            }
        }
    }

    fun setWifiNameOnTop() {
        checkConnection()
        if (isWifi) {
            val wifiManager =
                applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                val ssid = wifiInfo.getSSID()
                if (ssid != null) {
                    wifiname.setText("Wifi: $ssid")
                    connectionTv.text = ssid
                } else {
                    wifiname.setText("Wifi: Not Available")
                    connectionTv.text = "----"
                }
            }
        } else if (isFourG || isThreeG) {
            val manager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val carrierName = getDataSimOperator(applicationContext)
                wifiname.setText(carrierName)
                connectionTv.text = carrierName
        } else {
            wifiname.setText("Wifi: Not Available")
            connectionTv.text = "----"
        }
    }

    fun getDataSimOperator(context: Context?): String? {
        if (context == null) {
            return null
        }
        val tm = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        return if (tm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val dataSubId = SubscriptionManager.getDefaultDataSubscriptionId()
                    val dataSimManager = tm.createForSubscriptionId(dataSubId)
                    dataSimManager.simOperatorName
                } else {
                    val operator = getDataSimOperatorBeforeN(context)
                    operator ?: tm.simOperatorName
                }
            } else {
                tm.simOperator
            }
        } else null
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun getDataSimOperatorBeforeN(context: Context?): String? {
        if (context == null) {
            return null
        }
        var dataSubId = -1
        try {
            val getDefaultDataSubId: Method? = SubscriptionManager::class.java.getDeclaredMethod("getDefaultDataSubId")
            if (getDefaultDataSubId != null) {
                getDefaultDataSubId.setAccessible(true)
                dataSubId = getDefaultDataSubId.invoke(null) as Int
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (dataSubId != -1) {
            val sm = context.getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            if (sm != null && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                val si: SubscriptionInfo? = sm.getActiveSubscriptionInfo(dataSubId)
                if (si != null) {
                    // format keep the same with android.telephony.TelephonyManager#getSimOperator
                    // MCC + MNC format
                    return java.lang.String.valueOf(si.getMcc()) + si.getMnc()
                }
            }
        }
        return null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_CHECK_SETTINGS) {
            when (resultCode) {

                Activity.RESULT_OK -> locationUtils?.requestLocation()
                Activity.RESULT_CANCELED -> Toast.makeText(
                    this@SpeedTestActivity1,
                    "Gps not enabled!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun setServer() {
        serverImg.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        serverTv.text = "----"
        tempBlackList = HashSet()

        if (getSpeedTestHostsHandler == null) {
            getSpeedTestHostsHandler = GetSpeedTestHostsHandler()
            getSpeedTestHostsHandler?.start()
        }

        Thread(
            Runnable {
                var timeCount = 500 //1min
                if (getSpeedTestHostsHandler != null) {
                    try {
                        while (getSpeedTestHostsHandler != null && !getSpeedTestHostsHandler!!.isFinished()) {
                            timeCount--
                            try {
                                Thread.sleep(100)
                            } catch (e: InterruptedException) {

                            }
                            if (timeCount <= 0) {
                                this@SpeedTestActivity1.runOnUiThread {
                                    serverTv.text = "No Connection..."
                                    serverImg.visibility = View.VISIBLE
                                    progressBar.visibility = View.INVISIBLE
                                }
                                getSpeedTestHostsHandler = null
                                return@Runnable
                            }
                        }
                    } catch (exception: NullPointerException) {
                        exception.printStackTrace()
                        this@SpeedTestActivity1.runOnUiThread {
                            serverTv.text = "No Connection"
                            serverImg.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                        }
                        getSpeedTestHostsHandler = null
                        return@Runnable
                    } catch (e: Exception) {
                        e.printStackTrace()
                        this@SpeedTestActivity1.runOnUiThread {
                            serverTv.text = "No Connection..."
                            serverImg.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                        }
                        getSpeedTestHostsHandler = null
                        return@Runnable
                    }
                } else {
                    return@Runnable
                }

                //Find closest server
                val mapKey = getSpeedTestHostsHandler?.getMapKey()
                val mapValue = getSpeedTestHostsHandler?.getMapValue()
                val selfLat = getSpeedTestHostsHandler?.getSelfLat()
                val selfLon = getSpeedTestHostsHandler?.getSelfLon()
                var tmp = 10000.0
                var dist = 0.0
                var findServerIndex = 0
                var sponsor = ""

                if (mapKey == null || mapValue == null || selfLat == null || selfLon == null) {
                    return@Runnable
                } else {

                    for (index in mapKey.keys) {
                        if (tempBlackList!!.contains(mapValue[index]!!.get(5))) {
                            continue
                        }

                        val source = Location("Source")
                        source.latitude = selfLat
                        source.longitude = selfLon

                        val ls = mapValue[index]
                        val dest = Location("Dest")
                        dest.latitude = java.lang.Double.parseDouble(ls!!.get(0))
                        dest.longitude = java.lang.Double.parseDouble(ls.get(1))

                        val distance = source.distanceTo(dest).toDouble()
                        if (tmp > distance) {
                            tmp = distance
                            dist = distance
                            findServerIndex = index
                            sponsor = ls.get(5)
                            Log.d("server", sponsor)

                            this@SpeedTestActivity1.runOnUiThread {
                                serverTv.text = sponsor
                                serverImg.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }).start()
    }

    override fun getLocation(location: Location) {

    }
}