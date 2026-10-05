package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ScholarAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, ScholarAppWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (id in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.widget.ACTION_REFRESH_WIDGET"
        const val EXTRA_NAV_TARGET = "NAV_TARGET"

        fun updateAllWidgets(context: Context) {
            try {
                val intent = Intent(context, ScholarAppWidgetProvider::class.java).apply {
                    action = ACTION_REFRESH_WIDGET
                }
                context.sendBroadcast(intent)
            } catch (_: Exception) {}
        }

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_scholar_overview)

            // Setup basic click intents immediately
            setupClickIntents(context, views)

            // Date text
            val dateFmt = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            views.setTextViewText(R.id.widget_date_text, dateFmt.format(Date()).uppercase())

            // Default fallback state (graceful handling if app not opened or DB empty)
            views.setTextViewText(R.id.widget_class_text, "No classes scheduled today")
            views.setTextViewText(R.id.widget_priority_text, "All tasks up to date")
            views.setTextViewText(R.id.widget_research_text, "No active milestones due")
            views.setTextViewText(R.id.widget_chinese_text, "All flashcards reviewed")

            appWidgetManager.updateAppWidget(appWidgetId, views)

            // Load live data from Room DB in background coroutine
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getInstance(context)
                    val todayDayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(Date())
                    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    val currentDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    // 1. Next Class
                    val timetable = db.universityDao().getAllTimetableClasses().firstOrNull() ?: emptyList()
                    val todayClasses = timetable
                        .filter { it.dayOfWeek.equals(todayDayOfWeek, ignoreCase = true) }
                        .sortedBy { it.startTime }

                    val nextClass = todayClasses.firstOrNull { it.endTime >= currentTime } ?: todayClasses.firstOrNull()
                    val classText = if (nextClass != null) {
                        "${nextClass.startTime}-${nextClass.endTime} ${nextClass.courseCode} (${nextClass.classroom})"
                    } else {
                        "No classes scheduled today"
                    }

                    // 2. Priority & Nearest Deadline
                    val tasks = db.universityDao().getAllTasks().firstOrNull() ?: emptyList()
                    val pendingTasks = tasks.filter { !it.isCompleted }
                    val urgentTask = pendingTasks.firstOrNull { it.deadline <= currentDateStr }
                        ?: pendingTasks.minByOrNull { it.deadline }

                    val priorityText = when {
                        urgentTask != null -> {
                            val isOverdue = urgentTask.deadline < currentDateStr
                            val label = if (isOverdue) "OVERDUE" else "DUE ${urgentTask.deadline.takeLast(5)}"
                            "[$label] ${urgentTask.title}"
                        }
                        pendingTasks.isNotEmpty() -> "${pendingTasks.size} tasks pending"
                        else -> "All tasks up to date"
                    }

                    // 3. Research Priority
                    val milestones = db.researchDao().getAllMilestones().firstOrNull() ?: emptyList()
                    val pendingMilestones = milestones.filter { !it.isCompleted }
                    val nearestMilestone = pendingMilestones.minByOrNull { it.deadline }

                    val researchText = if (nearestMilestone != null) {
                        "${nearestMilestone.title} (Due: ${nearestMilestone.deadline.takeLast(5)})"
                    } else {
                        "No active milestones due"
                    }

                    // 4. Chinese Practice
                    val userEmail = "alexei.chen@ysu.edu.cn"
                    val vocabList = db.chineseDao().getAllVocabulary(userEmail).firstOrNull() ?: emptyList()
                    val dueCount = vocabList.count { it.reviewStatus == "Due" || it.reviewStatus == "New" || !it.isKnown }
                    val chineseText = if (dueCount > 0) {
                        "$dueCount flashcards due for practice"
                    } else if (vocabList.isNotEmpty()) {
                        "All ${vocabList.size} terms mastered"
                    } else {
                        "Daily practice ready"
                    }

                    // Update views with live data
                    views.setTextViewText(R.id.widget_class_text, classText)
                    views.setTextViewText(R.id.widget_priority_text, priorityText)
                    views.setTextViewText(R.id.widget_research_text, researchText)
                    views.setTextViewText(R.id.widget_chinese_text, chineseText)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (_: Exception) {
                    // Graceful fallback: maintain defaults
                }
            }
        }

        private fun setupClickIntents(context: Context, views: RemoteViews) {
            // Main widget click -> open app
            val appIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val appPendingIntent = PendingIntent.getActivity(
                context,
                100,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, appPendingIntent)

            // Quick Action 1: Ask AI
            val aiIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_NAV_TARGET, "command_center")
            }
            val aiPendingIntent = PendingIntent.getActivity(
                context,
                101,
                aiIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_ask_ai, aiPendingIntent)

            // Quick Action 2: Study
            val studyIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_NAV_TARGET, "university")
            }
            val studyPendingIntent = PendingIntent.getActivity(
                context,
                102,
                studyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_study, studyPendingIntent)

            // Quick Action 3: Research
            val researchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_NAV_TARGET, "research")
            }
            val researchPendingIntent = PendingIntent.getActivity(
                context,
                103,
                researchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_research, researchPendingIntent)
        }
    }
}
