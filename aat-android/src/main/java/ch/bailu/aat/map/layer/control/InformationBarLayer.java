package ch.bailu.aat.map.layer.control;

import android.view.View;

import ch.bailu.aat.R;
import ch.bailu.aat.dispatcher.DispatcherInterface;
import ch.bailu.aat.map.MapContext;
import ch.bailu.aat.menus.LocationMenu;
import ch.bailu.aat.menus.MapMenu;
import ch.bailu.aat.menus.MapQueryMenu;
import ch.bailu.aat.preferences.map.SolidLegend;
import ch.bailu.aat.preferences.map.SolidMapGrid;
import ch.bailu.aat.util.ui.AppTheme;
import ch.bailu.aat.util.ui.ToolTip;
import ch.bailu.aat.views.ImageButtonViewGroup;
import ch.bailu.aat.views.bar.ControlBar;
import ch.bailu.aat_lib.gpx.InfoID;
import ch.bailu.aat_lib.preferences.SolidIndexList;
import ch.bailu.aat_lib.preferences.StorageInterface;

public final class InformationBarLayer extends ControlBarLayer {

    private final ImageButtonViewGroup map, search, location;

    private final AbsNodeViewLayer selector;
    private final MapContext mcontext;


    public InformationBarLayer(MapContext cl, DispatcherInterface d) {
        super(cl,new ControlBar(cl.getContext(), getOrientation(RIGHT), AppTheme.bar), RIGHT);

        mcontext = cl;
        final SolidIndexList sgrid, slegend;


        sgrid = new SolidMapGrid(cl.getContext(), cl.getSolidKey());
        slegend = new SolidLegend(cl.getContext(), cl.getSolidKey());

        ControlBar bar = getBar();

        map = bar.addImageButton(R.drawable.open_menu);

        View grid=bar.addSolidIndexButton(sgrid);
        View legend=bar.addSolidIndexButton(slegend);

        search = bar.addImageButton(R.drawable.edit_find);
        location = bar.addImageButton(R.drawable.find_location);

        selector = new NodeViewLayer(cl);


        ToolTip.set(grid,R.string.tt_info_grid);
        ToolTip.set(legend,R.string.tt_info_legend);
        ToolTip.set(location, R.string.tt_info_location);

        d.addTarget(selector, InfoID.ALL);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);


        if (v == map) {
            new MapMenu(mcontext).showAsPopup(mcontext.getContext(),v);

        } else if (v==search) {
            new MapQueryMenu(mcontext).showAsPopup(mcontext.getContext(), v);

        } else if (v==location) {
            new LocationMenu(mcontext.getMapView()).showAsPopup(mcontext.getContext(), location);
        }



    }

    @Override
    public void onShowBar() {
        selector.showAtLeft();
    }


    @Override
    public void onHideBar() {
        selector.hide();
    }


    @Override
    public void onLayout(boolean c, int l, int t, int r, int b) {
        super.onLayout(c, l, t, r, b);
        selector.onLayout(c, l, t, r,b);
    }

    @Override
    public void drawForeground(MapContext mcontext) {
        if (isBarVisible()) selector.drawForeground(mcontext);
    }

    @Override
    public void drawInside(MapContext mcontext) {
        if (isBarVisible()) selector.drawInside(mcontext);
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onDetached() {

    }


    @Override
    public void onPreferencesChanged(StorageInterface s, String key) {

        selector.onPreferencesChanged(s, key);
    }
}
