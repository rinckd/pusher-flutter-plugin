package io.rinckd.pusher.platform_messages;

public class SubscriptionInstanceMessage extends InstanceMessage {

    private String channelName;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
