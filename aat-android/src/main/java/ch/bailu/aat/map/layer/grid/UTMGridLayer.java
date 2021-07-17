package ch.bailu.aat.map.layer.grid;

import android.content.SharedPreferences;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;

import ch.bailu.aat.coordinates.MeterCoordinates;
import ch.bailu.aat.coordinates.UTMCoordinates;
import ch.bailu.aat.map.MapContext;
import ch.bailu.aat_lib.preferences.StorageInterface;

public final class UTMGridLayer extends MeterGridLayer{
    public UTMGridLayer(MapContext cl) {
        super(cl.getContext());
    }

    @Override
    public MeterCoordinates getCoordinates(LatLong p) {
        return new UTMCoordinates(p);
    }



    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public boolean onTap(Point tapXY) {
        return false;
    }


    @Override
    public void onPreferencesChanged(StorageInterface s, String key) {}

    @Override
    public void onAttached() {}

    @Override
    public void onDetached() {}
}
