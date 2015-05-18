package mycompany.thistest.LoadClasses;

import android.os.AsyncTask;

/**
 * Created by trsq9010 on 16/01/2015.
 */
public abstract class LoadData extends AsyncTask<String, String, Object> {
    private OnTaskComplete onTaskComplete;
    private boolean isFinished = true;
    //private Object result;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isFinished = false;
    }

    public interface OnTaskComplete {
        public void setMyTaskComplete(Object obj);
    }

    public void setMyTaskCompleteListener(OnTaskComplete onTaskComplete) {
        this.onTaskComplete = onTaskComplete;
    }

    protected abstract Object doInBackground(String... args);

    protected void onPostExecute(Object result) {
        onTaskComplete.setMyTaskComplete(result);
        isFinished = true;
    }

    public boolean isFinished(){
        return isFinished;
    }
}