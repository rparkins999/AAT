package ch.bailu.aat_awt.window;


import org.mapsforge.core.model.LatLong;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

import ch.bailu.aat_awt.app.App;
import ch.bailu.aat_awt.map.AwtCustomMapView;
import ch.bailu.aat_awt.preferences.AwtStorage;
import ch.bailu.aat_awt.views.JCockpitPanel;
import ch.bailu.aat_awt.views.JNumberView;
import ch.bailu.aat_lib.description.AltitudeDescription;
import ch.bailu.aat_lib.description.FF;
import ch.bailu.aat_lib.description.GpsStateDescription;
import ch.bailu.aat_lib.description.LatitudeDescription;
import ch.bailu.aat_lib.description.LongitudeDescription;
import ch.bailu.aat_lib.description.TrackerStateDescription;
import ch.bailu.aat_lib.dispatcher.AppBroadcaster;
import ch.bailu.aat_lib.dispatcher.Broadcaster;
import ch.bailu.aat_lib.dispatcher.CurrentLocationSource;
import ch.bailu.aat_lib.dispatcher.Dispatcher;
import ch.bailu.aat_lib.dispatcher.OnContentUpdatedInterface;
import ch.bailu.aat_lib.dispatcher.TrackerSource;
import ch.bailu.aat_lib.gpx.GpxInformation;
import ch.bailu.aat_lib.gpx.InfoID;
import ch.bailu.aat_lib.preferences.location.CurrentLocationLayer;
import ch.bailu.aat_lib.service.ServicesInterface;

public class AwtMainWindow implements OnContentUpdatedInterface {
    private static final String MESSAGE = "Exit the application?";
    private static final String TITLE = "Confirm close";

    final JFrame frame;
    final AwtCustomMapView map;

    private final JLabel
            locationStatus = new JLabel(),
            trackerStatus = new JLabel(),
            errorStatus = new JLabel(),
            infoStatus = new JLabel();

    private final JCockpitPanel cockpit = new JCockpitPanel();
    private final JPanel
            preferences = new JPanel(),
            buttonPane = new JPanel(),
            statusPane = new JPanel();

    private final JButton
            showMap = new JButton("Map"),
            plus = new JButton("+"),
            minus = new JButton("-");

    private final JNumberView
            gpsButton = new JNumberView(new GpsStateDescription()),
            trackerButton = new JNumberView(new TrackerStateDescription());

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final JToggleButton center = new JToggleButton("Center");

    private LatLong currentPos = null;

    private final Dispatcher dispatcher = new Dispatcher();


    public AwtMainWindow(List<File> mapFiles, ServicesInterface services, Broadcaster broadcaster) {
        map = new AwtCustomMapView(mapFiles);

        center.addActionListener(itemEvent -> doCenter());
        showMap.addActionListener(itemEvent -> doShowMap());
        plus.addActionListener(itemEvent -> doZoomIn());
        minus.addActionListener(itemEvent -> doZoomOut());

        frame = new JFrame("AAT - AWT (swing) edition");
        frame.setName("test");
        frame.getContentPane().setLayout(new BorderLayout());

        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(center);
        buttonPane.add(showMap);
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(plus);
        buttonPane.add(minus);
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(gpsButton);
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(trackerButton);


        Container pane = frame.getContentPane();
        pane.add(buttonPane, BorderLayout.PAGE_START);
        tabbedPane.addTab("Map",map);
        tabbedPane.addTab("Cockpit",cockpit);
        tabbedPane.addTab("Preferences",preferences);

        pane.add(tabbedPane, BorderLayout.CENTER);

        statusPane.setLayout(new BoxLayout(statusPane, BoxLayout.PAGE_AXIS));
        statusPane.add(locationStatus);
        statusPane.add(trackerStatus);
        statusPane.add(errorStatus);
        statusPane.add(infoStatus);
        pane.add(statusPane, BorderLayout.PAGE_END);

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {

            }
        });


        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                doQuit();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                map.loadPreferences();
            }
        });


        try {
            Image icon = ImageIO.read(getClass().getResourceAsStream("/images/icon.png"));
            frame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // FIXME: frame does not receive resize event from initial window placement (on Mobian posh)
        frame.setPreferredSize(new Dimension(370, 675));
        frame.pack();
        frame.setVisible(true);

        broadcaster.register(objects -> {
            String tag = (String) objects[0];
            String msg = (String) objects[1];
            errorStatus.setText(tag + ": " + msg);
        }, AppBroadcaster.LOG_ERROR);

        broadcaster.register(objects -> {
            String tag = (String) objects[0];
            String msg = (String) objects[1];
            infoStatus.setText(tag + ": " + msg);
        }, AppBroadcaster.LOG_INFO);

        dispatcher.addSource(new CurrentLocationSource(services, broadcaster));
        dispatcher.addSource(new TrackerSource(services, broadcaster));

        map.add(new CurrentLocationLayer(map.getMContext(), new Dispatcher()));
    }

    private void doZoomOut() {
        byte zoom = (byte) (map.getModel().mapViewPosition.getZoomLevel() - 1);
        map.setZoomLevel(zoom);
    }


    private void doZoomIn() {
        byte zoom = (byte) (map.getModel().mapViewPosition.getZoomLevel() + 1);
        map.setZoomLevel(zoom);
    }

    @Override
    public void onContentUpdated(int iid, GpxInformation info) {
        LatitudeDescription la = new LatitudeDescription();
        LongitudeDescription lo = new LongitudeDescription();
        AltitudeDescription altitude = new AltitudeDescription(new AwtStorage());
        GpsStateDescription gps = new GpsStateDescription();
        TrackerStateDescription tracker = new TrackerStateDescription();

        la.onContentUpdated(iid, info);
        lo.onContentUpdated(iid, info);
        altitude.onContentUpdated(iid, info);

        String time = FF.f().LOCAL_TIME.format(info.getTimeStamp());

        cockpit.onContentUpdated(iid, info);
        if (iid == InfoID.LOCATION) {
            gps.onContentUpdated(iid, info);
            gpsButton.onContentUpdated(iid, info);

            locationStatus.setText(time + " " + gps.getLabel() + " " + gps.getValue() + " " + la.getValue() + ", " + lo.getValue() +", " + altitude.getValue());
            currentPos = new LatLong(info.getLatitude(), info.getLongitude());
            doCenter();

        } else if (iid == InfoID.TRACKER) {
            tracker.onContentUpdated(iid, info);
            trackerButton.onContentUpdated(iid, info);
            trackerStatus.setText(time + ": "+ tracker.getLabel() + " " + tracker.getValue() + " " + la.getValue() + ", " + lo.getValue() +", " + altitude.getValue());
        }
    }


    private void doQuit() {
        /*int result = JOptionPane.showConfirmDialog(frame, MESSAGE, TITLE, JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {*/
            map.savePreferences();
            frame.dispose();
            App.exit(0);
//        }
    }


    private void doCenter() {
        if (currentPos != null && center.isSelected()) {
            map.setCenter(currentPos);
        }
    }

    private void doShowMap() {
        map.showMap();
    }
}