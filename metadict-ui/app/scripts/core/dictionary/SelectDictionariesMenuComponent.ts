/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import IComponentOptions = angular.IComponentOptions;

    export class SelectDictionariesMenuComponent implements IComponentOptions {

        public templateUrl = "views/selectDictionariesMenu.html";

        public restrict = "E";
    }

    metadictModule.component("selectDictionariesMenu", new SelectDictionariesMenuComponent());
}

