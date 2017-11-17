/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;
    import ILocationService = angular.ILocationService;

    /**
     * Controller for handling the left navigation menu.
     */
    export class NavigationMenuController {

        public img: string;

        public topHeader: string;

        public subHeader: string;

        // @ngInject
        constructor(private $scope: IScope, private $log: ILogService, private userService: UserService,
                    private navigationMenuService: NavigationMenuService, private $location: ILocationService) {
            this.setupWatchers();

            $log.debug("NavigationMenuController started");
        }

        public isLoggedIn(): boolean {
            return this.userService.isUserLoggedIn();
        }

        public sections(): Array<NavigationSection> {
            return this.navigationMenuService.getSections();
        }

        public logout(): void {
            this.userService.logout();
            this.$location.path(MetadictApp.SEARCH_PAGE);
        }

        /**
         * Checks if the given {@link NavigationPage} may be rendered.
         */
        public isDisplayed(page: NavigationPage): boolean {
            return (this.isLoggedIn() && page.loginRequired)
                || (!this.isLoggedIn() && page.notLoggedIn)
                || (!page.loginRequired && !page.notLoggedIn);
        }

        private setupWatchers() {
            let self = this;
            this.$scope.$watch(self.userService.isUserLoggedIn, () => {
                if (self.userService.loggedInUser) {
                    // this.img = this.userService.loggedInUser.img;
                    self.topHeader = self.userService.loggedInUser.name;
                    // this.subHeader = this.userService.loggedInUser.email;
                } else {
                    self.resetHeader();
                }
            });
        };

        private resetHeader() {
            this.img = "images/logo-small.png";
            this.topHeader = "You are not logged in.";
            this.subHeader = "";
        }
    }

    metadictModule
        .controller("NavigationMenuController", NavigationMenuController);
}

