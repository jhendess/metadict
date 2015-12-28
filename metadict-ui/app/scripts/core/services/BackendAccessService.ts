///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IRestangularService = restangular.IService;
    import IRestangularElement = restangular.IElement;

    /**
     * @inheritDoc
     */
    class BackendAccessService implements IBackendAccessService {
        // @ngInject
        constructor(private $log: ILogService, private Restangular: IRestangularService) {
            this.setupResources();

            $log.debug("BackendAccessService started");
        }

        private _bilingualDictionaryAccess: IRestangularElement;

        private _bilingualQueryAccess: IRestangularElement;

        /**
         * @inheritDoc
         */
        public fetchBilingualDictionaries(success: SuccessCallback<BilingualDictionary[]>,
                                          error: ErrorCallback) {
            this._bilingualDictionaryAccess.get("bidirected")
                .then<ResponseContainer<BilingualDictionary[]>>(
                    this.buildSuccessHandler<BilingualDictionary[]>(success, error),
                    this.buildErrorHandler(error)
                );
        }

        /**
         * @inheritDoc
         */
        public executeBilingualQuery(dictionaries: string,
                              requestString: string,
                              success: SuccessCallback<QueryResponse>,
                              error: ErrorCallback) {
            this._bilingualQueryAccess
                .one(dictionaries)
                .one(requestString)
                .get()
                .then<ResponseContainer<QueryResponse>>(
                    this.buildSuccessHandler<QueryResponse>(success, error),
                    this.buildErrorHandler(error)
                );
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
        private buildSuccessHandler<T>(successCallback: SuccessCallback<T>,
                                       errorCallback: ErrorCallback): (responseContainer: ResponseContainer<T>) => ResponseContainer<T> {
            return (responseContainer: ResponseContainer<T>): ResponseContainer<T> => {
                let responseStatus = responseContainer.status;
                this.$log.debug(`Received response with status ${status}`);

                if (responseStatus === ResponseStatus.OK) {
                    successCallback(responseContainer.data);
                } else {
                    // TODO: do system-wide error handling
                    errorCallback(responseContainer.message);
                }
                return responseContainer;
            };
        }

        private buildErrorHandler(errorCallback: ErrorCallback): ErrorCallback {
            return (reason: string) => {
                // TODO: do system-wide error handling
                errorCallback(reason);
            };
        }

        /**
         * Setup internal backend resources.
         */
        private setupResources() {
            this._bilingualDictionaryAccess = this.Restangular.all("dictionaries/bilingual");
            this._bilingualQueryAccess = this.Restangular.all("query");
        }
    }

    metadictModule.service("backendAccessService", BackendAccessService);
}