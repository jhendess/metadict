/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    // Extend the $exceptionHandler service to also display a toast.
    export function extendedExceptionHandler($delegate: ng.IExceptionHandlerService,
                                             modalPopupService: ModalPopupService, $log: ILogService) {
        $log.debug("Configuring extendedExceptionHandler");

        return function (exception: Error, cause: string) {
            $delegate(exception, cause);
            modalPopupService.displayErrorPopup();
        };
    }
}