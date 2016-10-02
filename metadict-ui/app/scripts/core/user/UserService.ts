/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    /**
     * Service for accessing the current user.
     */
    export class UserService {

        // @ngInject
        constructor(private $log: ILogService) {
            $log.debug("UserService started");
        }

        private _loggedInUser: User = undefined;

        public get loggedInUser(): User {
            return this._loggedInUser;
        }

        public isUserLoggedIn = (): boolean => {
            return this._loggedInUser !== undefined;
        }
    }

    metadictModule
        .service("userService", UserService);
}

