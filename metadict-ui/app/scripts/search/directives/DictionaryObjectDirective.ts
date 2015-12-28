///<reference path="../../App.ts"/>

"use strict";

module MetadictApp {

    interface IDictionaryObjectDirectiveScope {

        entry: DictionaryObject;
    }

    class DictionaryObjectDirective implements ng.IDirective {

        public templateUrl = "views/dictionaryObject.html";

        public restrict = "E";

        public scope: IDictionaryObjectDirectiveScope = {
            entry: <any>"="
        };
    }

    function DictionaryObjectFactory() {
        return new DictionaryObjectDirective();
    }

    metadictModule.directive("dictionaryObject", DictionaryObjectFactory);
}