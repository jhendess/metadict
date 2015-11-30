/// <reference path="../App.ts" />

"use strict";

module MetadictApp {

    export class NavigationMenuDirective implements ng.IDirective {

        public templateUrl = "views/navigationMenu.html";

        public restrict = "E";

    }

    export function NavigationMenuFactory() {
        return new MetadictApp.NavigationMenuDirective();
    }

}

angular.module("MetadictApp")
    .directive("navigationMenu", MetadictApp.NavigationMenuFactory);
