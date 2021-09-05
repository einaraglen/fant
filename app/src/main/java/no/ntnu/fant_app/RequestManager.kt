package no.ntnu.fant_app

import android.content.Context
import com.android.volley.toolbox.Volley

import com.android.volley.RequestQueue


object RequestManager {
    private var mRequestManager: RequestManager? = null

    /**
     * Queue which Manages the Network Requests :-)
     */
    private var mRequestQueue: RequestQueue? = null
    operator fun get(context: Context?): RequestManager? {
        if (mRequestManager == null) mRequestManager = RequestManager
        return mRequestManager
    }

    /**
     * @param context application context
     */
    fun getnstance(context: Context?): RequestQueue? {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context)
        }
        return mRequestQueue
    }
}