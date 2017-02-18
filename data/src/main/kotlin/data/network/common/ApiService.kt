package data.network.common

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * Describes interactions with the API.
 */
internal interface ApiService {

    /**
     * Gets the list of models.
     * @param subreddit The subreddit to get the tops from.
     * @param time The time range for the query.
     * @param after The 'after' for pagination purposes.
     * @param limit The 'limit' for pagination purposes.
     */
    @GET("${ROUTE_SUBREDDIT}/{${PATH_PARAM_SUBREDDIT_NAME}}/${METHOD_TOP}/${FORMAT_JSON}")
    fun top(@Path(PATH_PARAM_SUBREDDIT_NAME) subreddit: CharSequence,
            @Query(QUERY_PARAM_TIME) time: CharSequence,
            @Query(QUERY_PARAM_AFTER) after: CharSequence?,
            @Query(QUERY_PARAM_LIMIT) limit: Int): Observable<ResponseBody>

    private companion object {
        private const val ROUTE_SUBREDDIT = "r"
        private const val PATH_PARAM_SUBREDDIT_NAME = "subreddit"
        private const val METHOD_TOP = "top"
        private const val FORMAT_JSON = ".json"
        private const val QUERY_PARAM_TIME = "t"
        private const val QUERY_PARAM_AFTER = "after"
        private const val QUERY_PARAM_LIMIT = "limit"
    }
}
