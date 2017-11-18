///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import IRootScopeService = angular.IRootScopeService;
    declare var Offline;
    import ILogService = angular.ILogService;

    /**
     * Service for initial bootstrapping of Metadict.
     */
    export class BootstrapService {
        // @ngInject
        constructor(private $log: ILogService, private dictionaryService: DictionaryService,
                    private userService: UserService, private statusService: StatusService,
                    private $rootScope: IRootScopeService) {
            $log.debug("BootstrapService started");
        }

        /**
         * Start and configure all required services.
         */
        public bootstrapApplication() {
            this.dictionaryService.reloadDictionaries();
            this.setupOfflineHandler();
            this.checkLoginStatus();
            this.$log.info("Application started");
        }

        private setupOfflineHandler() {
            // TODO: Automatically rerun the latest query (if visible) after a connection has been reestablished
            Offline.options = {
                interceptRequests: true,
                requests: true,
                checks: {
                    xhr: {
                        url: "connection-test"
                    }
                }
            };
            Offline.on("down", () => {
                this.$log.info("Application is now offline");
                this.$rootScope.$broadcast(CoreEvents.CONNECTION_LOST);
                this.statusService.isConnected = false;
            });
            Offline.on("up", () => {
                this.$log.info("Application is now online");
                this.$rootScope.$broadcast(CoreEvents.CONNECTION_RECOVERED);
                this.statusService.isConnected = true;
            });
            Offline.check();
            this.statusService.isConnected = Offline.state === "up";
        }

        private checkLoginStatus() {
            this.$log.debug("Checking session state...");
            this.userService.checkLoginStatus();
        }
    }

    metadictModule.service("bootstrapService", BootstrapService);
}