package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

public class SetAssociativeMapping extends MappingStrategy {

    Transformer t = new Transformer();

    /**
     * 组数，为256
     */
    private int SETS = 512; // 共256个组

    /**
     * 每组的行数，为4
     */
    private int setSize = 2;   // 每个组4行


    /**
     * 该方法会被用于测试，请勿修改
     *
     * @param SETS
     */
    public void setSETS(int SETS) {
        this.SETS = SETS;
    }

    /**
     * 该方法会被用于测试，请勿修改
     *
     * @param setSize
     */
    public void setSetSize(int setSize) {
        this.setSize = setSize;
    }

    /**
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前14位有效]
     */
    @Override
    public char[] getTag(int blockNO) {
        //TODO
        char[] tag = new char[22];
        String blockNumberString = t.intToBinary(String.valueOf(blockNO));
        int log2Set = 0;
        for (; ; log2Set++) {
            if (Math.pow(2, log2Set) == this.SETS) {
                break;
            }
        }
        for (int i = 10; i < 32 - log2Set; i++) {
            tag[i - 10] = blockNumberString.charAt(i);
        }
        for (int i = 32 - log2Set; i < 32; i++) {
            tag[i - 10] = '0';
        }
        return tag;
    }

    /**
     * @param blockNO 目标数据内存地址前22位int表示
     * @return -1 表示未命中
     */
    @Override
    public int map(int blockNO) {
        //TODO
        int setNumber = blockNO % this.SETS;
        return replacementStrategy.isHit(setNumber * this.setSize,
                (setNumber + 1) * this.setSize - 1, this.getTag(blockNO));
    }

    @Override
    public int writeCache(int blockNO) {
        //TODO
        int setNumber = blockNO % this.SETS;
        String eip = t.intToBinary(String.valueOf(blockNO * Cache.LINE_SIZE_B));
        char[] data = Memory.getMemory().read(eip, Cache.LINE_SIZE_B);
        return replacementStrategy.replace(setNumber * this.setSize,
                (setNumber + 1) * this.setSize - 1, this.getTag(blockNO), data, this.getTag(blockNO));
    }

    @Override
    public String getPAddr(int rowNo) {
        //TODO
        Cache.CacheLine line = Cache.getCache().getCachePool().get(rowNo);
        int setNumber = rowNo / this.setSize;
        String setStr = t.intToBinary(String.valueOf(setNumber));
        int log2Set = 0;
        for (; ; log2Set++) {
            if (Math.pow(2, log2Set) == this.SETS) {
                break;
            }
        }
        setStr = setStr.substring(32 - log2Set);
        char[] tag = line.tag;
        for (int i = 32 - log2Set; i < 32; i++) {
            tag[i - 10] = setStr.charAt(i - 32 + log2Set);
        }
        return String.valueOf(tag) + "0000000000";
    }

}










