package testutils;

import java.security.Permission;

public class NoExitSecurityManager extends SecurityManager {
    /**
        * Allow everything.
        */
    @Override
    public void checkPermission(Permission perm) {}

    /**
        * Allow everything.
        */
    @Override
    public void checkPermission(Permission perm, Object context) {}

    /**
        * Block calls to System.exit().
        */
    @Override
    public void checkExit(int status)
    {
        super.checkExit(status);
        throw new RuntimeException("Prevented call to System.exit()!");
    }
}
