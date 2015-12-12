/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    export class SelectDictionariesMenuDirective implements ng.IDirective {

        public templateUrl = "views/selectDictionariesMenu.html";

        public restrict = "E";
    }

    export function SelectDictionariesMenuFactory() {
        return new SelectDictionariesMenuDirective();
    }


    metadictModule.directive("selectDictionariesMenu", SelectDictionariesMenuFactory);
}

