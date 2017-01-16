///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    /**
     * The {@link ResponseStatus} represents different status codes that can be returned for a request.
     */
    export enum ResponseStatus {

        /**
         * The query executed successfully in the Metadict core.
         */
        OK,

            /**
             * The query failed for a reason that was not caused by the Metadict core.
             */
        ERROR,

            /**
             * An internal error occurred in the Metadict core.
             */
        INTERNAL_ERROR,

            /**
             * A malformed query was received. This does not indicate an error in the Metadict core.
             */
        MALFORMED_QUERY,

            /**
             * A resource with the same id already exists.
             */
        DUPLICATE,

            /**
             * User not authorized.
             */
        UNAUTHORIZED
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
