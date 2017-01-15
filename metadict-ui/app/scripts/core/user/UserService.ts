/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    /**
     * Interface which contains a registration request to the Metadict server.
     */
    export interface RegistrationData {
        /**
         * The username which should be used for registration.
         */
        name: string;
        /**
         * The password for logging in.
         */
        password: string;
        /**
         * The confirmation of the password.
         */
        confirmPassword: string;
    }

    /**
     * Service for accessing the current user.
     */
    export class UserService {

        // @ngInject
        constructor(private backendAccessService: BackendAccessService, private $log: ILogService) {
            $log.debug("UserService started");
        }

        private _loggedInUser: User = undefined;

        public get loggedInUser(): User {
            return this._loggedInUser;
        }

        public isUserLoggedIn = (): boolean => {
            return this._loggedInUser !== undefined;
        };

        public registerNewUser(user: MetadictApp.RegistrationData, success: SuccessCallback<any>, error: ErrorCallback) {
            this.backendAccessService.registerNewUser(user, success, error)
        }
    }

    metadictModule
        .service("userService", UserService);
}

