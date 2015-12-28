///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    let StatusService:IStatusService = {

        errorMessage: undefined,

        isConnected: false,

        isError: false
    };

    metadictModule.value("statusService", StatusService);
}