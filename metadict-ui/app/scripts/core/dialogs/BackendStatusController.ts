/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    /**
     * Scope for backend information.
     */
    interface IBackendStatusScope {
        isLoading: boolean;

        isError: boolean;

        backendStatus: SystemStatus;
    }

    class BackendStatusController {
        // @ngInject
        constructor(private $log: ILogService, private $scope: IBackendStatusScope,
                    private backendAccessService: BackendAccessService) {
            $scope.isLoading = true;
            this.reloadBackendStatus();

            $log.debug("BackendStatusController started");
        }

        private reloadBackendStatus() {
            this.$scope.isLoading = true;
            this.$scope.isError = false;
            this.backendAccessService.querySystemStatus(this.successHandler, this.errorHandler);
        }

        private successHandler: SuccessCallback<SystemStatus> = (systemStatus) => {
            this.$scope.backendStatus = systemStatus;
            this.$scope.isLoading = false;
        };

        private errorHandler: ErrorCallback = () => {
            this.$scope.isError = true;
            this.$scope.isLoading = false;
        };
    }

    metadictModule.controller("BackendStatusController", BackendStatusController);
}