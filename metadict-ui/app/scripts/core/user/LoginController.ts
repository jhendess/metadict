/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;
    import IFormController = angular.IFormController;
    import ILocationService = angular.ILocationService;

    declare let Materialize;

    interface IRegistrationScope extends IScope {
        /** The credentials for login. */
        user: Credentials;
        /** Indicates a running registration process. */
        loggingIn: boolean;
        /** Flag to indicate a failed login. */
        loginFailed: boolean;
        /** Form used for validation. */
        loginForm: IFormController;
    }

    /**
     * Controller for handling the left navigation menu.
     */
    class LoginController {
        // @ngInject
        constructor(private $scope: IRegistrationScope, private userService: UserService, private $location: ILocationService, private $log: ILogService) {
            $log.debug("LoginController started");
        }

        /**
         * Perform a login with the user data in scope.
         */
        public login(): void {
            if (!_.isEmpty(this.$scope.loginForm.$error) || !this.$scope.user || !this.$scope.user.name || !this.$scope.user.password) {
                this.$log.warn("Validation errors still open");
                this.$scope.loginFailed = true;
                return;
            }
            this.$scope.loginFailed = false;
            this.$scope.loggingIn = true;
            this.userService.login(this.$scope.user, this.afterSuccessHandler, this.loginErrorHandler);
        }

        private loginErrorHandler: ErrorCallback = (responseStatus: ResponseStatus, errorMessage: string) => {
            this.$log.error("Login failed");
            this.$scope.loggingIn = false;
            this.$scope.loginFailed = true;
        };

        private afterSuccessHandler: SuccessCallback<UserSession> = (response: UserSession) => {
            this.$location.path("/search");     // TODO: Display a welcome popup for a new registration
            Materialize.toast(`Welcome back ${response.name}!`, 4000);
        };
    }

    metadictModule.controller("LoginController", LoginController);
}