/// <reference path="../../App.ts" />

"use strict";

module MetadictApp {

    import ILogService = angular.ILogService;

    /**
     * Service which provides general UI related services.
     */
    export class GeneralUiService {
        // @ngInject
        constructor(private $log: ILogService) {
            $log.debug("GeneralUiService started");
        }

        /** Threshold for mobile screen widths. */
        private static MOBILE_SCREEN_WIDTH_THRESHOLD = 1366;

        /** Height of the header. */
        private static HEADER_HEIGHT = 64;

        /** Height of the Footer. */
        private static FOOTER_HEIGHT = 70;

        /**
         * Register important UI event handler.
         */
        public registerEventHandlers() {
            $(window).scroll(this.updateSidenavs);
            $(window).resize(this.updateSidenavs);
            this.onElementHeightChange($("main").get(), this.updateSidenavs);
        }

        /**
         * Recalculate the heights of the sidenav elements.
         */
        public updateSidenavs = () => {
            if (this.isMobileView()) {
                return;
            }

            const scrollTop = $(window).scrollTop();
            const windowHeight = $(window).height();
            let toBottom = $(document).height() - windowHeight - scrollTop;
            let newTop = (scrollTop > GeneralUiService.HEADER_HEIGHT) ? 0 : GeneralUiService.HEADER_HEIGHT - scrollTop;
            let bottomOffset = (toBottom > GeneralUiService.FOOTER_HEIGHT) ? 0 : GeneralUiService.FOOTER_HEIGHT - toBottom;
            let newHeight = windowHeight - newTop - bottomOffset;
            $("#menu-left").css("top", newTop + "px");
            $("#menu-left").css("height", newHeight + "px");
        };

        public isMobileView(): boolean {
            return window.innerWidth < GeneralUiService.MOBILE_SCREEN_WIDTH_THRESHOLD;
        }

        private onElementHeightChange(element, callback: Function) {
            let jqElement: JQuery = $(element);
            let lastHeight = jqElement.height(), newHeight;
            let onElementHeightChangeTimer: number;
            (function run() {
                newHeight = jqElement.height();
                if (lastHeight !== newHeight) {
                    callback();
                }
                lastHeight = newHeight;

                if (onElementHeightChangeTimer) {
                    clearTimeout(onElementHeightChangeTimer);
                }
                onElementHeightChangeTimer = setTimeout(run, 200);
            })();
        }
    }

    metadictModule.service("generalUiService", GeneralUiService);
}