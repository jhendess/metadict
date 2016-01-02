/// <reference path="../../App.ts" />
/// <reference path="../../core/services/UserService.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;
    import ILocationService = angular.ILocationService;

    declare var Materialize;

    interface ISearchScope extends IScope {

        searchRequest: string;

        dictionaryString: string;

        isSearching: boolean;

        queryResponse: QueryResponse;

        runSearch: Function;

        buildIconClass: Function;

        formatEntryType: Function;
    }

    class SearchController {
        // @ngInject
        constructor(private $scope: ISearchScope, private $log: ILogService,
                    private dictionaryService: IDictionaryService, private searchService: ISearchService,
                    private $location: ILocationService, private prettyFormattingService: IPrettyFormattingService) {

            $scope.runSearch = this.runSearch;
            $scope.dictionaryString = dictionaryService.getCurrentDictionaryString();
            $scope.buildIconClass = dictionaryService.buildIconClass;
            $scope.formatEntryType = prettyFormattingService.formatEntryType;

            $scope.$on(CoreEvents.DICTIONARY_SELECTION_CHANGE, (event) => {
                $log.debug("Received " + event.name + " event");
                $scope.dictionaryString = dictionaryService.getCurrentDictionaryString();
                // This is not the angular way, but seems to be the only way of updating the hidden field for custom
                // searches
                $("#dictionaryString").val($scope.dictionaryString);
            });

            // Load previously entered query string:
            $scope.searchRequest = $location.search()[Parameters.QUERY_STRING];

            // Initiate a search request if the query string parameter is set on page load
            if ($scope.searchRequest) {
                this.runSearch();
            }

            $log.debug("SearchController started");
        }

        public runSearch = () => {
            let requestString = this.$scope.searchRequest;
            let dictionaries = this.$scope.dictionaryString;

            if (!requestString || requestString.length <= 0) {
                Materialize.toast("No query entered", 4000);
                return;
            }
            if (!dictionaries || dictionaries.length <= 0) {
                Materialize.toast("No dictionaries selected", 4000);
                return;
            }

            this.$log.debug(`Invoking search for request string '${requestString}' in dictionaries ${dictionaries} ...`);

            this.$location.search(Parameters.QUERY_STRING, requestString);
            this.$scope.isSearching = true;
            this.searchService.runBilingualQuery(dictionaries, requestString, this.successCallback, this.errorCallback);
        };

        private successCallback: SuccessCallback<QueryResponse> = (data: QueryResponse) => {
            this.$scope.isSearching = false;
            this.$scope.queryResponse = data;
            // TODO
        };

        private errorCallback: ErrorCallback = (reason: any) => {
            this.$scope.isSearching = false;
            // TODO
        };
    }

    metadictModule
        .controller("SearchController", SearchController);
}

