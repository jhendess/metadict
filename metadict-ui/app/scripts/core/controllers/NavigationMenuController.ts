/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;

    /**
     * Controller for handling the left navigation menu.
     */
    export class NavigationMenuController {

        public img: string;

        public topHeader: string;

        public subHeader: string;

        // @ngInject
        constructor(private $scope: IScope, private $log: ILogService, private userService: IUserService,
                    private navigationMenuService: INavigationMenuService) {
            this.setupWatchers();

            $log.debug("NavigationMenuController started");
        }

        public isLoggedIn(): boolean {
            return this.userService.isUserLoggedIn();
        }

        public sections(): Array<NavigationSection> {
            return this.navigationMenuService.getSections();
        }

        private setupWatchers() {
            this.$scope.$watch(this.userService.isUserLoggedIn, () => {
                if (this.userService.isUserLoggedIn()) {
                    this.img = this.userService.getLoggedInUser().img;
                    this.topHeader = this.userService.getLoggedInUser().fullname;
                    this.subHeader = this.userService.getLoggedInUser().email;
                } else {
                    this.resetHeader();
                }
            });
        };

        private resetHeader() {
            this.img = "images/logo-icon.png";
            this.topHeader = "You are not logged in.";
            this.subHeader = "";
        }
    }

    metadictModule
        .controller("NavigationMenuController", NavigationMenuController);
}

