///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    declare var Offline;

    import ILogService = angular.ILogService;
    import IRestangularService = restangular.IService;
    import IRestangularElement = restangular.IElement;

    /**
     * Central service for accessing the Metadict backend data.
     */
    export class BackendAccessService {
        // @ngInject
        constructor(private $log: ILogService, private Restangular: IRestangularService) {
            this.setupResources();

            $log.debug("BackendAccessService started");
        }

        private _bilingualDictionaryAccess: IRestangularElement;

        private _bilingualQueryAccess: IRestangularElement;

        private _systemStatusAccess: IRestangularElement;

        private _registrationAccess: IRestangularElement;

        private _sessionAccess: IRestangularElement;

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
         * Execute a bilingual search query against the currently connected instance.
         *
         * @param dictionaries The dictionary query string.
         * @param requestString The query request to search for.
         * @param success The success callback which should be called upon successful retrieval.
         * @param error The error callback which should be called upon a failed request.
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
         * Query the current system status from the backend.
         *
         * @param success The success callback which should be called upon successful retrieval.
         * @param error The error callback which should be called upon a failed request.
         */
        public querySystemStatus(success: SuccessCallback<SystemStatus>, error: ErrorCallback) {
            this._systemStatusAccess
                .get()
                .then<ResponseContainer<SystemStatus>>(
                    this.buildSuccessHandler(success, error),
                    this.buildErrorHandler(error)
                );
        }

        /**
         *
         * @param user
         * @param success
         * @param error
         */
        public registerNewUser(user: RegistrationData, success: SuccessCallback<any>, error: ErrorCallback) {
            this._registrationAccess
                .post(user)
                .then<ResponseContainer<any>>(
                    this.buildSuccessHandler(success, error),
                    this.buildErrorHandler(error)
                );
        }

        /**
         * Performs a request to the backend session resource to check if a user is currently logged in.
         *
         * @param successCallback Callback which will be invoked if the user is logged on.
         * @param errorCallback Callback which will be invokd on a error.
         */
        public getSessionInfo(successCallback: SuccessCallback<UserSession>, errorCallback: ErrorCallback) {
            this.Restangular
                .one("session")
                .get()
                .then<ResponseContainer<UserSession>>(
                    this.buildSuccessHandler(successCallback, errorCallback),
                    this.buildErrorHandler(errorCallback)
                );
        }

        /**
         * Authenticates with given user data.
         *
         * @param authenticationRequest The credentials which will be used for logging in.
         * @param successCallback Callback which will be invoked if the user is logged on.
         * @param errorCallback Callback which will be invokd on a error.
         */
        public authenticate(authenticationRequest: Credentials, successCallback: SuccessCallback<UserSession>, errorCallback: ErrorCallback) {
            this._sessionAccess
                .post(authenticationRequest)
                .then<ResponseContainer<UserSession>>(
                    this.buildSuccessHandler(successCallback, errorCallback),
                    this.buildErrorHandler(errorCallback)
                );
        }

        /**
         * Perform a logout operation for the currently logged in user.
         * @param successCallback Callback which will be invoked if the user was logged off successfully.
         * @param errorCallback Callback which will be invokd on a error.
         */
        public logout(successCallback: SuccessCallback<any>, errorCallback: ErrorCallback) {
            this._sessionAccess
                .remove()
                .then<ResponseContainer<UserSession>>(
                    this.buildSuccessHandler(successCallback, errorCallback),
                    this.buildErrorHandler(errorCallback)
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
                let responseStatus: any;
                let containerData: T;

                if (responseContainer) {
                    responseStatus = ResponseStatus[responseContainer.status];
                    containerData = responseContainer.data;
                }

                this.$log.debug(`Received response with status ${ResponseStatus[responseStatus]}`);
                this.$log.debug(containerData);

                if (responseStatus === ResponseStatus.OK) {
                    successCallback(containerData);
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
        private buildErrorHandler(errorCallback: ErrorCallback): (reason: any) => any {
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
         * Setup internal backend resources.
         */
        private setupResources() {
            this._bilingualDictionaryAccess = this.Restangular.all("dictionaries/bilingual");
            this._bilingualQueryAccess = this.Restangular.all("query");
            this._systemStatusAccess = this.Restangular.one("status");
            this._registrationAccess = this.Restangular.all("register");
            this._sessionAccess = this.Restangular.all("session");
        }
    }

    metadictModule.service("backendAccessService", BackendAccessService);
}