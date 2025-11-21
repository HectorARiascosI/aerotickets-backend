package com.aerotickets.constants;

/**
 * @deprecated Use {@link ApiPaths.Catalog} instead.
 * This class will be removed in a future version.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class CatalogConstants {

    private CatalogConstants() {
    }

    public static final String BASE_PATH = ApiPaths.Catalog.BASE;
    public static final String AIRPORTS_CO_PATH = ApiPaths.Catalog.AIRPORTS_CO;
    public static final String AIRLINES_CO_PATH = ApiPaths.Catalog.AIRLINES_CO;

    public static final String COUNTRY_COLOMBIA = "Colombia";

    public static final String FIELD_IATA = "iata";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_COUNTRY = "country";
    public static final String FIELD_CODE = "code";
}