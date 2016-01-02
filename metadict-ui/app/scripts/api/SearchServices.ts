///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    /**
     * Service for searching through dictionaries.
     */
    export interface ISearchService {

        /**
         * Run a search query for bilingual dictionaries against the currently connected metadict instance. This method
         * will use the currently selected dictionaries from {@link DictionaryService} for querying.
         *
         * @param requestString The search request for which metadict shall search.
         * @param success The success callback which should be called upon successful retrieval.
         * @param error The error callback which should be called upon a failed request.
         */
        runBilingualQuery(dictionaries: string, requestString: string, success: SuccessCallback<QueryResponse>, error: ErrorCallback);
    }
}