///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import ILocationService = angular.ILocationService;


    /**
     * Service for searching through dictionaries.
     */
    export class SearchService {
        // @ngInject
        constructor(private $log: ILogService, private backendAccessService: BackendAccessService,
                    private dictionaryService: DictionaryService, private $location: ILocationService,
                    private statusService: StatusService) {
            $log.debug("SearchService started");
        }

        private _lastQueryString: string;

        get lastQueryString(): string {
            return this._lastQueryString;
        }

        set lastQueryString(value: string) {
            this._lastQueryString = value;
        }

        /**
         * Run a search query for bilingual dictionaries against the currently connected metadict instance. This method
         * will use the currently selected dictionaries from {@link DictionaryService} for querying.
         *
         * @param requestString The search request for which metadict shall search.
         * @param success The success callback which should be called upon successful retrieval.
         * @param error The error callback which should be called upon a failed request.
         */
        public runBilingualQuery(requestString: string,
                                 success: SuccessCallback<QueryResponse>,
                                 error: ErrorCallback) {
            let dictionaries = this.dictionaryService.getCurrentDictionaryString();
            this.$log.debug(`Invoking search for request string '${requestString}' in dictionaries ${dictionaries} ...`);
            this.$location.search(Parameters.QUERY_STRING, requestString);
            this.statusService.isSearching = true;
            this.lastQueryString = requestString;
            this.backendAccessService.executeBilingualQuery(dictionaries, requestString, success, error);
        }
    }

    metadictModule.service("searchService", SearchService);
}