package com.lolhistory

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lolhistory.datamodel.MatchHistory
import com.lolhistory.datamodel.SummonerRankInfo
import com.lolhistory.datamodel.summonerIDInfo
import com.lolhistory.repository.RiotRepository
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.time.LocalDate

class MainActivityViewModel: ViewModel() {
    private val repo = RiotRepository()

    //LiveDatas
    private val summonerIDInfoLiveData = MutableLiveData<summonerIDInfo>()
    private val summonerRankInfoLiveData = MutableLiveData<SummonerRankInfo>()
    private val historyAdapterLiveData = MutableLiveData<HistoryAdapter>()



    private var summonerName = ""

    private var matchHistories: ArrayList<MatchHistory> = ArrayList()



    //LiveData GET 선언 함수
    fun getSummonerIDInfoLiveData(): MutableLiveData<summonerIDInfo> {
        return summonerIDInfoLiveData
    }

    fun getSummonerRankInfoLiveData(): MutableLiveData<SummonerRankInfo> {
        return summonerRankInfoLiveData
    }

    fun getHistoryAdapterLiveData(): MutableLiveData<HistoryAdapter> {
        return historyAdapterLiveData
    }



    //소환사 검색
    fun searchSummoner(name: String) {
        summonerName = name
        matchHistories.clear()

        repo.getSummonerIdInfo(name).subscribe(object : SingleObserver<summonerIDInfo>{
            override fun onSubscribe(d: Disposable) {

            }

            override fun onSuccess(summonerIdInfo: summonerIDInfo) {
                summonerName = summonerIdInfo.name
                getSummonerRankInfo(summonerIdInfo.id)
                getMatchList(summonerIdInfo.puuid)
            }

            override fun onError(e: Throwable) {
                Log.e("TESTLOG", "[getSummonerIdInfo] error: $e")
                summonerIDInfoLiveData.value = null
            }
        })
    }

    //소환사 랭크 정보 받아오는 fun
    fun getSummonerRankInfo(summonerId: String) {
        repo.getSummonerRankInfo(summonerId)
            .subscribe(object : SingleObserver<List<SummonerRankInfo>> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onSuccess(t: List<SummonerRankInfo>) {
                setSummonerRankInfo(t)
            }

            override fun onError(e: Throwable) {
                Log.e("TESTLOG", "[getSummonerIdInfo] error: $e")
            }
        })
    }

    //소환사 최근 게임 정보 리스트 fun
    private fun getMatchList(puuid: String) {
        repo.getMatchList(puuid).subscribe(object: SingleObserver<ArrayList<String>> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onSuccess(array: ArrayList<String>) {
                //최대가 20개 까지 요청가능이므로 15개만 요청
                var count = 0

                for (matchId in array) {
                    if (count < 15) {
                        //상세 매치 히스토리 검색
                        count++
                        getMatchHistory(matchId, puuid)
                        //Log.d("TESTLOG", "matchID: $matchId")
                    } else {
                        break
                    }
                }
            }

            override fun onError(e: Throwable) {
                Log.e("TESTLOG", "[getMatchList] error: $e")
            }
        })
    }


    private fun getMatchHistory(matchId: String, puuid: String) {
        repo.getMatchHistory(matchId).subscribe(object: SingleObserver<MatchHistory> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onSuccess(t: MatchHistory) {
                //Log.d("TESTLOG", "gameCreation: " + t.info.gameCreation)
                matchHistories.add(t)
                if (matchHistories.size > 14) {
                    val historyAdapter = HistoryAdapter(matchHistories, puuid)
                    historyAdapterLiveData.value = historyAdapter
                }
            }

            override fun onError(e: Throwable) {
                Log.e("TESTLOG", "[getMatchHistory] error: $e")
                historyAdapterLiveData.value = null
            }
        })
    }

    private fun setSummonerRankInfo(summonerRankInfos: List<SummonerRankInfo>) {
        //솔랭 / 자랭 중에서 더 티어 높은 랭크를 구분하는 function
        //Log.d("TESTING", "tier: " + summonerRankInfos[0].tier)
        //Log.d("TESTLOG", "rank: " + summonerRankInfos[0].rank)

        var soloRankInfo: SummonerRankInfo? = null
        var flexRankInfo: SummonerRankInfo? = null
        var soloRankTier = 0
        var flexRankTier = 0

        if(summonerRankInfos.isEmpty()) {
            //언랭
            val unRankInfo = SummonerRankInfo(summonerName, "",
                "UNRANKED", "", 0, 0, 0,)
            summonerRankInfoLiveData.value = unRankInfo
        } else {
            for (info in summonerRankInfos) {
                if (info.queueType == "RANKED_SOLO_5x5") {
                    //솔랭
                    soloRankInfo = info
                    soloRankTier = calcTier(info.tier, info.rank, info.leaguePoints)
                } else if (info.queueType == "RANKED_FLEX_SR") {
                    //자랭
                    flexRankInfo = info
                    flexRankTier = calcTier(info.tier, info.rank, info.leaguePoints)
                }
            }
            if (soloRankTier < flexRankTier) {
                //자랭 티어가 높을 때
                summonerRankInfoLiveData.value = flexRankInfo
            } else {
                // 솔랭 티어가 높을 때
                summonerRankInfoLiveData.value = soloRankInfo
            }
        }

    }

    private fun calcTier(tier: String, rank: String, lp: Int): Int {
        //티어 계산 함수
        var tierPoint = 0
        when (tier) {
            "BRONZE" -> tierPoint = 1000
            "SILVER" -> tierPoint = 2000
            "GOLD" -> tierPoint = 3000
            "PLATINUM" -> tierPoint = 4000
            "DIAMOND" -> tierPoint = 5000
            "MASTER" -> tierPoint = 6000
            "GRANDMASTER" -> tierPoint = 7000
            "CHALLENGER" -> tierPoint = 8000
        }

        when (rank) {
            "IV" -> tierPoint += 100
            "III" -> tierPoint += 300
            "II" -> tierPoint += 500
            "I" -> tierPoint += 700
        }
        tierPoint += lp
        // GOLD IV 34 = 3000 + 100 + 34 = 3134
        // SILVER II 80 = 2000 + 500 + 80 = 2580
        return tierPoint

    }
}