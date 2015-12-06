/// <reference path="../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    class UserService implements IUserService {

        // @ngInject
        constructor(private $log : ILogService) {
            $log.debug("UserService started!");
        }

        private loggedInUser: IUser;

        public getLoggedInUser(): MetadictApp.IUser {
            return this.loggedInUser;
        }

        public isUserLoggedIn(): boolean {
            return this.loggedInUser !== undefined;
        }
    }

    class User implements IUser {
        constructor(public email: string, public img: string, public fullname: string) {

        };
    }

    metadictModule
        .service("UserService", UserService);
}

