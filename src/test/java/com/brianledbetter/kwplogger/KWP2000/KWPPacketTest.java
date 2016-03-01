package com.brianledbetter.kwplogger.KWP2000;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Created by b3d on 2/29/16.
 */
public class KWPPacketTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testBuildPacket() throws Exception {
        KWPPacket kwpPacket = new KWPPacket((byte)0x10, new byte[] {(byte)0x1A, (byte)0x9B});
        byte[] resultBytes = kwpPacket.buildPacket();
        Assert.assertArrayEquals(new byte[] {(byte)0x80, (byte)0x10, (byte)0xF1, (byte)0x02, (byte)0x1A, (byte)0x9B, (byte)0x38}, resultBytes);
        kwpPacket = new KWPPacket((byte)0x10, new byte[] {(byte)0x81});
        resultBytes = kwpPacket.buildPacket();
        Assert.assertArrayEquals(new byte[] {(byte)0x80, (byte)0x10, (byte)0xF1, (byte)0x01, (byte)0x81, (byte)0x03}, resultBytes);
    }
}