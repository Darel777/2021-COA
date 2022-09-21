package memory.cacheMappingStrategy;

import memory.cacheReplacementStrategy.ReplacementStrategy;

public abstract class MappingStrategy {

    protected ReplacementStrategy replacementStrategy;

    /**
     * 组数，为256
     */
    private int SETS;

    /**
     * 每组的行数，为4
     */
    private int setSize;

    public void setReplacementStrategy(ReplacementStrategy replacementStrategy) {
        this.replacementStrategy = replacementStrategy;
    }

    /**
     * 根据块号，结合具体的映射策略，计算数据块在Cache行中的Tag
     *
     * @param blockNO 块号
     * @return 长度为22
     */
    public abstract char[] getTag(int blockNO);


    /**
     * 根据目标数据内存地址前22位的int表示，进行映射与替换
     *
     * @param blockNO 块号
     * @return 返回cache中所对应的行，-1表示未命中
     */
    public abstract int map(int blockNO);

    /**
     * 未命中的情况下，将内存读取出的input数据写入cache
     *
     * @param blockNO 块号
     * @return 返回cache中所对应的行
     */
    public abstract int writeCache(int blockNO);

    /**
     * 通过映射倒推出内存地址
     *
     * @param rowNo 行号
     * @return 返回具体的地址
     */
    public abstract String getPAddr(int rowNo);

}
