package com.lolhistory.repository

import com.lolhistory.datamodel.MatchHistory
import com.lolhistory.datamodel.SummonerRankInfo
import com.lolhistory.datamodel.summonerIDInfo
import com.lolhistory.retrofit.APIClient
import com.lolhistory.retrofit.RiotAPI
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RiotRepository {
    private val riotAPI = APIClient.getRiotClient().create(RiotAPI::class.java)
    private val riotAPIv5 = APIClient.getRiotV5Client().create(RiotAPI::class.java)

    fun getSummonerIdInfo(summonerName: String): Single<summonerIDInfo> = riotAPI
        .getSummonerIdInfo(summonerName)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())


    fun getSummonerRankInfo(summonerId: String): Single<List<SummonerRankInfo>> = riotAPI
        .getSummonerRank(summonerId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun getMatchList(puuid: String): Single<ArrayList<String>> = riotAPIv5
        .getMatchHistoryList(puuid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun getMatchHistory(matchId: String): Single<MatchHistory> = riotAPIv5
        .getMatchHistory(matchId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

}