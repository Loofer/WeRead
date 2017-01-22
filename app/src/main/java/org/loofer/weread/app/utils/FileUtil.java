package org.loofer.weread.app.utils;

import android.os.Environment;

import org.loofer.framework.utils.L;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * 版权： xx有限公司 版权所有（c）2017
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/2 21:13.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

public class FileUtil {

    public static final String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String ADPATH = FileUtil.SDPATH + "/WeRead";

    public static void createSdDir() {
        File file = new File(FileUtil.ADPATH);
        if (!file.exists()) {
            boolean create = file.mkdirs();
            L.d("create = " + create);
//            LogUtils.d("create = " + create);
        } else {
            if (!file.isDirectory()) {
                file.delete();
                file.mkdir();
            }
        }
    }
    public static boolean isFileExist(String paramString) {
        if (paramString == null)
            return false;
        File localFile = new File(ADPATH + "/" + paramString);
        if (localFile.exists()) {
            return true;
        }
        return false;
    }
    public static File createFile(String fileName) throws IOException {
        File file = new File(ADPATH,fileName);
        file.createNewFile();
        return file;
    }
    public static List<String> getAllAD(){
        File file = new File(FileUtil.ADPATH);
        File[] fileList = file.listFiles();
        List<String> list = new ArrayList<>();
        if(null != fileList){
            for (File f:fileList) {
                list.add(f.getAbsolutePath());
            }
        }
        return list;
    }

}
