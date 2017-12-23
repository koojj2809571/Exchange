package com.opp.android.exchange.database;

/**
 * Created by OPP on 2017/8/31.
 */

public class CountryDBSchema {
    public static final class CountryTable{
        public static final String NAME = "countryInfo";

        public static final class Cols{
            public static final String COUNTRY_NAME = "name";
            public static final String CURRENCY = "currency";
            public static final String FLAG_PATH = "flagPath";
        }
    }
}
