package com.example.quran_app_android

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import es.antonborri.home_widget.HomeWidgetPlugin

class MyHomeWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.my_home_widget)
            val data = HomeWidgetPlugin.getData(context)
            val zekr = data.getString("currentZekr", "سبحان الله")
            views.setTextViewText(R.id.widget_text, zekr)
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}
