package com.example.myapplication

import android.Manifest
import android.net.wifi.ScanResult
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.os.StrictMode
import android.util.Log
import androidx.core.app.ActivityCompat.requestPermissions
import com.litslink.networkhelper.NetSwitchUtil


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // NETWORK ON MAIN THREAD
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        val netSwitchUtil = NetSwitchUtil(applicationContext)

        requestPermissions(this, arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ), 1)

//        netSwitchUtil.startWiFiScan({ scanResults: MutableList<ScanResult> ->
//            for (scanResult in scanResults) {
//                Log.i("WIFISCAN", "NET: " + scanResult.SSID);
//            }
//        })

        fab.setOnClickListener { view ->
//            val sendWiFiRequest = netSwitchUtil.postWiFiRequest(
//                "http://192.168.1.1/osc/commands/execute",
//                "{\"name\": \"camera.takePicture\"}"
//            )

            val connectToWiFi = netSwitchUtil.connectToWiFi("THETAYL00104052.OSC", "00104052")

            Snackbar.make(view, "COMPLETE: " + connectToWiFi, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
