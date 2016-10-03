/// <reference path="../../App.ts" />
/// <reference path="../../core/user/UserService.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;
    import ILocationService = angular.ILocationService;

    declare var Materialize;

    export interface ISearchScope extends ISuccessErrorScope<QueryResponse> {

        searchRequest: string;

        queryResponse: QueryResponse;

        runSearch: Function;

        buildIconClass: Function;

        formatEntryType: Function;

        enabledDictionaries: BilingualDictionary[];
    }

    class SearchController {
        // @ngInject
        constructor(private $scope: ISearchScope, private $log: ILogService,
                    private searchService: SearchService, private $location: ILocationService,
                    private prettyFormattingService: PrettyFormattingService,
                    private dictionaryService: DictionaryService,
                    private statusService: StatusService) {

            this.prepareScope();
            this.registerEvents();

            // Load previously entered query string:
            $scope.searchRequest = $location.search()[Parameters.QUERY_STRING];

            // Initiate a search request if the query string parameter is set on page load
            if ($scope.searchRequest) {
                this.runSearch();
            }

            $log.debug("SearchController started");
        }

        public runSearch = () => {
            let requestString: string = this.$scope.searchRequest;
            let dictionaries: string = this.dictionaryService.getCurrentDictionaryString();

            if (!requestString || requestString.length <= 0) {
                Materialize.toast("No query entered", 4000);
                return;
            }
            if (!dictionaries || dictionaries.length <= 0) {
                Materialize.toast("No dictionaries selected", 4000);
                return;
            }

            this.searchService.runBilingualQuery(requestString, this.successCallback, this.errorCallback);
        };

        private animateClickQuery = () => {
            $("html, body").animate({scrollTop: "0"});
        };

        private successCallback: SuccessCallback<QueryResponse> = (data: QueryResponse) => {
            this.statusService.isSearching = false;
            this.$scope.queryResponse = data;
            // TODO
        };

        private errorCallback: ErrorCallback = (reason: any) => {
            this.statusService.isSearching = false;
            // TODO
        };

        private registerEvents() {
            // Update an hidden form field which is used by browser's for creating a custom search URL
            this.$scope.$on(CoreEvents.DICTIONARY_SELECTION_CHANGE, (event) => {
                this.$log.debug("Received " + event.name + " event");
                // This is not the angular way, but seems to be the only way of updating the hidden field for custom searches
                $("#dictionaryString").val(this.dictionaryService.getCurrentDictionaryString());
            });
            this.$scope.$on(CoreEvents.INVOKE_CLICK_QUERY, this.animateClickQuery);
        };

        private prepareScope() {
            this.$scope.runSearch = this.runSearch;
            this.$scope.buildIconClass = this.dictionaryService.buildIconClass;
            this.$scope.formatEntryType = this.prettyFormattingService.formatEntryType;
            this.$scope.success = this.successCallback;
            this.$scope.error = this.errorCallback;
            this.$scope.enabledDictionaries = this.dictionaryService.selectedBilingualDictionaries;
            this.$scope.$watch(
                () => this.searchService.lastQueryString,
                (newValue: string, oldValue: string, scope: ISearchScope) => scope.searchRequest = newValue
            );
            this.$scope.$on(CoreEvents.DICTIONARY_SELECTION_CHANGE,
                () => this.$scope.enabledDictionaries = this.dictionaryService.selectedBilingualDictionaries
            );
        };
    }

    metadictModule
        .controller("SearchController", SearchController);
}

