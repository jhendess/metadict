///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    class SearchService implements ISearchService {
        //@ngInject
        constructor(private $log: ILogService, private backendAccessService: IBackendAccessService, private dictionaryService: IDictionaryService) {
            $log.debug("SearchService started");
        }

        /**
         * @inheritDoc
         */
        runBilingualQuery(requestString: string, success: SuccessCallback<QueryResponse>, error: ErrorCallback) {

        }
    }

    metadictModule.service("searchService", SearchService)
}