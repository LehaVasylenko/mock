package mock.config;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import mock.model.Auth;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MyIdentityProvider implements IdentityProvider<UsernamePasswordAuthenticationRequest> {
    @ConfigProperty(name = "app.username", defaultValue = "Crocodillo")
    String username;

    @ConfigProperty(name = "app.password", defaultValue = "Bombardillo")
    String password;

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(UsernamePasswordAuthenticationRequest request,
                                              AuthenticationRequestContext context) {
        String username = request.getUsername();
        String password = new String(request.getPassword().getPassword());

        Auth auth = getAuth(username);

        if (auth != null && auth.password().equals(password)) {
            SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                    .setPrincipal(() -> username)
                    .addCredential(request.getPassword())
                    .addAttribute("username", username)
                    .build();

            return Uni.createFrom().item(identity);
        }

        return Uni.createFrom().nullItem(); // отказ в доступе
    }

    private Auth getAuth(String username) {
        Auth result = new Auth(this.username, this.password);
        if (username.equals(this.username)) return result;
        else return null;
    }
}

