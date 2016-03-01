package com.brianledbetter.kwplogger.KWP2000;

import java.util.Arrays;

/**
 * Created by b3d on 2/29/16.
 */
public class KWPPacket {
    byte m_controllerAddress;
    byte[] m_message;
    public KWPPacket(byte controllerAddress, byte[] message) {
        super();
        m_controllerAddress = controllerAddress;
        m_message = message;
    }

    private byte checksum(byte[] packet) {
        byte checksum = 0;

        for (int i = 0; i < packet.length; i++)
        {
            checksum += 0xff & packet[i];
        }

        return checksum;
    }

    public byte[] buildPacket() {
        byte[] packet = m_message;
        byte protocol = (byte)0x80;
        byte address = m_controllerAddress;
        byte testerAddress = (byte)0xF1;
        byte length = (byte)packet.length;
        byte[] header = new byte[] {protocol, address, testerAddress, length};
        byte[] result = Arrays.copyOf(header, header.length + packet.length + 1);
        System.arraycopy(packet, 0, result, header.length, packet.length);
        byte[] checksum = new byte[] { checksum(result) };
        System.arraycopy(checksum, 0, result, header.length + packet.length, 1);
        return result;

    }
}
