
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


使用：


private static void initErrorHandler(Context context) {
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(context);
    }

