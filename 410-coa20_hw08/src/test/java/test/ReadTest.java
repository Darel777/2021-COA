import cpu.MMU;
import memory.Disk;
import memory.Memory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * <p>description: </p>
 *
 * @date 2020/12/04
 */
public class ReadTest {

    Memory memory = Memory.getMemory();
    MMU mmu = MMU.getMMU();

    // 段.式
    @Test
    public void testSegPageMode() {
        memory.PAGE=true;
        memory.SEGMENT=true;
        String eip = "00000000000000000000000000000000";
        int len = 200 * 1024;
        char[] data = fillData((char) 0b00001111, len);
        memory.alloc_seg_force(0, eip, len/2, true, "");
        assertArrayEquals(data,
                mmu.read("000000000000000000000000000000000000000000000000", len));
    }

    // 实模式
    @Test
    public void testRealMode() {
        int len = 128;
        char[] data = fillData((char)0b00001111, 128);
        assertArrayEquals(data,
                mmu.read("000000000000000000000000000000000000000000000000", len));
    }

    // 段式
    @Test
    public void testSegMode() {
        memory.SEGMENT=true;
        memory.PAGE = false;
        String eip = "00000000000000000000000000000000";
        int len = 1024 * 1024;
        char[] data = fillData((char)0b00001111, len);
        memory.alloc_seg_force(0, eip, len, false, eip);
        char[] da = mmu.read("000000000000000000000000000000000000000000000000", len);

        assertArrayEquals(data,
                da);
    }

    public char[] fillData(char dataUnit, int len) {
        char[] data = new char[len];
        Arrays.fill(data, dataUnit);
        return data;
    }

    @Test
    public void testTest(){
        memory.PAGE=true;
        memory.SEGMENT=true;
        memory.alloc_seg_force(0, "00000000000000000000000000000000", 1024 , false, "");
        char[] data1 = null;
        //虚拟地址：00000100111111111111  000000000000
        //虚拟地址：00000101000000000000  000000000000
        //虚拟地址：00000100111111111111  000000000000
        //虚拟地址：00000101000000000000  000000000000
        //包括20-bits虚页页号和12-bits页内偏移
        //物理地址=
        data1 = mmu.read("000000000000000000000100111111111111000000000000",1024);
        System.out.println(Disk.ToBitStream(data1));
        data1 = mmu.read("000000000000000000000101000000000000000000000000",1024);;
        System.out.println(Disk.ToBitStream(data1));
        data1 = mmu.read("000000000000000000000100111111111111000000000000",1024);;
        System.out.println(Disk.ToBitStream(data1));
        data1 = mmu.read("000000000000000000000101000000000000000000000000",1024);;
        System.out.println(Disk.ToBitStream(data1));
    }

    @Test
    public void testTLB() {
        memory.PAGE=true;
        memory.SEGMENT=true;
        memory.alloc_seg_force(0, "00000000000000000000000000000000", 10485760, true, "");
        memory.alloc_seg_force(1, "00000000101000000000000000000000", 16777216, true, "");
        memory.alloc_seg_force(2, "00000001101000000000000000000000", 8388608, true, "");
        char[] data1 = mmu.read("000000000001000000001000000000000000000000000000", 1049600);
        //System.out.println(Disk.ToBitStream(data1));
        char[] data2 = mmu.read("000000000000000000000000000000000000000000000000", 10485760);
        //System.out.println(Disk.ToBitStream(data2));

    }

}
