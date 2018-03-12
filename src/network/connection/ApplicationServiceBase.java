
public class ApplicationServiceBase extends ApiBase {

    public static final String SSL = "https://";
    public static final String host = "execute-api.eu-west-1.amazonaws.com";
    public static final String AppHOST = "abc.com";



    @Override
    public String getKey() {
        return API_KEY;
    }

}
