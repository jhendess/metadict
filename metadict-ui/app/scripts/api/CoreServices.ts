///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    export type SuccessCallback<T> = (data: T) => any;
    export type ErrorCallback = (responseStatus: ResponseStatus, reason: any) => any;

    export interface INavigationMenuService {
        getSections() : Array<NavigationSection>;
    }
}
