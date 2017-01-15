/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    class NavigationMenuService implements INavigationMenuService {

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
                    {
                        "title": "Account settings",
                        "icon": "settings",
                        "target": "/account",
                        "loginRequired": true,
                        "notLoggedIn": false
                    },
                    {
                        "title": "Register / Login",
                        "icon": "person",
                        "target": "/login",
                        "loginRequired": false,
                        "notLoggedIn": true
                    },
                    {
                        "title": "About Metadict",
                        "icon": "info",
                        "target": "/about",
                        "loginRequired": false,
                        "notLoggedIn": false
                    },
                    {
                        "title": "Logout",
                        "icon": "exit_to_app",
                        "target": "/logout",
                        "loginRequired": true,
                        "notLoggedIn": false
                    }
                ]
            }
        ];

        public getSections(): Array<NavigationSection> {
            return this.sections;
        }
    }

    metadictModule
        .service("navigationMenuService", NavigationMenuService);
}

