/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    /**
     * Simple validator directive that can be used for comparing to values. See
     * http://odetocode.com/blogs/scott/archive/2014/10/13/confirm-password-validation-in-angularjs.aspx for more
     * information.
     */
    let compareTo = function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=compareTo"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.compareTo = function (modelValue) {
                    return modelValue == scope.otherModelValue.$modelValue;
                };

                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    };

    metadictModule.directive("compareTo", compareTo);
}