///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import IScope = angular.IScope;
    import IComponentOptions = angular.IComponentOptions;

    class DictionaryObjectComponent implements IComponentOptions {

        public templateUrl = "views/dictionaryObject.html";

        public restrict = "E";

        public scope = {
            entry: "=",
            success: "=success",
            error: "=error"
        };
    }

    metadictModule.component("dictionaryObject", new DictionaryObjectComponent());
}