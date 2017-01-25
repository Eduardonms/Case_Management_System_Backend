package se.teknikhogskolan.springcasemanagement.security;

import java.security.Permission;

public final class OurSecurityManager extends SecurityManager {

    @Override
    public void checkPermission(Permission perm) {
        if ("suppressAccessChecks".equals(perm.getName())) {
            throw new SecurityException("Not allowed.");
        }
        super.checkPermission(perm);
    }
}