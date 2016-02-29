///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import IScope = angular.IScope;
    import IAugmentedJQuery = angular.IAugmentedJQuery;
    import IDirectiveFactory = angular.IDirectiveFactory;
    import ILogService = angular.ILogService;

    class ClickQueryDirective implements ng.IDirective {
        // @ngInject
        constructor(private $log: ILogService, private searchService: SearchService) {
            $log.debug("ClickQueryDirective started");
        }

        public scope = {
            success: "=success",
            error: "=error"
        };

        public restrict = "A";

        public link = (scope: ISuccessErrorScope<QueryResponse>, element: IAugmentedJQuery) => {
            element.on("click", () => {
                let queryString = element.text();
                scope.$emit(CoreEvents.INVOKE_CLICK_QUERY);
                this.searchService.runBilingualQuery(queryString, scope.success, scope.error);
            });
        };

        public static factory(): IDirectiveFactory {
            let directive = ($log: ILogService, searchService: SearchService) => new ClickQueryDirective($log, searchService);
            directive.$inject = ["$log", "searchService"];
            return directive;
        }
    }

    metadictModule.directive("clickQuery", ClickQueryDirective.factory());

}