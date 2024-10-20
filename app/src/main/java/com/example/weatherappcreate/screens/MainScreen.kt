package com.example.weatherappcreate.screens

import android.app.DownloadManager.Request
import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Request.Method
import coil.compose.AsyncImage
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherappcreate.R
import com.example.weatherappcreate.data.WeatherModel
import com.example.weatherappcreate.ui.theme.BlueLight
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject




@Composable
fun MainCard(currentDay : MutableState<WeatherModel> , onClickSync: () -> Unit ,
             onClickSearch: () -> Unit) {
    val state = remember {
        mutableStateOf("Unknown")
    }

    Column(modifier = Modifier.padding(5.dp)){
        Card(
            modifier = Modifier.fillMaxWidth() ,
            colors = CardDefaults.cardColors(
                containerColor = BlueLight
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            ) ,
            shape = RoundedCornerShape(10.dp)
        ){
            Column(modifier = Modifier.fillMaxWidth() ,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.SpaceBetween ,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()){
                    Text(modifier = Modifier.padding(start=8.dp),
                        text = currentDay.value.time ,
                        style = TextStyle(fontSize = 15.sp) ,
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https:" + currentDay.value.icon ,
                        contentDescription = "some image" ,
                        modifier = Modifier.padding(top = 8.dp , end = 8.dp).size(35.dp))
                }
                Text(text = currentDay.value.city ,
                    style = TextStyle(fontSize = 24.sp) ,
                    color = Color.White
                )

                Text(text = if(currentDay.value.currentTemp.isNotEmpty()) currentDay.value.currentTemp + "°C"
                    else "${currentDay.value.maxTemp}°C/${currentDay.value.minTemp}°C",
                    style = TextStyle(fontSize = 65.sp) ,
                    color = Color.White)

                Text(text = currentDay.value.condition ,
                    style = TextStyle(fontSize = 16.sp) ,
                    color = Color.White)

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically)
                {
                    IconButton(onClick = {
                        onClickSearch.invoke()
                    }) {
                        Icon(painter = painterResource(id = R.drawable.search), contentDescription = null,
                            tint = Color.White)
                    }

                    Text(text = "${currentDay.value.maxTemp}/${currentDay.value.minTemp}" ,
                        style = TextStyle(fontSize = 16.sp) ,
                        color = Color.White)

                    IconButton(onClick = {
                        onClickSync.invoke()
                    }) {
                        Icon(painter = painterResource(id = R.drawable.sync), contentDescription = null,
                            tint = Color.White)
                    }

                }
            }

        }
    }

}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabLayout(daysList : MutableState<List<WeatherModel>> , currentDay: MutableState<WeatherModel>){
    val tabList = listOf("HOURS" , "DAYS")
    val pagerState = rememberPagerState(initialPage = 0 , pageCount = { 2 })
    val tabIndex = pagerState.currentPage
    val corrutineScope = rememberCoroutineScope()


    Column(modifier = Modifier.clip(RoundedCornerShape(10.dp)),
        ){

        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = BlueLight,
            modifier = Modifier.padding(start = 6.dp , end = 6.dp) ,
            contentColor = Color.White
        ){
            tabList.forEachIndexed{index , text ->
                Tab(selected = pagerState.currentPage == index ,onClick = {
                    corrutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }

                },
                    text = {
                        Text(text = text)
                    })

            }
        }
        HorizontalPager(
            state = pagerState,

        ) { page: Int ->
            val list = when(page){
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value
            }
            MainList(list , currentDay)

        }
    }

}

private fun getWeatherByHours(hours : String) : List<WeatherModel> {
    if(hours.isEmpty()){
        return listOf()
    }

    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()

    for(i in 0 until hoursArray.length()){
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "" ,
                item.getString("time"),
                item.getString("temp_c"),
                item.getJSONObject("condition").getString("text") ,
                item.getJSONObject("condition").getString("icon") ,
                "" ,
                "" ,
                ""

            )
        )
    }
    return list
}






