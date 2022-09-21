package memory.cacheReplacementStrategy;

import memory.Cache;
import memory.cacheWriteStrategy.WriteBackStrategy;
import memory.cacheWriteStrategy.WriteStrategy;

import java.util.Arrays;

/**
 * 先进先出算法
 */
public class FIFOReplacement extends ReplacementStrategy {

    @Override
    public int isHit(int start, int end, char[] addrTag) {
        //TODO
        for (int i = start; i <= end; i++) {
            Cache.CacheLine line = Cache.getCache().getCachePool().get(i);
            if (String.valueOf(addrTag).equals(String.valueOf(line.tag)) && line.validBit) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int replace(int start, int end, char[] addrTag, char[] input, char[] tag) {
        //TODO
        for (int i = start; i <= end; i++) {
            Cache.CacheLine line = Cache.getCache().getCachePool().get(i);
            if (!line.validBit) {
                line.validBit = true;
                line.tag = tag;
                line.data = Arrays.copyOf(input, input.length);
                if (i == end) {
                    Cache.getCache().getCachePool().get(start).timeStamp = 1L;
                }
                return i;
            }
        }
        for (int i = start; i <= end; i++) {
            Cache.CacheLine line = Cache.getCache().getCachePool().get(i);
            if (line.timeStamp == 1L) {
                if (Cache.getCache().writeStrategy.isWriteBack()) {
                    Cache.getCache().writeStrategy.writeBack(i);
                }
                line.timeStamp = 0L;
                line.validBit = true;
                line.tag = tag;
                line.data = Arrays.copyOf(input, input.length);
                if (i != end) {
                    Cache.getCache().getCachePool().get(i + 1).timeStamp = 1L;
                } else {
                    Cache.getCache().getCachePool().get(start).timeStamp = 1L;
                }
                return i;
            }
        }
        return -1;
    }

}
