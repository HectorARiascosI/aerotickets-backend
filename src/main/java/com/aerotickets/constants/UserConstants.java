package com.aerotickets.constants;

/**
 * @deprecated Use {@link ApiPaths.Users} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class UserConstants {

    private UserConstants() {
    }

    public static final String BASE_PATH = ApiPaths.Users.BASE;
    public static final String REGISTER_PATH = ApiPaths.Users.REGISTER;
}