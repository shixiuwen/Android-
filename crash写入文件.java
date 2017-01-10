
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.io.File;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = CrashHandler.class.getSimpleName();

    private static CrashHandler INSTANCE = new CrashHandler();

    private Context mContext;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private final static int ERROR_LEVEL = 0;


    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context ctx) {
        mContext = ctx.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * @param thread
     * @param ex
     */
    @Override
    public synchronized void uncaughtException(Thread thread, final Throwable ex) {
        try {
            
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            Throwable cause = e.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            
            File file = new File(getDiskCacheDir(mContext, "crash"), "crash.log");

            if (file.exists() && file.length() > 10 * 1024 * 1024) {
                boolean delete = file.delete();
            } else {
                File folder = new File(getDiskCacheDir(mContext, "crash"));
                if (folder.exists() && folder.isDirectory()) {
                    if (!file.exists()) {
                        boolean newFile = file.createNewFile();
                    }
                } else {
                    boolean mkDir = folder.mkdir();
                    if (mkDir) {
                        boolean newFile = file.createNewFile();
                    }
                }
                FileOutputStream fos = new FileOutputStream(file, true);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                String format = sdf.format(new Date());
                byte[] bytes = (format +
                        "\n" + e.toString() +
                        "\n" + "----------------------" +
                        "\n").getBytes();
                fos.write(bytes);
                fos.flush();
                fos.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        //crash后重启
        /*ComponentName componentName = new ComponentName("com.jlcf.jiuxin", "com.jlcf.jiuxin.JXApplication");
        Intent schemIntent = new Intent();
        schemIntent.setComponent(componentName);
        schemIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(schemIntent);*/

        if (true) {
            mDefaultHandler.uncaughtException(t, e);    //该代码不执行的话程序无法终止
        }
}
    
    /**
     * 获得Crash文件路径
     * <p>
     * getCacheDir和getFilesDir是放在/data/data/packagename下的，
     * 所以这个目录中的内容必须是root的手机在文件操作系统中才能看到。当然
     * 如果在应用程序中清空数据或者卸载应用，那么这俩个目录下的文件也将会
     * 被清空的。getExternalCacheDir和getExternalFilesDir是存放
     * 在/storage/sdcard0/Android/data/packagename下面的，这个是放
     * 在外置存储卡的，这个目录下的内容 可以使用文件浏览系统查看到，但是如果
     * 清空数据或者卸载应用，俩个目录下的文件也将被清空。或者也可以理解为带
     * external这样的是存储在外置sd卡的，而直接使用getFilesDir这种是放
     * 在/data/data下面的。
     *
     * @param context context
     * @param dirName dirName
     * @return dir
     */
    private String getDiskCacheDir(Context context, String dirName) {
        String cachePath = null;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            }
        }
        if (cachePath == null) {
            File cacheDir = context.getCacheDir();
            if ((cacheDir != null) && (cacheDir.exists())) {
                cachePath = cacheDir.getPath();
            }
        }
        //0/emulate/Android/data/data/com.jxph.jiurongjk/crash/crash.log
        return cachePath + File.separator + dirName;
    }

}

使用：


private static void initErrorHandler(Context context) {
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(context);
    }

