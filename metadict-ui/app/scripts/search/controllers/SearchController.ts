/// <reference path="../../App.ts" />
/// <reference path="../../core/services/UserService.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;

    interface ISearchScope extends IScope {

        searchRequest: string;

        isSearching: boolean;
    }

    class SearchController {

        // @ngInject
        constructor(private $scope: ISearchScope, private $log: ILogService) {
            $log.debug($scope);
            $log.debug("SearchController started");
        }
    }

    metadictModule
        .controller("SearchController", SearchController);
}

