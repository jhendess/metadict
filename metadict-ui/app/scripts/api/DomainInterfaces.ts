///<reference path="../App.ts"/>

"use strict";

module MetadictApp {

    /**
     * Main container which wraps wrap request results.
     */
    export interface ResponseContainer<T> {

        /** Short status code of the response. */
        status: string;

        /** Detailed message of the response. */
        message: string;

        /** Wrapped data inside the container. */
        data: T;

        /** Links to further result sets. */
        links: Link[];
    }

    /** A link to a resource. */
    export interface Link {

        /** The relationship between this link and the original resource. */
        rel: string;

        /** Location of the linked resource. */
        href: string;
    }

    export interface BilingualDictionary {

        source: Language;

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

        additionalForms?: Map<string>;

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

    /**
     * Information about the current backend status like build version, etc.
     */
    export interface SystemStatus {

        /** Version of the backend system. */
        version: string;

        /** SCM revision that was used to build the backend system. */
        revision: string;

        /** Time when the backend was built. */
        buildTime: string;

        /** Time when the backend was last started. */
        startTime: string;

        /** Uptime since the last start of the backend. */
        uptime: string;

    }

    /**
     * Contains information about the current active user session.
     */
    export interface UserSession {
        /** Name of the logged in user. */
        name: string;
    }

    /**
     * Credentials used for logging in on the Metadict backend.
     */
    export interface Credentials {
        /** Login name. */
        name: string;
        /** Password for logging in. */
        password: string;
        /** Flag which indicates if the user session should be long-running. */
        stayLoggedIn: boolean;
    }

    /**
     * Interface which contains a registration request to the Metadict server.
     */
    export interface RegistrationData {
        /**
         * The username which should be used for registration.
         */
        name: string;
        /**
         * The password for logging in.
         */
        password: string;
        /**
         * The confirmation of the password.
         */
        confirmPassword: string;
    }

    /**
     * Single entry of the query log.
     */
    export interface QueryLogEntry {

        /**
         * The query sent to the backend.
         */
        queryString: string;

        /**
         * Time when the request was sent.
         */
        requestTime: string;

        /**
         * Dictionaries used for querying.
         */
        dictionaries: BilingualDictionary[];

        /**
         * Languages used for querying.
         */
        languages: Language[];
    }
}