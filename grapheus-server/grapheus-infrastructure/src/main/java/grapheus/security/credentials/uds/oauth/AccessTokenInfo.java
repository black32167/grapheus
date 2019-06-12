package grapheus.security.credentials.uds.oauth;

import lombok.Data;

@Data
public class AccessTokenInfo {
    private String access_token;
    private String scopes;
    private String refresh_token;
    private long expires_in;
    private String token_type;
    private String id_token;
}