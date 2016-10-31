/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    declare var $;

    export class ModalPopupService {
        constructor(private $log: ILogService) {
            // Initialize all modals
            $("#errorModal").modal({
                in_duration: 300, // Transition in duration
                out_duration: 200, // Transition out duration
            });

            $log.debug("ModalPopupService started");
        }

        public displayPopup(title: string, message: string) {
            // TODO: Implement when necessary
        };

        public displayErrorPopup() {
            $("#errorModal").modal("open");
        }
    }

    metadictModule.service("modalPopupService", ModalPopupService);
}