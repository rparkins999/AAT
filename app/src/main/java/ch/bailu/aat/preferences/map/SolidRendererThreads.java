package ch.bailu.aat.preferences.map;

import android.content.Context;
import android.os.Build;

import org.mapsforge.core.util.Parameters;

import ch.bailu.aat.R;
import ch.bailu.aat.preferences.SolidIndexList;
import ch.bailu.aat.util.ToDo;

public class SolidRendererThreads extends SolidIndexList {

    private final static String KEY = "renderer_threads";
    private final static int[] values = {0,2,3,4,1};

    public SolidRendererThreads(Context c) {
        super(c, KEY);
        if (values[0] == 0) values[0] = numberOfBackgroundThreats();
    }


    public static int numberOfBackgroundThreats() {
        return Math.max(1, numberOfCores()-1);
    }


    public static int numberOfCores() {
        if(Build.VERSION.SDK_INT >= 17) {
            return Math.max(Runtime.getRuntime().availableProcessors(), 1);
        }
        return 1;
    }

    @Override
    public int length() {
        return values.length;
    }

    @Override
    protected String getValueAsString(int i) {
        return toDefaultString(String.valueOf(values[i]), i);
    }

    @Override
    public String getLabel() {
        return getString(R.string.p_render_threads);
    }


    public int getValue() {
        return values[getIndex()];
    }

    public void set() {
        Parameters.NUMBER_OF_THREADS = getValue();
    }
}
