package memory;


import util.CRC;

/**
 * 磁盘抽象类，磁盘大小为64M
 */
public class Disk {

    /**
     * 磁盘大小 64 MB
     */
    public static int DISK_SIZE_B = 64 * 1024 * 1024;

    private static Disk diskInstance = new Disk();

    /**
     * 请勿修改下列属性，至少不要修改一个扇区的大小，如果要修改请保证磁盘的大小为64MB
     * 柱面数：8
     * 磁道数：16
     * 扇区数：128
     * 字节数：512
     * 盘数：8
     */
    public static final int CYLINDER_NUM = 8;
    public static final int TRACK_PRE_PLATTER = 16;
    public static final int SECTOR_PRE_TRACK = 128;
    public static final int BYTE_PRE_SECTOR = 512;
    public static final int PLATTER_PRE_CYLINDER = 8;

    /**
     * 生成多项式
     */
    public static final String POLYNOMIAL = "11000000000100001";
    public disk_head DISK_HEAD = new disk_head();

    RealDisk disk = new RealDisk();

    /**
     * 初始化
     */
    private Disk() {
    }

    public static Disk getDisk() {
        return diskInstance;
    }

    /**
     * 读磁盘
     *
     * @param eip 起始地址
     * @param len 读取的长度
     * @return 读取到的数据
     */
    public char[] read(String eip, int len) {
        //TODO
        this.DISK_HEAD.Seek(Integer.parseInt(eip, 2));
        char[] res = new char[len];
        for (int i = 0; i < len; i++) {
//            if (DISK_HEAD.point == 0) {
//                CRC.Check(disk.cylinders[DISK_HEAD.cylinder].platters[DISK_HEAD.platter]
//                                .tracks[DISK_HEAD.track].sectors[DISK_HEAD.sector].dataField.Data,
//                        POLYNOMIAL, this.disk.getCRC(this.DISK_HEAD));
//            }
            res[i] = disk.cylinders[DISK_HEAD.cylinder].platters[DISK_HEAD.platter]
                    .tracks[DISK_HEAD.track].sectors[DISK_HEAD.sector].dataField.Data[DISK_HEAD.point];
            DISK_HEAD.point++;
            if (i != len - 1) {
                DISK_HEAD.adjust();
            }
        }
        return res;
    }

    /**
     * 写磁盘
     *
     * @param eip  起始地址
     * @param len  写的长度
     * @param data 要写的数据
     */
    public void write(String eip, int len, char[] data) {
        //TODO
        this.DISK_HEAD.Seek(Integer.parseInt(eip, 2));
        for (int i = 0; i < len; i++) {
            disk.cylinders[DISK_HEAD.cylinder].platters[DISK_HEAD.platter].tracks[DISK_HEAD.track]
                    .sectors[DISK_HEAD.sector].dataField.Data[DISK_HEAD.point] = data[i];
            DISK_HEAD.point++;
            if (DISK_HEAD.point == BYTE_PRE_SECTOR || i == len - 1) {
                char[] dataToTransform = disk.cylinders
                        [DISK_HEAD.cylinder].platters[DISK_HEAD.platter].tracks[DISK_HEAD.track]
                        .sectors[DISK_HEAD.sector].dataField.Data;
                char[] crc = CRC.Calculate(ToBitStream(dataToTransform), POLYNOMIAL);
                disk.cylinders[DISK_HEAD.cylinder].platters[DISK_HEAD.platter].tracks[DISK_HEAD.track]
                        .sectors[DISK_HEAD.sector].dataField.CRC = ToByteStream(crc);
            }
            if (i != len - 1) {
                DISK_HEAD.adjust();
            }
        }
    }

    /**
     * 写磁盘（地址为Integer型）
     * 测试会调用该方法
     *
     * @param eip  起始地址
     * @param len  写的长度
     * @param data 要写的数据
     */
    public void write(int eip, int len, char[] data) {
        //TODO
        this.DISK_HEAD.Seek(eip);
        for (int i = 0; i < len; i++) {
            disk.cylinders[DISK_HEAD.cylinder].platters[DISK_HEAD.platter].tracks[DISK_HEAD.track]
                    .sectors[DISK_HEAD.sector].dataField.Data[DISK_HEAD.point] = data[i];
            DISK_HEAD.point++;
            if (DISK_HEAD.point == BYTE_PRE_SECTOR || i == len - 1) {
                char[] dataToTransform = disk.cylinders
                        [DISK_HEAD.cylinder].platters[DISK_HEAD.platter].tracks[DISK_HEAD.track]
                        .sectors[DISK_HEAD.sector].dataField.Data;
                char[] crc = CRC.Calculate(ToBitStream(dataToTransform), POLYNOMIAL);
                disk.cylinders[DISK_HEAD.cylinder].platters[DISK_HEAD.platter].tracks[DISK_HEAD.track]
                        .sectors[DISK_HEAD.sector].dataField.CRC = ToByteStream(crc);
            }
            if (i != len - 1) {
                DISK_HEAD.adjust();
            }
        }
    }

    /**
     * 该方法仅用于测试
     */
    public char[] getCRC() {
        return disk.getCRC(DISK_HEAD);
    }

    /**
     * 磁头
     */
    private class disk_head {

        /**
         * 柱面数
         */
        int cylinder;

        /**
         * 盘数
         */
        int platter;

        /**
         * 磁道数
         */
        int track;

        /**
         * 扇区数
         */
        int sector;

        /**
         * 位置
         */
        int point;

