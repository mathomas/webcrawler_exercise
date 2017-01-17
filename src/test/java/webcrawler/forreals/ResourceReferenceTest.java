package webcrawler.forreals;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static webcrawler.forreals.ResourceReference.*;
import static webcrawler.forreals.ResourceReference.page;

public class ResourceReferenceTest {
    @Test
    public void isReportable() {
        assertTrue(page("http://something").isReportable());
        assertTrue(image("http://something").isReportable());
        assertTrue(image("/something.jpg").isReportable());
        assertTrue(image("something.jpg").isReportable());
        assertFalse(image("mailto:somebody").isReportable());
        assertFalse(page("#sometarget").isReportable());
        assertFalse(page("#sometarget").isReportable());
        assertFalse(page("").isReportable());
    }


    @Test
    public void isLocal() {
        assertTrue(page("http://local.domain.com/somepage").isLocal("http://local.domain.com"));
        assertFalse(page("http://foreign.domain.com/somepage").isLocal("http://local.domain.com"));
        assertTrue(page("/somepage").isLocal("http://local.domain.com"));
    }

    @Test
    public void isPage() {
        assertTrue(page("/somepage").isPage());
        assertTrue(page("somepage").isPage());
    }
}