/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IRootScopeService = angular.IRootScopeService;
    import IScope = angular.IScope;

    declare var Materialize;


    export interface IMainScope extends IScope {

        statusService: StatusService;

        /**
         * Function which prepands the current base path of the application in front of a URL.
         * @param target The target to which the base path should be prepended.
         */
        prependBasePath: (target: string) => string;
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
                    private $rootScope: IRootScopeService, private navigationMenuService: NavigationMenuService,
                    private generalUiService: GeneralUiService) {
            this.checkFinishedUpdate();
            $scope.statusService = statusService;
            $scope.prependBasePath = this.navigationMenuService.prependBasePath;
            $log.debug("MainController started");
        }

        public isMobileView() {
            return this.generalUiService.isMobileView();
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
