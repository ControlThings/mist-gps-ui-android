package fi.ct.mist.gpsui;

import android.content.Context;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jan on 3/25/17.
 */

public class MapMarkerOverlay {
    private MapView map;
    private Context ctx;
    private ItemizedOverlayWithFocus<OverlayItem> mOverlay;
    private ArrayList<OverlayItem> items = new ArrayList<>();

    public MapMarkerOverlay(MapView map, Context ctx) {
        this.map = map;
        this.ctx = ctx;
    }

    int cnt = 0;
    public void addMarker(Point point) {
        items.add(new OverlayItem(point.getAlias(), "Point: "+ ++cnt, point));

        map.getOverlays().removeAll(map.getOverlays());

        //a new overlay which includes the markers created so far
        mOverlay = new ItemizedOverlayWithFocus(items,
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

    public void clear() {
        items.clear();
        cnt = 0;
    }
}
