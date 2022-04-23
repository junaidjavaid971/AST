package app.com.ast

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.com.ast.database.RoomDB
import app.com.ast.database.SpeedTest
import app.com.ast.speedtest.*
import com.airbnb.lottie.LottieAnimationView
import com.github.anastr.speedviewlib.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class SpeedTestActivity2 : AppCompatActivity(), SpeedTestContract.View {

    lateinit var goAnim: LottieAnimationView
    lateinit var connAnim: LottieAnimationView
    lateinit var wifiname: TextView
    lateinit var upload: TextView

    //    lateinit var backImg: ImageView
    lateinit var ping: TextView
    lateinit var download: TextView
    lateinit var connText: TextView
    lateinit var btnStart: Button
    lateinit var speedview: AwesomeSpeedometer
    lateinit var tvHistory: TextView

    lateinit var database: RoomDB

    companion object {
        internal var position = 0
        internal var lastPosition = 0
    }

    internal var getSpeedTestHostsHandler: GetSpeedTestHostsHandler? = null
    internal var tempBlackList: HashSet<String>? = null

    private var isPeedTestRunning = false

    var isWifi = false
    var isThreeG = false
    var isFourG = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_test2)
        supportActionBar?.hide()

        database = RoomDB.getInstance(this);
        initViews()

        setWifiNameOnTop()
        val dec = DecimalFormat("#.#")

        tempBlackList = HashSet()

        getSpeedTestHostsHandler = GetSpeedTestHostsHandler()
        getSpeedTestHostsHandler?.start()

        if (getSpeedTestHostsHandler == null) {
            getSpeedTestHostsHandler = GetSpeedTestHostsHandler()
            getSpeedTestHostsHandler?.start()
        }

        btnStart.setOnClickListener {
            startTest(dec)
        }

        btnStart.setOnClickListener {
            checkConnection()
            if (!isWifi && !isThreeG && !isFourG) {
                val toast = Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT)
                toast.setGravity(
                    Gravity.CENTER, 0, 0
                )
                toast.show()
                finish()
            } else {
                setWifiNameOnTop()
                startTestAgain()
            }
        }
        tvHistory.setOnClickListener {
            val intent = Intent(this@SpeedTestActivity2, HistoryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    fun setWifiNameOnTop() {
        checkConnection()
        if (isWifi) {
            val wifiManager =
                getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                val ssid = wifiInfo.getSSID()
                if (ssid != null) {
                    wifiname.setText("Wifi: $ssid")
                } else {
                    wifiname.setText("Wifi: Not Available")
                }
            }
        } else if (isFourG || isThreeG) {

            val manager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val carrierName = manager.networkOperatorName
            wifiname.setText(carrierName)
        } else {
            wifiname.setText("Wifi: Not Available")
        }
    }

    fun initViews() {
        goAnim = findViewById(R.id.goAnim)
        wifiname = findViewById(R.id.wifiname)
        connAnim = findViewById(R.id.connAnim)
        upload = findViewById(R.id.tvUploadSpeed)
//        backImg = findViewById(R.id.backImg)
        ping = findViewById(R.id.tvPingSpeed)
        download = findViewById(R.id.tvDownloadSpeed)
        connText = findViewById(R.id.connText)
        btnStart = findViewById(R.id.analyzeBtn)
        speedview = findViewById(R.id.speedView)
        tvHistory = findViewById(R.id.tv_history)
    }

    fun checkConnection() {
        when (AppUtils.getNetworkType(this)) {

            Constants.NOT_CONNECTED -> {
                Log.d("connection", "not connected")
                isWifi = false
                isThreeG = false
                isFourG = false
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                finish()
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


    fun startTestAgain() {
        connAnim.visibility = View.VISIBLE
        speedview.visibility = View.INVISIBLE
        goAnim.visibility = View.GONE

        ping.text = "0"
        download.text = "0"
        upload.text = "0"

        val dec = DecimalFormat("#.#")
        getSpeedTestHostsHandler = GetSpeedTestHostsHandler()
        getSpeedTestHostsHandler?.start()


        if (getSpeedTestHostsHandler == null) {
            getSpeedTestHostsHandler = GetSpeedTestHostsHandler()
            getSpeedTestHostsHandler?.start()
        }

        startTest(dec)
    }

    override fun onPause() {
        isPeedTestRunning = false
        connText.visibility = View.INVISIBLE
        connAnim.visibility = View.INVISIBLE
        speedview.visibility = View.INVISIBLE
        goAnim.visibility = View.VISIBLE

        super.onPause()
    }

    fun openWifiOnoff(view: View) {
        ping.text = "0"
        download.text = "0"
        upload.text = "0"
    }

    @SuppressLint("SetTextI18n", "HardwareIds")
    fun startTest(dec: DecimalFormat) {
        btnStart.visibility = View.INVISIBLE
        Snackbar.make(
            findViewById(android.R.id.content),
            "Ping test might take some time. Please be patient!",
            20000
        ).show()
        connText.visibility = View.VISIBLE
        isPeedTestRunning = true

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
                                runOnUiThread {
                                    Toast.makeText(
                                        applicationContext,
                                        "No Connection...",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    connText.text = "No Connection"

                                }
                                getSpeedTestHostsHandler = null
                                return@Runnable
                            }
                        }
                    } catch (exception: NullPointerException) {
                        exception.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "No Connection...",
                                Toast.LENGTH_LONG
                            ).show()

                            connText.text = "No Connection"

                        }
                        getSpeedTestHostsHandler = null
                        return@Runnable
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "No Connection...",
                                Toast.LENGTH_LONG
                            ).show()

                            connText.text = "No Connection"
                        }
                        getSpeedTestHostsHandler = null
                        return@Runnable
                    }
                } else {
                    return@Runnable
                }

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

                    for (index in mapKey!!.keys) {
                        if (tempBlackList!!.contains(mapValue!![index]!!.get(5))) {
                            continue
                        }

                        val source = Location("Source")
                        source.latitude = selfLat!!
                        source.longitude = selfLon!!

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
                        }
                    }
                    val uploadAddr = mapKey[findServerIndex]
                    val info = mapValue[findServerIndex]


                    if (info == null) {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "There was a problem in getting Host Location. Try again later.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        return@Runnable
                    }

                    //Reset value, graphics
                    runOnUiThread {
                        ping.text = "0"
                        download.text = "0"
                        upload.text = "0"
                        speedview.speedTo(0f, 20)
                    }

                    val pingRateList = ArrayList<Double>()
                    val downloadRateList = ArrayList<Double>()
                    val uploadRateList = ArrayList<Double>()
                    var pingTestStarted: Boolean? = false
                    var pingTestFinished: Boolean? = false
                    var downloadTestStarted: Boolean? = false
                    var downloadTestFinished: Boolean? = false
                    var uploadTestStarted: Boolean? = false
                    var uploadTestFinished: Boolean? = false

                    //Init Test
                    val pingTest = PingTest(info[6].replace(":8080", ""), 6)
                    val downloadTest =
                        DownloadSpeedTest(
                            uploadAddr?.replace(
                                uploadAddr.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()[uploadAddr.split(
                                    "/".toRegex()
                                ).dropLastWhile { it.isEmpty() }.toTypedArray().size - 1], ""
                            )
                        )
                    val uploadTest = UploadSpeedTest(uploadAddr)


                    while (isPeedTestRunning) {

                        if (!pingTestStarted!!) {
                            pingTest.start()
                            pingTestStarted = true

                            runOnUiThread {
                                connText.visibility = View.GONE
                                connAnim.visibility = View.GONE
                                speedview.visibility = View.VISIBLE
                            }
                        }
                        if (pingTestFinished!! && !downloadTestStarted!!) {
                            downloadTest.start()
                            downloadTestStarted = true
                        }
                        if (downloadTestFinished!! && !uploadTestStarted!!) {
                            uploadTest.start()
                            uploadTestStarted = true
                        }

                        val pingDec = DecimalFormat("#")
                        //Ping Test
                        if (pingTestFinished) {
                            //Failure
                            if (pingTest.avgRtt === 0.0) {
                                println("Ping error...")
                            } else {
                                //Success
                                runOnUiThread { ping.setText(pingDec.format(pingTest.avgRtt) + " ms") }
                            }
                        } else {
                            pingRateList.add(pingTest.instantRtt)

                            runOnUiThread { ping.setText(pingDec.format(pingTest.instantRtt) + " ms") }

                        }


                        //Download Test
                        if (pingTestFinished) {
                            if (downloadTestFinished) {
                                //Failure
                                if (downloadTest.finalDownloadRate === 0.0) {
                                    println("Download error...")
                                } else {
                                    //Success
                                    runOnUiThread {
                                        download.setText(dec.format(downloadTest.finalDownloadRate) + " Mbps")
                                    }
                                }
                            } else {
                                //Calc position
                                val downloadRate = downloadTest.instantDownloadRate
                                downloadRateList.add(downloadRate)
                                position = getPositionByRate(downloadRate)

                                runOnUiThread {
                                    download.text =
                                        dec.format(downloadTest.instantDownloadRate) + " Mbps"
                                    speedview.speedTo(
                                        downloadTest.instantDownloadRate.toFloat(), 20
                                    )
                                }
                                lastPosition =
                                    position

                            }
                        }

                        //Upload Test
                        if (downloadTestFinished) {
                            if (uploadTestFinished!!) {
                                Log.d("checkupload", uploadTest?.finalUploadRate.toString())
                                //Failure
                                if (uploadTest.finalUploadRate == 0.0) {
                                    println("Upload error...")
                                } else {
                                    //Success

                                    runOnUiThread {
                                        upload.text =
                                            dec.format(uploadTest.finalUploadRate) + " Mbps"
                                    }
                                }
                            } else {
                                //Calc position
                                val uploadRate = uploadTest.instantUploadRate
                                uploadRateList.add(uploadRate)
                                position = getPositionByRate(uploadRate)


                                runOnUiThread {
                                    speedview.speedTo(uploadTest.instantUploadRate.toFloat(), 20)

                                    upload.text = dec.format(uploadTest.instantUploadRate) + " Mbps"
                                }
                                lastPosition =
                                    position

                            }
                        }

                        //Test bitti
                        if (pingTestFinished && downloadTestFinished && uploadTest.isFinished) {
                            runOnUiThread {
                                Log.d("checkupload", uploadTest.isFinished.toString())
                                speedview.speedTo(0f, 20)
                                Handler().postDelayed({
                                    goAnim.visibility = View.VISIBLE
                                    speedview.visibility = View.INVISIBLE

                                    val sdf = SimpleDateFormat("dd/MM/yyyy  HH:mm:ss")
                                    val currentDateandTime = sdf.format(Date())

                                    val speedTest = SpeedTest()
                                    speedTest.setPingTest(ping.text.toString())
                                    speedTest.setUploadSpeed(upload.text.toString())
                                    speedTest.setDownloadSpeed(download.text.toString())
                                    speedTest.setTestingMode(wifiname.text.toString())
                                    speedTest.setTestTime(currentDateandTime)

                                    database.mainDao().insert(speedTest)

                                    val reference = FirebaseDatabase.getInstance().reference

                                    val imei = Settings.Secure.getString(
                                        applicationContext.contentResolver,
                                        Settings.Secure.ANDROID_ID
                                    )
                                    reference.child("SpeedTest").child(imei)
                                        .child(getRandomString()).setValue(speedTest)

                                    Toast.makeText(
                                        this@SpeedTestActivity2,
                                        "Test results stored in local database",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    btnStart.visibility = View.VISIBLE
                                }, 3000)

                            }
                            break
                        }

                        if (pingTest.isFinished) {

                            pingTestFinished = true
                        }
                        if (downloadTest.isFinished) {


                            downloadTestFinished = true
                        }
                        if (uploadTest.isFinished) {

                            uploadTestFinished = true
                        }

                        if (pingTestStarted && !pingTestFinished) {
                            try {
                                Thread.sleep(300)
                            } catch (e: InterruptedException) {
                            }

                        } else {
                            try {
                                Thread.sleep(100)
                            } catch (e: InterruptedException) {
                            }

                        }

                    }
                }

            }).start()
    }


    fun getPositionByRate(rate: Double): Int {
        if (rate <= 1) {
            return (rate * 30).toInt()

        } else if (rate <= 10) {
            return (rate * 6).toInt() + 30

        } else if (rate <= 30) {
            return ((rate - 10) * 3).toInt() + 90

        } else if (rate <= 50) {
            return ((rate - 30) * 1.5).toInt() + 150

        } else if (rate <= 100) {
            return ((rate - 50) * 1.2).toInt() + 180
        }

        return 0
    }

    override fun saved() {
        Log.d("checkhistory", "saved")
    }

    override fun failed() {
        Log.d("checkhistory", "error")

    }

    private fun getRandomString(): String {
        val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"
        val random = Random()
        val sb = StringBuilder(16)
        for (i in 0 until 16)
            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        return sb.toString()
    }
}