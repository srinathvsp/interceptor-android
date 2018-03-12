
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

public interface IDataSource {

    interface DataOnLoadCallback<T>{
        void onSuccess(T t);
        void onDataNotAvailable();
        void onError(String message);
    }

    interface DataOnLoadCallbackQuery<T,M>{
        void onSuccess(T t,M m);
        void onDataNotAvailable();
        void onError(String message);
    }
    void doLogin(@NonNull UserCredentials userCredentials, @NonNull DataOnLoadCallback<UserToken> callback);
}
