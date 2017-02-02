package fi.ct.mist.gpsui;

import org.osmdroid.util.GeoPoint;

/**
 * Created by jan on 2/2/17.
 */

public class Point extends GeoPoint {
    private boolean latitudeInitialised = false;
    private boolean longitudeInitialised = false;

    public Point(double latitude, double longitude) {
        super(latitude, longitude);
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
