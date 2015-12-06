/// <reference path="../App.ts" />
/// <reference path="../core/UserService.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;

    class SearchController {

        // @ngInject
        constructor(private $scope: IScope, private $log: ILogService) {
            $log.debug("SearchController started!");
        }
    }

    metadictModule
        .controller("SearchController", SearchController);
}

