///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import ILocationService = angular.ILocationService;
    import IRootScopeService = angular.IRootScopeService;


    /**
     * Service for searching through dictionaries.
     */
    export class SearchService {
        // @ngInject
        constructor(private $log: ILogService, private $rootScope: IRootScopeService, private backendAccessService: BackendAccessService,
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
            this.statusService.isSearching = true;
            this.lastQueryString = requestString;
            this.backendAccessService.executeBilingualQuery(dictionaries, requestString, success, error);
        }

        /**
         * Trigger a search by changing the requestString and dictionaries parameter to the requested search value. This
         * workaround is necessary to support correct behaviour of the HTML5 history API.
         * @param requestString The string that will be searched for.
         * @param {string} dictionaries The dictionaries to use in that query.
         */
        public triggerSearch(requestString: string, dictionaries: string) {
            let oldRequest = this.$location.search()[Parameters.QUERY_STRING];
            if (oldRequest === requestString) {
                this.$rootScope.$broadcast(SearchEvents.FORCE_SEARCH);
            } else {
                let request : Map<string> = {};
                request[Parameters.QUERY_STRING] = requestString;
                request[Parameters.DICTIONARIES] = dictionaries ? dictionaries :   this.dictionaryService.getCurrentDictionaryString();
                this.$location.path(SEARCH_PAGE).search(request);
            }
        }
    }

    metadictModule.service("searchService", SearchService);
}