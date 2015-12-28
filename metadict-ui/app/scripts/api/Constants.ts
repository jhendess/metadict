///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    /**
     * The {@link ResponseStatus} represents different status codes that can be returned for a request.
     */
    export class ResponseStatus {

        /**
         * The query executed successfully in the Metadict core.
         */
        public static OK = "OK";

        /**
         * The query failed for a reason that was not caused by the Metadict core.
         */
        public static ERROR = "ERROR";

        /**
         * An internal error occurred in the Metadict core.
         */
        public static INTERNAL_ERROR = "INTERNAL_ERROR";

        /**
         * A malformed query was received. This does not indicate an error in the Metadict core.
         */
        public static MALFORMED_QUERY = "MALFORMED_QUERY";
    }

    /**
     * Constants for URL parameters.
     */
    export class Parameters {

        public static QUERY_STRING = "queryString";

        public static DICTIONARIES = "dictionaries";

        public static SEPARATOR = ",";
    }

    /**
     * Predefined keys for local storage.
     */
    export class StorageKeys {

        public static LAST_SELECTED_DICTIONARIES = "lastSelectedDictionaries";
    }
}
