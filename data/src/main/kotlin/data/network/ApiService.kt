package data.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Describes interactions with the API.
 */
internal interface ApiService {

    /**
     * Gets the list of models.
     * @param apiKey The API key.
     */
    @GET(METHOD_LIST)
    fun list(@Query(value = QUERY_PARAM_PAGE) page: Int, @Query(value = QUERY_PARAM_KEY) apiKey: CharSequence): Observable<ResponseBody>

    /**
     * Gets detailed information about a model.
     * @param modelId The identifier for the model.
     * @param apiKey The API key.
     * @return
     */
    @GET(METHOD_DETAIL)
    fun detail(@Query(value = QUERY_PARAM_MODEL_ID) modelId: CharSequence, @Query(value = QUERY_PARAM_KEY) apiKey: CharSequence): Observable<ResponseBody>

    companion object {
        private const val METHOD_LIST = "search"
        private const val METHOD_DETAIL = "get"
        private const val QUERY_PARAM_KEY = "key"
        private const val QUERY_PARAM_PAGE = "page"
        private const val QUERY_PARAM_MODEL_ID = "rId"
    }
}
