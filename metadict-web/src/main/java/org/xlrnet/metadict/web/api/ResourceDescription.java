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

package org.xlrnet.metadict.web.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Description for a single REST resource. Contains the base path and all supported methods of a resource.
 */
public class ResourceDescription {

    private String basePath;

    private List<MethodDescription> calls;

    public ResourceDescription(String basePath) {
        this.basePath = basePath;
        this.calls = Lists.newArrayList();
    }

    private static String mostPreferredOrNull(List<WeightedMediaType> preferred) {
        if (preferred.isEmpty()) {
            return null;
        } else {
            return preferred.get(0).toString();
        }
    }

    public static List<ResourceDescription> fromBoundResourceInvokers(Set<Map.Entry<String, List<ResourceInvoker>>> bound) {
        Map<String, ResourceDescription> descriptions = Maps.newHashMap();

        for (Map.Entry<String, List<ResourceInvoker>> entry : bound) {
            ResourceMethodInvoker aMethod = (ResourceMethodInvoker) entry.getValue().get(0);
            String basePath = aMethod.getMethod().getDeclaringClass().getAnnotation(Path.class).value();
            String thisPath = Optional.ofNullable(aMethod.getMethod()
                    .getAnnotation(Path.class))
                    .map(a -> basePath + a.value())
                    .orElse(basePath);

            if (!descriptions.containsKey(thisPath)) {
                descriptions.put(thisPath, new ResourceDescription(thisPath));
            }

            for (ResourceInvoker invoker : entry.getValue()) {
                ResourceMethodInvoker methodinvoker = (ResourceMethodInvoker) invoker;
                descriptions.get(thisPath).addMethod(basePath, methodinvoker);
            }
        }

        return Lists.newLinkedList(descriptions.values());
    }

    public void addMethod(String path, ResourceMethodInvoker method) {
        for (String verb : method.getHttpMethods()) {
            calls.add(new MethodDescription(verb, path, method.getProduces(), method.getConsumes()));
        }
    }

    public String getBasePath() {
        return basePath;
    }

    public List<MethodDescription> getCalls() {
        return calls;
    }
}
