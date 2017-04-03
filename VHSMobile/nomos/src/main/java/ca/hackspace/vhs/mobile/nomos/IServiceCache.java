package ca.hackspace.vhs.mobile.nomos;

/**
 * Created by Thomas on 8/23/2016.
 */
public interface IServiceCache {
    void PutValue(String key, Object value);
    Object Get(String key);
}
