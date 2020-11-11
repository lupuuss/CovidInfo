package pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.global.objects

import com.google.gson.annotations.SerializedName

data class Summary (

        @SerializedName("Global") val global : GlobalSummary,
        @SerializedName("Countries") val countrySummaries : List<CountrySummary>,
        @SerializedName("Date") val date : String
)