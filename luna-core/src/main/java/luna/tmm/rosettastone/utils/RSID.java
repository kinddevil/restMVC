package luna.tmm.rosettastone.utils;

/**
 * @author
 * Class to store cookie like {"access_token":xxx, "userId":xxx}
 *
 */
public class RSID {
    private String access_token;
    private String userId;
    
    public String getAccess_token() {
        return access_token;
    }
    public String getUserId() {
        return userId;
    }
    public RSID(){}
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    @Override
    public String toString() {
        return "RSID [access_token=" + access_token + ", userId=" + userId
                + "]";
    }
}
