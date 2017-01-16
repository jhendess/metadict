///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    declare var Offline;
    import ILogService = angular.ILogService;

    /**
     * Service for initial bootstrapping of Metadict.
     */
    export class BootstrapService {
        // @ngInject
        constructor(private $log: ILogService, private dictionaryService: DictionaryService, private userService: UserService) {
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
        }

        private checkLoginStatus() {
            this.$log.debug("Checking session state...");
            this.userService.checkLoginStatus();
        }
    }

    metadictModule.service("bootstrapService", BootstrapService);
}