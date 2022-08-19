package ch.bailu.aat_lib.map.tile;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.TileBitmap;

import java.io.IOException;

import javax.annotation.Nonnull;

import ch.bailu.aat_lib.util.Rect;
import ch.bailu.foc.Foc;

public interface MapTileInterface {
    boolean isLoaded();

    void set(TileBitmap tileBitmap);
    void set(Foc file, int defaultTileSize, boolean transparent);
    void setSVG(Foc file, int size, boolean transparent) throws IOException;

    void set(int defaultTileSize, boolean transparent);

    void free();

    TileBitmap getTileBitmap();

    long getSize();
    Canvas getCanvas();

    void setBuffer(@Nonnull int[] buffer, @Nonnull Rect interR);
}
