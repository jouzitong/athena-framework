package org.arthena.framework.common.provider;

/**
 *
 * @author zhouzhitong
 * @since 2026/5/30
 */
public interface ErrCodeProvider {

    String getMsg(int code, Object[] args);

    /**
     * Reloads the error code and message mappings, refreshing them from the source.
     * This method should be called whenever there is a need to update the error messages,
     * such as after a configuration change or during an application restart.
     */
    void reload();

}
