///<reference path="../../typings/index.d.ts" />
///<reference path="api/CoreInterfaces.ts"/>
///<reference path="api/DomainInterfaces.ts"/>
///<reference path="api/CoreServices.ts"/>
///<reference path="Config.ts"/>


/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015,2016 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

"use strict";

module MetadictApp {

    import Config = MetadictApp.Config;
    import IRouteProvider = angular.route.IRouteProvider;
    import IRestangularProvider = restangular.IProvider;
    import ILocationProvider = angular.ILocationProvider;

    declare var window;

    export let metadictModule = angular.module("MetadictApp", [
        "ngRoute",
        "restangular",
        "ui.materialize",
        "LocalStorageModule"
    ]);

    let basePathElement: JQuery = $("base").first();

    if (basePathElement.attr("href") === "{{clientBasePath}}") {
        let finalBasePath = `${window.location.protocol}//${window.location.hostname}:${window.location.port}/`;
        basePathElement.attr("href", finalBasePath);
    }

    metadictModule
        .config(($routeProvider: IRouteProvider) => {
            $routeProvider.when("/search", {
                controller: "SearchController",
                controllerAs: "searchController",
                templateUrl: "views/search.html",
                reloadOnSearch: false
            }).when("/trainer", {
                templateUrl: "views/trainer.html"
            }).when("/favorites", {
                templateUrl: "views/favorites.html"
            }).when("/about", {
                templateUrl: "views/about.html"
            }).when("/help", {
                templateUrl: "views/help.html"
            }).otherwise({
                redirectTo: "/search"
            });
        })
        .config(($locationProvider: ILocationProvider) => {
            $locationProvider.html5Mode(true);
        })
        .config((RestangularProvider: IRestangularProvider) => {
            RestangularProvider.setBaseUrl(Config.API_URL);
        })
        .config(($provide: ng.auto.IProvideService) => {
            $provide.decorator("$exceptionHandler", extendedExceptionHandler);
        })
        .run((clientUpdateService: ClientUpdateService, bootstrapService: BootstrapService) => {
            clientUpdateService.registerEventHandlers();
            bootstrapService.bootstrapApplication();
        });
}