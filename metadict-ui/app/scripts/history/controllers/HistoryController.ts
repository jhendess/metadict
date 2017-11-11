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
    import IScope = angular.IScope;
    import ILocationService = angular.ILocationService;

    interface HistoryScope extends IScope {

        queryLogs: QueryLogEntry[];

        isEmpty: boolean;

        disableLoading: boolean;

        buildIconClass;
    }

    class HistoryController {

        /** Flag to indicate that the controller is reloading. */
        private loading: boolean;

        /** Link to the next entries. */
        private next: Link;

        /** Flag to indicate if query logs have already been queried. */
        private firstCalled: boolean;

        // @ngInject
        constructor(private $log: ILogService, private historyAccessService: HistoryAccessService, private $location: ILocationService, private $scope: HistoryScope, private dictionaryService: DictionaryService) {
            $log.debug("HistoryController started");
            $scope.queryLogs = [];
            $scope.buildIconClass = dictionaryService.buildIconClass;
        }

        public loadMore() : QueryLogEntry[] {
            if (this.loading) {
                this.$log.debug("Already loading log entries - not fetching any more");
                return null;
            }
            this.loading = true;
            this.firstCalled = true;

            this.$log.debug("Loading query logs...");
            this.historyAccessService.loadHistory(this.next, this.handleQueryLogLoadSuccess, this.handleQueryLogLoadError);
        }

        private handleQueryLogLoadSuccess = (queryLogs: QueryLogEntry[], links: LinkContainer) => {
            this.next = links.next;
            this.loading = false;
            if (!this.next && (!queryLogs || queryLogs.length === 0)) {
                this.$scope.isEmpty = true;
                this.$log.debug("No query logs found");
            } else {
                this.$log.debug("Successfully fetched query logs: ", queryLogs);
                _.forEach(queryLogs, q => this.$scope.queryLogs.push(q));
            }
            if (!this.next) {
                this.$scope.disableLoading = true;
            }
        };

        private handleQueryLogLoadError = (responseStatus: ResponseStatus, reason: any) => {
            this.loading = false;
            this.$log.error("Loading query logs failed: ", responseStatus, reason);
            if (responseStatus === ResponseStatus.UNAUTHORIZED) {
                this.$location.path(MetadictApp.DEFAULT_PAGE);
            }
        }
    }

    metadictModule.controller("HistoryController", HistoryController);
}