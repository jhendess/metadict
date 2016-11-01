///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import IScope = angular.IScope;

    interface IDictionaryObjectDirectiveScope extends IScope {

        entry: DictionaryObject;
    }

    class DictionaryObjectDirective implements ng.IDirective {

        public templateUrl = "views/dictionaryObject.html";

        public restrict = "E";

        public scope = {
            entry: "=",
            success: "=success",
            error: "=error"
        };
    }

    function DictionaryObjectFactory() {
        return new DictionaryObjectDirective();
    }

    metadictModule.directive("dictionaryObject", DictionaryObjectFactory);
}