package com.task.newapp.workmanager

import android.content.Context
import androidx.work.*
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.RUNNING
import com.google.common.util.concurrent.ListenableFuture
import com.task.newapp.utils.showLog
import java.util.concurrent.ExecutionException


object WorkManagerScheduler {

    private val TAG: String = WorkManagerScheduler.javaClass.simpleName
    const val WORK_MANAGER_NAME = "ChatSyncWorkManager"


    fun refreshPeriodicWork(context: Context) {
        showLog(TAG, "WORK START")

/*
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        // Set Execution around 04:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 17)
        dueDate.set(Calendar.MINUTE, 45)
        dueDate.set(Calendar.SECOND, 0)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff)

        Log.d("MyWorker", "time difference $minutes")
*/

        //define constraints
        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshWork = OneTimeWorkRequest.Builder(ChatSyncWorker::class.java)
            //.setInitialDelay(0, TimeUnit.MINUTES)
            .setConstraints(myConstraints)
            .addTag(WORK_MANAGER_NAME)
            .build()

        if (!isWorkScheduled(WORK_MANAGER_NAME))
            WorkManager.getInstance(context).enqueueUniqueWork(WORK_MANAGER_NAME, ExistingWorkPolicy.REPLACE, refreshWork)
//        WorkManager.getInstance(context).enqueue(refreshWork)


    }

    private fun isWorkScheduled(tag: String): Boolean {
        val instance = WorkManager.getInstance()
        val statuses: ListenableFuture<List<WorkInfo>> = instance.getWorkInfosByTag(tag)
        return try {
            var running = false
            val workInfoList: List<WorkInfo> = statuses.get()
            for (workInfo in workInfoList) {
                val state = workInfo.state
                running = state == RUNNING || state == ENQUEUED
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }

    fun check(context: Context, workName: String) {
        showLog(TAG, "$workName.check")
        val workManager = WorkManager.getInstance()

        val workInfos = workManager.getWorkInfosForUniqueWork(workName).get()
        if (workInfos.size > 0) {
            for (workInfo in workInfos) {
                val workInfo = workInfos[0]
                showLog(TAG, "workInfo.state=${workInfo.state}, id=${workInfo.id}")
                if (workInfo.state == WorkInfo.State.BLOCKED || workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING) {
                    showLog(TAG, "isAlive")
                } else {
                    showLog(TAG, "isDead")

                }
            }
        } else {
            showLog(TAG, "notFound")
        }
    }

    fun cancel(context: Context) {
        showLog(TAG, "WORK FINISH")
        WorkManager.getInstance(context).cancelAllWorkByTag("WORK_MANAGER_NAME")
    }
}