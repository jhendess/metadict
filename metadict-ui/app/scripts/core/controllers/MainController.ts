/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import ISidenavService = angular.material.ISidenavService;

    interface IMainScope extends ng.IScope {
        toggleLeftNav: Function;
        toggleRightNav: Function;
    }

    class MainController {

        // @ngInject
        constructor (private $scope: IMainScope, private $log : ILogService, private $mdSidenav : ISidenavService) {
            $scope.toggleLeftNav = this.toggleLeftNav;
            $scope.toggleRightNav = this.toggleRightNav;

            $log.debug("MainController started");
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

    metadictModule
        .controller("MainController", MainController);
}
