/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    class MainController {

        // @ngInject
        constructor (private $scope: IMainScope, private $log : ILogService, private statusService: IStatusService) {
            $scope.statusService = statusService;
            $log.debug("MainController started");
        }
    }

    metadictModule
        .controller("MainController", MainController);
}
