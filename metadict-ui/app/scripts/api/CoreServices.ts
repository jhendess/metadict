///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    export interface INavigationMenuService {
        getSections() : Array<NavigationSection>;
    }

    export interface IUserService {

        getLoggedInUser() : User;

        isUserLoggedIn() : boolean;
    }

    /**
     * The dictionary service provides access to all available dictionaries on the backend side. Its
     * {@link #reloadDictionaries} must be called at least one time on initialization before all methods work.
     */
    export interface IDictionaryService {

        /**
         * Returns true if the list of supported dictionaries has been completely loaded. If the dictionary list is
         * either not yet loaded or currently reloading, this method will return false.
         */
        isDictionaryListLoaded(): boolean;

        /**
         * Returns true if the list of supported dictionaries is currently reloading. Otherwise false will be returned.
         */
        isDictionaryListLoading(): boolean;

        /**
         * Reloads the list of currently supported dictionaries from the backend. After reloading has finished,
         * {@link #isDictionaryListLoaded} will return true.
         */
        reloadDictionaries();

        /**
         * Returns a list of all currently supported bilingual dictionaries. If the list of dictionaries has not yet
         * been loaded, the returned list will be empty. The returned list will be sorted alphabetically by the most
         * frequently used input languages.
         */
        getBilingualDictionaries(): BilingualDictionary[];

        /**
         * Toggle the selection status of the dictionary with the given identifier. The identifier must be written in
         * the usual format as specified in the Metadict API docs. Identifiers will be handled bidirectionally.
         *
         * @param dictionaryIdentifier The identifier of the dictionary to toggle.
         * @return True if the dictionary is now selected for the next query. False will be returned if either the
         * dictionary is not selected anymore or if the dictionary is not supported on the backend and cannot be toggled.
         */
        toggleDictionarySelection(dictionaryIdentifier: string): boolean;

        /**
         * Checks if a dictionary with the given identifier is currently selected for querying. The identifier must be
         * written in the usual format as specified in the Metadict API docs. Identifiers will be handled
         * bidirectionally.
         *
         * @param dictionaryIdentifier The identifier of the dictionary to check.
         * @return True if the dictionary is selected for querying.
         */
        isDictionarySelected(dictionaryIdentifier: string): boolean;

        /**
         * Returns a list of all dictionaries which support the given language as an input. The language identifier must
         * be written in the usual format as specified in the Metadict API docs. Identifiers will be handled
         * bidirectionally.
         *
         * @param languageIdentifer The language identifier which will be used for looking up the dictionaries.
         * @return A list of dictionaries which support the given language as input.
         */
        getDictionariesForInputLanguage(languageIdentifer: string): BilingualDictionary[];
    }

    /**
     * Central service for accessing the Metadict backend data.
     */
    export interface IBackendAccessService {

        fetchBilingualDictionaries(successCallback: (data: BilingualDictionary[]) => any,
                                   errorCallback: (reason: string) => any);
    }

    /**
     * Service for initial bootstrapping of Metadict.
     */
    export interface IBootstrapService {

        /**
         * Start and configure all required services.
         */
        bootstrapApplication();
    }
}
