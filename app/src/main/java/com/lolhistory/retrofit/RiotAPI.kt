package com.lolhistory.retrofit

import com.lolhistory.datamodel.MatchHistory
import com.lolhistory.datamodel.SummonerRankInfo
import com.lolhistory.datamodel.summonerIDInfo
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface RiotAPI {
    //아이디 정보
    @Headers("Accept: Application/json", "X-Riot-Token: " + BaseUrl.RIOT_API_KEY)
    @GET(BaseUrl.RIOT_API_GET_SUMMONER + "{userId}")
    fun getSummonerIdInfo(@Path("userId") userId: String): Single<summonerIDInfo>

    //랭크
    @Headers("Accept: Application/json", "X-Riot-Token: " + BaseUrl.RIOT_API_KEY)
    @GET(BaseUrl.RIOT_API_GET_RANK + "{id}")
    fun getSummonerRank(@Path("id") userId: String): Single<List<SummonerRankInfo>>

    //매치 리스트
    @Headers("Accept: Application/json", "X-Riot-Token: " + BaseUrl.RIOT_API_KEY)
    @GET(BaseUrl.RIOT_API_GET_MATCH_LIST + "{puuid}/ids")
    fun getMatchHistoryList(@Path("puuid") puuid: String): Single<ArrayList<String>>

    // 매치 히스토리
    @Headers("Accept: Application/json", "X-Riot-Token: " + BaseUrl.RIOT_API_KEY)
    @GET(BaseUrl.RIOT_API_GET_MATCH + "{matchId}")
    fun getMatchHistory(@Path("matchId") matchId: String): Single<MatchHistory>

}