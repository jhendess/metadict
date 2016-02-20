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
    }
}