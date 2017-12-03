/// <reference path="../../App.ts" />
/// <reference path="../../core/user/UserService.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;
    import ILocationService = angular.ILocationService;

    export interface ISearchScope extends IScope {

        searchRequest: string;

        queryResponse: QueryResponse;

        prepareSearch: Function;

        buildIconClass: Function;

        formatEntryType: Function;

        enabledDictionaries: BilingualDictionary[];
    }

    class SearchController {
        // @ngInject
        constructor(private $scope: ISearchScope, private $log: ILogService,
                    private searchService: SearchService, private $location: ILocationService,
                    private prettyFormattingService: PrettyFormattingService,
                    private dictionaryService: DictionaryService, private generalUiService: GeneralUiService,
                    private statusService: StatusService) {

            this.prepareScope();
            this.registerEvents();

            // New searches are started when the queryString in the URL parameter changes
            $scope.$watch(() => ($location.search()[Parameters.QUERY_STRING]), () => {
                this.$log.debug("Search parameter changed");
                this.internalRunSearch();
            });

            $log.debug("SearchController started");
        }

        public prepareSearch = (queryString: string) => {
            let requestString: string = queryString ? queryString : this.$scope.searchRequest;
            let dictionaries: string = this.dictionaryService.getCurrentDictionaryString();

            if (!requestString || requestString.length <= 0) {
                this.generalUiService.showSmallPopup("No query entered", 4000);
                return;
            }
            if (!dictionaries || dictionaries.length <= 0) {
                this.generalUiService.showSmallPopup("No dictionaries selected", 4000);
                return;
            }

            this.searchService.triggerSearch(requestString, null);
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
                // This is not the angular way, but seems to be the only way of updating the hidden field for custom
                // searches
                $("#dictionaryString").val(this.dictionaryService.getCurrentDictionaryString());
            });
            this.$scope.$on(CoreEvents.INVOKE_CLICK_QUERY, this.animateClickQuery);
        };

        /**
         * Never call this method directly! Either use {@link SearchController#prepareSearch} or {@link
            * SearchService#triggerSearch}.
         */
        private internalRunSearch = () => {
            // Update the scope variable (in case the request was triggered externally)
            this.$scope.searchRequest = this.$location.search()[Parameters.QUERY_STRING];

            // Initialize the dictionaries manually in case they aren't loaded yet and ignore unknown dictionaries
            if (!this.dictionaryService.isDictionaryListLoaded()) {
                this.dictionaryService.initializeDictionaryConfiguration(false);
            }

            if (this.$scope.searchRequest) {
                this.searchService.runBilingualQuery(this.$scope.searchRequest, this.successCallback, this.errorCallback);
            }
        };

        private prepareScope() {
            this.$scope.prepareSearch = this.prepareSearch;
            this.$scope.buildIconClass = this.dictionaryService.buildIconClass;
            this.$scope.formatEntryType = this.prettyFormattingService.formatEntryType;
            this.$scope.enabledDictionaries = this.dictionaryService.selectedBilingualDictionaries;
            this.$scope.$watch(
                () => this.searchService.lastQueryString,
                (newValue: string, oldValue: string, scope: ISearchScope) => scope.searchRequest = newValue
            );
            this.$scope.$on(CoreEvents.DICTIONARY_SELECTION_CHANGE,
                () => this.$scope.enabledDictionaries = this.dictionaryService.selectedBilingualDictionaries
            );
            this.$scope.$on(SearchEvents.FORCE_SEARCH, this.internalRunSearch);
        };
    }

    metadictModule
        .controller("SearchController", SearchController);
}

