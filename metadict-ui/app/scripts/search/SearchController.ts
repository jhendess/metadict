/// <reference path="../App.ts" />
/// <reference path="../user/UserService.ts"/>

"use strict";

module MetadictApp {
    import ILogService = angular.ILogService;
    import IScope = angular.IScope;

    export class SearchController {

        // @ngInject
        constructor(private $scope: IScope, private $log: ILogService) {
            $log.info("SearchController started!");
        }
    }
}

angular.module("MetadictApp")
    .controller("SearchController", MetadictApp.SearchController);
