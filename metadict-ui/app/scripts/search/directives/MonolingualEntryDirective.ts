///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    interface IMonolingualEntryDirectiveScope {

        entry: MonolingualEntry;

        syllabification?: string;

        additionalHeader?: string;

        iconClass?: string;
    }

    class MonolingualEntryDirective implements ng.IDirective {

        constructor(private $log: ILogService, private prettyFormattingService: PrettyFormattingService,
                    private dictionaryService: DictionaryService) {
            this.$log.debug("MonolingualEntryDirective started");
        }

        public templateUrl = "views/monolingualEntry.html";

        public restrict = "E";

        public scope: IMonolingualEntryDirectiveScope = {
            entry: <any>"="
        };

        public link = (scope: IMonolingualEntryDirectiveScope) => {
            this.buildAdditionalHeader(scope);
            scope.iconClass = this.dictionaryService.buildIconClass(scope.entry.content.language);
            scope.syllabification = this.prettyFormattingService.formatSyllabificationList(scope.entry.content.syllabification);
        };

        private buildAdditionalHeader(scope: IMonolingualEntryDirectiveScope) {
            let tempAdditionalHeader = [];
            if (scope.entry.entryType) {
                tempAdditionalHeader.push(
                    this.prettyFormattingService.formatEntryType(scope.entry.entryType)
                );
            }
            if (scope.entry.content.grammaticalGender) {
                tempAdditionalHeader.push(
                    this.prettyFormattingService.formatGrammaticalGender(scope.entry.content.grammaticalGender)
                );
            }

            scope.additionalHeader = `(${tempAdditionalHeader.join(", ")})`;
        }
    }

    // @ngInject
    function MonolingualEntryFactory($log: ILogService, prettyFormattingService: PrettyFormattingService,
                                     dictionaryService: DictionaryService) {
        return new MonolingualEntryDirective($log, prettyFormattingService, dictionaryService);
    }

    metadictModule.directive("monolingualEntry", MonolingualEntryFactory);
}