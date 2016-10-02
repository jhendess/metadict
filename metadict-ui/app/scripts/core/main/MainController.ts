/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IRootScopeService = angular.IRootScopeService;
    import IScope = angular.IScope;

    declare var Materialize;


    export interface IMainScope extends IScope {

        statusService: StatusService;
    }

    /**
     * Main controller for the application. Used for handling application-wide events and actions.
     */
    class MainController {

        /**
         * Message after successful update.
         * @type {string}
         */
        private static APPLICATION_UPDATE_MESSAGE = "Application has been updated to latest version.";

        /**
         * Timeout for the successful update message.
         * @type {number}
         */
        private static APPLICATION_UPDATE_MESSAGE_TIMEOUT = 4000;

        // @ngInject
        constructor(private $scope: IMainScope, private $log: ILogService, private statusService: StatusService,
                    private $rootScope: IRootScopeService) {
            this.checkFinishedUpdate();
            $scope.statusService = statusService;
            $log.debug("MainController started");
        }

        private checkFinishedUpdate() {
            if (this.statusService.isUpdateFinished === true) {
                Materialize.toast(MainController.APPLICATION_UPDATE_MESSAGE, MainController.APPLICATION_UPDATE_MESSAGE_TIMEOUT);
                this.statusService.isUpdateFinished = false;
            }
        }
    }

    metadictModule
        .controller("MainController", MainController);
}
