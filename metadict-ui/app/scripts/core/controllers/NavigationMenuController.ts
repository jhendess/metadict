/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;

    export class NavigationMenuController {

        public img: string;

        public topHeader: string;

        public subHeader: string;

        // @ngInject
        constructor(private $scope: IScope, private $log: ILogService, private userService: IUserService,
                    private navigationMenuService: INavigationMenuService) {
            $scope.$watch(userService.isUserLoggedIn, () => {
                if (userService.isUserLoggedIn()) {
                    this.img = userService.getLoggedInUser().img;
                    this.topHeader = userService.getLoggedInUser().fullname;
                    this.subHeader = userService.getLoggedInUser().email;
                } else {
                    this.resetHeader();
                }
            });

            $log.debug("NavigationMenuController started");
        }

        public isLoggedIn(): boolean {
            return this.userService.isUserLoggedIn();
        }

        public sections(): Array<NavigationSection> {
            return this.navigationMenuService.getSections();
        }

        private resetHeader() {
            this.img = "images/logo-icon.png";
            this.topHeader = "You are not logged in.";
            this.subHeader = "";
        }
    }

    metadictModule
        .controller("NavigationMenuController", NavigationMenuController);
}
