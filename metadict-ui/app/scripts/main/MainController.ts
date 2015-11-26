/// <reference path="../App.ts" />

"use strict";

module MetadictApp {
    import ILogService = angular.ILogService;
    import ISidenavService = angular.material.ISidenavService;

    export interface IMainScope extends ng.IScope {
        toggleLeftNav: Function;
        toggleRightNav: Function;
    }

    export class MainController {
        // @ngInject
        constructor (private $scope: IMainScope, private $log : ILogService, private $mdSidenav : ISidenavService) {
            $log.info("MainController started!");

            $scope.toggleLeftNav = this.toggleLeftNav;
            $scope.toggleRightNav = this.toggleRightNav;
        }

        private toggleLeftNav = () => {
            this.$mdSidenav("menu-left")
                .toggle();
        };

        private toggleRightNav = () =>  {
            this.$mdSidenav("menu-right")
                .toggle();
        };
    }
}

angular.module("MetadictApp")
    .controller("MainController", MetadictApp.MainController);
