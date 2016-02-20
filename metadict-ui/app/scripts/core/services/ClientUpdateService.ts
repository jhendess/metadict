///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IRootScopeService = angular.IRootScopeService;
    import ILocalStorageService = angular.local.storage.ILocalStorageService;

    let webappCache = window.applicationCache;

    /**
     * Service which checks for client updates and restarts the application if an update has been downloaded to the
     * browser cache.
     */
    export class ClientUpdateService {

        /**
         * Local storage key to indicate, that a update process was started and is finished upon the next application
         * reload.
         * @type {string}
         */
        private static UPDATE_STARTED_KEY = "metadictUpdateStarted";

        // @ngInject
        constructor(private $log: ILogService, private $rootScope: IRootScopeService,
                    private statusService: IStatusService, private localStorageService: ILocalStorageService) {
            if (localStorageService.get(ClientUpdateService.UPDATE_STARTED_KEY)) {
                this.localStorageService.remove(ClientUpdateService.UPDATE_STARTED_KEY);
                $log.debug("Client update finished");
                statusService.isUpdateFinished = true;
            }
            $log.debug("ClientUpdateService started");
        }

        private updateReady = () => {
            this.$log.info("Update ready");
            this.localStorageService.set(ClientUpdateService.UPDATE_STARTED_KEY, "true");
            webappCache.swapCache();
            location.reload(true);
        };

        private downloadingCache = (event: any) => {
            this.$rootScope.$broadcast(CoreEvents.UPDATING_CACHE);
            this.statusService.isUpdating = true;
            this.$log.debug("Downloading update...");
        };

        public registerEventHandlers() {
            webappCache.onupdateready = this.updateReady;
            webappCache.ondownloading = this.downloadingCache;
            webappCache.onprogress = (e) => {
                let percentage = e.loaded / e.total * 100;
                this.statusService.updateProgress = percentage;
            };
        }
    }

    metadictModule.service("clientUpdateService", ClientUpdateService);
}