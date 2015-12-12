/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    class UserService implements IUserService {

        // @ngInject
        constructor(private $log : ILogService) {
            $log.debug("UserService started");
        }

        private loggedInUser: User;

        public getLoggedInUser(): MetadictApp.User {
            return this.loggedInUser;
        }

        public isUserLoggedIn(): boolean {
            return this.loggedInUser !== undefined;
        }
    }

    metadictModule
        .service("userService", UserService);
}

