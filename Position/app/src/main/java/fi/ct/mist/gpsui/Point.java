package fi.ct.mist.gpsui;

import org.osmdroid.util.GeoPoint;

import mist.Peer;

/**
 * Created by jan on 2/2/17.
 */

public class Point extends GeoPoint {
    private boolean latitudeInitialised = false;
    private boolean longitudeInitialised = false;
    private Peer peer;
    String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    boolean added = false;

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public Point(Peer peer, String alias, double latitude, double longitude) {
        super(latitude, longitude);
        this.alias = alias;
        this.peer = peer;
    }

    public Peer getPeer() {
        return peer;
    }

    @Override
    public void setLatitude(double latitude) {
        super.setLatitude(latitude);
        this.latitudeInitialised = true;
    }

    @Override
    public void setLongitude(double aLongitude) {
        super.setLongitude(aLongitude);
        this.longitudeInitialised = true;
    }

    public boolean isFix() {
        return latitudeInitialised && longitudeInitialised;
    }


}
