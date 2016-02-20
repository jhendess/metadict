///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    export type SuccessCallback<T> = (data: T) => any;
    export type ErrorCallback = (reason: any) => any;

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
         * The list of currently selected dictionaries as ids.
         */
        selectedDictionaries: string[];

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
         * frequently used source languages.
         */
        getBilingualDictionaries(): BilingualDictionary[];

        /**
         * Toggle the selection status of the dictionary with the given identifier. The identifier must be written in
         * the usual format as specified in the Metadict API docs. Identifiers will be handled bidirectionally.
         *
         * @param dictionaryIdentifier The identifier of the dictionary to toggle.
         * @return True if the dictionary is now selected for the next query. False will be returned if either the
         * dictionary is not selected anymore or if the dictionary is not supported on the backend and cannot be
         *     toggled.
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
         * Returns a list of all dictionaries which support the given language as source. The language identifier must
         * be written in the usual format as specified in the Metadict API docs. Identifiers will be handled
         * bidirectionally.
         *
         * @param languageIdentifer The language identifier which will be used for looking up the dictionaries.
         * @return A list of dictionaries which support the given language as source.
         */
        getDictionariesForSourceLanguage(languageIdentifer: string): BilingualDictionary[];

        /**
         * Build a metadict-compatible request string which contains the list of given dictionaries. Each dictionary
         * will be converted to the form [IN_LANG]_[IN_DIALECT]-[OUT-LANG]_[OUT_DIALECT]. Multiple dictionaries will be
         * separated by a comma.
         *
         * @param dictionaries The array of bilingual dictionaries which shall be converted to a string.
         */
        buildDictionaryString(dictionaries: BilingualDictionary[]): string;

        /**
         * Get the currently selected dictionaries as a metadict-compatible request string.
         */
        getCurrentDictionaryString(): string;

        /**
         * Derive a css class which can be used to display the flag of the country which belongs to the given language.

         * @param language The language.
         */
        buildIconClass(language: Language): string;
    }

    /**
     * Central service for accessing the Metadict backend data.
     */
    export interface IBackendAccessService {

        fetchBilingualDictionaries(success: SuccessCallback<BilingualDictionary[]>,
                                   error: ErrorCallback);

        /**
         * Execute a bilingual search query against the currently connected instance.
         *
         * @param dictionaries The dictionary query string.
         * @param requestString The query request to search for.
         * @param success The success callback which should be called upon successful retrieval.
         * @param error The error callback which should be called upon a failed request.
         */
        executeBilingualQuery(dictionaries: string, requestString: string, success: SuccessCallback<QueryResponse>, error: ErrorCallback);
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

    /**
     * Central service for exchanging statuses between components.
     */
    export interface IStatusService {

        /** Indicator if the app has encountered a global error */
        isError: boolean;

        /** Indicator if the app is connected to a metadict instance */
        isConnected: boolean;

        /** Error message for the user */
        errorMessage: string;

        /** The current git revision of the executing client */
        clientRevision: string;

        /** The current version of the executing client */
        clientVersion: string;

        /** The version of the currently connected server */
        serverVersion: string;

        /** Indicator if the browser cache is currently reloading the application. */
        isUpdating: boolean;

        /** Indicator if a previous update has been finished. */
        isUpdateFinished: boolean;

        /** Progress of reloading in percentage. */
        updateProgress: number;
    }

    /**
     * Service for formatting various data nicely for the user. This includes also e.g. abbreviating certain phrases,
     * so that the user can read them more easily.
     */
    export interface IPrettyFormattingService {

        /**
         * Format an input string which shall be used as the grammatical gender of an entry. This method will abbreviate
         * the input to respectively either "masc.", "fem.", "neut." or "nat.". If abbreviation is not possible, the string
         * will be only lowercased.
         *
         * @param gender The grammatical gender to format.
         */
        formatGrammaticalGender(gender: string): string;

        /**
         * Format an input string which shall be used as the entry type of an entry. All inputs will be lowercased and
         * underscores (if any) will be replaced by spaces.
         *
         * @param entryType The entry type to format.
         */
        formatEntryType(entryType: string): string;

        /**
         * Takes an array of strings which represent the syllabification of a word and merges them into a single string.
         *
         * @param syllabificationList
         */
        formatSyllabificationList(syllabificationList: string[]): string;
    }
}
