///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    class BootstrapService implements IBootstrapService {
        // @ngInject
        constructor(private $log: ILogService, private dictionaryService: IDictionaryService) {
            $log.debug("BootstrapService started");
        }

        public bootstrapApplication() {
            this.dictionaryService.reloadDictionaries();
            this.$log.info("Application started");
        }
    }

    metadictModule.service("bootstrapService", BootstrapService);
}