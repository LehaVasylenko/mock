package mock.service;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response.ResponseBuilder;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class HeaderService {

    public ResponseBuilder applyHeaders(ResponseBuilder responseBuilder, int length, String sysId) {
        String path = "http://testsand.duckdns.org:8080/api/now/v1/table/u_test_table/" + sysId;
        return responseBuilder
                .header("Server-Timing", "sem_wait;dur=0, sesh_wait;dur=0")
                .header("X-Is-Logged-In", "true")
                .header("X-Transaction-ID", "c0049a4e8391")
                .header("X-Total-Count", String.valueOf(length))
                .header("Location", path)
                .header("X-Content-Type-Options", "nosniff")
                .header("Pragma", "no-store,no-cache")
                .header("Cache-Control", "no-cache,no-store,must-revalidate,max-age=-1")
                .header("Expires", "0")
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Transfer-Encoding", "chunked")
                .header("Keep-Alive", "timeout=70")
                .header("Connection", "keep-alive")
                .header("Server", "ServiceNow")
                .header("Strict-Transport-Security", "max-age=63072000; includeSubDomains")
                .header("Date", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))

                        .cookie(new NewCookie("JSESSIONID", "74FC306CCE2FAD6047F875059C6F223A", "/", null, null, 1800, true, true))

                        .header("Set-Cookie", "glide_user=; Max-Age=0; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/; Secure; HttpOnly; SameSite=None")
                        .header("Set-Cookie", "glide_user_session=; Max-Age=0; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/; Secure; HttpOnly; SameSite=None")
                        .header("Set-Cookie", "glide_user_route=glide.062aa51aed0551a7591c49318a84080a; Max-Age=2147483647; Expires=Sat, 23-May-2093 19:17:04 GMT; Path=/; Secure; HttpOnly; SameSite=None")
                        .header("Set-Cookie", "glide_node_id_for_js=2b68abc...; Path=/; Secure; SameSite=None")
                        .header("Set-Cookie", "glide_session_store=8C04D24E8391E2103F7ECB29FEAAD389; Max-Age=1800; Expires=Mon, 05-May-2025 16:32:57 GMT; Path=/; Secure; HttpOnly; SameSite=None");
    }
}
