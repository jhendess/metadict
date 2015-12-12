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

        output: Language;

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
}