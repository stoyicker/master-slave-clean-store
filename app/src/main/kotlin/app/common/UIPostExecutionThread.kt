package app.common

import domain.exec.PostExecutionThread

object UIPostExecutionThread : PostExecutionThread {
    override fun provideScheduler(): rx.Scheduler = rx.android.schedulers.AndroidSchedulers.mainThread()
}
