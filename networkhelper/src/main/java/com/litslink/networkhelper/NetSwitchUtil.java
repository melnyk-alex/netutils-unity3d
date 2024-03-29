package com.litslink.networkhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class NetSwitchUtil extends BroadcastReceiver {

    private Context context;
    private OnWiFiFoundListener onWiFiFoundListener;

    public NetSwitchUtil(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Getting system service WiFiManager.
     *
     * @param context application context instance.
     * @return WiFiManager instance.
     */
    public WifiManager getWiFiManager(Context context) {
        return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Getting system service WiFiManager.
     *
     * @return WiFiManager instance.
     */
    public WifiManager getWiFiManager() {
        return getWiFiManager(this.context);
    }

    /**
     * Getting system service ConnectionManager.
     *
     * @param context application context instance.
     * @return ConnectionManager instance.
     */
    public ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Getting system service ConnectionManager.
     *
     * @return ConnectionManager instance.
     */
    public ConnectivityManager getConnectivityManager() {
        return getConnectivityManager(this.context);
    }

    /**
     * Send request thought the WiFi if it's enabled and connected.
     *
     * @param url  - URL to send request.
     * @param json - JSON data to be send in request.
     * @return Response from URL.
     */
    public String postWiFiRequest(String url, String json) {
        ConnectivityManager connectivityManager = getConnectivityManager();

        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkCapabilities networkCapabilities =
                    connectivityManager.getNetworkCapabilities(network);

            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                try {
                    HttpURLConnection con = (HttpURLConnection) network.openConnection(new URL(url));
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    OutputStream os = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                    writer.write(json, 0, json.length());
                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = con.getResponseCode(); // To Check for 200

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        return readResponse(con.getInputStream());
                    } else {
                        return readResponse(con.getErrorStream());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Reads input stream to string.
     *
     * @param inputStream source input stream.
     * @return output string.
     */
    private String readResponse(InputStream inputStream) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer sb = new StringBuffer("");
            String line = "";

            while ((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            in.close();

            // RESPONSE
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Send request thought the WiFi if it's enabled and connected.
     *
     * @param url  - URL to send request.
     * @param json - JSON data to be send in request.
     * @return Response from URL.
     */
    public byte[] postWiFiRequestRaw(String url, String json) {
        ConnectivityManager connectivityManager = getConnectivityManager();

        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkCapabilities networkCapabilities =
                    connectivityManager.getNetworkCapabilities(network);

            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                try {
                    HttpURLConnection con = (HttpURLConnection) network.openConnection(new URL(url));
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    OutputStream os = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                    writer.write(json, 0, json.length());
                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = con.getResponseCode(); // To Check for 200

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        return readResponseRaw(con.getInputStream());
                    } else {
                        return readResponseRaw(con.getErrorStream());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Send request thought the WiFi if it's enabled and connected.
     *
     * @param url - URL to send request.
     * @return Response from URL.
     */
    public byte[] getWiFiRequestRaw(String url) {
        ConnectivityManager connectivityManager = getConnectivityManager();

        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkCapabilities networkCapabilities =
                    connectivityManager.getNetworkCapabilities(network);

            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                try {
                    HttpURLConnection con = (HttpURLConnection) network.openConnection(new URL(url));
                    con.setRequestMethod("GET");
                    con.setDoOutput(false);
                    con.setDoInput(true);

                    int responseCode = con.getResponseCode(); // To Check for 200

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        return readResponseRaw(con.getInputStream());
                    } else {
                        return readResponseRaw(con.getErrorStream());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Reads input stream to byte array.
     *
     * @param inputStream source input stream.
     * @return output byte array.
     */
    private byte[] readResponseRaw(InputStream inputStream) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns list of pre-configured networks.
     *
     * @return List of pre-configured networks.
     */
    public List<WifiConfiguration> getConfiguredNetworks() {
        return getWiFiManager().getConfiguredNetworks();
    }

    /**
     * Start a service to scan WiFi networks.
     *
     * @param onWiFiFoundListener - listener which will be used to callback when WiFi list updates.
     */
    public void startWiFiScan(OnWiFiFoundListener onWiFiFoundListener) {
        startWiFiScan(onWiFiFoundListener, false);
    }

    /**
     * Start a service to scan WiFi networks.
     *
     * @param onWiFiFoundListener - listener which will be used to callback when WiFi list updates.
     * @param forceWiFiOn         - true if need to turn on WiFi if it's off, otherwise false.
     */
    public boolean startWiFiScan(OnWiFiFoundListener onWiFiFoundListener, boolean forceWiFiOn) {
        if (onWiFiFoundListener == null) {
            return false;
        }

        this.onWiFiFoundListener = onWiFiFoundListener;


        WifiManager wifiManager = getWiFiManager();

        if (forceWiFiOn && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        this.context.registerReceiver(this, intentFilter);

        wifiManager.startScan();

        return true;
    }

    /**
     * Check whether app is default route is WiFi or not.
     *
     * @return true if app default route is WiFi, otherwise false.
     */
    public boolean isWiFiDefault() {
        ConnectivityManager connectivityManager = getConnectivityManager();

        Network boundNetwork = connectivityManager.getBoundNetworkForProcess();
        NetworkInfo boundNetworkInfo = connectivityManager.getNetworkInfo(boundNetwork);

        return boundNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Force switch to the WiFi or Mobile Data route.
     *
     * @param isWiFi true if need to switch to the WiFi or false if to the Mobile Data.
     * @return true if successful, otherwise false.
     */
    public boolean selectDefaultNetwork(boolean isWiFi) {
        int connType = isWiFi
                ? ConnectivityManager.TYPE_WIFI
                : ConnectivityManager.TYPE_MOBILE;

        ConnectivityManager connectivityManager = getConnectivityManager();

        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);

            if (networkInfo.getType() == connType) {
                connectivityManager.bindProcessToNetwork(network);
                return true;
            }
        }

        return false;
    }

    /**
     * Stop scanning service.
     */
    public void stopWiFiScan() {
        this.onWiFiFoundListener = null;

        this.context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = getWiFiManager(context);

        if (onWiFiFoundListener != null && WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
            onWiFiFoundListener.onWiFiListUpdate(wifiManager.getScanResults());
        }
    }

    /**
     * Connect to WiFi network with SSID and password.
     *
     * @param SSID     - SSID of WiFi which need to be connected.
     * @param password - password for WiFi network.
     */
    public void connectToWiFi(String SSID, String password) throws Exception {
        String networkSSID = String.format("\"%s\"", SSID);
        String networkPassword = String.format("\"%s\"", password);

        WifiManager wifiManager = getWiFiManager();
        wifiManager.disconnect();

        WifiConfiguration foundWiFiConfiguration = getWifiConfiguration(wifiManager, networkSSID);

        if (foundWiFiConfiguration != null) {
            wifiManager.removeNetwork(foundWiFiConfiguration.networkId);
        }

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = networkSSID;
        wifiConfiguration.preSharedKey = networkPassword;
        wifiConfiguration.priority = getMaxConfigurationPriority(wifiManager) + 1;
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

        int networkId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(networkId, true);

        if (!wifiManager.reconnect()) {
            throw new Exception("Unable to connect");
        }
    }

    /**
     * Get list of WiFi configurations.
     *
     * @param wifiManager - WiFiManager instance.
     * @param SSID        - WiFi network SSID.
     * @return WiFiConfiguration instance if found.
     */
    private WifiConfiguration getWifiConfiguration(WifiManager wifiManager, String SSID) {
        final List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();

        for (final WifiConfiguration config : configurations) {
            if (config.SSID.equals(SSID)) {
                return config;
            }
        }

        return null;
    }

    /**
     * Get maximum priority assigned to a network configuration.
     * This helps to prioritize which network to connect to.
     *
     * @param wifiManager
     * @return int: Maximum configuration priority number.
     */
    private int getMaxConfigurationPriority(final WifiManager wifiManager) {
        final List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        int maxPriority = 0;
        for (final WifiConfiguration config : configurations) {
            if (config.priority > maxPriority)
                maxPriority = config.priority;
        }

        return maxPriority;
    }

    /**
     * Checks WiFi state.
     *
     * @return true if WiFi currently enabled, otherwise false.
     */
    public boolean isWiFiEnabled() {
        return getWiFiManager().isWifiEnabled();
    }

    /**
     * Sets WiFi state.
     *
     * @param state true to turn on, otherwise false.
     */
    public void setWiFiEnabled(boolean state) {
        WifiManager wifiManager = getWiFiManager();
        wifiManager.setWifiEnabled(state);
    }

    /**
     * Checks WiFi connection.
     *
     * @return true if WiFi currently connected, otherwise false.
     */
    public boolean isWiFiConnected() {
        WifiManager wifiManager = getWiFiManager();

        if (wifiManager.isWifiEnabled()) {
            return wifiManager.getConnectionInfo().getNetworkId() != -1;
        }

        return false;
    }

    /**
     * Gets WiFi access point SSID.
     *
     * @return SSID of connected WiFi access point, otherwise null.
     */
    public String getWiFiNetwork() {
        WifiManager wifiManager = getWiFiManager();

        if (wifiManager.isWifiEnabled()) {
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();

            if (connectionInfo.getNetworkId() != -1) {
                return connectionInfo.getSSID();
            }
        }

        return null;
    }

    /**
     * Opens settings activity.
     */
    public void openSettings() {
        Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        this.context.startActivity(settingIntent);
    }

    /**
     * Interface for callback feature using in scan WiFi networks.
     */
    public static interface OnWiFiFoundListener {

        /**
         * Callback function which called each time when WiFi networks list updated.
         *
         * @param scanResults - list of results of scanning.
         */
        public void onWiFiListUpdate(List<ScanResult> scanResults);

    }
}
