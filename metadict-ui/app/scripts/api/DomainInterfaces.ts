///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    export interface ResponseContainer<T> {

        status: string;

        message: string;

        data: T;
    }

    export interface BilingualDictionary {

        input: Language;

        target: Language;

        bidirectional: boolean;

        queryString: string;

        queryStringWithDialect: string;
    }

    export interface Language {

        identifier: string;

        displayName: string;

        dialect: string;

        dialectDisplayName: string;
    }

    export interface QueryResponse {

        externalContents: ExternalContent[];

        groupedBilingualEntries: BilingualResultGroup[];

        groupingType: string;

        monolingualEntries: MonolingualEntry[];

        performanceStatistics?: any;

        requestString: string;

        similarRecommendations: DictionaryObject;

        synonymEntries: SynonymEntry[];
    }

    export interface DictionaryObject {

        abbreviation?: string;

        additionalForms: Map<string>;

        description?: string;

        domain?: string;

        generalForm: string;

        grammaticalGender: string;

        language: Language;

        pronunciation?: string;

        meanings?: string[];

        syllabification?: string[];

        alternateForms?: string[];
    }

    export interface Entry {

        entryType: string;
    }

    export interface MonolingualEntry extends Entry {

        content: DictionaryObject;
    }

    export interface BilingualEntry extends Entry {

        entryScore: number;

        sourceEngine: string;

        source: DictionaryObject;

        target: DictionaryObject;
    }

    export interface SynonymEntry {

        baseEntryType: string;

        baseObject: DictionaryObject;

        synonymGroups: SynonymGroup[];
    }

    export interface SynonymGroup {

        baseMeaning: DictionaryObject;

        synonyms: DictionaryObject[];
    }

    export interface BilingualResultGroup {

        groupIdentifier: string;

        resultEntries: BilingualEntry;
    }

    export interface ExternalContent {

        description: string;

        link: string;

        title: string;
    }
}