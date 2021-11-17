package ch.bailu.aat_lib.service.directory;

import ch.bailu.aat_lib.util.WithStatusText;
import ch.bailu.aat_lib.util.sql.ResultSet;

public interface DirectoryServiceInterface extends WithStatusText {
    ResultSet query(String selection);

    void rescan();
}
