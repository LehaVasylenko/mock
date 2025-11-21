package mock.service.automation;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import mock.client.AuthClient;
import mock.model.automation.RequestBody;
import mock.model.automation.ResponseBody;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@ApplicationScoped
public class AutomationService {
    private static final Logger log = LoggerFactory.getLogger(AutomationService.class);

    @Inject
    @RestClient
    AuthClient authClient;

    private String token = "";
    private long expiresAt = 0;
    private RequestBody requestBody = new RequestBody("johnytesterazaza123@gmail.com",
                                                        "739a23a7-f77a-4336-b07a-ee19feae9cda",
                                                    21588);
    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public Uni<String> getToken() {
        return Uni.createFrom().item(token);
    }

//    @Scheduled(cron = "0 */1 * * * ?")
    void schedule() {
        int h = LocalDateTime.now().getHour();
        if (h > 19 || h < 8) return;
        if (this.expiresAt > System.currentTimeMillis()) {
            authClient.getUserNameByEmail(requestBody.userName()).subscribe().with(
                    log::info,
                    error -> {
                        log.error(error.getMessage());
                        this.init();
                    });
            return;
        }
        this.init();
    }

//    void onStart(@Observes StartupEvent ev) {
//        this.init();
//    }

    private void init() {
        this.auth().subscribe().with(r -> {
            this.token = r.accessToken();
            this.expiresAt = System.currentTimeMillis() + r.expiresIn() - 10 * 60 * 1000;
        }, e -> log.error(e.getMessage()));
    }

    private Uni<ResponseBody> auth() {
        return authClient.getAuth(this.requestBody);
    }
}
