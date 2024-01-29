package com.hezy.blemsg

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezy.blemsg.ui.theme.BleMsgTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BleMsgTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage() {
    Scaffold(topBar = { TopAppBar(title = { Text(text = "DeviceList") }, actions = {}) }) {
        Spacer(modifier = Modifier.height(20.dp))
        DevicesList(modifier = Modifier.padding(20.dp,0.dp))
//        Greeting(name = "Demo", modifier = Modifier.padding(it))
    }
}

@Composable
fun DevicesList(modifier: Modifier = Modifier){
    val scrollState = rememberLazyListState()
    LazyColumn(modifier,state = scrollState) {
        items(10){
             Text(text = "Item $it")
            Divider(color = Color.Black, thickness = 1.5.dp)
        }
    }
}

@Composable
fun BtnArea(){
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { }) {
            Text(text = "Button")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Button(onClick = { }) {
            Text(text = "Button")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BleMsgTheme {
        HomePage()
//        Greeting("Android")
    }
}