package ch.bailu.aat.services.cache;


import ch.bailu.aat_lib.service.cache.Obj;
import ch.bailu.aat_lib.service.cache.ObjImageInterface;

public abstract class ObjImageAbstract extends Obj implements ObjImageInterface {
    public ObjImageAbstract(String id) {
        super(id);
    }
}
