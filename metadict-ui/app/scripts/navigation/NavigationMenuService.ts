/// <reference path="../App.ts" />

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

    export class NavigationMenuService implements INavigationMenuService {

        private sections: Array<INavigationSection> = [
            {
                "title": null,
                "pages": [
                    {
                        "title": "Lookup dictionary",
                        "icon": "search",
                        "target": "/search",
                        "loginRequired" : false
                    },
                    {
                        "title": "Favorites",
                        "icon": "star",
                        "target": "/favorites",
                        "loginRequired" : false
                    },
                    {
                        "title": "Learn",
                        "icon": "school",
                        "target": "/trainer",
                        "loginRequired" : false
                    }
                ]
            },
            {
                "title": "More",
                "pages": [
                    {
                        "title": "Account settings",
                        "icon": "settings",
                        "target": "/account",
                        "loginRequired" : true
                    },
                    {
                        "title": "Help",
                        "icon": "help",
                        "target": "/help",
                        "loginRequired" : false
                    },
                    {
                        "title": "About Metadict",
                        "icon": "info",
                        "target": "/about",
                        "loginRequired" : false
                    },
                    {
                        "title" : "Logout",
                        "icon" : "exit_to_app",
                        "target": "/logout",
                        "loginRequired" : true
                    }
                ]
            }
        ];

        public getSections(): Array<INavigationSection> {
            return this.sections;
        }
    }
}

angular.module("MetadictApp")
    .service("NavigationMenuService", MetadictApp.NavigationMenuService);
