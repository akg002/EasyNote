package in.easynote.easynote.Listeners;

/**
 * Created by AKG002 on 14-02-2016.
 */
public interface FetchDataListener {
    public void preRequest();
    public void postRequest(String result);
}
