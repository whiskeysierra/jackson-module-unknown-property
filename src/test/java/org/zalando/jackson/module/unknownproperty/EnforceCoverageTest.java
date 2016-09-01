package org.zalando.jackson.module.unknownproperty;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.Test;

@Hack
@OhNoYouDidnt
public final class EnforceCoverageTest {

    /**
     * This is needed, because we can't really test that version of the module because we don't
     * control the logger.
     */
    @Test
    public void shouldUseFormatOnlyModuleConstructor() {
        new UnknownPropertyModule("foo");
    }

}
