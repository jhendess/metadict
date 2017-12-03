///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;

    interface ISelectDictionariesScope extends IScope {

        loadingDictionaries: boolean;

        availableDictionaries: BilingualDictionary[];

        selectionMap: Map<boolean>;
    }

    class SelectDictionariesMenuController {
        // @ngInject
        constructor(private $log: ILogService, private $scope: ISelectDictionariesScope,
                    private dictionaryService: DictionaryService) {
            this.initializeSelectionMap();

            $scope.$watch(
                () => dictionaryService.isDictionaryListLoading(),
                (newValue: boolean) => $scope.loadingDictionaries = newValue
            );

            $scope.$watch(
                () => dictionaryService.isDictionaryListLoaded(),
                () => {
                    $scope.availableDictionaries = dictionaryService.getBilingualDictionaries();
                    this.initializeSelectionMap();
                }
            );

            // Force update of selected dictionaries if a click query modified the selected dictionaries
            $scope.$on(CoreEvents.DICTIONARY_SELECTION_CHANGE_BY_CLICK_QUERY,
                () => {
                    this.initializeSelectionMap();
                });

            $log.debug("SelectDictionariesMenuController started");
        }

        public toggleSelection(dictionaryIdentifier: string) {
            this.dictionaryService.toggleDictionarySelection(dictionaryIdentifier);
        }

        public buildIconClass(language: Language): string {
            return this.dictionaryService.buildIconClass(language);
        }

        private initializeSelectionMap() {
            this.$log.debug("Initializing selected dictionaries menu");
            this.$scope.selectionMap = {};
            for (let dictionaryId of this.dictionaryService.selectedDictionaryIds) {
                this.$scope.selectionMap[dictionaryId] = true;
            }
        };
    }

    metadictModule.controller("SelectDictionariesMenuController", SelectDictionariesMenuController);
}