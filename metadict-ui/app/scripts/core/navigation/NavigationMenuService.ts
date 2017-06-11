/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    export class NavigationMenuService {

        // @ngInject
        constructor(private $log: ILogService) {
            $log.debug("NavigationMenuService started");
        }

        private sections: Array<NavigationSection> = [
            {
                "title": null,
                "pages": [
                    {
                        "title": "Lookup dictionary",
                        "icon": "search",
                        "target": "/search",
                        "loginRequired": false,
                        "notLoggedIn": false
                    }/*,
                     {
                     "title": "Favorites",
                     "icon": "star",
                     "target": "/favorites",
                     "loginRequired": false
                     },
                     {
                     "title": "Learn",
                     "icon": "school",
                     "target": "/trainer",
                     "loginRequired": false
                     }*/
                ]
            },
            {
                "title": "More",
                "pages": [
                    /*{
                     "title": "Account settings",
                     "icon": "settings",
                     "target": "/account",
                     "loginRequired": true,
                     "notLoggedIn": false
                     },*/
                    {
                        "title": "Register / Login",
                        "icon": "person",
                        "target": "/login",
                        "loginRequired": false,
                        "notLoggedIn": true
                    },
                    {
                        "title": "Help",
                        "icon": "help",
                        "target": "/help",
                        "loginRequired": false,
                        "notLoggedIn": false
                    },
                    {
                        "title": "About",
                        "icon": "info",
                        "target": "/about",
                        "loginRequired": false,
                        "notLoggedIn": false
                    }
                ]
            }
        ];

        public getSections(): Array<NavigationSection> {
            return this.sections;
        }

        public prependBasePath(target: string) {
            return Config.CLIENT_BASE_PATH + (target !== undefined && target.indexOf("/") === 0) ? target.substr(1) : target;
        }
    }

    metadictModule
        .service("navigationMenuService", NavigationMenuService);
}

