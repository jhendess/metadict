///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    /**
     * Central service for exchanging statuses between components.
     */
    export interface StatusService {

        /** Indicator if the app has encountered a global error */
        isError: boolean;

        /** Indicator if the app is connected to a metadict instance */
        isConnected: boolean;

        /** Error message for the user */
        errorMessage: string;

        /** The current git revision of the executing client */
        clientRevision: string;

        /** The current version of the executing client */
        clientVersion: string;

        /** The version of the currently connected server */
        serverVersion: string;

        /** Indicator if the browser cache is currently reloading the application. */
        isUpdating: boolean;

        /** Indicator if a previous update has been finished. */
        isUpdateFinished: boolean;

        /** Indicator if there is currently a search request being processed. */
        isSearching: boolean;

        /** Progress of reloading in percentage. */
        updateProgress: number;
    }


    let StatusService: StatusService = {

        errorMessage: undefined,

        isConnected: false,

        isError: false,

        clientVersion: Config.CLIENT_VERSION,

        clientRevision: Config.CLIENT_REVISION,

        serverVersion: "...",

        isUpdating: false,

        isUpdateFinished: false,

        isSearching: false,

        updateProgress: 0
    };

    metadictModule.value("statusService", StatusService);
}