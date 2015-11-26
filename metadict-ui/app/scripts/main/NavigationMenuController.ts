/// <reference path="../App.ts" />
/// <reference path="UserService.ts"/>


"use strict";

module MetadictApp {
    import ILogService = angular.ILogService;
    import IScope = angular.IScope;

    export class NavigationMenuController {

        public img: string;

        public topHeader: string;

        public subHeader: string;

        // @ngInject
        constructor(private $scope: IScope, private $log: ILogService, private UserService: IUserService) {
            $log.info("NavigationMenuController started!");

            $scope.$watch(UserService.isUserLoggedIn, () => {
                if (UserService.isUserLoggedIn()) {
                    this.img = UserService.getLoggedInUser().img;
                    this.topHeader = UserService.getLoggedInUser().fullname;
                    this.subHeader = UserService.getLoggedInUser().email;
                } else {
                    this.resetHeader();
                }
            });

        }

        private resetHeader() {
            this.img = "images/logo-icon.png";
            this.topHeader = "You are not logged in.";
            this.subHeader = "";
        }


    }
}

angular.module("MetadictApp")
    .controller("NavigationMenuController", MetadictApp.NavigationMenuController);
