package ch.bailu.aat_lib.util.fs;

import ch.bailu.aat_lib.logger.AppLog;
import ch.bailu.aat_lib.resources.Res;
import ch.bailu.foc.Foc;

public class AFile {
    public static void logErrorExists(Foc f) {
        AppLog.e( f.getPathName() + Res.str().file_exists());
    }


    public static void logErrorReadOnly(Foc f) {
        AppLog.e(f.getPathName() + " is read only.*");
    }

    public static void logErrorNoAccess(Foc f) {
        AppLog.e(f.getPathName() + " no access.*");
    }


    public static void logInfoAcess(Foc f) {
        String msg = ": no acess.*";
        if (f.canWrite()) {
            msg = " is writeable.*";
        } else if (f.canRead()) {
            msg = " is read only.*";
        }

        AppLog.i(f.getPathName() + msg);
    }


}
