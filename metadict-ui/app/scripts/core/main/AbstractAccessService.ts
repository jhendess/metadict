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

    import LinkContainer = MetadictApp.LinkContainer;
    import ILogService = angular.ILogService;
    import IRestangularService = restangular.IService;
    import IRestangularResponse = restangular.IResponse;
    import IRootScopeService = angular.IRootScopeService;
    declare let Offline;


    /**
     * Abstract implementation of an access service. Provides convenient methods for building success and error handlers.
     */
    export abstract class AbstractAccessService {
        // @ngInject
        constructor(protected $log: ILogService, protected Restangular: IRestangularService, protected $rootScope: IRootScopeService) {
            Restangular.setErrorInterceptor(this.errorInterceptor);
        }

        /**
         * Generic handler for unwrapping the response container from the backend. Detects backend errors automatically
         * and will notify both the internal error registry and invoke a custom {@link ErrorCallback} for the client.
         *
         * @param successCallback The callback function that will be invoked if the response container was sent with
         * status {@link ResponseStatus.OK}.
         * @param errorCallback The callback function that will be invoked if the response container was sent with any
         * other status than {@link ResponseStatus.OK}.
         * @returns {function(ResponseContainer<T>): undefined}
         */
        protected buildSuccessHandler<T>(successCallback: SuccessCallback<T>,
                                         errorCallback: ErrorCallback): (responseContainer: ResponseContainer<T>) => ResponseContainer<T> {
            return (responseContainer: ResponseContainer<T>): ResponseContainer<T> => {
                let responseStatus: any;
                let containerData: T;
                let linkContainer: LinkContainer = {};

                if (responseContainer) {
                    responseStatus = ResponseStatus[responseContainer.status];
                    containerData = responseContainer.data;
                    if (responseContainer.links) {
                        linkContainer = this.buildLinkContainer(responseContainer.links);
                    }
                }

                this.$log.debug(`Received response with status ${ResponseStatus[responseStatus]}`);
                this.$log.debug("Container data:", containerData);
                this.$log.debug("Links in response:", linkContainer);

                if (responseStatus === ResponseStatus.OK) {
                    successCallback(containerData, linkContainer);
                } else {
                    let message = responseContainer ? responseContainer.message : undefined;
                    errorCallback(responseStatus, message);
                }

                return responseContainer;
            };
        }

        /**
         * Builds an restangular-compatible error handler which wraps the internal Metadict error callback.
         * @param errorCallback The internal callback to wrap.
         * @returns {(reason:string)=>undefined} A restangular-compatible error callback.
         */
        protected buildErrorHandler(errorCallback: ErrorCallback): (reason: any) => any {
            return (reason: any) => {
                let responseContainer = <ResponseContainer<any>> reason.data;
                // TODO: do system-wide error handling
                Offline.check();
                if (responseContainer != null) {
                    errorCallback(ResponseStatus[responseContainer.status], responseContainer.message);
                } else {
                    errorCallback(ResponseStatus.ERROR, reason);
                }
            };
        }

        /**
         * Checks if the response status is 401 and sets the status in the returned ResponseContainer to {@link ResponseStatus#UNAUTHORIZED}.
         * @returns {(data: any, operation: string, what: string, url: string, response: restangular.IResponse) => boolean} A restangular response interceptor.
         */
        protected errorInterceptor = (response: IRestangularResponse) => {
            if (response.status === 401) {
                this.$log.debug("User not authorized");
                response.data = {
                    status: ResponseStatus[ResponseStatus.UNAUTHORIZED]
                };
            } else if (response.status === 429) {
                this.$rootScope.$broadcast(CoreEvents.TOO_MANY_REQUESTS);
            }
            return true;
        };

        /**
         * Builds a {@link LinkContainer} from a collection of {@link Link} objects.
         * @param {MetadictApp.Link[]} links
         * @returns {MetadictApp.LinkContainer}
         */
        private buildLinkContainer(links: MetadictApp.Link[]): LinkContainer {
            let linkContainer: LinkContainer = {};
            _.forEach(links, (link: Link) => {
                if (link.rel === "next") {
                    linkContainer.next = link;
                } else if (link.rel === "previous") {
                    linkContainer.previous = link;
                } else {
                    this.$log.warn("Unknown link relationship: ", link.rel);
                }
            });
            return linkContainer;
        }
    }
}