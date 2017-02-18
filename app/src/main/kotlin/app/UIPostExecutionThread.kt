package app

import domain.exec.PostExecutionThread
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers

object UIPostExecutionThread : PostExecutionThread {
    override fun provideScheduler(): Scheduler = AndroidSchedulers.mainThread()
}
