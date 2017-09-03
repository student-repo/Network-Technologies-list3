
import org.junit.Test;


import static org.junit.Assert.*;

public class CRCTest {

    @Test
    public void getCRCSimpleTest() {
        CRC crc  = new CRC("1011");
        assertEquals(crc.getCRC("11010011101110"), "010");
    }

    @Test
    public void getCRCTest() {
        CRC crc  = new CRC("1000111001");
        assertEquals(crc.getCRC("00010000111110010000"), "001100011");
    }

    @Test
    public void largeGetCRCTest() {
        int testStringLength = 100;
        RandomBitStreamString rs = new RandomBitStreamString(testStringLength);
        RandomBitStreamString crcRs = new RandomBitStreamString(7);
        int testNumber = 100000;
        CRC crc  = new CRC("1" + crcRs.nextString());
        for(int i = 0; i < testNumber; i++){
            assertEquals(crc.checkCorrectnessData(crc.appendCRC( rs.nextString())), true);
        }

        crcRs = new RandomBitStreamString(15);
        crc  = new CRC("1" + crcRs.nextString());
        for(int i = 0; i < testNumber; i++){
            assertEquals(crc.checkCorrectnessData(crc.appendCRC( rs.nextString())), true);
        }

        crcRs = new RandomBitStreamString(31);
        crc  = new CRC("1" + crcRs.nextString());
        for(int i = 0; i < testNumber; i++){
            assertEquals(crc.checkCorrectnessData(crc.appendCRC( rs.nextString())), true);
        }

//
//        crcRs = new RandomBitStreamString(7);
//        crc  = new CRC("1" + crcRs.nextString());
//        for(int i = 0; i < testNumber; i++){
//            String ss = crc.appendCRC( rs.nextString());
//            StringBuilder myName = new StringBuilder(ss);
//            int randomNum = ThreadLocalRandom.current().nextInt(0, testStringLength);
//            if(ss.charAt(randomNum) == '0'){
//                myName.setCharAt(randomNum, '1');
//            }
//            else{
//                myName.setCharAt(randomNum, '0');
//            }
//            assertEquals(crc.checkCorrectnessData(myName.toString()), false);
//        }
//
//        crcRs = new RandomBitStreamString(15);
//        crc  = new CRC("1" + crcRs.nextString());
//        for(int i = 0; i < testNumber; i++){
//            String ss = crc.appendCRC( rs.nextString());
//            StringBuilder myName = new StringBuilder(ss);
//            int randomNum = ThreadLocalRandom.current().nextInt(0, testStringLength);
//            if(ss.charAt(randomNum) == '0'){
//                myName.setCharAt(randomNum, '1');
//            }
//            else{
//                myName.setCharAt(randomNum, '0');
//            }
//            assertEquals(crc.checkCorrectnessData(myName.toString()), false);
//        }
//
//        crcRs = new RandomBitStreamString(31);
//        crc  = new CRC("1" + crcRs.nextString());
//        for(int i = 0; i < testNumber; i++){
//            String ss = crc.appendCRC( rs.nextString());
//            StringBuilder myName = new StringBuilder(ss);
//            int randomNum = ThreadLocalRandom.current().nextInt(0, testStringLength + 1);
//            if(ss.charAt(randomNum) == '0'){
//                myName.setCharAt(randomNum, '1');
//            }
//            else{
//                myName.setCharAt(randomNum, '0');
//            }
//            assertEquals(crc.checkCorrectnessData(myName.toString()), false);
//        }

    }


    @Test
    public void stretchBitesSimpleTest() {
        CRC crc  = new CRC("1000111001");
        assertEquals(crc.stretchBites("11111"), "111110");
    }

    @Test
    public void stretchBitesTest() {
        CRC crc  = new CRC("1000111001");
        assertEquals(crc.stretchBites("000001111010101010"), "000001111010101010");
        assertEquals(crc.stretchBites("0000011111010101010"), "00000111110010101010");
        assertEquals(crc.stretchBites("1111"), "1111");
        assertEquals(crc.stretchBites("11111111111111111111"), "111110111110111110111110");
        assertEquals(crc.stretchBites("1111111111111111111"), "1111101111101111101111");
        assertEquals(crc.stretchBites("111101111"), "111101111");
    }

    @Test
    public void shortBitesSimpleTest() {
        CRC crc  = new CRC("101110");
        assertEquals(crc.shortBites("111110"), "11111");
        assertEquals(crc.shortBites("11111"), "11111");
    }


    @Test
    public void shortBitesTest() {
        CRC crc  = new CRC("101110");
        assertEquals(crc.shortBites("11111011111011111011"), "11111111111111111");
        assertEquals(crc.shortBites("0011111011111011111011"), "0011111111111111111");
        assertEquals(crc.shortBites("111110111110111110"), "111111111111111");
        assertEquals(crc.shortBites("1111111111111"), "1111111111111");
        assertEquals(crc.shortBites("00000000"), "00000000");
    }

    @Test
    public void removeEndFrameTest() {
        CRC crc  = new CRC("101110");
        assertEquals(crc.removeEndFrame("01111110"), "");
        assertEquals(crc.removeEndFrame("001111110"), "0");
        assertEquals(crc.removeEndFrame("101111110"), "1");
        assertEquals(crc.removeEndFrame("1111001111110"), "11110");
        assertEquals(crc.removeEndFrame("111100111110"), "");
        assertEquals(crc.removeEndFrame("11110011111101010101"), "11110");
    }

    @Test
    public void removeStartFrameTest() {
        CRC crc  = new CRC("101110");
        assertEquals(crc.removeStartFrame("01111110"), "");
        assertEquals(crc.removeStartFrame("011111101111"), "1111");
        assertEquals(crc.removeStartFrame("011111100000"), "0000");
        assertEquals(crc.removeStartFrame("01111110101010"), "101010");
        assertEquals(crc.removeStartFrame("0111110101010"), "");
    }



}