        /**
         * 调整磁头的位置
         */
        public void adjust() {
            if (point == BYTE_PRE_SECTOR) {
                point = 0;
                sector++;
            }
            if (sector == SECTOR_PRE_TRACK) {
                sector = 0;
                track++;
            }
            if (track == TRACK_PRE_PLATTER) {
                track = 0;
                platter++;
            }
            if (platter == PLATTER_PRE_CYLINDER) {
                platter = 0;
                cylinder++;
            }
            if (cylinder == CYLINDER_NUM) {
                cylinder = 0;
            }
        }

        /**
         * 磁头回到起点
         */
        public void Init() {
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            cylinder = 0;
            track = 0;
            sector = 0;
            point = 0;
            platter = 0;
        }

        /**
         * 将磁头移动到目标位置
         *
         * @param start
         */
        public void Seek(int start) {
//            try {
//                Thread.sleep(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            for (int i = cylinder; i < CYLINDER_NUM; i++) {
                for (int t = platter; t < PLATTER_PRE_CYLINDER; t++) {
                    for (int j = track; j < TRACK_PRE_PLATTER; j++) {
                        for (int z = sector; z < SECTOR_PRE_TRACK; z++) {
                            for (int k = point; k < BYTE_PRE_SECTOR; k++) {
                                if ((i * PLATTER_PRE_CYLINDER * TRACK_PRE_PLATTER * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + t * TRACK_PRE_PLATTER * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + j * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + z * BYTE_PRE_SECTOR + k) == start) {
                                    cylinder = i;
                                    track = j;
                                    sector = z;
                                    point = k;
                                    platter = t;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            Init();
            Seek(start);
        }

        @Override
        public String toString() {
            return "The Head Of Disk Is In\n" +
                    "platter:\t" + cylinder + "\n" +
                    "track:\t\t" + track + "\n" +
                    "sector:\t\t" + sector + "\n" +
                    "point:\t\t" + point;
        }
    }

    /**
     * 600 Bytes/Sector
     */
    private class Sector {
        char[] gap1 = new char[17];
        IDField idField = new IDField();
        char[] gap2 = new char[41];
        DataField dataField = new DataField();
        char[] gap3 = new char[20];
    }

    /**
     * 7 Bytes/IDField
     */
    private class IDField {
        char SynchByte;
        char[] Track = new char[2];
        char Head;
        char sector;
        char[] CRC = new char[2];
    }

    /**
     * 515 Bytes/DataField
     */
    private class DataField {
        char SynchByte;
        char[] Data = new char[512];
        char[] CRC = new char[2];
    }

    /**
     * 128 sectors pre track
     */
    private class Track {
        Sector[] sectors = new Sector[SECTOR_PRE_TRACK];

        Track() {
            for (int i = 0; i < SECTOR_PRE_TRACK; i++) {
                sectors[i] = new Sector();
            }
        }
    }


    /**
     * 32 tracks pre platter
     */
    private class Platter {
        Track[] tracks = new Track[TRACK_PRE_PLATTER];

        Platter() {
            for (int i = 0; i < TRACK_PRE_PLATTER; i++) {
                tracks[i] = new Track();
            }
        }
    }

    /**
     * 8 platter pre Cylinder
     */
    private class Cylinder {
        Platter[] platters = new Platter[PLATTER_PRE_CYLINDER];

        Cylinder() {
            for (int i = 0; i < PLATTER_PRE_CYLINDER; i++) {
                platters[i] = new Platter();
            }
        }
    }


    private class RealDisk {
        Cylinder[] cylinders = new Cylinder[CYLINDER_NUM];

        public RealDisk() {
            for (int i = 0; i < CYLINDER_NUM; i++) {
                cylinders[i] = new Cylinder();
            }
        }

        public char[] getCRC(disk_head d) {
            return cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.CRC;
        }
    }

    /**
     * 将Byte流转换成Bit流
     *
     * @param data 要转换的Byte流
     * @return 转换成的Bit流
     */
    public static char[] ToBitStream(char[] data) {
        char[] t = new char[data.length * 8];
        int index = 0;
        //TODO
        for (int i = 0; i < data.length; i++) {
            char[] temp = toBitStream(data[i]);
            for (int j = 0; j < 8; j++) {
                t[index] = temp[j];
                index++;
            }
        }
        return t;
    }

    private static char[] toBitStream(char data) {
        char[] res = new char[8];
        for (int i = 7; i >= 0; i--) {
            res[i] = (char)((char)((data >> i) & (0b00000001)) + '0');
        }
        return res;
    }

    /**
     * 将Bit流转换为Byte流
     *
     * @param data 要转换的Bit流
     * @return 转换成的Byte流
     */
    public static char[] ToByteStream(char[] data) {
        char[] t = new char[data.length / 8];
        int index = 0;
        //TODO
        for (int i = 0; i < t.length; i++) {
            char[] temp = new char[8];
            for (int j = 0; j < 8; j++) {
                temp[j] = data[index];
                index++;
            }
            t[i] = toByteStream(temp);
        }
        return t;
    }

    private static char toByteStream(char[] data) {
        char res = 0;
        for (int i = 0; i < 8; i++) {
            res = (char)(res | ((data[i] - '0') << i));
        }
        return res;
    }

    /**
     * 这个方法仅供测试，请勿修改
     *
     * @param eip
     * @param len
     * @return
     */
    public char[] readTest(String eip, int len) {
        char[] data = read(eip, len);
        System.out.print(data);
        return data;
    }

    public static void main(String[] args) {
        Disk disk = new Disk();
        char[] data = new char[1024];
        for (int i = 0; i < 512; i++) {
            data[i] = '0';
        }
        for (int i = 512; i < 1024; i++) {
            data[i] = '0';
        }
        disk.write(0, 1024, data);
        disk.DISK_HEAD.Seek(0);
        System.out.println(ToBitStream(disk.getCRC()));
    }

}
