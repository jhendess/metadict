/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IRootScopeService = angular.IRootScopeService;
    import IScope = angular.IScope;

    export interface IMainScope extends IScope {

        statusService: StatusService;

        /**
         * Function which prepands the current base path of the application in front of a URL.
         * @param target The target to which the base path should be prepended.
         */
        prependBasePath: (target: string) => string;

        /**
         * Flag to show if the application is currently connected to a backend.
         */
        isConnected: boolean;
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
            $rootScope.$on(CoreEvents.TOO_MANY_REQUESTS, () => {
                this.$log.warn("Too many requests sent to the backend");
                this.generalUiService.showSmallPopup("You sent too many requests. <br/>Please wait a moment and try again.", 4000);
            });
            $scope.$on(CoreEvents.CONNECTION_LOST, () => this.$scope.$apply(() => this.$scope.isConnected = false));
            $scope.$on(CoreEvents.CONNECTION_RECOVERED, () => this.$scope.$apply(() => this.$scope.isConnected = true));
            this.$scope.isConnected = statusService.isConnected;
        }

        public isMobileView() {
            return this.generalUiService.isMobileView();
        }

        private checkFinishedUpdate() {
            if (this.statusService.isUpdateFinished === true) {
                this.generalUiService.showSmallPopup(MainController.APPLICATION_UPDATE_MESSAGE, MainController.APPLICATION_UPDATE_MESSAGE_TIMEOUT);
                this.statusService.isUpdateFinished = false;
            }
        }
    }

    metadictModule
        .controller("MainController", MainController);
}
