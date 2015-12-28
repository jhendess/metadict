/// <reference path="../../App.ts" />
/// <reference path="../../core/services/UserService.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;
    import ILocationService = angular.ILocationService;

    interface ISearchScope extends IScope {

        searchRequest: string;

        dictionaryString: string

        isSearching: boolean;

        queryResponse: QueryResponse;

        runSearch: Function;

        buildIconClass: Function;
    }

    class SearchController {
        // @ngInject
        constructor(private $scope: ISearchScope, private $log: ILogService,
                    private dictionaryService: IDictionaryService, private backendAccessService: IBackendAccessService,
                    private $location: ILocationService) {

            $scope.runSearch = this.runSearch;
            $scope.dictionaryString = dictionaryService.getCurrentDictionaryString();
            $scope.buildIconClass = dictionaryService.buildIconClass;

            $scope.$on(CoreEvents.DICTIONARY_SELECTION_CHANGE, (event) => {
                $log.debug("Received " + event.name + " event");
                $scope.dictionaryString = dictionaryService.getCurrentDictionaryString();
                // This is not the angular way, but seems to be the only way of updating the hidden field for custom
                // searches
                $("#dictionaryString").val($scope.dictionaryString);
            });

            // Load previously entered query string:
            $scope.searchRequest = $location.search()[Parameters.QUERY_STRING];

            $log.debug("SearchController started");
        }

        public runSearch = () => {
            let requestString = this.$scope.searchRequest;
            let dictionaries = this.$scope.dictionaryString;
            this.$log.debug(`Invoking search for request string '${requestString}' in dictionaries ${dictionaries} ...`);


            this.$location.search(Parameters.QUERY_STRING, requestString);
            this.$scope.isSearching = true;
            this.backendAccessService.executeBilingualQuery(dictionaries, requestString, this.successCallback, this.errorCallback);

        };

        private successCallback: SuccessCallback<QueryResponse> = (data: QueryResponse) => {
            this.$scope.isSearching = false;
            this.$scope.queryResponse = data;
            // TODO
        };

        private errorCallback: ErrorCallback = (reason: any) => {
            this.$scope.isSearching = false;
            // TODO
        }
    }

    metadictModule
        .controller("SearchController", SearchController);
}

