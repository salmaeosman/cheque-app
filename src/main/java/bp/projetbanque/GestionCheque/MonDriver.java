package bp.projetbanque.GestionCheque;

import bp.projetbanque.GestionCheque.NativeLoader;

public class MonDriver {
    static {
        NativeLoader.loadLibrary("monDriver");
    }

    public native void hello();
}
