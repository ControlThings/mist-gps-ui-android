package fi.ct.mist.gpsui;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import mist.MistIdentity;
import mist.MistService;
import mist.Peer;
import mist.api.Control;
import mist.api.Identity;
import mist.api.Mist;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private ArrayList<Point> points = new ArrayList<>();
    private MapView map;
    private MapMarkerOverlay markerOverlay;

    Intent mistService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mistService = new Intent(this, MistService.class);
        startService(mistService);

        // Login to sandbox
        Mist.login(new Mist.LoginCb() {
            @Override
            public void cb(boolean connected) {
                // on successful login to sandbox
                Log.d(TAG, "Login cb:" + connected);
                if (connected) {
                    ready();
                }
            }

            @Override
            public void err(int code, String msg) {}

            @Override
            public void end() {}
        });

        //important! set your user agent to prevent getting banned from the osm servers
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(5);

        /* Create the map overlay where we will add the markers */
        markerOverlay = new MapMarkerOverlay(map, ctx);

    }

    ArrayList<Integer> followIds = new ArrayList<>();

    private void follow(final Peer peer) {
        int id = Control.follow(peer, new Control.FollowCb() {
            @Override
            public void cbBool(String epid, boolean value) {
                if (epid.equals("enabled")) {
                    Log.d(TAG, "enabled" + value);
                    //enabled.setChecked(value);
                }
            }

            @Override
            public void cbInt(final String epid, final int value) {

                String str = Integer.toString(value);

                if (epid.equals("counter")) {
                    Log.d(TAG, "counter" + value);
                    //counter.setText(str);
                }
            }

            @Override
            public void cbFloat(String epid, float value) {
                Point currentPoint = null;
                for (Point point : points) {
                    if (point.getPeer().equals(peer)) {
                        currentPoint = point;
                        break;
                    }
                }

                if (currentPoint == null) {
                    Log.d(TAG, "Error! point not found.");
                }

                if (currentPoint.isFix() && !currentPoint.isAdded()) {
                    markerOverlay.addMarker(currentPoint);
                    currentPoint.setAdded(true);


                }

                if (epid.equals("lon")) {
                    //lon.setText(str);
                    currentPoint.setLongitude(value);
                }

                if (epid.equals("lat")) {
                    //lat.setText(str);
                    currentPoint.setLatitude(value);
                }

                if (epid.equals("accuracy")) {
                    //accuracy.setText(str);
                    Log.d(TAG, "accuracy " + value);
                }

                double mLat = 0;
                double mLon = 0;
                int numPoints = 0;

                boolean first = false;

                int minLat = Integer.MAX_VALUE;
                int maxLat = Integer.MIN_VALUE;
                int minLon = Integer.MAX_VALUE;
                int maxLon = Integer.MIN_VALUE;


                for (Point point : points) {
                    if (point.isFix()) {
                        int lat = point.getLatitudeE6();
                        int lon = point.getLongitudeE6();

                        maxLat = Math.max(lat, maxLat);
                        minLat = Math.min(lat, minLat);
                        maxLon = Math.max(lon, maxLon);
                        minLon = Math.min(lon, minLon);

                        mLat += point.getLatitude();
                        mLon += point.getLongitude();
                        numPoints++;
                    }
                }

                if (numPoints > 1) {
                    map.getController().zoomToSpan(Math.abs(maxLat - minLat) / 1000000.0, Math.abs(maxLon - minLon) / 1000000.0);
                    map.getController().setCenter(new GeoPoint((double) (maxLat + minLat) / 2.0 / 1000000.0,
                            (double) (maxLon + minLon) / 2.0 / 1000000.0));
                }
                else {
                    map.getController().setCenter(currentPoint);
                    map.getController().setZoom(14);
                }



            }

            @Override
            public void cbString(String epid, String value) {
                if (epid.equals("name")) {
                    if (value.length() != 0){
                        setTitle("position of " + value);
                    }
                }
            }

            @Override
            public void err(int code, String msg) {}

            @Override
            public void end() {}
        });
        followIds.add(new Integer(id));

    }

    private void updatePeersList(ArrayList<Peer> peers) {

        for (final Peer peer : peers) {
            if(peer.isOnline()) {
                Toast.makeText(getApplicationContext(), "Peer is online.", Toast.LENGTH_SHORT).show();
                //peerOnlineState.setText("Peer is online.");


                Identity.list(new Identity.ListCb() {
                    @Override
                    public void cb(ArrayList<MistIdentity> arrayList) {
                        for (MistIdentity identity : arrayList) {
                            if (Arrays.equals(identity.getUid(), peer.getRemoteId())) {
                                Point newPoint = new Point(peer, identity.getAlias(), 0,0);
                                points.add(newPoint);
                                Control.model(peer, new Control.ModelCb() {
                                    @Override
                                    public void cb(JSONObject data) {
                                        follow(peer);
                                    }

                                    @Override
                                    public void err(int code, String msg) {}

                                    @Override
                                    public void end() {}
                                });

                            }
                        }
                    }

                    @Override
                    public void err(int i, String s) {

                    }

                    @Override
                    public void end() {

                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "Peer is offline.", Toast.LENGTH_SHORT).show();
                //peerOnlineState.setText("Peer is offline.");
            }
        }
    }

    int readySignalId = 0;

    private void ready() {
        // Start listening to signals from Mist Sandbox

        // ok:     immediate response if signals request is successful
        // peers:  whenever the available peers changes, comes online, goes offline, sees new peer, lost access to peer

        readySignalId = Mist.signals(new Mist.SignalsCb() {
            @Override
            public void cb(String signal) {
                Log.d(TAG, "signal: " + signal);
                if (signal.equals("peers") || signal.equals("ok")) {
                    Mist.listPeers(new Mist.ListPeersCb() {
                        @Override
                        public void cb(ArrayList<Peer> peers) {
                            if (peers.size() == 0) {
                                // if there are no peers, request settings page for this app to be shown
                                // this feature will enable claiming wifi devices etc.
                                Mist.settings(Mist.Settings.Hint.addPeer, new Mist.SettingsCb() {
                                    @Override
                                    public void cb() {
                                    }

                                    @Override
                                    public void err(int i, String s) {
                                    }

                                    @Override
                                    public void end() {
                                    }

                                });
                            }

                            updatePeersList(peers);
                        }

                        @Override
                        public void err(int code, String msg) {
                        }

                        @Override
                        public void end() {
                        }
                    });
                }
            }
            @Override
            public void err(int code, String msg) {}

            @Override
            public void end() {}

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mistService);

        if (readySignalId != 0) {
            Mist.cancel(readySignalId);
            readySignalId = 0;
        }

        for (Integer id : followIds) {
            if (id.intValue() != 0) {
                Control.cancel(id.intValue());
            }
            followIds = new ArrayList<>();
        }
    }
}

