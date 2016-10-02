///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    import IScope = angular.IScope;

    export interface Map<T> {
        [index: string]: T;
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

    /**
     * Private scope with a success and error callback.
     */
    export interface ISuccessErrorScope<T> extends IScope {

        /**
         * The callback function that will be executed upon a successful request.
         */
        success: SuccessCallback<T>;

        /**
         * The callback function that will be executed upon a failed request.
         */
        error: ErrorCallback;
    }
}