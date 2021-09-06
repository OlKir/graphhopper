package com.graphhopper.routing.util;

import com.graphhopper.routing.ev.EnumEncodedValue;
import com.graphhopper.routing.ev.RoadClass;
import com.graphhopper.routing.ev.RoadEnvironment;
import com.graphhopper.storage.IntsRef;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.GHUtility;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class SnapPreventionEdgeFilterTest {

    @Test
    public void accept() {
        EdgeFilter trueFilter = new EdgeFilter() {
            @Override
            public boolean accept(EdgeIteratorState edgeState) {
                return true;
            }
        };
        EncodingManager em = new EncodingManager.Builder().build();
        EnumEncodedValue<RoadClass> rcEnc = em.getEnumEncodedValue(RoadClass.KEY, RoadClass.class);
        EnumEncodedValue<RoadEnvironment> reEnc = em.getEnumEncodedValue(RoadEnvironment.KEY, RoadEnvironment.class);
        SnapPreventionEdgeFilter filter = new SnapPreventionEdgeFilter(trueFilter, rcEnc, reEnc, Arrays.asList("motorway", "ferry"));

        IntsRef intsRef = em.createEdgeFlags();
        assertTrue(filter.accept(GHUtility.createMockedEdgeIteratorState(1, intsRef)));
        reEnc.setEnum(false, intsRef, RoadEnvironment.FERRY);
        assertFalse(filter.accept(GHUtility.createMockedEdgeIteratorState(1, intsRef)));
        reEnc.setEnum(false, intsRef, RoadEnvironment.FORD);
        assertTrue(filter.accept(GHUtility.createMockedEdgeIteratorState(1, intsRef)));

        rcEnc.setEnum(false, intsRef, RoadClass.RESIDENTIAL);
        assertTrue(filter.accept(GHUtility.createMockedEdgeIteratorState(1, intsRef)));
        rcEnc.setEnum(false, intsRef, RoadClass.MOTORWAY);
        assertFalse(filter.accept(GHUtility.createMockedEdgeIteratorState(1, intsRef)));
    }
}