/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * Construct a new client to access a Metadict REST API.
 *
 * @param baseUrl The base URL that will preceed each request.
 * @constructor
 */
function MetadictClient(baseUrl) {

    var baseUrl = (baseUrl != undefined) ? baseUrl : "";

    var self = this;

    this._invokeRequest = function (targetUrl, success, fail) {
        $.ajax({
            dataType: "json",
            url: baseUrl + targetUrl,
            success: function (data) {
                console.log("Query success!");
                success(data)
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Query failed");
                fail((errorThrown != undefined) ? errorThrown : textStatus);
            }
        });
    };

    /**
     * Send a query with a given queryString and given dictionaries to the Metadict instance.
     * @param queryString The query string that should be looked up.
     * @param dictionaries An array of dictionary query strings.
     */
    this.query = function (queryString, dictionaries, success, fail) {
        var queryString = encodeURIComponent(queryString);
        var dictionaryString = dictionaries.join(",");
        var path = "/query/" + dictionaryString + "/" + queryString

        console.log("Querying entries from " + path + " ...");
        this._invokeRequest(path, success, fail);
    };

    this.getBidirectedDictionaries = function (success, fail) {
        console.log("Querying for bidirected dictionaries...");
        this._invokeRequest("/dict/bi", success, fail);
    };

    this.getUnidirectedDictionaries = function (success, fail) {
        console.log("Querying for unidirected dictionaries...");
        this._invokeRequest("/dict/uni", success, fail);
    };

    this.getAllDictionaries = function (success, fail) {
        console.log("Querying for all dictionaries...");
        this._invokeRequest("/dict/all", success, fail);
    }
}
