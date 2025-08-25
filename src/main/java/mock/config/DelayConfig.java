package mock.config;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DelayConfig {
    private volatile long delayMillis = 0;

    public long getDelay() {
        return delayMillis;
    }

    public void setDelay(long millis) {
        this.delayMillis = millis;
    }

    public void reset() {
        this.delayMillis = 0;
    }
}

