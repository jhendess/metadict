///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    export interface Map<T> {
        [index: string]: T;
    }

    export type SuccessCallback<T> = (data: T) => any;
    export type ErrorCallback = (responseStatus: ResponseStatus, reason: any) => any;

    /**
     * The navigation entry for a single page.
     */
    export interface NavigationPage {
        /** Title of the page to display. */
        title: string;
        /** CSS class which will be used for displaying the icon. */
        icon: string;
        /** The target URL. */
        target: string;
        /** If true, then the link will only be displayed if the user is logged in. */
        loginRequired: boolean;
        /** If true, then the link will only be displayed if no user is logged in. If this is true, then {@link #loginRequired} may not be true. */
        notLoggedIn: boolean;
    }

    export interface NavigationSection {
        title: string;
        pages: Array<NavigationPage>;
    }

    export interface User {

        email?: string;

        img?: string;

        name: string;
    }
}