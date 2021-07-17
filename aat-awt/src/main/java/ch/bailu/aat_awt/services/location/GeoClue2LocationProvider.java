package ch.bailu.aat_awt.services.location;

import org.freedesktop.dbus.exceptions.DBusException;

import ch.bailu.aat_awt.services.location.interfaces.Client;
import ch.bailu.aat_lib.gpx.StateID;
import ch.bailu.aat_lib.logger.AppLog;
import ch.bailu.aat_lib.service.location.LocationInformation;
import ch.bailu.aat_lib.service.location.LocationService;
import ch.bailu.aat_lib.service.location.LocationStackChainedItem;
import ch.bailu.aat_lib.service.location.LocationStackItem;

/**
 * GeoClue2 DBus interface:
 * https://www.freedesktop.org/software/geoclue/docs/
 *
 * Java-DBus library:
 * https://github.com/hypfvieh/dbus-java
 *
 * Nice GUI DBus debugger:
 * https://wiki.gnome.org/Apps/DFeet/
 *
 * How to create GeoClue2 java interfaces:
 * 1. Install 'geoclue-2.0' package
 * 2. Get introspection files (xml-format):
 *    'ls /usr/share/dbus-1/interfaces/* | grep GeoClue2'
 * 3. Clone 'https://github.com/hypfvieh/dbus-java' and read 'docs/code-generation.html'
 * 4. Generate java classes from introspection files according to documentation
 * 5. Manually adjust java files
 *
 */
public class GeoClue2LocationProvider extends LocationStackChainedItem {


    private final GeoClue2Dbus geoClue2;


    public GeoClue2LocationProvider(LocationStackItem i) throws DBusException {
        super(i);

        geoClue2 = new GeoClue2Dbus();

        try {
            geoClue2.connect(signal -> updateStateAndLocation(signal));
            geoClue2.start();
            updateStateAndLocation();

        } catch (DBusException e) {
            AppLog.e(this, e);
        }
    }

    private void updateStateAndLocation(Client.LocationUpdated signal) {
        try {
            AppLog.d(this, "pass location");
            LocationInformation location = geoClue2.getLocation(signal.getNew());
            passState(StateID.ON);
            passLocation(location);
        } catch (Exception e) {
            AppLog.e(this, e);
            passState(StateID.NOSERVICE);
        }
    }

    private void updateStateAndLocation() {

        if (geoClue2.getActive()) {
            try {
                LocationInformation location = geoClue2.getLocation();
                passState(StateID.ON);
                passLocation(location);
            } catch (Exception e) {
                AppLog.e(this, e);
                passState(StateID.NOSERVICE);
            }
        } else {
            passState(LocationService.INITIAL_STATE);
        }
    }

    @Override
    public void close() {
        geoClue2.stop();
    }

}
