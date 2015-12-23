///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;
    import IScope = angular.IScope;
    import Dictionary = _.Dictionary;

    interface ISelectDictionariesScope extends IScope {

        loadingDictionaries: boolean;

        availableDictionaries: BilingualDictionary[];

        selectionMap: Dictionary<boolean>;
    }

    class SelectDictionariesMenuController {
        // @ngInject
        constructor(private $log: ILogService, private $scope: ISelectDictionariesScope,
                    private dictionaryService: IDictionaryService) {
            $scope.selectionMap = {};

            $scope.$watch(
                () => dictionaryService.isDictionaryListLoading(),
                (newValue: boolean) => $scope.loadingDictionaries = newValue
            );

            $scope.$watch(
                () => dictionaryService.isDictionaryListLoaded(),
                () => $scope.availableDictionaries = dictionaryService.getBilingualDictionaries()
            );

            $log.debug("SelectDictionariesMenuController started");
        }

        public toggleSelection(dictionaryIdentifier: string) {
            this.$scope.selectionMap[dictionaryIdentifier] = !this.$scope.selectionMap[dictionaryIdentifier];
            this.dictionaryService.toggleDictionarySelection(dictionaryIdentifier);
        }

        public buildIconClass(language: Language) {
            let identifier = language.identifier;
            if (identifier === "en") {
                identifier = "gb";
            } else if (identifier === "se") {
                identifier = "sv";
            }

            return "flag-icon-" + identifier;
        }
    }

    metadictModule.controller("SelectDictionariesMenuController", SelectDictionariesMenuController);
}