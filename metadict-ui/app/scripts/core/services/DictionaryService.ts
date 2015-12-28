///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IRootScopeService = angular.IRootScopeService;
    import ILocationService = angular.ILocationService;
    import ILocalStorageService = angular.local.storage.ILocalStorageService;

    /**
     * @inheritDoc
     */
    class DictionaryService implements IDictionaryService {

        // @ngInject
        constructor(private $log: ILogService, private backendAccessService: IBackendAccessService,
                    private $rootScope: IRootScopeService, private $location: ILocationService,
                    private localStorageService: ILocalStorageService) {
            this.initializeDictionaryConfiguration();
            $log.debug("DictionaryService started");
        }

        private _dictionaryListLoaded = false;

        private _dictionaryListLoading = false;

        private _bilingualDictionaries: BilingualDictionary[] = [];

        private _selectedDictionaries: string[] = [];

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
            _.filter(this._selectedDictionaries, (dictionaryId: string) => {
                return this.isDictionaryAvailable(dictionaryId);
            });

            this._dictionaryListLoaded = true;
            this._dictionaryListLoading = false;
        };

        /**
         * @inheritDoc
         */
        public isDictionaryListLoading(): boolean {
            return this._dictionaryListLoading;
        }

        /**
         * @inheritDoc
         */
        public isDictionaryListLoaded() {
            return this._dictionaryListLoaded;
        }

        /**
         * @inheritDoc
         */
        public reloadDictionaries() {
            this._dictionaryListLoading = true;
            this._dictionaryListLoaded = false;
            this.$log.debug("Reloading dictionaries...");
            this.backendAccessService.fetchBilingualDictionaries(this.reloadSuccessCallback, this.reloadErrorCallback);
        }

        /**
         * @inheritDoc
         */
        public getBilingualDictionaries(): MetadictApp.BilingualDictionary[] {
            return this._bilingualDictionaries;
        }

        /**
         * @inheritDoc
         */
        public toggleDictionarySelection(dictionaryIdentifier: string): boolean {
            return this.internalToggleDictionarySelection(dictionaryIdentifier, false);
        }

        /**
         * @inheritDoc
         */
        public isDictionarySelected(dictionaryIdentifier: string): boolean {
            return _.contains(this._selectedDictionaries, dictionaryIdentifier);
        }

        /**
         * @inheritDoc
         */
        public getDictionariesForSourceLanguage(languageIdentifer: string): BilingualDictionary[] {
            // TODO
            return undefined;
        }

        /**
         * @inheritDoc
         */
        public buildDictionaryString(dictionaries: MetadictApp.BilingualDictionary[]): string {
            return _.map(dictionaries, (d: BilingualDictionary) => d.queryStringWithDialect).join(",");
        }

        /**
         * @inheritDoc
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
         * @inheritDoc
         */
        public getCurrentDictionaryString(): string {
            return this._selectedDictionaries.join(Parameters.SEPARATOR);
        }

        public get selectedDictionaries(): string[] {
            return this._selectedDictionaries;
        }

        private initializeDictionaryConfiguration() {
            let dictionaryString = this.$location.search()[Parameters.DICTIONARIES];

            if (!dictionaryString) {
                dictionaryString = this.localStorageService.get(StorageKeys.LAST_SELECTED_DICTIONARIES);
            }

            this.initializeDictionariesFromParameters(dictionaryString);
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
                _.pull(this._selectedDictionaries, dictionaryIdentifier);
                result = false;
            } else {
                if (checkIfExisting && this.isDictionaryAvailable(dictionaryIdentifier)
                    || !checkIfExisting) {
                    this.$log.debug("Enabled dictionary " + dictionaryIdentifier + " for query");
                    this._selectedDictionaries.push(dictionaryIdentifier);
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