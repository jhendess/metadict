
"use strict";

module MetadictApp {

    /**
     * This configuration class will be automatically generated. Do not edit the .ts file but only the *.ts.tpl file.
     */
    export class Config {

        public static API_URL = "<%= config.apiUrl %>";

        public static CLIENT_VERSION = "<%= pkg.version %>";

        public static CLIENT_REVISION = "<%= revision %>";

        public static CLIENT_BASE_PATH = "<%= clientBasePath %>";
    }
}