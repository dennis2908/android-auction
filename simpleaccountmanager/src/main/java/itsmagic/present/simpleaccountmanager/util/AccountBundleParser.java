package itsmagic.present.simpleaccountmanager.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Iterator;
import java.util.Set;

import itsmagic.present.simpleaccountmanager.BuildConfig;
import itsmagic.present.simpleaccountmanager.json.JSONException;
import itsmagic.present.simpleaccountmanager.json.JSONObject;

/**
 * Created by Alvin Rusli on 04/04/2017.
 * <p/>
 * A parser for {@link Bundle} objects.
 */
public class AccountBundleParser {

    /**
     * Convert a json Object into an android {@link Bundle}
     * @param jsonObject the json object
     * @return the bundle
     */
    @NonNull
    public static Bundle jsonToBundle(JSONObject jsonObject) throws JSONException {
        Bundle bundle = new Bundle();
        Iterator iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = jsonObject.getString(key);
            bundle.putString(key, value);
        }
        return bundle;
    }

    /**
     * Convert an android {@link Bundle} into a json Object
     * @param bundle the bundle
     * @return the json object
     */
    @NonNull
    public static JSONObject bundleToJson(Bundle bundle) {
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                json.put(key, JSONObject.wrap(bundle.get(key)));
            } catch(JSONException e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }
        }

        return json;
    }

}
