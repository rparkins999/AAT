package ch.bailu.aat.preferences;


public abstract class SolidCheckList extends AbsSolidType {

    public abstract CharSequence[] getStringArray();
    public abstract boolean[] getEnabledArray();
    public abstract void setEnabled(int i, boolean isChecked);




    @Override
    public void setValueFromString(String s) {}

    @Override
    public String getValueAsString() {
        return null;
    }

}

