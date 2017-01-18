package fi.ct.mist.gpsui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import mist.MistService;
import mist.Peer;
import mist.api.Control;
import mist.api.Mist;

public class MainActivity extends AppCompatActivity {

    private TextView peerOnlineState;
    private Switch enabled;
    private TextView counter;
    private TextView lon;
    private TextView lat;
    private TextView accuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MistService.class);
        startService(intent);

        // Login to sandbox
        Mist.login(new Mist.LoginCb() {
            @Override
            public void cb(boolean connected) {
                // on successful login to sandbox
                ready();
            }

            @Override
            public void err(int code, String msg) {}

            @Override
            public void end() {}
        });

        // Get handles to ui components
        peerOnlineState = (TextView) findViewById(R.id.peerOnlineState);
        enabled = (Switch) findViewById(R.id.enabled);
        counter = (TextView) findViewById(R.id.counter);
        lon = (TextView) findViewById(R.id.lon);
        lat = (TextView) findViewById(R.id.lat);
        accuracy = (TextView) findViewById(R.id.accuracy);
    }

    int id = 0;

    private void follow(final Peer peer) {
        if (id != 0) {
            Control.cancel(id);
            id = 0;
        }
        id = Control.follow(peer, new Control.FollowCb() {
            @Override
            public void cbBool(String epid, boolean value) {
                if (epid.equals("enabled")) {
                    enabled.setChecked(value);
                }
            }

            @Override
            public void cbInt(final String epid, final int value) {

                String str = Integer.toString(value);

                if (epid.equals("counter")) {
                    counter.setText(str);
                }
            }

            @Override
            public void cbFloat(String epid, float value) {

                String str = Double.toString(value);

                if (epid.equals("lon")) {
                    lon.setText(str);
                }

                if (epid.equals("lat")) {
                    lat.setText(str);
                }

                if (epid.equals("accuracy")) {
                    accuracy.setText(str);
                }

            }

            @Override
            public void cbString(String epid, String value) {}

            @Override
            public void err(int code, String msg) {}

            @Override
            public void end() {}
        });

        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getApplicationContext(), "Writing enabled to "+b, Toast.LENGTH_SHORT).show();
                Control.write(peer, "enabled", b, new Control.WriteCb() {
                    @Override
                    public void cb() {}

                    @Override
                    public void err(int code, String msg) {}

                    @Override
                    public void end() {}
                });
            }
        });

    }

    private void updatePeersList(ArrayList<Peer> peers) {

        if(peers.size() > 1) {
            // This demo app does not support multiple peers
            Toast.makeText(getApplicationContext(), "Warning: Multiple devices not supported in this app.", Toast.LENGTH_LONG).show();
        }

        for (final Peer peer : peers) {
            if(peer.isOnline()) {
                Toast.makeText(getApplicationContext(), "Peer is online.", Toast.LENGTH_SHORT).show();
                peerOnlineState.setText("Peer is online.");
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
            } else {
                Toast.makeText(getApplicationContext(), "Peer is offline.", Toast.LENGTH_SHORT).show();
                peerOnlineState.setText("Peer is offline.");
            }
        }
    }

    private void ready() {
        // Start listening to signals from Mist Sandbox

        // ok:     immediate response if signals request is successful
        // peers:  whenever the available peers changes, comes online, goes offline, sees new peer, lost access to peer

        Mist.signals(new Mist.SignalsCb() {
            @Override
            public void cb(String signal) {
                if (signal.equals("peers") || signal.equals("ok")) {
                    Mist.listPeers(new Mist.ListPeersCb() {
                        @Override
                        public void cb(ArrayList<Peer> peers) {
                            if (peers.size() == 0) {
                                // if there are no peers, request settings page for this app to be shown
                                // this feature will enable claiming wifi devices etc.
                                Mist.settings(new Mist.SettingsCb() {
                                    @Override
                                    public void cb() {}

                                    @Override
                                    public void err(int i, String s) {}

                                    @Override
                                    public void end() {}
                                });
                            }

                            updatePeersList(peers);
                        }

                        @Override
                        public void err(int code, String msg) {}

                        @Override
                        public void end() {}
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

        if (id != 0) {
            // Cancel the last follow request
            Control.cancel(id);
        }
    }
}

