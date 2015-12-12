///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    /**
     * @inheritDoc
     */
    class DictionaryService implements IDictionaryService {

        // @ngInject
        constructor(private $log: ILogService, private backendAccessService: IBackendAccessService) {
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
            if (this.isDictionarySelected(dictionaryIdentifier)) {
                this.$log.debug("Disabled dictionary " + dictionaryIdentifier + " for query");
                _.pull(this._selectedDictionaries, dictionaryIdentifier);
                return false;
            } else {
                if (this.isDictionaryAvailable(dictionaryIdentifier)) {
                    this.$log.debug("Enabled dictionary " + dictionaryIdentifier + " for query");
                    this._selectedDictionaries.push(dictionaryIdentifier);
                } else {
                    this.$log.debug("Couldn't select unknown dictionary " + dictionaryIdentifier);
                }
                return false;
            }
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
        public getDictionariesForInputLanguage(languageIdentifer: string): MetadictApp.BilingualDictionary[] {
            return undefined;
        }

        private isDictionaryAvailable(dictionaryIdentifier: string): boolean {
            return _.findIndex(this._bilingualDictionaries, (bilingualDictionary: BilingualDictionary) =>
                bilingualDictionary.queryString === dictionaryIdentifier
                || bilingualDictionary.queryStringWithDialect === dictionaryIdentifier
            ) > -1;
        }


        private updateDictionaryIndices(dictionaries: BilingualDictionary[]) {
            // TODO
        }
    }

    metadictModule.service("dictionaryService", DictionaryService);
}