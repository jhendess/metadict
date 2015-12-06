///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    export interface INavigationPage {
        title: string;
        icon: string;
        target: string;
        loginRequired: boolean;
    }

    export interface INavigationSection {
        title: string;
        pages: Array<INavigationPage>;
    }

    export interface INavigationMenuService {
        getSections() : Array<INavigationSection>;
    }

    export interface IMainScope extends ng.IScope {
        toggleLeftNav: Function;
        toggleRightNav: Function;
    }

    export interface IUser {

        email: string;

        img: string;

        fullname: string;
    }

    export interface IUserService {

        getLoggedInUser() : IUser;

        isUserLoggedIn() : boolean;
    }
}
