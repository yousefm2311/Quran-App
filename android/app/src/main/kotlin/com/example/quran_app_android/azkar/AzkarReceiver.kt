package com.example.quran_app_android.azkar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import kotlin.random.Random

class AzkarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            Log.i("AzkarReceiver", "📿 Received scheduled Azkar trigger")

            // ✅ قراءة ملف الأذكار من مجلد assets
            val inputStream = context.assets.open("azkar.json")
            val reader = InputStreamReader(inputStream, "UTF-8")

            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val azkarList: List<Map<String, Any>> = Gson().fromJson(reader, type)

            // ✅ اختيار ذكر عشوائي من أي فئة
            val category = azkarList[Random.nextInt(azkarList.size)]
            val array = category["array"] as List<Map<String, Any>>
            val randomZikr = array[Random.nextInt(array.size)]
            val zikrText = randomZikr["text"] as String

            reader.close()
            inputStream.close()

            // ✅ تجهيز وتشغيل خدمة البابل حتى لو التطبيق مقفول
            val serviceIntent = Intent(context, AzkarBubbleService::class.java).apply {
                putExtra("zikr_text", zikrText)
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

        } catch (e: Exception) {
            Log.e("AzkarReceiver", "❌ Error showing Azkar: ${e.message}")
            e.printStackTrace()
        }
    }
}
