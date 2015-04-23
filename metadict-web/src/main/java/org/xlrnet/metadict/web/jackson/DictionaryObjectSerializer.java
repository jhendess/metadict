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

package org.xlrnet.metadict.web.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.impl.util.FormatUtils;

import java.io.IOException;

/**
 * Custom serializer for {@link DictionaryObject} objects that adds a precalculated field with additional information
 * (i.e. additional forms, grammatical gender, etc) that can be used on clients to avoid performance bottlenecks.
 */
class DictionaryObjectSerializer extends BeanSerializerBase {

    protected static final String ADDITIONAL_REPRESENTATION_FIELD_NAME = "additionalRepresentation";

    public DictionaryObjectSerializer(BeanSerializerBase src, ObjectIdWriter objectIdWriter) {
        super(src, objectIdWriter);
    }

    protected DictionaryObjectSerializer(BeanSerializerBase src) {
        super(src);
    }

    public DictionaryObjectSerializer(DictionaryObjectSerializer src, String[] toIgnore) {
        super(src, toIgnore);
    }

    @Override
    public void serialize(Object bean, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        serializeFields(bean, jsonGenerator, serializerProvider);
        if (bean instanceof DictionaryObject) {
            String representation = FormatUtils.formatDictionaryObjectRepresentation((DictionaryObject) bean);
            if (StringUtils.isNotEmpty(representation))
                jsonGenerator.writeStringField(ADDITIONAL_REPRESENTATION_FIELD_NAME, representation);
        }
        jsonGenerator.writeEndObject();
    }

    @Override
    public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
        return new DictionaryObjectSerializer(this, objectIdWriter);
    }

    /**
     * See {@see BeanSerializer#asArraySerializer} implementation.
     */
    @Override
    protected BeanSerializerBase asArraySerializer() {
        if ((_objectIdWriter == null)
                && (_anyGetterWriter == null)
                && (_propertyFilterId == null)
                ) {
            return new BeanAsArraySerializer(this);
        }
        return this;
    }

    @Override
    protected BeanSerializerBase withFilterId(Object filterId) {
        throw new NotImplementedException(getClass().getCanonicalName() + ".withFilterId() is not implemented - avoid using @JsonFilter annotations");
    }

    @Override
    protected BeanSerializerBase withIgnorals(String[] strings) {
        return new DictionaryObjectSerializer(this, strings);
    }
}
