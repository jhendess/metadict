///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    /**
     * Service for formatting various data nicely for the user. This includes also e.g. abbreviating certain phrases,
     * so that the user can read them more easily.
     */
    export class PrettyFormattingService{

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
         * Format an input string which shall be used as the grammatical gender of an entry. This method will abbreviate
         * the input to respectively either "masc.", "fem.", "neut." or "nat.". If abbreviation is not possible, the string
         * will be only lowercased.
         *
         * @param gender The grammatical gender to format.
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
         * Format an input string which shall be used as the entry type of an entry. All inputs will be lowercased and
         * underscores (if any) will be replaced by spaces.
         *
         * @param entryType The entry type to format.
         */
        public formatEntryType(entryType: string): string {
            let lowercaseEntryType = entryType.toLowerCase();
            return lowercaseEntryType.replace(/\_/, " ");
        }

        /**
         * Takes an array of strings which represent the syllabification of a word and merges them into a single string.
         *
         * @param syllabificationList
         */
        public formatSyllabificationList(syllabificationList: string[]): string {
            return syllabificationList.join(PrettyFormattingService.SYLLABIFICATION_JOIN_CHARACTER);
        }
    }

    metadictModule.service("prettyFormattingService", PrettyFormattingService);

}