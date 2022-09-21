package cpu;

import memory.Disk;
import transformer.Transformer;

/**
 * @CreateTime: 2020-11-04 16:23
 */
public class TLB {

    /**
     * 默认启动TLB
     */
    public static final boolean isAvailable = true;

    /**
     * 总大小为4KB
     */
    public static final int TLB_SIZE_B = 4 * 1024;

    /**
     * 64 Bit = 8 Byte，每行的大小
     */
    public static final int LINE_SIZE_B = 8;

    /**
     * 快表行，一共512行
     */
    private TLBLinePool TLB = new TLBLinePool(TLB_SIZE_B / LINE_SIZE_B);

    private TLB() {
    }

    public static TLB tlbInstance = new TLB();

    public static TLB getTLB() {
        return tlbInstance;
    }

    /**
     * if match return the index else return -1
     *
     * @param virtualPageNo 虚拟页号
     * @return TLB行号
     */
    public int isMatch(int virtualPageNo) {
        Transformer transformer = new Transformer();
        String spageNo = transformer.intToBinary(String.valueOf(virtualPageNo));
        for (int i = 0; i < TLB_SIZE_B / LINE_SIZE_B; i++) {
            if (spageNo.equals(new String(Disk.ToBitStream(TLB.get(i).getPage()))) && TLB.get(i).valid) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取物理页框（内存中的）
     *
     * @param row TLB的行号，这个行号你应该使用上一个函数获取
     * @return
     */
    public char[] getFrameOfPage(int row) {
        return Disk.ToBitStream(TLB.get(row).getPageFrame());
    }

    /**
     * 向TLB中写入数据
     *
     * @param virtualPageNo     虚拟页号
     * @param physicalPageFrame 物理页号
     * @return
     */
    public int writeTLB(char[] virtualPageNo, char[] physicalPageFrame) {
        long minTime = Long.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < TLB_SIZE_B / LINE_SIZE_B; i++) {
            long curTime = getTimeStamp(i);
            if (curTime < minTime) {
                minTime = curTime;
                minIndex = i;
            }
        }
        if (minIndex != -1) {
            TLB.get(minIndex).update(Disk.ToByteStream(virtualPageNo), Disk.ToByteStream(physicalPageFrame));
            return minIndex;
        } else return -1;
    }


    public void invalid(int row) {
        TLB.get(row).valid = false;
    }

    // 获取时间戳
    private long getTimeStamp(int row) {
        TLBLine tlbLine = TLB.get(row);
        if (tlbLine.valid) {
            return tlbLine.timeStamp;
        }
        return -1;
    }


    private class TLBLinePool {
        TLBLinePool(int lines) {
            tlbLines = new TLBLine[lines];
        }

        private TLBLine[] tlbLines;

        private TLBLine get(int lineNO) {
            if (lineNO >= 0 && lineNO < tlbLines.length) {
                TLBLine l = tlbLines[lineNO];
                if (l == null) {
                    tlbLines[lineNO] = new TLBLine();
                    l = tlbLines[lineNO];
                }
                return l;
            }
            return null;
        }
    }

    /**
     * The PageNumber and PageFrame is stored in TLBLine.
     */
    private class TLBLine {
        // 有效位，标记该条数据是否有效
        boolean valid = false;
        // 虚拟页号
        char[] page = new char[LINE_SIZE_B / 2];
        Long timeStamp = 0l;
        // 数据，物理页号
        char[] pageFrame = new char[LINE_SIZE_B / 2];

        char[] getPageFrame() {
            return this.pageFrame;
        }
        char[] getPage() {
            return this.page;
        }

        /**
         * @param npage 虚拟页号
         * @param npageFrame 物理页号
         */
        void update(char[] npage, char[] npageFrame) {
            valid = true;
            timeStamp = System.currentTimeMillis();
            for (int i = 0; i < npage.length; i++) {
                this.page[i] = npage[i];
            }
            // input.length <= this.data.length
            for (int i = 0; i < npageFrame.length; i++) {
                this.pageFrame[i] = npageFrame[i];
            }
        }
    }

}
