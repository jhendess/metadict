///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    /**
     * Service for initial bootstrapping of Metadict.
     */
    export class BootstrapService {
        // @ngInject
        constructor(private $log: ILogService, private dictionaryService: DictionaryService) {
            $log.debug("BootstrapService started");
        }

        /**
         * Start and configure all required services.
         */
        public bootstrapApplication() {
            this.dictionaryService.reloadDictionaries();
            this.$log.info("Application started");
        }
    }

    metadictModule.service("bootstrapService", BootstrapService);
}