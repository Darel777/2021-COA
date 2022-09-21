package memory;

/**
 * @Author: A cute TA
 * @CreateTime: 2020-11-07 11:36
 */
public interface MemoryInterface {

    /**
     * 从内存读取
     *
     * @param eip 起始位置
     * @param len 长度
     * @return 读取的数据
     */
    char[] read(String eip, int len);

    /**
     * 写回内存
     *
     * @param eip  起始位置
     * @param len  长度
     * @param data 写回的数据
     */
    void write(String eip, int len, char[] data);

}
