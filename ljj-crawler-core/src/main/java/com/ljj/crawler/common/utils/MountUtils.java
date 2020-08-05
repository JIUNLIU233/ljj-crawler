package com.ljj.crawler.common.utils;

/**
 * 数据挂载的工具类
 * Create by JIUN·LIU
 * Create time 2020/8/5
 **/
public class MountUtils {


    /**
     * 是否需要新建traceId。
     * <p>
     * 一条单独的新数据需要new traceId。
     * 对象的数组子节点也需要new traceId。
     *
     * @param mount
     * @return
     */
    public static boolean isNewTraceId(String mount) {
        if (mount != null && (mount.startsWith("[new]") || mount.contains("[array]"))) return true;
        return false;
    }

    /**
     * 获取mount 上的存储集合名称
     *
     * @param mount
     * @return
     */
    public static String getCollectionName(String mount) {
        if (mount == null) return null;
        return mount.split("\\.")[0];
    }


    /**
     * 获取挂载节点
     * 分为直接节点和内部节点
     *
     * @param mount
     * @return
     */
    public static String getMountKey(String mount) {
        if (mount != null) {
            String[] split = mount.split("\\.");
            if (split.length == 2) { // 直接挂载节点。
                return split[1];
            } else if (split.length == 3) {
                int i = split[1].indexOf("[");
                String substring = split[1].substring(0, i);
                return substring + "." + split[2];
            }
        }
        return null;
    }


    public static boolean isArrayMount(String mount) {
        if (mount != null && mount.contains("[array]")) return true;
        else return false;
    }

    public static void main(String[] args) {
//        String mount = "collection.address[object].city";
        String mount = "collection.links[array].name";
        String mountKey = getMountKey(mount);
        System.out.println(mountKey);
    }
}
