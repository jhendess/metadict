/**
 * Created by xolor on 02.02.17.
 */


module MetadictApp {

    /**
     * Classs with common helper functions.
     */
    export class CommonUtils {

        /**
         * Deletes a given cookie.
         * @param name The name of the cookie to delete.
         */
        static deleteCookie(name) {
            document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
        };
    }
}