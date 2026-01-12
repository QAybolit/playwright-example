package tests.codeexamples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import tests.BaseTest;

public class PlaywrightSetupTest extends BaseTest {

    @Test
    public void playwrightSetupTest() {
        page.navigate("https://example.com");
        String title = page.title();
        assertEquals("Example Domain", title);
    }

}
