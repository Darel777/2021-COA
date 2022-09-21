package test;

import cpu.MMU;
import memory.Disk;
import memory.DiskInterface;
import memory.Memory;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class MyTest {

    Memory memory;

    DiskInterface disk;

    MMU mmu;

    @Before
    public void init() {
        this.memory = Memory.getMemory();
        this.disk = Disk.getDisk();
        this.mmu = MMU.getMMU();
    }

    @Test
    public void test1() {
        // 段页式
        Memory.SEGMENT = true;
        Memory.PAGE = true;
        String eip = "00000000000000000000000000000000";
        int len = 2 * 1024;
        char[] data = this.fillData((char) 0b00001111, len);
        memory.alloc_seg_force(0, eip, len, true, "");
        mmu.read("000000000000000000000000000000000000000000000000", len);
        assertArrayEquals(data, mmu.read("000000000000000000000000000000000000000000000000", len));
    }

    @Test
    public void test2() {
        // 实模式
        Memory.SEGMENT = false;
        Memory.PAGE = false;
        int len = 128;
        char[] data = this.fillData((char)0b00001111, 128);
        assertArrayEquals(data, mmu.read("000000000000000000000000000000000000000000000000", len));
    }

    @Test
    public void test3() {
        // 段式
        Memory.SEGMENT = true;
        Memory.PAGE = false;
        String eip = "00000000000000000000000000000000";
        int len = 1024 * 1024;
        char[] data = this.fillData((char)0b00001111, len);
        memory.alloc_seg_force(0, eip, len, false, eip);
        assertArrayEquals(data, mmu.read("000000000000000000000000000000000000000000000000", len));
    }

    @Test
    public void test4() {
        // 段页式
        Memory.SEGMENT = true;
        Memory.PAGE = true;
        String eip = "00000000000000000000000000000000";
        int len = 1024 * 1024;
        char[] data = this.fillData((char) 0b00001111, len);
        memory.alloc_seg_force(0, eip, len, true, "");
        mmu.read("000000000000000000000000000000000000000000000000", len);
        assertArrayEquals(data, mmu.read("000000000000000000000000000000000000000000000000", len));
    }

    @Test
    public void testTLB() {
        Memory.PAGE = true;
        Memory.SEGMENT = true;
        memory.alloc_seg_force(0, "00000000000000000000000000000000", 10485760, true, "");
        memory.alloc_seg_force(1, "00000000101000000000000000000000", 16777216, true, "");
        memory.alloc_seg_force(2, "00000001101000000000000000000000", 8388608, true, "");
        mmu.read("000000000001000000001000000000000000000000000000", 1049600);
        mmu.read("000000000000000000000000000000000000000000000000", 10485760);
    }

    @Test
    public void psTest() {

    }

    public char[] fillData(char dataUnit, int len) {
        char[] data = new char[len];
        Arrays.fill(data, dataUnit);
        return data;
    }

}
