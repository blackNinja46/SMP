package live.blackninja.webhook.listener;

public class ChatDataManger {

    private final java.util.Queue<Long> timestamps = new java.util.LinkedList<>();

    public void addMessage(long timestamp) {
        timestamps.add(timestamp);
    }

    public void removeOldMessages(long cutoffTime) {
        while (!timestamps.isEmpty() && timestamps.peek() < cutoffTime) {
            timestamps.poll();
        }
    }

    public int getMessageCount() {
        return timestamps.size();
    }

}
