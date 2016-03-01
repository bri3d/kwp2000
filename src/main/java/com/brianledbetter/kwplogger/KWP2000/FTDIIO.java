package com.brianledbetter.kwplogger.KWP2000;

import net.sf.yad2xx.*;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by b3d on 2/28/16.
 */
public class FTDIIO implements KWP2000IO {
    private Device m_ftdiDevice;
    private byte m_controllerAddress;
    private static final Logger LOGGER = Logger.getAnonymousLogger();
    @Override
    public byte[] readBytes() throws KWPException {
        try {
            byte[] headerBytes = new byte[4];
            m_ftdiDevice.read(headerBytes);
            if (headerBytes[0] != (byte) 0x80) {
                throw new KWPException("Got byte " + HexUtil.bytesToHexString(headerBytes) + " not 0x80");
            }
            byte[] responsePacket = new byte[headerBytes[3]];
            m_ftdiDevice.read(responsePacket);
            byte[] checksum = new byte[1];
            m_ftdiDevice.read(checksum);
            return responsePacket;
        } catch(FTDIException e) {
            throw new KWPException(e.toString());
        }
    }

    @Override
    public void writeBytes(byte[] bytesToWrite) throws KWPException {
        byte[] finalPacket = new KWPPacket(m_controllerAddress, bytesToWrite).buildPacket();
        try {
            m_ftdiDevice.write(finalPacket);
        } catch (FTDIException e) {
            new KWPException(e.toString());
        }
    }

    @Override
    public void startKWPIO(byte initAddress, byte controllerAddress) throws KWPException {
        try {
            Device[] devices = FTDIInterface.getDevices();
            if (devices.length < 1) {
                throw new KWPException("No FTDI Device!");
            }
            m_ftdiDevice = devices[0];
            m_controllerAddress = controllerAddress;
            if (!m_ftdiDevice.isOpen()) {
                m_ftdiDevice.open();
            }
            System.out.println("FTDI Device is open : " + m_ftdiDevice.getDescription());
            slowFTDIInit(initAddress);
        } catch (FTDIException e) {
            throw new KWPException(e.toString());
        } catch (InterruptedException e) {
            throw new KWPException(e.toString());
        }
    }

    private void slowFTDIInit(byte initAddress) throws FTDIException, InterruptedException {
        // 300ms init
        m_ftdiDevice.setBreak(false);
        Thread.sleep(300);
        // 200ms wakeup
        m_ftdiDevice.setBreak(true);
        Thread.sleep(200);
        // Bitbang the address
        for (int i = 0; i < 8; i++)
        {
            // yes, there are 8 bits in a byte
            if (((0x01 << i) & initAddress) > 0)
            {
                // is this bit number set?
                m_ftdiDevice.setBreak(false);
            } else {
                m_ftdiDevice.setBreak(true);
            }
            Thread.sleep(200);
        }
        m_ftdiDevice.setBreak(false);
        // Setup 104008N1
        m_ftdiDevice.setBaudRate(10400);
        m_ftdiDevice.setDataCharacteristics(FTDIConstants.FT_BITS_8, FTDIConstants.FT_STOP_BITS_1, FTDIConstants.FT_PARITY_NONE);
        byte[] input = new byte[1];
        do
        {
            // This API is a major WTF
            m_ftdiDevice.read(input);
            LOGGER.log(Level.ALL, "Got " + Integer.toHexString(input[0]));
        } while (input[0] != (byte)0x8F);
        m_ftdiDevice.write((byte)0x70);
        do {
            m_ftdiDevice.read(input);
            LOGGER.log(Level.ALL, "Got " + Integer.toHexString(input[0]));
        } while (input[0] != ((byte)0xFF - initAddress));
    }
}
