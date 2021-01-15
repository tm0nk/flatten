package org.example;

import com.google.gson.JsonIOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FlattenTest {

    private static final String RESOURCES = "src/test/resources/";

    @Test
    void testFixture1() throws IOException {
        runTest("fixture1.json", new HashSet<>(Arrays.asList("a", "b", "c.d", "c.e")));
    }

    @Test
    void testFixture2() throws IOException {
        runTest("fixture2.json", Collections.emptySet());
    }

    @Test
    void testFixture3() throws IOException {
        runTest("fixture3.json",
                new HashSet<>(Arrays.asList("a", "b", "c.d", "c.e", "f.g.h.i.j.k.l.m.n")));
    }

    @Test
    void testFixture4() {
        Exception exception = Assertions.assertThrows(RuntimeException.class,
                () -> runTest("fixture4.json", Collections.emptySet()));
        Assertions.assertTrue(exception.getMessage().contains("Expected JsonObject but got"));
    }

    @Test
    void testFixture5() {
        Exception exception = Assertions.assertThrows(JsonIOException.class,
                () -> runTest("fixture5.json", Collections.emptySet()));
        Assertions.assertTrue(exception.getMessage().contains("End of input at line"));
    }

    private void runTest(String path, Set<String> expectedKeySet) throws IOException {
        Set<String> actualKeySet = Flatten.parse(new FileInputStream(RESOURCES + path)).entrySet()
                .stream().map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedKeySet.size(), actualKeySet.size());
        Assertions.assertTrue(expectedKeySet.containsAll(actualKeySet));
    }

}
