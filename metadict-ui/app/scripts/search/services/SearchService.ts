///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    class SearchService implements ISearchService {
        // @ngInject
        constructor(private $log: ILogService, private backendAccessService: IBackendAccessService,
                    private dictionaryService: IDictionaryService) {
            $log.debug("SearchService started");
        }

        /**
         * @inheritDoc
         */
        public runBilingualQuery(dictionaries: string, requestString: string,
                                 success: SuccessCallback<QueryResponse>,
                                 error: ErrorCallback) {
            this.backendAccessService.executeBilingualQuery(dictionaries, requestString, success, error);
        }
    }

    metadictModule.service("searchService", SearchService);
}