package com.doktorthe2nd.min.modules.sync;

public class SyncState {
    private static final String DEFAULT_CONFIG_HASH =
            "00000000-0000000000000000-00000000-"+
                    "0000000000000000-0000000000000000-0-"+
                    "0000000000000000-00000000";

    public int chats_sync = -1;
    public int contacts_sync = -1;
    public int drafts_sync = -1;
    public int presence_sync = -1;
    public String config_hash = DEFAULT_CONFIG_HASH;
}
