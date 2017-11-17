/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Jakob Hendeß
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

///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IRestangularService = restangular.IService;
    import IRestangularElement = restangular.IElement;
    import IRootScopeService = angular.IRootScopeService;

    /**
     * Access service for fetching data from the history resource.
     */
    export class HistoryAccessService extends AbstractAccessService {

        private static HISTORY_RESOURCE_NAME = "history";

        private _historyAccess: IRestangularElement;

        // @ngInject
        constructor($log: ILogService, Restangular: IRestangularService, $rootScope: IRootScopeService) {
            super($log, Restangular, $rootScope);

            $log.debug("HistoryAccessService started");
            this._historyAccess = Restangular.one(HistoryAccessService.HISTORY_RESOURCE_NAME);
        }

        public loadHistory(link: Link, successCallback: SuccessCallback<QueryLogEntry[]>, errorCallback: ErrorCallback) {
            let promise: restangular.IPromise<any>;
            if (link && link.href && link.href.indexOf(HistoryAccessService.HISTORY_RESOURCE_NAME) === 1) {
                this.$log.debug(`Using link ${link.href} for fetching result`);
                promise = this.Restangular.one(link.href).get();
            } else {
                promise = this._historyAccess.get();
            }
            promise.then(
                this.buildSuccessHandler(successCallback, errorCallback),
                this.buildErrorHandler(errorCallback)
            );

        }
    }

    metadictModule.service("historyAccessService", HistoryAccessService);
}