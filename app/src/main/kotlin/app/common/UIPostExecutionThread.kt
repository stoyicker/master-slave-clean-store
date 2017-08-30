package app.common

import domain.exec.PostExecutionThread
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

object UIPostExecutionThread : PostExecutionThread {
    override fun scheduler(): Scheduler = AndroidSchedulers.mainThread()
}
