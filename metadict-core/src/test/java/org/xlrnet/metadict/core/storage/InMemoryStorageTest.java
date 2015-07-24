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

package org.xlrnet.metadict.core.storage;

import com.google.common.base.Objects;
import org.junit.Before;
import org.junit.Test;
import org.xlrnet.metadict.api.storage.StorageOperationException;
import org.xlrnet.metadict.api.storage.StorageService;

import java.io.Serializable;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests for ensuring correct function of {@link InMemoryStorage}.
 */
public class InMemoryStorageTest implements Serializable {

    private static final long serialVersionUID = 5595069952682611649L;

    StorageService storageService;

    DummyStorageObject dummyStorageObject2 = new DummyStorageObject(
            "some_String2",
            -1,
            1.234,
            null
    );

    DummyStorageObject dummyStorageObject = new DummyStorageObject(
            "some_String",
            42,
            3.14,
            dummyStorageObject2
    );

    @Before
    public void setUp() throws Exception {
        storageService = new InMemoryStorage();
    }

    @Test
    public void testCreate_simple() throws Exception {
        DummyStorageObject returnedValue = storageService.create("namespace", "key", this.dummyStorageObject);
        assertSame(dummyStorageObject, returnedValue);
    }

    @Test(expected = StorageOperationException.class)
    public void testCreate_existing() throws Exception {
        storageService.create("namespace", "key", this.dummyStorageObject);
        storageService.create("namespace", "key", this.dummyStorageObject);
    }

    @Test
    public void testCreate_differentNamespaces() throws Exception {
        DummyStorageObject dummyStorageObject1 = storageService.create("namespace1", "key", this.dummyStorageObject);
        DummyStorageObject dummyStorageObject2 = storageService.create("namespace2", "key", this.dummyStorageObject);
        assertSame(this.dummyStorageObject, dummyStorageObject1);
        assertSame(this.dummyStorageObject, dummyStorageObject2);
    }

    @Test
    public void testDelete_notExisting() throws Exception {
        assertFalse(storageService.delete("namespace", "key"));
    }

    @Test
    public void testDelete_existing() throws Exception {
        storageService.create("namespace", "key", this.dummyStorageObject);
        assertTrue(storageService.delete("namespace", "key"));
        Optional<DummyStorageObject> read = storageService.read("namespace", "key", DummyStorageObject.class);
        assertFalse(read.isPresent());
    }

    @Test
    public void testDelete_multiNamespaces() throws Exception {
        storageService.create("namespace1", "key", this.dummyStorageObject);
        storageService.create("namespace2", "key", this.dummyStorageObject);
        assertTrue(storageService.delete("namespace1", "key"));
        Optional<DummyStorageObject> read = storageService.read("namespace2", "key", DummyStorageObject.class);
        assertTrue(read.isPresent());
    }

    @Test
    public void testRead_notExisting() throws Exception {
        Optional<DummyStorageObject> read = storageService.read("namespace", "key", DummyStorageObject.class);
        assertFalse(read.isPresent());
    }

    @Test
    public void testRead_existing() throws Exception {
        storageService.create("namespace", "key", this.dummyStorageObject);
        Optional<DummyStorageObject> read = storageService.read("namespace", "key", DummyStorageObject.class);
        assertTrue(read.isPresent());
        assertNotSame("Object should have been clone, but wasn't", read.get(), this.dummyStorageObject);
    }

    @Test(expected = ClassCastException.class)
    public void testRead_classCast() throws Exception {
        storageService.create("namespace", "key", this.dummyStorageObject);
        storageService.read("namespace", "key", Exception.class);
    }

    @Test(expected = StorageOperationException.class)
    public void testUpdate_notExisting() throws Exception {
        storageService.update("namespace", "key", this.dummyStorageObject);
    }

    @Test
    public void testContains_existing() throws Exception {
        storageService.put("namespace", "key", this.dummyStorageObject);
        assertEquals(true, storageService.containsKey("namespace", "key"));
    }

    @Test
    public void testContains_deleted() throws Exception {
        storageService.put("namespace", "key", this.dummyStorageObject);
        storageService.delete("namespace", "key");
        assertEquals(false, storageService.containsKey("namespace", "key"));
    }

    @Test
    public void testPut_existing() throws Exception {
        storageService.create("namespace", "key", this.dummyStorageObject);
        storageService.update("namespace", "key", this.dummyStorageObject2);
        Optional<DummyStorageObject> read = storageService.read("namespace", "key", DummyStorageObject.class);
        assertTrue(read.isPresent());
        assertEquals(read.get(), this.dummyStorageObject2);
    }

    @Test
    public void testPut_notExisting() throws Exception {
        storageService.put("namespace", "key", this.dummyStorageObject);
        Optional<DummyStorageObject> read = storageService.read("namespace", "key", DummyStorageObject.class);
        assertTrue(read.isPresent());
        assertNotSame("Object should have been clone, but wasn't", read.get(), this.dummyStorageObject);
    }

    @Test
    public void testUpdate_existing() throws Exception {
        storageService.create("namespace", "key", this.dummyStorageObject);
        storageService.update("namespace", "key", this.dummyStorageObject2);
        Optional<DummyStorageObject> read = storageService.read("namespace", "key", DummyStorageObject.class);
        assertTrue(read.isPresent());
        assertEquals(this.dummyStorageObject2, read.get());
    }

    class DummyStorageObject implements Serializable {

        private static final long serialVersionUID = 3301156091855162058L;

        String someString;
        int someInt;
        double someFloat;
        DummyStorageObject anotherDummyStorage;

        public DummyStorageObject(String someString, int someInt, double someFloat, DummyStorageObject anotherDummyStorage) {
            this.someString = someString;
            this.someInt = someInt;
            this.someFloat = someFloat;
            this.anotherDummyStorage = anotherDummyStorage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DummyStorageObject)) return false;
            DummyStorageObject that = (DummyStorageObject) o;
            return Objects.equal(someInt, that.someInt) &&
                    Objects.equal(someFloat, that.someFloat) &&
                    Objects.equal(someString, that.someString) &&
                    Objects.equal(anotherDummyStorage, that.anotherDummyStorage);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(someString, someInt, someFloat, anotherDummyStorage);
        }

    }
}