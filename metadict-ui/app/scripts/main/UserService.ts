/// <reference path="../App.ts" />

"use strict";

module MetadictApp {
    export interface IUser {
        email: string;
        img: string;
        fullname: string;
    }

    export interface IUserService {
        getLoggedInUser() : IUser;
        isUserLoggedIn() : boolean;
    }


    export class UserService implements IUserService {
        private loggedInUser: User;

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
}

angular.module("MetadictApp")
    .service("UserService", MetadictApp.UserService);
