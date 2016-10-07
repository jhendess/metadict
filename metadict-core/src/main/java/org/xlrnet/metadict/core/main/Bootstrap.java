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

package org.xlrnet.metadict.core.main;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Short sample of how a Metadict core could be started in a Java SE environment.
 */
public class Bootstrap {

    public static void main(String... args) {
        Weld weld = new Weld();

        WeldContainer container = weld.initialize();

        MetadictCore core = container.instance().select(MetadictCore.class).get();
        final EngineRegistryService engineRegistryService = core.getEngineRegistryService();
        /*for (String engineName : engineRegistry.getRegisteredEngineNames()) {
            System.out.println(engineName);
            System.out.println(engineRegistry.getEngineDescriptionByName(engineName));
            System.out.println(engineRegistry.getFeatureSetByName(engineName));
            QueryResponse queryResponse = core.createNewQueryRequestBuilder().setQueryString("foobar").addQueryDictionary(Dictionary.fromLanguages(Language.ENGLISH, Language.GERMAN, true)).build().executeRequest();
            System.out.println(queryResponse);
        }*/


        weld.shutdown();
    }

}
