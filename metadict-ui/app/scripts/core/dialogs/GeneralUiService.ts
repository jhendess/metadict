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
            const scrollTop = $(window).scrollTop();
            const windowHeight = $(window).height();
            let toBottom = $(document).height() - windowHeight - scrollTop;
            let newTop = (scrollTop > 64) ? 0 : 64 - scrollTop;
            let bottomOffset = (toBottom > 70) ? 0 : 70 - toBottom;
            let newHeight = windowHeight - newTop - bottomOffset;
            $(".side-nav").css("top", newTop + "px");
            $(".side-nav").css("height", newHeight + "px");
        };

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