///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    import IScope = angular.IScope;

    export interface IMainScope extends IScope {

        status: IStatusService;
    }

    export interface NavigationPage {
        title: string;
        icon: string;
        target: string;
        loginRequired: boolean;
    }

    export interface NavigationSection {
        title: string;
        pages: Array<NavigationPage>;
    }

    export interface User {

        email: string;

        img: string;

        fullname: string;
    }
}