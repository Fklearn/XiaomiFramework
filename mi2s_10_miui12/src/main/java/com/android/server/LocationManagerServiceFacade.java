package com.android.server;

public class LocationManagerServiceFacade {
    static boolean checkLocationAccess(LocationManagerService sLocationManagerService, int pid, int uid, String packageName, int allowedResolutionLevel) {
        if (sLocationManagerService != null) {
            return sLocationManagerService.checkLocationAccess(pid, uid, packageName, allowedResolutionLevel);
        }
        return false;
    }
}
