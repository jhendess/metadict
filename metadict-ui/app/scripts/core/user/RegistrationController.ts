/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;
    import Config = MetadictApp.Config;
    import IFormController = angular.IFormController;

    interface IRegistrationScope extends IScope {
        /** The registration request data. */
        user: RegistrationData,
        /** Indicates a running registration process. */
        registering: boolean,
        /** Flag to indicate a failed registration. */
        registrationFailed: boolean
        /** If the user already exists, use this field to indicate the duplicate name. */
        duplicateUserName: string,
        /** Form used for validation. */
        registrationForm: IFormController
    }

    /**
     * Controller for handling the left navigation menu.
     */
    class RegistrationController {
        //@ngInject
        constructor(private $scope: IRegistrationScope, private userService: UserService, private $log: ILogService) {
            $log.debug("RegistrationController started")
        }

        /**
         * Perform a registration with the user data that is in the current scope.
         */
        public register(): void {
            if (!_.isEmpty(this.$scope.registrationForm.$error)) {
                this.$log.warn("Validation errors still open");
                return;
            }
            this.$log.debug("Requesting new user registration for", this.$scope.user.name);
            this.$scope.registrationFailed = false;
            this.$scope.duplicateUserName = null;
            this.$scope.registering = true;
            this.userService.registerNewUser(this.$scope.user, this.registrationSuccessHandler, this.registrationErrorHandler);
        }

        private registrationSuccessHandler: SuccessCallback<any> = (response: any) => {
            this.$log.debug("Registered new user successfully");
            this.$scope.registering = false;
        };

        private registrationErrorHandler: ErrorCallback = (responseStatus: ResponseStatus, errorMessage: string) => {
            this.$log.error("Registration failed");
            this.$scope.registering = false;
            if (responseStatus === ResponseStatus.DUPLICATE) {
                this.$scope.duplicateUserName = this.$scope.user.name;
            } else {
                this.$scope.duplicateUserName = null;
                this.$scope.registrationFailed = true;
            }
        }
    }

    metadictModule.controller("RegistrationController", RegistrationController);
}