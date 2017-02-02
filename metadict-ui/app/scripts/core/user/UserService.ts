/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import ICookiesService = angular.cookies.ICookiesService;

    /**
     * Service for accessing the current user.
     */
    export class UserService {

        private static SESSION_KEY_COOKIE = "sessionCookie";
        // @ngInject
        constructor(private backendAccessService: BackendAccessService, private $log: ILogService) {
            $log.debug("UserService started");
        }

        private _loggedInUser: User = undefined;

        public get loggedInUser(): User {
            return this._loggedInUser;
        }

        public isUserLoggedIn = (): boolean => {
            return this._loggedInUser !== undefined;
        };

        /**
         * Tries to register a user with the given registration data in the backend. After the registration finished,
         * no login will performed.
         * @param user The user data used for registration.
         * @param success The handler will will be called if the registration was successful.
         * @param error The handler will will be called if the registration failed.
         */
        public registerNewUser(user: RegistrationData, success: SuccessCallback<any>, error: ErrorCallback) {
            this.backendAccessService.registerNewUser(user, success, error);
        }

        public login(authenticationRequest: Credentials, success: SuccessCallback<UserSession>, error: ErrorCallback) {
            this.$log.debug("Attempting login for", authenticationRequest.name);
            this.backendAccessService.authenticate(authenticationRequest, this.loginSuccessHandler(success), this.loginErrorHandler(error));
        }

        /**
         * Perform a logout on the backend.
         */
        public logout() {
            this.resetSession();
            this.backendAccessService.logout(this.afterLogoutHandler, this.afterLogoutHandler);
        }

        /**
         * Checks if a server side session is currently active and update the internal user information if so.
         * @param success The handler will will be called if the session is currently active or null if none.
         * @param error The handler will will be called if no session is currently active or the request failed.
         */
        public checkLoginStatus(success?: SuccessCallback<UserSession>, error?: ErrorCallback) {
            this.$log.debug("Checking session information");
            this.backendAccessService.getSessionInfo(this.loginStatusSuccessHandler(success), this.loginStatusErrorHandler(error));
        }

        /**
         * Wrapping handler for a successful login. Calls the given callback afterwards.
         * @param success
         * @returns {(sessionData:UserSession)=>undefined}
         */
        private loginSuccessHandler = (success: SuccessCallback<UserSession>): SuccessCallback<UserSession> => {
            return (sessionData: UserSession) => {
                if (sessionData && sessionData.name) {
                    this._loggedInUser = sessionData;
                    this.$log.debug("Logged in as", this._loggedInUser.name);
                } else {
                    this.$log.error("Received invalid data structure");
                }
                if (success) {
                    success(sessionData);
                }
            };
        };

        private loginErrorHandler = (error: ErrorCallback): ErrorCallback => {
            return (responseStatus: ResponseStatus, reason: any) => {
                this.$log.warn("User login failed");
                this.resetSession();
                if (error) {
                    error(responseStatus, reason);
                }
            };
        };

        private loginStatusSuccessHandler = (success: SuccessCallback<UserSession>): SuccessCallback<UserSession> => {
            return (sessionData: UserSession) => {
                if (sessionData && sessionData.name) {
                    this._loggedInUser = sessionData;
                    this.$log.debug("Logged in as", this._loggedInUser.name);
                } else {
                    this.$log.error("Received invalid response data - resetting session");
                }
                if (success) {
                    success(sessionData);
                }
            };
        };

        private loginStatusErrorHandler = (error: ErrorCallback): ErrorCallback => {
            return (responseStatus: ResponseStatus, reason: any) => {
                this.$log.debug("User is not logged in - resetting session");
                this.resetSession();
                if (error) {
                    error(responseStatus, reason);
                }
            };
        };

        private afterLogoutHandler = () => {
            CommonUtils.deleteCookie(UserService.SESSION_KEY_COOKIE)
        };

        private resetSession() {
            this._loggedInUser = undefined;
        }
    }

    metadictModule.service("userService", UserService);
}

