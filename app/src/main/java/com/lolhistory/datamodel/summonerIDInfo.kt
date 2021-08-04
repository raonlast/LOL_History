package com.lolhistory.datamodel

import com.google.gson.annotations.SerializedName


/**
 * {
"id": "leC85dWIXxGFwT2ZtBJ0SXzN0cZbAcDTh7mCMWxzNjDEROs",
"accountId": "DCQ86OAglBHSTTT1L84ztxvNRwVqOb-WnMzc2aFccsIphUQ",
"puuid": "9AEyjCPYubb9ry8VtGS2FDfMSAMlrFhNFmjxKNY2E4L5CJ3hNg4gAmQ6ZidzmOJS8P0AYVcP3TuQhw",
"name": "RaonLast",
"profileIconId": 1044,
"revisionDate": 1627337627000,
"summonerLevel": 255
}
 */


data class summonerIDInfo(
    @SerializedName("id")
    var id: String,
    // id == leC85dWIXxGFwT2ZtBJ0SXzN0cZbAcDTh7mCMWxzNjDEROs

    @SerializedName("accountId")
    var accountId: String,

    @SerializedName("puuid")
    var puuid: String,

    @SerializedName("name")
    var name: String,

    @SerializedName("summonerLevel")
    var summonerLevel: Int
)