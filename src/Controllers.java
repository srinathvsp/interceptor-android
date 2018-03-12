
import android.content.Context;



public abstract class Controllers {

    private ApiDataSource mRemoteDataSource = null;

    public Controllers(Context context){
        mRemoteDataSource = ApiDataSource.getInstance(context);
    }

    public ApiDataSource getRemoteInstance(){
        return mRemoteDataSource;
    }


}
