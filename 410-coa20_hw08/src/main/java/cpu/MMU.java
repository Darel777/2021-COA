package cpu;

import memory.Cache;
import memory.Memory;
import memory.MemoryInterface;
import transformer.Transformer;

import java.util.Arrays;


/**
 * MMU接收一个48-bits的逻辑地址，并最终将其转换成32-bits的物理地址
 * <p>
 * Memory.SEGMENT和Memory.PAGE标志用于表示是否开启分段和分页。
 * 实际上在机器里从实模式进入保护模式后分段一定会保持开启(即使实模式也会使用段寄存器)，因此一共只要实现三种模式的内存管理即可：实模式、只有分段、段页式
 * 所有模式的物理地址都采用32-bits，实模式的物理地址高位补0
 * <p>
 * 大致流程(仅供参考)：
 * 1. 逻辑地址：总长度48-bits(16-bits段选择符+32位段内偏移)
 * 2. 段选择符高13-bits表示段描述符的索引，低3-bits本作业不使用
 * 3. 通过段选择符查询段表，获得段描述符，包括32-bits的基地址、31-bits的限长、1-bit的有效位(判断段是否被加载到内存或者失效)
 * 3.1 如果分页未启用且段未加载/失效，则将段从磁盘读取到内存(分段下根据段描述符中的32-bits磁盘基址，段页式下根据虚页号)
 * 4. 根据基地址和段内偏移计算线性地址(32-bits，包括20-bits虚页页号和12-bits页内偏移)
 * 5. 通过虚页页号查询页表，并获得20-bits的页框号和1-bit标志位(记录该页是否在内存中或者失效)
 * 5.1 如果页不在内存，则将页从磁盘读取到内存
 * 6. 页框号与页内偏移组合成物理地址，根据物理地址和数据长度读Memory
 */
public class MMU {

	private static MMU mmuInstance = new MMU();

	private MMU() {
	}

	public static MMU getMMU() {
		return mmuInstance;
	}

	public MemoryInterface memory = Memory.getMemory();

	public void setMemory(MemoryInterface memory) {
		this.memory = memory;
	}

	Transformer t = new Transformer();

	Cache cache = Cache.getCache();

	TLB tlb = TLB.getTLB();

	/**
	 * 地址转换
	 *
	 * @param logicAddr 48-bits逻辑地址。实模式和分段模式下，磁盘物理地址==内存物理地址，段页式下，磁盘物理地址==虚页号 * 页框大小 + 偏移量
	 * @param length    读取数据的长度
	 * @return 内存中的数据
	 */
	//TODO need to add cache and tlb
	public char[] read(String logicAddr, int length) {
		// 32位线性地址
		String linearAddr = "";
		// 32位物理地址
		String physicalAddr = "";
		// 实模式或分段模式
		if (!Memory.PAGE) {
			if (!Memory.SEGMENT) {
				//TODO 获取线性地址并将数据加载到从磁盘加载到内存
				// 实模式：线性地址等于物理地址
				linearAddr = toRealLinearAddr(logicAddr);
				// 从磁盘中加载到内存
				memory.real_load(linearAddr, length);
			} else {
				//TODO 获取段号并且将段加载到内存，接着获取线性地址
				// 分段模式：线性地址等于物理地址
				// Code Here
				int segIndex = Integer.parseInt(logicAddr.substring(0, 13), 2);
				if (memory.isValidSegDes(segIndex)) {
					linearAddr = toSegLinearAddr(logicAddr);
				} else {
					String baseSegmentAddress = memory.seg_load(segIndex);
					linearAddr = this.add(baseSegmentAddress.toCharArray(), logicAddr.substring(16, 48));
				}
			}
			physicalAddr = linearAddr;
		} else {
			// 段页式
			int segIndex = getSegIndex(logicAddr);
			// 段的限长
			int limit = Integer.parseInt(t.binaryToInt(String.valueOf(memory.getLimitOfSegDes(segIndex))));
			if (length > limit * 2) {
				throw new SecurityException("访问限制");
			}
			if (!memory.isValidSegDes(segIndex)) {
				// 缺段中断，该段不在内存中，在内存中为该段分配内存
				memory.seg_load(segIndex);
			}
			//TODO 计算段页式下的起始虚拟页号与结束虚拟页号与偏移量
			// 线性地址的中间20位表示虚拟页号 最后12位表示页内偏移
			linearAddr = toSegLinearAddr(logicAddr);
			int startvPageNo = Integer.parseInt(linearAddr.substring(0, 20), 2);
			int offset = Integer.parseInt(linearAddr.substring(20), 2);
			int endvPageNo = startvPageNo + length / Memory.PAGE_SIZE_B - 1;
			char[] res = new char[length];
			int p = 0;
			for (int i = startvPageNo; i <= endvPageNo; i++) {
				String pageAddr;
				//TODO ADD TLB HERE
				int matchRes = tlb.isMatch(i);
				if (matchRes == -1) {
					if (!memory.isValidPage(i)) {
						// 缺页
						// 从内存中加载该页,并返回页地址
						pageAddr = memory.page_load(segIndex, i);
						// System.out.println("page_load invoke " + i);
					} else {
						// 访问页表
						pageAddr = String.valueOf(memory.getFrameOfPage(i));
					}
				} else {
					pageAddr = String.valueOf(tlb.getFrameOfPage(matchRes));
				}
				if (i == startvPageNo) {
					// 读第一页
					char[] pageData = memory.read(pageAddr, Memory.PAGE_SIZE_B);
					for (int j = offset; j < Memory.PAGE_SIZE_B && p < length; j++) {
						res[p++] = pageData[j];
					}
				} else {
					// 读取非第一页的数据
					char[] pageData = memory.read(pageAddr, Memory.PAGE_SIZE_B);
					for (int j = 0; j < Memory.PAGE_SIZE_B && p < length; j++) {
						res[p++] = pageData[j];
					}
				}
			}
			return res;
		}

		return memory.read(physicalAddr, length);
	}

