///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    /**
     * A class with static properties for different event names in the core module.
     */
    export class CoreEvents {

        /**
         * Event which will be fired, when the list of selected dictionaries has changed.
         * @type {string}
         */
        public static DICTIONARY_SELECTION_CHANGE = "DICTIONARY_SELECTION_CHANGE";

        /**
         * Event which will be fired, when a cache update has started.
         * @type {string}
         */
        public static UPDATING_CACHE = "UPDATING_CACHE";

        /**
         * Event which will be fired after an application update has succeeded and the application was restarted.
         * @type {string}
         */
        public static UPDATE_FINISHED = "UPDATE_FINISHED";

        /**
         * Event which will be fired if the user started a new query by clicking on a result entry.
         * @type {string}
         */
        public static INVOKE_CLICK_QUERY = "INVOKE_CLICK_QUERY";

        /**
         * The user sent to many requests to a limited backend resource.
         * @type {string}
         */
        public static TOO_MANY_REQUESTS = "TOO_MANY_REQUESTS";

        /**
         * The device was offline and has now again a connection to the backend.
         */
        public static CONNECTION_RECOVERED = "CONNECTION_RECOVERED";

        /**
         * The device was online and has lost the connection
         */
        public static CONNECTION_LOST = "CONNECTION_LOST";

        /**
         * Event which will be fired when the dictionary selection was changed by a click query.
         */
        public static DICTIONARY_SELECTION_CHANGE_BY_CLICK_QUERY = "DICTIONARY_SELECTION_CHANGE_BY_CLICK_QUERY";
    }
}