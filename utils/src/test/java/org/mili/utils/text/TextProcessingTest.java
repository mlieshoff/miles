package org.mili.utils.text;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TextProcessingTest {

    private static final String TEXT = "TEST 1 2 3 TEST TEST 123: Fischer Fritze fischt frische Fische! TEST";

    private static List<String> actual = new ArrayList<>();
    private List<String> expected = new ArrayList<>();

    private TextProcessing.Builder _unitUnderTest;

    @Before
    public void setUp() {
        _unitUnderTest = TextProcessing.builder();
    }

    @After
    public void tearDown() {
        actual.clear();
        expected.clear();
    }

    @Test
    public void shouldFindBetweenSame() {
        _unitUnderTest = _unitUnderTest.one("TEST", "TEST", new TestAction());
        run(TEXT, " 1 2 3 ");
    }

    @Test
    public void shouldFindBetweenSameOne() {
        _unitUnderTest = _unitUnderTest.one("1", "3", new TestAction());
        run(TEXT, " 2 ");
    }

    @Test
    public void shouldFindMultiBetweenSameOne() {
        _unitUnderTest = _unitUnderTest.multi("1", "3", new TestAction());
        run(TEXT, " 2 ", "2");
    }

    @Test
    public void shouldFindBetweenDifferent() {
        _unitUnderTest = _unitUnderTest.one("TEST", "123", new TestAction());
        run(TEXT, " 1 2 3 TEST TEST ");
    }

    @Test
    public void shouldFindMultiBetweenSame() {
        _unitUnderTest = _unitUnderTest.multi("TEST", "TEST", new TestAction());
        run(TEXT,
                " 1 2 3 ",
                " 123: Fischer Fritze fischt frische Fische! "
        );
    }

    @Test
    public void shouldChain() {
        _unitUnderTest = _unitUnderTest.multi("TEST", "TEST", new TestAction());
        _unitUnderTest = _unitUnderTest.chain("1", "3", new TestAction());
        run(TEXT,
                " 1 2 3 ",
                " 2 ",
                " 123: Fischer Fritze fischt frische Fische! ",
                "2"
        );
    }

    @Test
    public void shouldChainMulti() {
        _unitUnderTest = _unitUnderTest.multi("TEST", "TEST", new TestAction());
        _unitUnderTest = _unitUnderTest.chainMulti(" ", " ", new TestAction());
        run(TEXT,
                " 1 2 3 ",
                "1",
                "2",
                "3",
                " 123: Fischer Fritze fischt frische Fische! ",
                "123:",
                "Fischer",
                "Fritze",
                "fischt",
                "frische",
                "Fische!"
        );
    }

    private void run(String source, String... expectations) {
        expected.addAll(Arrays.asList(expectations));
        _unitUnderTest.build().start(source);
        assertEquals(expected, actual);
    }

    private static class TestAction implements Action {
        @Override
        public void execute(String sub) {
            actual.add(sub);
        }
    }

}