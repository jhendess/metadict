/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    interface IMainScope extends ng.IScope {
        isLoading: boolean;
    }

    class MainController {

        // @ngInject
        constructor (private $scope: IMainScope, private $log : ILogService) {
            $log.debug("MainController started");
        }
    }

    metadictModule
        .controller("MainController", MainController);
}