	/**
	 * 该方法仅用于测试，请勿修改
	 *
	 * @param logicAddr
	 * @param length
	 * @return
	 */
	public void readTest(String logicAddr, int length) {
		System.out.print(this.read(logicAddr, length));
	}

	/**
	 * 根据逻辑地址找到对应的段号
	 * 段选择符高13-bits表示段描述符的索引，低3-bits本作业不使用
	 *
	 * @param logicAddr 逻辑地址
	 * @return
	 */
	private int getSegIndex(String logicAddr) {
		//TODO
		return Integer.parseInt(logicAddr.substring(0, 13), 2);
	}


	/**
	 * 实模式下的逻辑地址转线性地址
	 *
	 * @param logicAddr 48位 = 16位段选择符(高13位index选择段表项) + 32位offset，计算公式为：①(16-bits段寄存器左移4位 + offset的低16-bits) = 20-bits物理地址 ②高位补0到32-bits
	 * @return 32-bits实模式线性地址
	 */
	private String toRealLinearAddr(String logicAddr) {
		//TODO
		String base = logicAddr.substring(0, 16);
		base = base + "0000";
		int addr = Integer.parseInt(base, 2) + Integer.parseInt(logicAddr.substring(32), 2);
		String linearAddr = Integer.toBinaryString(addr);
		int length = linearAddr.length();
		for (int i = 0; i < 32 - length; i++) {
			linearAddr = "0" + linearAddr;
		}
		return linearAddr;
	}


	/**
	 * 分段模式下的逻辑地址转线性地址
	 *
	 * @param logicAddr 48位 = 16位段选择符(高13位index选择段表项) + 32位段内偏移
	 * @return 32-bits 线性地址
	 */
	private String toSegLinearAddr(String logicAddr) {
		//TODO linearAddr = SegBase + offset
		int index = this.getSegIndex(logicAddr);
		String linearAddr = this.add(memory.getBaseOfSegDes(index), logicAddr.substring(16));
		int length = linearAddr.length();
		for (int i = 0; i < 32 - length; i++) {
			linearAddr = "0" + linearAddr;
		}
		return linearAddr;
	}


	/**
	 * 基地址+偏移地址
	 *
	 * @param base      20/32位基地址
	 * @param offsetStr 20/32位偏移
	 * @return 32-bits 线性地址
	 */
	private String add(char[] base, String offsetStr) {
		char[] offset = offsetStr.toCharArray();
		StringBuilder res = new StringBuilder();
		char carry = '0';
		for (int i = base.length - 1; i >= 0; i--) {
			res.append((carry - '0') ^ (base[i] - '0') ^ (offset[i] - '0'));
			carry = (char) (((carry - '0') & (base[i] - '0')) | ((carry - '0') & (offset[i] - '0')) | ((base[i] - '0') & (offset[i] - '0')) + '0');
		}

		for (int i = 0; i < 32 - res.length(); i++) {
			res.append("0");
		}
		return res.reverse().toString();
	}

}
