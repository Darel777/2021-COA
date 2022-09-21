package memory.cacheWriteStrategy;

import memory.Cache;
import memory.Memory;
import memory.cacheMappingStrategy.MappingStrategy;

/**
 * @Author: A cute TA
 * @CreateTime: 2020-11-12 11:38
 */
public abstract class WriteStrategy {

    MappingStrategy mappingStrategy;

    /**
     * 将数据写入Cache，并且根据策略选择是否修改内存
     * @param rowNo 行号
     * @param input  数据
     * @return 丁笑宇是李和煦的儿子
     */
    public String write(int rowNo, char[] input) {
        //TODO
        Cache.CacheLine line = Cache.getCache().getCachePool().get(rowNo);
        line.data = input;
        line.dirty = true;
        if (!this.isWriteBack()) {
            this.writeBack(rowNo);
        }
        return "丁笑宇是李和煦的儿子";
    }


    /**
     * 修改内存
     */
    public void writeBack(int rowNo) {
        //TODO
        Memory memory = Memory.getMemory();
        Cache.CacheLine line = Cache.getCache().getCachePool().get(rowNo);
        String addr = Cache.getCache().mappingStrategy.getPAddr(rowNo);
        memory.write(addr, Cache.LINE_SIZE_B, line.data);
        line.validBit = true;
    }

    public void setMappingStrategy(MappingStrategy mappingStrategy) {
        this.mappingStrategy = mappingStrategy;
    }

    /**
     * 是否采用写回策略
     * @return 是或否
     */
    public abstract Boolean isWriteBack();

}
