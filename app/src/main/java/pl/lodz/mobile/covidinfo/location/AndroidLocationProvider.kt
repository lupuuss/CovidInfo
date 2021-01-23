package pl.lodz.mobile.covidinfo.location

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.SingleSubject
import pl.lodz.mobile.covidinfo.base.BaseActivity

class AndroidLocationProvider : LocationProvider {

    private var activity: BaseActivity? = null
    private var task: Task<Location>? = null

    fun init(activity: BaseActivity) {

        this.activity = activity
    }

    @SuppressLint("MissingPermission")
    override fun getLastKnownLocation(): Single<LatLng> {

        val subject = SingleSubject.create<Location>()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        task = fusedLocationClient.lastLocation
            .addOnSuccessListener(subject::onSuccess)
            .addOnFailureListener(subject::onError)
            .addOnCanceledListener {
                subject.onError(Exception("Operation canceled!"))
            }

        return subject.map { LatLng(it.latitude, it.longitude) }
    }

    fun destroy() {
        this.activity = null
        this.task = null
    }
}