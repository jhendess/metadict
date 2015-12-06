/// <reference path="../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    class NavigationMenuService implements INavigationMenuService {

        // @ngInject
        constructor(private $log : ILogService) {
            $log.debug("NavigationMenuService started!");
        }

        private sections: Array<INavigationSection> = [
            {
                "title": null,
                "pages": [
                    {
                        "title": "Lookup dictionary",
                        "icon": "search",
                        "target": "/search",
                        "loginRequired": false
                    },
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
                        "loginRequired": true
                    },
                    {
                        "title": "Help",
                        "icon": "help",
                        "target": "/help",
                        "loginRequired": false
                    },
                    {
                        "title": "About Metadict",
                        "icon": "info",
                        "target": "/about",
                        "loginRequired": false
                    },
                    {
                        "title": "Logout",
                        "icon": "exit_to_app",
                        "target": "/logout",
                        "loginRequired": true
                    }
                ]
            }
        ];

        public getSections(): Array<INavigationSection> {
            return this.sections;
        }
    }

    metadictModule
        .service("NavigationMenuService", NavigationMenuService);
}

