///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    let StatusService: IStatusService = {

        errorMessage: undefined,

        isConnected: false,

        isError: false,

        clientVersion: Config.CLIENT_VERSION,

        clientRevision: Config.CLIENT_REVISION,

        serverVersion: "..."
    };

    metadictModule.value("statusService", StatusService);
}