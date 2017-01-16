package fi.ct.mist.gpsui;

import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.BsonBinaryWriter;
import org.bson.BsonWriter;
import org.bson.io.BasicOutputBuffer;
import org.json.JSONObject;

import java.util.ArrayList;

import mist.MistService;
import mist.Peer;
import mist.RequestInterface;
import mist.api.Control;
import mist.api.Mist;
import mist.sandbox.Callback;

public class MainActivity extends AppCompatActivity {

    private boolean mBound = false;
    private MistService mistService;
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

        Mist.login(new Mist.LoginCb() {
            @Override
            public void cb(boolean connected) {
                ready();
            }

            @Override
            public void err(int code, String msg) {

            }

            @Override
            public void end() {

            }
        });

        peerOnlineState = (TextView) findViewById(R.id.peerOnlineState);
        enabled = (Switch) findViewById(R.id.enabled);
        counter = (TextView) findViewById(R.id.counter);
        lon = (TextView) findViewById(R.id.lon);
        lat = (TextView) findViewById(R.id.lat);
        accuracy = (TextView) findViewById(R.id.accuracy);
    }

    int id = 0;

    private void follow(final Peer peer) {
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

                Log.d("Follow", epid + " : " + value);

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
            public void cbString(String epid, String value) {

            }

            @Override
            public void err(int code, String msg) {

            }

            @Override
            public void end() {

            }
        });

        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getApplicationContext(), "Writing enabled to "+b, Toast.LENGTH_SHORT).show();
                Control.write(peer, "enabled", b, new Control.WriteCb() {
                    @Override
                    public void cb() {

                    }

                    @Override
                    public void err(int code, String msg) {

                    }

                    @Override
                    public void end() {

                    }
                });
            }
        });

    }

    private void cancel() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (id != 0) {
                            Control.cancel(id);
                        }
                    }
                },
                10000);
    }

    private void ready() {



        Mist.signals(new Mist.SignalsCb() {
            @Override
            public void cb(String signal) {
                Log.d("Signals", signal);
                if (signal.equals("peers")) {
                    Mist.listPeers(new Mist.ListPeersCb() {
                        @Override
                        public void cb(ArrayList<Peer> peers) {
                            for (final Peer peer : peers) {
                                if(peer.isOnline()) {
                                    Toast.makeText(getApplicationContext(), "Peer is online.", Toast.LENGTH_SHORT).show();
                                    peerOnlineState.setText("Peer is online.");
                                    Control.model(peer, new Control.ModelCb() {
                                        @Override
                                        public void cb(JSONObject data) {
                                            Log.d("Model:", data.toString());
                                            follow(peer);

                                        }

                                        @Override
                                        public void err(int code, String msg) {

                                        }

                                        @Override
                                        public void end() {

                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Peer is offline.", Toast.LENGTH_SHORT).show();
                                    peerOnlineState.setText("Peer is offline.");
                                }
                            }
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
            public void err(int code, String msg) {

            }

            @Override
            public void end() {

            }

        });


        Mist.listPeers(new Mist.ListPeersCb() {
            @Override
            public void cb(ArrayList<Peer> peers) {
                if (peers.size() == 0) {
                    Mist.settings(new Mist.SettingsCb() {
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

                for (final Peer peer : peers) {
                    Control.model(peer, new Control.ModelCb() {
                        @Override
                        public void cb(JSONObject data) {
                            Log.d("Model:", data.toString());

                            counter.setText("This is bahamas.");

                            follow(peer);
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
            public void err(int code, String msg) {

            }

            @Override
            public void end() {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancel();
    }
}

