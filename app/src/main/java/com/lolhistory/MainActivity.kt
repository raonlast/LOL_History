package com.lolhistory

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lolhistory.databinding.ActivityMainBinding
import com.lolhistory.datamodel.SummonerRankInfo
import com.lolhistory.datamodel.summonerIDInfo
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainActivityViewModel

    // 검색한 뒤 키보드를 내려가게 하는 메소드
    lateinit var inputMethodManager: InputMethodManager


    private var isVisibleInfoLayout = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)


        //소환사 이름이 존재하지 않을 때 Toast 출력
        viewModel.getSummonerIDInfoLiveData().observe(this, { summonerIDInfo ->
            if (summonerIDInfo == null) {
                val notExistToast = Toast.makeText(applicationContext, R.string.not_exist_summoner, Toast.LENGTH_SHORT)
                notExistToast.show()
            }
        })

        //소환사가 언랭일 때
        viewModel.getSummonerRankInfoLiveData().observe(this, { summonerRankInfo ->
            if (summonerRankInfo != null) {
                setRankInfoView(summonerRankInfo)
                isVisibleInfoLayout = true
                binding.inputLayout.visibility = View.GONE
            }
        })


        //소환사 전적 / 소환사 전적 리로딩 / 소환사의 전적이 없을 때
        viewModel.getHistoryAdapterLiveData().observe(this, { historyAdapter ->
            if (historyAdapter != null) {
                binding.rvHistory.adapter = historyAdapter
                binding.swipeLayout.isRefreshing = false
            } else {
                val historyErrorToast = Toast.makeText(applicationContext, R.string.history_error, Toast.LENGTH_SHORT)
                historyErrorToast.show()
            }
        })
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.setHasFixedSize(true)


        binding.swipeLayout.setOnRefreshListener { ->
            viewModel.searchSummoner(binding.tvSummonerName.text.toString())
        }



        binding.btnInputSummoner.setOnClickListener{ v ->
            //키보드 숨기기
            inputMethodManager.hideSoftInputFromWindow(binding.etInputSummoner.windowToken, 0)

            viewModel.searchSummoner(binding.etInputSummoner.text.toString())
        }

    }

    override fun onBackPressed() {
        if (isVisibleInfoLayout) {
            // 검색 된 상태
            binding.infoLayout.visibility = View.GONE
            binding.inputLayout.visibility = View.VISIBLE
            isVisibleInfoLayout = !isVisibleInfoLayout
        } else {
            // 검색 전 상태
            super.onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setRankInfoView(rankInfo: SummonerRankInfo) {
        setTierEmblem(rankInfo.tier) //티어 이미지

        binding.tvSummonerName.text = rankInfo.summonerName

        var tierRank = rankInfo.tier + " " + rankInfo.rank  // ex) PLATINUM IV
        binding.tvTier.text = tierRank

        if(rankInfo.tier == "UNRANKED") {  // 언랭일때
            binding.tvRankType.text = ""
            binding.tvLp.text = ""
            binding.tvTotalWinLose.text = ""
            binding.tvTotalWinRate.text = ""
        } else {
            binding.tvRankType.text = rankInfo.queueType

            //LP 표시
            val point = rankInfo.leaguePoints.toString() + " LP"
            binding.tvLp.text = point

            // 승률 소수점 2번째 자리까지 xx.xx
            val rate = rankInfo.wins.toDouble() / (rankInfo.wins + rankInfo.losses).toDouble() * 100
            binding.tvTotalWinRate.text = String.format(Locale.getDefault(), "%.2f%%", rate)

            //승패 ex) x승x패
            val winAndLosses = rankInfo.wins.toString() + "승 " + rankInfo.losses.toString() + "패"
            binding.tvTotalWinLose.text = winAndLosses
        }

        binding.infoLayout.visibility = View.VISIBLE

    }

    private fun setTierEmblem(tier: String) {
        when(tier) {
            "UNRANKED" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_unranked)
            "IRON" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_iron)
            "BRONZE" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_bronze)
            "SILVER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_silver)
            "GOLD" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_gold)
            "PLATINUM" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_platinum)
            "DIAMOND" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_diamond)
            "MASTER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_master)
            "GRANDMASTER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_grandmaster)
            "CHALLENGER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_challenger)
        }
    }
}