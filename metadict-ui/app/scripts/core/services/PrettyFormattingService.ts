///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    class PrettyFormattingService implements IPrettyFormattingService {

        // @ngInject
        constructor(private $log: ILogService) {
            $log.debug("PrettyFormattingService started");
        }

        private static SYLLABIFICATION_JOIN_CHARACTER: string = "|";

        /** Map with abbreviations for supported grammatical genders. **/
        private static GENDER_ABBREVIATION_MAP: Map<string> = {

            "masculine": "masc.",

            "feminine": "fem.",

            "neuter": "neut.",

            "natural": "nat."
        };

        /**
         * @inheritDoc
         */
        public formatGrammaticalGender(gender: string): string {
            let lowercaseGender = gender.toLowerCase();

            if (PrettyFormattingService.GENDER_ABBREVIATION_MAP[lowercaseGender]) {
                return PrettyFormattingService.GENDER_ABBREVIATION_MAP[lowercaseGender];
            } else {
                return lowercaseGender;
            }
        }

        /**
         * @inheritDoc
         */
        public formatEntryType(entryType: string): string {
            let lowercaseEntryType = entryType.toLowerCase();
            return lowercaseEntryType.replace(/\_/, " ");
        }

        /**
         * @inheritDoc
         */
        public formatSyllabificationList(syllabificationList: string[]): string {
            return syllabificationList.join(PrettyFormattingService.SYLLABIFICATION_JOIN_CHARACTER);
        }
    }

    metadictModule.service("prettyFormattingService", PrettyFormattingService);

}