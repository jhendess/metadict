///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    import IScope = angular.IScope;

    interface IDictionaryObjectDirectiveScope {

        entry: DictionaryObject;
    }

    class DictionaryObjectDirective implements ng.IDirective {

        public templateUrl = "views/dictionaryObject.html";

        public restrict = "E";

        public scope: IDictionaryObjectDirectiveScope = {
            entry: <any>"="
        };

        public link(scope: IDictionaryObjectDirectiveScope) {
        }
    }

    function DictionaryObjectFactory() {
        return new DictionaryObjectDirective();
    }

    metadictModule.directive("dictionaryObject", DictionaryObjectFactory);
}