///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IRootScopeService = angular.IRootScopeService;
    import ILocationService = angular.ILocationService;
    import ILocalStorageService = angular.local.storage.ILocalStorageService;

    /**
     * The dictionary service provides access to all available dictionaries on the backend side. Its
     * {@link #reloadDictionaries} must be called at least one time on initialization before all methods work.
     */
    export class DictionaryService {

        // @ngInject
        constructor(private $log: ILogService, private backendAccessService: BackendAccessService,
                    private $rootScope: IRootScopeService, private $location: ILocationService,
                    private localStorageService: ILocalStorageService) {
            this.initializeDictionaryConfiguration();
            $log.debug("DictionaryService started");
        }

        private _dictionaryListLoaded = false;

        private _dictionaryListLoading = false;

        private _bilingualDictionaries: BilingualDictionary[] = [];

        private _selectedDictionaryIds: string[] = [];

        private reloadErrorCallback = (reason: string) => {
            this._dictionaryListLoading = false;
            // TODO: Error handling?
            this.$log.error("Updating dictionary list failed");
            this.$log.error(reason);
        };

        private reloadSuccessCallback = (dictionaries: BilingualDictionary[]) => {
            this.$log.debug("Updated dictionary list");

            // TODO: Check if selected dictionaries are still available after reload
            this.updateDictionaryIndices(dictionaries);

            this._bilingualDictionaries = dictionaries;

            // Filter unavailable dictionaries from selection
            _.filter(this._selectedDictionaryIds, (dictionaryId: string) => {
                return this.isDictionaryAvailable(dictionaryId);
            });

            this._dictionaryListLoaded = true;
            this._dictionaryListLoading = false;
        };

        /**
         * Returns true if the list of supported dictionaries is currently reloading. Otherwise false will be returned.
         */
        public isDictionaryListLoading(): boolean {
            return this._dictionaryListLoading;
        }

        /**
         * Returns true if the list of supported dictionaries has been completely loaded. If the dictionary list is
         * either not yet loaded or currently reloading, this method will return false.
         */
        public isDictionaryListLoaded() {
            return this._dictionaryListLoaded;
        }

        /**
         * Reloads the list of currently supported dictionaries from the backend. After reloading has finished,
         * {@link #isDictionaryListLoaded} will return true.
         */
        public reloadDictionaries() {
            this._dictionaryListLoading = true;
            this._dictionaryListLoaded = false;
            this.$log.debug("Reloading dictionaries...");
            this.backendAccessService.fetchBilingualDictionaries(this.reloadSuccessCallback, this.reloadErrorCallback);
        }

        /**
         * Returns a list of all currently supported bilingual dictionaries. If the list of dictionaries has not yet
         * been loaded, the returned list will be empty. The returned list will be sorted alphabetically by the most
         * frequently used source languages.
         */
        public getBilingualDictionaries(): MetadictApp.BilingualDictionary[] {
            return this._bilingualDictionaries;
        }

        /**
         * Toggle the selection status of the dictionary with the given identifier. The identifier must be written in
         * the usual format as specified in the Metadict API docs. Identifiers will be handled bidirectionally.
         *
         * @param dictionaryIdentifier The identifier of the dictionary to toggle.
         * @return True if the dictionary is now selected for the next query. False will be returned if either the
         * dictionary is not selected anymore or if the dictionary is not supported on the backend and cannot be
         *     toggled.
         */
        public toggleDictionarySelection(dictionaryIdentifier: string): boolean {
            return this.internalToggleDictionarySelection(dictionaryIdentifier, false);
        }

        /**
         * Checks if a dictionary with the given identifier is currently selected for querying. The identifier must be
         * written in the usual format as specified in the Metadict API docs. Identifiers will be handled
         * bidirectionally.
         *
         * @param dictionaryIdentifier The identifier of the dictionary to check.
         * @return True if the dictionary is selected for querying.
         */
        public isDictionarySelected(dictionaryIdentifier: string): boolean {
            return _.includes(this._selectedDictionaryIds, dictionaryIdentifier);
        }

        /**
         * Returns a list of all dictionaries which support the given language as source. The language identifier must
         * be written in the usual format as specified in the Metadict API docs. Identifiers will be handled
         * bidirectionally.
         *
         * @param languageIdentifer The language identifier which will be used for looking up the dictionaries.
         * @return A list of dictionaries which support the given language as source.
         */
        public getDictionariesForSourceLanguage(languageIdentifer: string): BilingualDictionary[] {
            // TODO
            return undefined;
        }

        /**
         * Build a metadict-compatible request string which contains the list of given dictionaries. Each dictionary
         * will be converted to the form [IN_LANG]_[IN_DIALECT]-[OUT-LANG]_[OUT_DIALECT]. Multiple dictionaries will be
         * separated by a comma.
         *
         * @param dictionaries The array of bilingual dictionaries which shall be converted to a string.
         */
        public buildDictionaryString(dictionaries: MetadictApp.BilingualDictionary[]): string {
            return _.map(dictionaries, (d: BilingualDictionary) => d.queryStringWithDialect).join(",");
        }

        /**
         * Derive a css class which can be used to display the flag of the country which belongs to the given language.

         * @param language The language.
         */
        public buildIconClass(language: MetadictApp.Language): string {
            let identifier = language.identifier;
            if (identifier === "en") {
                identifier = "gb";
            } else if (identifier === "sv") {
                identifier = "se";
            }

            return "flag-icon-" + identifier;
        }

        /**
         * Get the currently selected dictionaries as a metadict-compatible request string.
         */
        public getCurrentDictionaryString(): string {
            return this._selectedDictionaryIds.join(Parameters.SEPARATOR);
        }

        /**
         * The list of currently selected dictionaries as ids.
         */
        public get selectedDictionaryIds(): string[] {
            return this._selectedDictionaryIds;
        }

        /**
         * Returns an array of the currently selected bilingual dictionaries.
         * @returns {any}
         */
        public get selectedBilingualDictionaries(): BilingualDictionary[] {
            return _.filter(this.getBilingualDictionaries(), (bd: BilingualDictionary) => this.isDictionarySelected(bd.queryStringWithDialect));
        }

        private initializeDictionaryConfiguration() {
            let dictionaryString = this.$location.search()[Parameters.DICTIONARIES];

            if (!dictionaryString) {
                dictionaryString = this.localStorageService.get(StorageKeys.LAST_SELECTED_DICTIONARIES);
            }

            if (dictionaryString) {
                this.initializeDictionariesFromParameters(dictionaryString);
            }
        }

        private initializeDictionariesFromParameters(dictionaryString) {
            _.forEach(dictionaryString.split(Parameters.SEPARATOR), (dictionaryId: string) => {
                this.toggleDictionarySelection(dictionaryId);
            });
        };

        private isDictionaryAvailable(dictionaryIdentifier: string): boolean {
            return _.findIndex(this._bilingualDictionaries, (bilingualDictionary: BilingualDictionary) =>
                    bilingualDictionary.queryString === dictionaryIdentifier
                    || bilingualDictionary.queryStringWithDialect === dictionaryIdentifier
                ) > -1;
        }

        private updateDictionaryIndices(dictionaries: BilingualDictionary[]) {
            // TODO
        }

        private internalToggleDictionarySelection(dictionaryIdentifier, checkIfExisting: boolean) {
            let result: boolean;

            if (this.isDictionarySelected(dictionaryIdentifier)) {
                this.$log.debug("Disabled dictionary " + dictionaryIdentifier + " for query");
                _.pull(this._selectedDictionaryIds, dictionaryIdentifier);
                result = false;
            } else {
                if (checkIfExisting && this.isDictionaryAvailable(dictionaryIdentifier)
                    || !checkIfExisting) {
                    this.$log.debug("Enabled dictionary " + dictionaryIdentifier + " for query");
                    this._selectedDictionaryIds.push(dictionaryIdentifier);
                    result = true;
                } else {
                    this.$log.debug("Couldn't select unknown dictionary " + dictionaryIdentifier);
                    result = false;
                }
            }

            let currentDictionaryString = this.getCurrentDictionaryString();

            this.$rootScope.$broadcast(CoreEvents.DICTIONARY_SELECTION_CHANGE);
            this.$location.search(Parameters.DICTIONARIES, currentDictionaryString);
            this.localStorageService.set(StorageKeys.LAST_SELECTED_DICTIONARIES, currentDictionaryString);

            return result;
        };
    }

    metadictModule.service("dictionaryService", DictionaryService);
}