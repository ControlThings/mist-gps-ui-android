package fi.ct.mist.gpsui;

import android.content.Context;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

/**
 * Created by jan on 2/2/17.
 */

public class MapMarker {
    private OverlayItem marker;
    public MapMarker(Point point, MapView map, Context ctx) {
                /* Add a marker tied to currentPosition */
        ArrayList<OverlayItem> items = new ArrayList();
        marker = new OverlayItem("Title", "Description", point);
        items.add(marker); // Lat/Lon decimal degrees

        //the overlay which includes the marker
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, ctx);
        mOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(mOverlay);
    }
}
